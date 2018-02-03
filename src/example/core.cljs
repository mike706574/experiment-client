(ns example.core
  (:require [example.events]
            [example.subs]
            [example.views]
            [day8.re-frame.async-flow-fx]
            [day8.re-frame.http-fx]
            [devtools.core :as devtools]
            [goog.events :as events]
            [reagent.core :as r]
            [re-frame.core :as rf]
            [taoensso.timbre :as log]
            [clojure.spec.alpha :as s]))

(devtools/install!)
(enable-console-print!)
(log/set-level! :debug)

(defn ^:export run
  []
  (rf/dispatch-sync [:boot])
  (r/render [example.views/app] (js/document.getElementById "app")))
