(ns vip-feed-format-converter.xml2csv.election-administration
  (:require [vip-feed-format-converter.xml2csv.department :as department]
            [vip-feed-format-converter.util :as util]))

(def headers
  [:id :absentee_uri :am_i_registered_uri :elections_uri :registration_uri
   :rules_uri :what_is_on_my_ballot_uri :where_do_i_vote_uri])

(defn assoc-chars [key]
  (fn [ctx value]
    (util/assoc-chars :election-administration ctx value key)))

(def handlers
  {:start (fn [ctx event]
            (-> ctx
                (assoc-in [:tmp :election-administration]
                          {:id (get-in event [:attrs :id])})))
   :AbsenteeUri         {:chars (assoc-chars :absentee_uri)}
   :AmIRegisteredUri    {:chars (assoc-chars :am_i_registered_uri)}
   :ElectionsUri        {:chars (assoc-chars :elections_uri)}
   :RegistrationUri     {:chars (assoc-chars :registration_uri)}
   :RulesUri            {:chars (assoc-chars :rules_uri)}
   :WhatIsOnMyBallotUri {:chars (assoc-chars :what_is_on_my_ballot_uri)}
   :WhereDoIVoteUri     {:chars (assoc-chars :where_do_I_vote_uri)}
   :Department          (department/handlers
                         [:tmp :election-administration :id])
   :end (fn [ctx _]
          (-> ctx
              (update-in [:csv-data :election-administration :data]
                         conj (get-in ctx [:tmp :election-administration]))
              (update :tmp dissoc :election-administration)))})
