(ns example.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub
 :status
 (fn [db _]
   (:app/status db)))

(rf/reg-sub
 :error
 (fn [db _]
   (select-keys db [:error-context
                    :error-data
                    :error-event])))

(rf/reg-sub
 :tweet-count
 (fn [db _]
   (count (:tweets db))))

(rf/reg-sub
 :latest-10
 (fn [db _]
   (take 10 (:tweets db))))
