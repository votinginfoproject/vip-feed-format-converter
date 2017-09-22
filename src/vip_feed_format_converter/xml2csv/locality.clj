(ns vip-feed-format-converter.xml2csv.locality
  (:require [vip-feed-format-converter.util :as util]))

(def headers
  [:id :election_administration_id :external_identifier_type
   :external_identifier_othertype :external_identifier_value
   :name :polling_location_ids :state_id :type :other_type])

(defn assoc-chars [key]
  (fn [ctx event]
    (util/assoc-chars :locality ctx event key)))

(def handlers
  {:start (fn [ctx event]
            (assoc-in ctx [:tmp :locality]
                      {:id (get-in event [:attrs :id])}))
   :ElectionAdministrationId {:chars (assoc-chars :election_administration_id)}
   :ExternalIdentifiers
   {:ExternalIdentifier
    {:Type      {:chars (assoc-chars :external_identifier_type)}
     :OtherType {:chars (assoc-chars :external_identifier_othertype)}
     :Value     {:chars (assoc-chars :external_identifier_value)}}}
   :Name                     {:chars (assoc-chars :name)}
   :PollingLocationIds       {:chars (assoc-chars :polling_location_ids)}
   :StateId                  {:chars (assoc-chars :state_id)}
   :Type                     {:chars (assoc-chars :type)}
   :OtherType                {:chars (assoc-chars :other_type)}
   :end (fn [ctx _]
          (-> ctx
              (update-in [:csv-data :locality :data]
                         conj (get-in ctx [:tmp :locality]))
              (update :tmp dissoc :locality)))})
