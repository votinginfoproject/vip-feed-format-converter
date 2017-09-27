(ns vip-feed-format-converter.xml2csv.precinct
  (:require [vip-feed-format-converter.util :as util]))

(def headers
  [:id :ballot_style_id :electoral_district_ids :external_identifier_type
   :external_identifier_othertype :external_identifier_value :is_mail_only
   :locality_id :name :number :polling_location_ids :precinct_split_name :ward])

(defn assoc-chars [key]
  (fn [ctx value]
    (util/assoc-chars :precinct ctx value key)))

(def handlers
  {:start (fn [ctx event]
            (assoc-in ctx [:tmp :precinct]
                      {:id (get-in event [:attrs :id])}))
   :BallotStyleId        {:chars (assoc-chars :ballot_style_id)}
   :ElectoralDistrictIds {:chars (assoc-chars :electoral_district_ids)}
   ;; Note, will currently only save the LAST External Identifier in the CSV
   :ExternalIdentifiers
   {:ExternalIdentifier
    {:Type        {:chars (assoc-chars :external_identifier_type)}
     :OtherType   {:chars (assoc-chars :external_identifier_othertype)}
     :Value       {:chars (assoc-chars :external_identifier_value)}}}
   :IsMailOnly           {:chars (assoc-chars :is_mail_only)}
   :LocalityId           {:chars (assoc-chars :locality_id)}
   :Name                 {:chars (assoc-chars :name)}
   :Number               {:chars (assoc-chars :number)}
   :PollingLocationIds   {:chars (assoc-chars :polling_location_ids)}
   :PrecinctSplitName    {:chars (assoc-chars :precinct_split_name)}
   :Ward                 {:chars (assoc-chars :ward)}
   :end (fn [ctx _]
          (-> ctx
              (update-in [:csv-data :precinct :data]
                         conj (get-in ctx [:tmp :precinct]))
              (update :tmp dissoc :precinct)))})
