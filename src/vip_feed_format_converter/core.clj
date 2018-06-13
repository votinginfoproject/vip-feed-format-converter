(ns vip-feed-format-converter.core
  (:require [vip-feed-format-converter.csv :as csv]
            [vip-feed-format-converter.xml2csv.election :as election]
            [vip-feed-format-converter.xml2csv.electoral-district
             :as electoral-district]
            [vip-feed-format-converter.xml2csv.locality :as locality]
            [vip-feed-format-converter.xml2csv.polling-location
             :as polling-location]
            [vip-feed-format-converter.xml2csv.precinct :as precinct]
            [vip-feed-format-converter.xml2csv.source :as source]
            [vip-feed-format-converter.xml2csv.state :as state]
            [vip-feed-format-converter.xml2csv.street-segment
             :as street-segment]
            [vip-feed-format-converter.xml :as xml]
            [clojure.java.io :as io]
            [clojure.core.async :as async])
  (:gen-class))

(defn open-input-file [ctx]
  (assoc ctx :input (io/input-stream (:in-file ctx))))

(defn close-input-file [{:keys [input] :as ctx}]
  (when input
    (.close input))
  (dissoc ctx :input))

(defn set-handlers [ctx]
  (assoc ctx :handlers
         {:VipObject
          {:Election           election/handlers
           :Locality           locality/handlers
           :ElectoralDistrict  electoral-district/handlers
           :PollingLocation    polling-location/handlers
           :Precinct           precinct/handlers
           :Source             source/handlers
           :State              state/handlers
           :StreetSegment      (street-segment/handlers
                                (-> ctx :channels :street-segment))}}))

(defn set-headers [ctx]
  (-> ctx
      (assoc-in [:csv-data :election :headers]
                election/headers)
      (assoc-in [:csv-data :locality :headers]
                locality/headers)
      (assoc-in [:csv-data :electoral-district :headers]
                electoral-district/headers)
      (assoc-in [:csv-data :polling-location :headers]
                polling-location/headers)
      (assoc-in [:csv-data :precinct :headers]
                precinct/headers)
      (assoc-in [:csv-data :source :headers]
                source/headers)
      (assoc-in [:csv-data :state :headers]
                state/headers)))

(defn setup-writers [ctx]
  (-> ctx
      (csv/setup-writer :street-segment street-segment/headers
                        (-> ctx :channels :street-segment))))

(defn close-channels [ctx]
  (println "Closing read channels")
  (run! async/close! (-> ctx :channels vals))
  ctx)

(defn wait-for-finish [ctx]
  (println "Waiting for write threads to finish")
  (run! async/<!! (-> ctx :write-channels))
  ctx)

(defn process [in-file out-dir]
  (-> {:in-file in-file
       :out-dir out-dir
       :tag-path []
       :channels {:street-segment (async/chan 100)}
       :write-channels []}
      open-input-file
      setup-writers
      set-handlers
      set-headers
      xml/parse-file
      close-channels
      csv/write-files
      wait-for-finish
      close-input-file)
  (println "Done!"))

(defn -main
  [in-file out-dir & args]
  (println "Reading XML feed from" in-file "and outputting CSV files to"
           (str out-dir "/") "\n")
  (process in-file out-dir))
