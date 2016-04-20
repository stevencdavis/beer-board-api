(ns signafire.lib.config
  (:require nomad
            [clojure.java.io :as java.io]))

(defn get-config
  "Get the nomad config"
  []
  (nomad/read-config (java.io/resource "config/config.edn")))