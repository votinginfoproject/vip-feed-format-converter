(ns vip-feed-format-converter.core
  (:require [vip-feed-format-converter.csv :as csv]
            [vip-feed-format-converter.xml2csv.ballot-measure-contest
             :as ballot-measure-contest]
            [vip-feed-format-converter.xml2csv.ballot-measure-selection
             :as ballot-measure-selection]
            [vip-feed-format-converter.xml2csv.ballot-selection
             :as ballot-selection]
            [vip-feed-format-converter.xml2csv.ballot-style
             :as ballot-style]
            [vip-feed-format-converter.xml2csv.candidate
             :as candidate]
            [vip-feed-format-converter.xml2csv.candidate-contest
             :as candidate-contest]
            [vip-feed-format-converter.xml2csv.candidate-selection
             :as candidate-selection]
            [vip-feed-format-converter.xml2csv.contact-information
             :as contact-information]
            [vip-feed-format-converter.xml2csv.contest
             :as contest]
            [vip-feed-format-converter.xml2csv.department :as department]
            [vip-feed-format-converter.xml2csv.election :as election]
            [vip-feed-format-converter.xml2csv.election-administration
             :as election-administration]
            [vip-feed-format-converter.xml2csv.electoral-district
             :as electoral-district]
            [vip-feed-format-converter.xml2csv.hours-open :as hours-open]
            [vip-feed-format-converter.xml2csv.locality :as locality]
            [vip-feed-format-converter.xml2csv.office :as office]
            [vip-feed-format-converter.xml2csv.party
             :as party]
            [vip-feed-format-converter.xml2csv.person
             :as person]
            [vip-feed-format-converter.xml2csv.polling-location
             :as polling-location]
            [vip-feed-format-converter.xml2csv.precinct :as precinct]
            [vip-feed-format-converter.xml2csv.schedule :as schedule]
            [vip-feed-format-converter.xml2csv.source :as source]
            [vip-feed-format-converter.xml2csv.state :as state]
            [vip-feed-format-converter.xml2csv.street-segment
             :as street-segment]
            [vip-feed-format-converter.xml2csv.voter-service
             :as voter-service]
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
          {:BallotMeasureContest    ballot-measure-contest/handlers
           :BallotMeasureSelection  ballot-measure-selection/handlers
           :BallotSelection         ballot-selection/handlers
           :BallotStyle             ballot-style/handlers
           :Candidate               candidate/handlers
           :CandidateContest        candidate-contest/handlers
           :CandidateSelection      candidate-selection/handlers
           :Contest                 contest/handlers
           :Election                election/handlers
           :ElectionAdministration  election-administration/handlers
           :ElectoralDistrict       electoral-district/handlers
           :HoursOpen               hours-open/handlers
           :Locality                locality/handlers
           :Office                  office/handlers
           :Party                   party/handlers
           :Person                  person/handlers
           :PollingLocation         polling-location/handlers
           :Precinct                precinct/handlers
           :Source                  source/handlers
           :State                   state/handlers
           :StreetSegment           (street-segment/handlers
                                     (-> ctx :channels :street-segment))}}))

(defn set-headers [ctx]
  (-> ctx
      (assoc-in [:csv-data :ballot-measure-contest :headers]
                ballot-measure-contest/headers)
      (assoc-in [:csv-data :ballot-measure-selection :headers]
                ballot-measure-selection/headers)
      (assoc-in [:csv-data :ballot-selection :headers]
                ballot-selection/headers)
      (assoc-in [:csv-data :ballot-style :headers]
                ballot-style/headers)
      (assoc-in [:csv-data :candidate :headers]
                candidate/headers)
      (assoc-in [:csv-data :candidate-contest :headers]
                candidate-contest/headers)
      (assoc-in [:csv-data :candidate-selection :headers]
                candidate-selection/headers)
      (assoc-in [:csv-data :contact-information :headers]
                contact-information/headers)
      (assoc-in [:csv-data :contest :headers]
                contest/headers)
      (assoc-in [:csv-data :department :headers]
                department/headers)
      (assoc-in [:csv-data :election :headers]
                election/headers)
      (assoc-in [:csv-data :election-administration :headers]
                election-administration/headers)
      (assoc-in [:csv-data :electoral-district :headers]
                electoral-district/headers)
      (assoc-in [:csv-data :hours-open :headers]
                hours-open/headers)
      (assoc-in [:csv-data :locality :headers]
                locality/headers)
      (assoc-in [:csv-data :office :headers]
                office/headers)
      (assoc-in [:csv-data :party :headers]
                party/headers)
      (assoc-in [:csv-data :person :headers]
                person/headers)
      (assoc-in [:csv-data :polling-location :headers]
                polling-location/headers)
      (assoc-in [:csv-data :precinct :headers]
                precinct/headers)
      (assoc-in [:csv-data :schedule :headers]
                schedule/headers)
      (assoc-in [:csv-data :source :headers]
                source/headers)
      (assoc-in [:csv-data :state :headers]
                state/headers)
      (assoc-in [:csv-data :voter-service :headers]
                voter-service/headers)))

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
