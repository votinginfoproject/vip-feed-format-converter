(ns vip-feed-format-converter.xml2csv.state
  (:require [clojure.string :as str]
            [vip-feed-format-converter.util :as util]))

(def headers
  [:id :election_administration_id :external_identifier_type
   :external_identifier_othertype :external_identifier_value
   :name :polling_location_ids])

(defn assoc-chars [key]
  (fn [ctx event]
    (util/assoc-chars :state ctx event key)))

(def assoc-intl-text (partial util/assoc-intl-text :state "en"))

(def handlers
  {:start (fn [ctx event]
            (assoc-in ctx [:tmp :state]
                      {:id (get-in event [:attrs :id])}))
   :ElectionAdministrationId {:chars (assoc-chars :election_administration_id)}
   :ExternalIdentifiers
   {:ExternalIdentifier
    {:Type      {:chars (assoc-chars :external_identifier_type)}
     :OtherType {:chars (assoc-chars :external_identifier_othertype)}
     :Value     {:chars (assoc-chars :external_identifier_value)}}}
   :Name                     {:chars (assoc-chars :name)}
   :PollingLocationIds       {:chars (assoc-chars :polling_location_ids)}
   :end (fn [ctx _]
          (-> ctx
              (update-in [:csv-data :state :data]
                         conj (get-in ctx [:tmp :state]))
              (update :tmp dissoc :state)))})
