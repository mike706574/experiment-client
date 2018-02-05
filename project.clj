(defproject fun.mike/example-twitter-client "0.0.1-SNAPSHOT"
  :description "A project."
  :url "https://github.com/mike706574/tweet-example-client"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"
            :distribution :repo
            :comments "same as Clojure"}
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/spec.alpha "0.1.123"]
                 [org.clojure/clojurescript "1.9.671"]
                 [com.stuartsierra/component "0.3.2"]
                 [com.taoensso/timbre "4.10.0"]
                 [com.cognitect/transit-cljs "0.8.239"]
                 [reagent "0.7.0"]
                 [re-frame "0.9.4"]
                 [day8.re-frame/http-fx "0.1.3"]
                 [day8.re-frame/async-flow-fx "0.0.7"]
                 [cljs-ajax "0.6.0"]
                 [binaryage/devtools "0.9.4"]
                 [com.cemerick/url "0.1.1"]
                 [fun.mike/re-frame-websocket-fx "0.0.1"]
                 [fun.mike/re-frame-spec-interceptor "0.0.1"]]
  :plugins [[lein-cljsbuild "1.1.5"]
            [lein-doo "0.1.7"]
            [lein-figwheel "0.5.10"]]
  :source-paths ["src/cljs"]
  :hooks [leiningen.cljsbuild]
  :profiles {:test {:source-paths ["src" "test"]
                    :compiler {:output-to "target/test.js"
                               :output-dir "target/test"
                               :main "example.runner"
                               :optimizations :simple}}
             :dev {:source-paths ["dev"]
                   :target-path "target/dev"
                   :dependencies [[com.cemerick/piggieback "0.2.2"]
                                  [figwheel-sidecar "0.5.10"]
                                  [org.clojure/tools.namespace "0.2.11"]]
                   :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}}}
  :cljsbuild {:builds {:dev {:source-paths ["src"]
                             :figwheel {:on-jsload "example.core/run"}
                             :compiler {:output-to "resources/public/js/client.js"
                                        :output-dir "resources/public/js"
                                        :main "example.core"
                                        :asset-path "js"
                                        :optimizations :none
                                        :source-map true
                                        :source-map-timestamp true}}}}
  :doo {:build "test"
        :repl false
        :alias {:default [:phantom]
                :browsers [:chrome :firefox]
                :all [:browsers :headless]}}
  :figwheel {:repl false
             :http-server-root "public"
             :server-port 3500}
  :clean-targets ^{:protect false} ["resources/public/js"])
