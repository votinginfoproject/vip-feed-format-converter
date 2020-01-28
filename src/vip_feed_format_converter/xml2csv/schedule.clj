(ns vip-feed-format-converter.xml2csv.schedule
  (:require [clojure.string :as str]
            [vip-feed-format-converter.util :as util]))

(def headers
  [:id :start_time :end_time :is_only_by_appointment :is_or_by_appointment
   :is_subject_to_change :start_date :end_date :hours_open_id])

(defn assoc-chars [key]
  (fn [ctx value]
    (util/assoc-chars :schedule ctx value key)))

(defn assoc-chars-hours [key]
  (fn [ctx value]
    (util/assoc-chars :hours ctx value key)))

(defn handlers [path-to-parent-id]
  {:start (fn [ctx event]
            (-> ctx
                (assoc-in [:tmp :schedule]
                          {:hours_open_id (get-in ctx path-to-parent-id)})
                (assoc-in [:tmp :schedule-hours] [])))
   :Hours {:start (fn [ctx event]
                    (assoc-in ctx [:tmp :hours] {}))
           :StartTime {:chars (assoc-chars-hours :start_time)}
           :EndTime {:chars (assoc-chars-hours :end_time)}
           :end (fn [ctx _]
                  (-> ctx
                      (update-in [:tmp :schedule-hours] conj
                                 (get-in ctx [:tmp :hours]))
                      (update :tmp dissoc :hours)))}
   :IsOnlyByAppointment {:chars (assoc-chars :is_only_by_appointment)}
   :IsOrByAppointment   {:chars (assoc-chars :is_or_by_appointment)}
   :IsSubjectToChange   {:chars (assoc-chars :is_subject_to_change)}
   :StartDate           {:chars (assoc-chars :start_date)}
   :EndDate             {:chars (assoc-chars :end_date)}
   :end (fn [ctx _]
          (let [hours (get-in ctx [:tmp :schedule-hours])
                schedule (get-in ctx [:tmp :schedule])
                schedule-fn (fn [h]
                              (-> schedule
                                  (merge h)
                                  (merge {:id (.toString
                                               (java.util.UUID/randomUUID))})))
                schedules (mapv schedule-fn hours)]
            #_(println (count (get-in ctx [:csv-data :schedule :data])))
            (if-let [data (get-in ctx [:csv-data :schedule :data])]
              (-> ctx
                  (update-in [:csv-data :schedule :data]
                             into schedules)
                  (update :tmp dissoc :schedule)
                  (update :tmp dissoc :schedule-hours))
              (-> ctx
                  (update-in [:csv-data :schedule]
                             assoc :data schedules)
                  (update :tmp dissoc :schedule)
                  (update :tmp dissoc :schedule-hours)))))})
