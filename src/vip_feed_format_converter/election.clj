(ns vip-feed-format-converter.election
  (:require [clojure.string :as str]
            [vip-feed-format-converter.util :as util]))

(def headers
  [:id :date :name :election_type :state_id :is_statewide
   :registration_info :absentee_ballot_info :results_uri
   :polling_hours :has_election_day_registration
   :registration_deadline :absentee_request_deadline
   :hours_open_id])

(def assoc-chars (partial util/assoc-chars :election))

(def assoc-intl-text (partial util/assoc-intl-text :election "en"))

(def handlers
  {:start (fn [ctx event]
            (assoc-in ctx [:tmp :election]
                      {:id (get-in event [:attrs :id])}))
   :Date {:chars (fn [ctx event] (assoc-chars ctx event :date))}
   :Name {:Text {:chars (fn [ctx event]
                          (assoc-intl-text ctx event :name))}}
   :ElectionType {:Text {:chars
                         (fn [ctx event]
                           (assoc-intl-text ctx event :election_type))}}
   :StateId {:chars (fn [ctx event]
                      (assoc-chars ctx event :state_id))}
   :IsStatewide {:chars (fn [ctx event]
                          (assoc-chars ctx event :is_statewide))}
   :RegistrationInfo {:chars (fn [ctx event]
                               (assoc-intl-text ctx event :registration_info))}
   :AbsenteeBallotInfo {:chars (fn [ctx event]
                                 (assoc-intl-text ctx event
                                                  :absentee_ballot_info))}
   :ResultsUri {:chars (fn [ctx event]
                         (assoc-chars ctx event :results_uri))}
   :PollingHours {:chars (fn [ctx event]
                           (assoc-intl-text ctx event :polling_hours))}
   :HoursOpenId {:chars (fn [ctx event]
                          (assoc-chars ctx event :hours_open_id))}
   :HasElectionDayRegistration
   {:chars (fn [ctx event]
             (assoc-chars ctx event :has_election_day_registration))}
   :RegistrationDeadline
   {:chars (fn [ctx event]
             (assoc-chars ctx event :registration_deadline))}
   :AbsenteeRequestDeadline
   {:chars (fn [ctx event]
             (assoc-chars ctx event :absentee_request_deadline))}
   :end (fn [ctx _]
          (-> ctx
              (update-in [:csv-data :election :data]
                         conj (get-in ctx [:tmp :election]))
              (update :tmp dissoc :election)))})
