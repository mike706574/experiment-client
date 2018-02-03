(ns example.views
  (:require [reagent.core :as r]
            [re-frame.core :as rf]))

(defn ok []
  (let [tweet-count @(rf/subscribe [:tweet-count])
        latest @(rf/subscribe [:latest-10])]
    [:div
     [:h1 "Tweets"]
     [:p (str tweet-count " total.")]
     [:ul
      (for [tweet latest]
        (let [{:keys [id username text]} tweet]
          [:li {:key id}
           [:p (str username " => " text)]]))]]))

(defn pretty [form] (with-out-str (cljs.pprint/pprint form)))

(defn error []
  (let [error @(rf/subscribe [:error])]
    [:div.error
     [:h1 "Error: " (:error-context error)]
     [:pre (pretty (:error-data error))]
     (when-let [db (:db error)]
       [:div
        [:h5 "Database:"]
        [:pre (pretty db)]])]))

(defn app []
  (let [status @(rf/subscribe [:status])]
    (case status
      :ok [ok]
      :booting [:p "Booting..."]
      :error [error]
      [:p "Error!"])))
