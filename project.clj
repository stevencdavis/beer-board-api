(defproject signafire/beer-board-api "0.1.0"
  :description "WeWork Beer Board API"
  :url "TBD"
  :license {:name "None"
            :url  "http://choosealicense.com/"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [jarohen/nomad "0.7.2"]
                 [com.taoensso/timbre "4.3.1"]
                 [com.stuartsierra/component "0.3.1"]
                 [http-kit "2.1.19"]
                 [ring-cors "0.1.7"]
                 [ring/ring-devel "1.4.0"]
                 [compojure "1.5.0"]
                 [liberator "0.14.1"]
                 [org.clojure/data.json "0.2.6"]
                 [org.clojure/java.jdbc "0.5.8"]
                 [org.xerial/sqlite-jdbc "3.8.11.2"]
                 [com.mchange/c3p0 "0.9.5.2"]]
  :main ^:skip-aot signafire.core
  :profiles {:uberjar {:aot [signafire.core]}
             :dev     {:dependencies [[javax.servlet/servlet-api "2.5"]
                                      [org.clojure/tools.namespace "0.2.11"]
                                      [midje "1.8.3"]]
                       :source-paths ["dev"]}}
  :plugins [[lein-midje "3.2"]])
