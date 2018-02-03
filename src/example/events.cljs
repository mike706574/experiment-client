(ns example.events
  (:require [ajax.core :as ajax]
            [example.effects]
            [cemerick.url :as url]
            [clojure.spec.alpha :as s]
            [clojure.string :as str]
            [re-frame.core :as rf]
            [re-frame.spec-interceptor :refer [spec-interceptor]]
            [taoensso.timbre :as log]))

;; Config

(def host "localhost:8001")
(def streaming-tweets-url (str "ws://" host "/streaming/tweets"))

;; Specs

(defmulti db-status :app/status)

;; booting
(defmethod db-status :booting [_]
  (s/keys :req [:app/status]))

;; ok
(defmethod db-status :ok [_]
  (s/keys :req-un []))

;; error

(s/def :example/error map?)
(s/def :example/error-context string?)

(defmethod db-status :error [_]
  (s/keys :req [:app/status]
          :req-un [:example/error-context
                   :example/error-data]
          :opt-un [:example/error-event]))

;; db
(s/def :example/db (s/multi-spec db-status :app/status))

;; Utility

(defn fail
  ([context data]
   {:app/status :error
    :error-context context
    :error-data data})
  ([context data db]
   (assoc (fail context data) :db db)))

;; Event Handlers

(defn handle-invalid-db
  [db event data]
  (if (= (:app/status db) :error)
    db
    (fail (str "Validating db after " (first event) ".") (assoc data :db db))))

(def custom-spec-interceptor
  (spec-interceptor :example/db handle-invalid-db))
(def interceptors [rf/debug custom-spec-interceptor rf/trim-v])

(defn reg-event-db [k f] (rf/reg-event-db k interceptors f))
(defn reg-event-fx [k f] (rf/reg-event-fx k interceptors f))

(defn parse-message [event]
  (let [data (.-data event)]
    (js->clj (.parse js/JSON data) :keywordize-keys true)))

(reg-event-db
 :message-received
 (fn [db [event]]
   (let [{:keys [type body] :as message} (parse-message event)]
     (case type
       :article (update db :articles #(cons body %))
       :tweet (update db :tweets #(cons body %))
       (fail (str "Invalid message received.") message)))))

(reg-event-fx
 :connect-websocket
 (fn [{db :db} _]
   {:websocket {:method :get
                :uri streaming-tweets-url
                :on-message [:tweet-received]
                :on-success [:websocket-success]
                :on-failure [:websocket-failure]}}))

(reg-event-db
 :websocket-success
 (fn [db [socket]]
   (log/debug "Connected.")
   (merge db {:websocket socket
              :tweets '()})))

(reg-event-db
 :websocket-failure
 (fn [db [failure]]
   (fail "Connecting websocket." {:message (str "Failed to connect to " (-> failure .-target .-url) ".")})))

(reg-event-db
 :websocket-error
 (fn [db [failure]]
   (fail "Websocket connection.." failure)))

;; Life cycle
(reg-event-fx
 :boot
 (fn [{db :db} _]
   (when-let [websocket (:websocket db)]
     (log/info "Closing existing websocket.")
     (.close websocket))
   {:db {:app/status :booting}
    :async-flow {:first-dispatch [:connect-websocket]
                 :rules [{:when :seen? :events :token-success :dispatch-n [[:connect-websocket]]}
                         {:when :seen-all-of? :events [:websocket-success] :dispatch [:start]}
                         {:when :seen-any-of? :events [:websocket-failure] :halt? true}]}}))

(reg-event-db
 :start
 (fn [db _]
   (assoc db :app/status :ok)))
