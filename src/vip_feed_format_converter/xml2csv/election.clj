(ns vip-feed-format-converter.xml2csv.election
  (:require [vip-feed-format-converter.util :as util]))

(def headers
  [:id :date :name :election_type :state_id :is_statewide
   :registration_info :absentee_ballot_info :results_uri
   :polling_hours :has_election_day_registration
   :registration_deadline :absentee_request_deadline
   :hours_open_id])

(defn assoc-chars [key]
  (fn [ctx value]
    (util/assoc-chars :election ctx value key)))

(defn assoc-intl-text [key]
  (fn [ctx value]
    (util/assoc-intl-text :election "en" ctx value key)))

(def handlers
  {:start (fn [ctx event]
            (assoc-in ctx [:tmp :election]
                      {:id (get-in event [:attrs :id])}))
   :Date                 {:chars (assoc-chars :date)}
   :Name          {:Text {:chars (assoc-intl-text :name)}}
   :ElectionType  {:Text {:chars (assoc-intl-text :election_type)}}
   :StateId              {:chars (assoc-chars :state_id)}
   :IsStatewide          {:chars (assoc-chars :is_statewide)}
   :RegistrationInfo     {:chars (assoc-intl-text :registration_info)}
   :AbsenteeBallotInfo   {:chars (assoc-intl-text :absentee_ballot_info)}
   :ResultsUri           {:chars (assoc-chars :results_uri)}
   :PollingHours         {:chars (assoc-intl-text :polling_hours)}
   :HoursOpenId          {:chars (assoc-chars :hours_open_id)}
   :HasElectionDayRegistration
                         {:chars (assoc-chars :has_election_day_registration)}
   :RegistrationDeadline {:chars (assoc-chars :registration_deadline)}
   :AbsenteeRequestDeadline
                         {:chars (assoc-chars :absentee_request_deadline)}
   :end (fn [ctx _]
          (-> ctx
              (update-in [:csv-data :election :data]
                         conj (get-in ctx [:tmp :election]))
              (update :tmp dissoc :election)))})
