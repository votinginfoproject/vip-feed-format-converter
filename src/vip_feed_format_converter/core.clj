(ns vip-feed-format-converter.core
  (:require [vip-feed-format-converter.csv :as csv]
            [vip-feed-format-converter.election :as election]
            [vip-feed-format-converter.locality :as locality]
            [vip-feed-format-converter.xml :as xml]
            [clojure.java.io :as io])
  (:gen-class))

(defn open-input-file [ctx]
  (assoc ctx :input (io/reader (:in-file ctx))))

(defn close-input-file [{:keys [input] :as ctx}]
  (when input
    (.close input))
  (dissoc ctx :input))

(defn election-headers [ctx]
  (assoc-in ctx [:csv-data :election :headers] election/headers))

(defn locality-headers [ctx]
  (assoc-in ctx [:csv-data :locality :headers] locality/headers))

(defn set-handlers [ctx]
  (assoc ctx :handlers
         {:VipObject
          {:Election election/handlers
           :Locality locality/handlers}}))

(defn process [in-file out-dir]
  (-> {:in-file in-file
       :out-dir out-dir
       :tag-path []}
      open-input-file
      set-handlers
      election-headers
      locality-headers
      xml/parse-file
      csv/write-files
      close-input-file))

(defn -main
  [in-file out-dir & args]
  (println "Reading XML feed from" in-file "and outputting CSV files to"
           (str out-dir "/") "\n")
  (process in-file out-dir))

