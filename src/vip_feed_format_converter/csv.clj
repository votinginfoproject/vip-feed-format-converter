(ns vip-feed-format-converter.csv
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]))

(defn write-files [ctx]
  (doseq [[file {:keys [headers data]}] (:csv-data ctx)]
    (let [csv-data (reduce (fn [m d]
                             (conj m
                                   (for [h headers]
                                     (get d h))))
                           [(map name headers)] data)]
      (with-open [csv-file (io/writer (str (:out-dir ctx)
                                           "/" (name file) ".csv"))]
        (csv/write-csv csv-file csv-data)))))