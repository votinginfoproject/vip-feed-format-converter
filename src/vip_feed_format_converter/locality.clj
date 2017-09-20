(ns vip-feed-format-converter.locality
  (:require [clojure.string :as str]
            [vip-feed-format-converter.util :as util]))

(def headers
  [:id :election_administration_id :external_identifier_type
   :external_identifier_othertype :external_identifier_value
   :name :polling_location_ids :state_id :type :other_type])

(def assoc-chars (partial util/assoc-chars :locality))

(def handlers
  {:start (fn [ctx event]
            (assoc-in ctx [:tmp :locality]
                      {:id (get-in event [:attrs :id])}))
   :ElectionAdministrationId
   {:chars #(assoc-chars %1 %2 :election_administration_id)}
   :ExternalIdentifiers
   {:ExternalIdentifier
    {:Type {:chars #(assoc-chars %1 %2 :external_identifier_type)}
     :OtherType {:chars #(assoc-chars %1 %2 :external_identifier_othertype)}
     :Value {:chars #(assoc-chars %1 %2 :external_identifier_value)}}}
   :Name {:chars #(assoc-chars %1 %2 :name)}
   :PollingLocationIds {:chars #(assoc-chars %1 %2 :polling_location_ids)}
   :StateId {:chars #(assoc-chars %1 %2 :state_id)}
   :Type {:chars #(assoc-chars %1 %2 :type)}
   :OtherType {:chars #(assoc-chars %1 %2 :other_type)}
   :end (fn [ctx _]
          (-> ctx
              (update-in [:csv-data :locality :data]
                         conj (get-in ctx [:tmp :locality]))
              (update :tmp dissoc :locality)))})