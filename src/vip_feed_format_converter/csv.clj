(ns vip-feed-format-converter.csv
  (:require [clojure.core.async :as async]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [clojure.string :as str]))

(defn filename
  [ctx file]
  (let [basename (str/replace (name file) "-" "_")
        dir (:out-dir ctx)]
    (str dir "/" basename ".txt")))

(defn setup-writer
  "Sets up to write to a CSV based on pulling values from the
   supplied async channel."
  [ctx type headers channel]
  (update ctx :write-channels conj
          (async/thread
            (println "Start writing stream for" (name type))
            (with-open [csv-file (io/writer (filename ctx type))]
              ;; write the file header
              (csv/write-csv csv-file [(map name headers)])
              ;; write data as it streams in
              (loop [data (async/<!! channel)]
                (when data
                  (csv/write-csv csv-file [(map #(get data %) headers)])
                  (recur (async/<!! channel))))
              (println "Stop writing stream for" (name type)))
            ;; return something on the thread channel
            type)))

(defn write-files [ctx]
  (println "Writing :csv-data files")
  (doseq [[file {:keys [headers data]}] (:csv-data ctx)]
    (let [csv-data (reduce (fn [m d]
                             (conj m
                                   (for [h headers]
                                     (get d h))))
                           [(map name headers)] data)]
      (with-open [csv-file (io/writer (filename ctx file))]
        (csv/write-csv csv-file csv-data))))
  (println "Done writing :csv-data files")
  ctx)
