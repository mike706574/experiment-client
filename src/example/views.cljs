(ns example.views
  (:require [reagent.core :as r]
            [re-frame.core :as rf]))


(defn articles []
  (let [article-count @(rf/subscribe [:article-count])
        latest @(rf/subscribe [:last-n-articles 10])]
    [:div
     [:h1 "Articles"]
     [:p (str article-count " total.")]
     [:ul
      (for [article latest]
        (let [{:keys [id title url author]} article]
          [:li {:key id} [:a {:href url} title " - " author]]))]]))

(defn tweets []
  (let [tweet-count @(rf/subscribe [:tweet-count])
        latest @(rf/subscribe [:last-n-tweets 10])]
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
      :ok [:div
           [tweets]
           [articles]]
      :booting [:p "Booting..."]
      :error [error]
      [:p "Error!"])))
