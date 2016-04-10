(defproject ec "0.1.0-SNAPSHOT"

  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.170"]]

  :plugins [[lein-cljsbuild "1.1.1"][lein-figwheel "0.5.0-2"]]
 
  :source-paths ["src"]

  :clean-targets ^{:protect false} ["resources/public/js/out" "resources/public/js/prod-out" "target"]

  :cljsbuild {:builds [
   {:id "dev"
    :source-paths ["src"]
    :figwheel {:on-jsload "game.core/on-js-reload"}
    :compiler {:main "game.core"
               :asset-path "js/out"
               :output-to "resources/public/main.js"
               :output-dir "resources/public/js/out"
               :parallel-build true
               :optimizations :none
               :pretty-print false}}
    {:id "prod"
    :source-paths ["src"]
    :compiler {:main "game.core"
              :asset-path "js/out"
              :output-to "resources/public/ec.js"
              :output-dir "resources/public/js/prod-out"
              :parallel-build true
              :optimizations :advanced
              :pretty-print false}}]}
  :figwheel {:server-port 3449
             :css-dirs ["resources/public/css"]
             :reload-clj-files {:clj true :cljc false}})
