(ns vip-feed-format-converter.xml2csv.party-contest
  (:require [vip-feed-format-converter.util :as util]))

(def headers
  [:id :abbreviation :ballot_selection_ids :ballot_sub_title :ballot_title
   :electoral_district_id :electorate_specification :external_identifier_type
   :has_rotation :name :sequence_order :vote_variation :other_vote_variation])

(defn assoc-chars [key]
  (fn [ctx value]
    (util/assoc-chars :party-contest ctx value key)))

(defn assoc-intl-text [key]
  (fn [ctx value]
    (util/assoc-intl-text :party-contest "en" ctx value key)))

(defn handlers [path-to-parent-id]
  {:start (fn [ctx event]
            (-> ctx
                (assoc-in [:tmp :party-contest]
                          {:id (get-in event [:attrs :id])})))
   :Abbreviation                     {:chars (assoc-chars :abbreviation)}
   :BallotSelectionIds               {:chars (assoc-chars :ballot_selection_ids)}
   :BallotSubTitle            {:Text {:chars (assoc-intl-text :ballot_sub_title)}}
   :BallotTitle               {:Text {:chars (assoc-intl-text :ballot_title)}}
   :ElectoralDistrictId              {:chars (assoc-chars :electoral_district_id)}
   :ElectorateSpecification   {:Text {:chars (assoc-intl-text :electorate_specification)}}
   ;; Note, will currently only save the LAST External Identifier in the CSV
   :ExternalIdentifiers
   {:ExternalIdentifier
    {:Type      {:chars (assoc-chars :external_identifier_type)}
     :OtherType {:chars (assoc-chars :external_identifier_othertype)}
     :Value     {:chars (assoc-chars :external_identifier_value)}}}
   :HasRotation                      {:chars (assoc-chars :has_rotation)}
   :Name                             {:chars (assoc-chars :name)}
   :SequenceOrder                    {:chars (assoc-chars :sequence_order)}
   :VoteVariation                    {:chars (assoc-chars :vote_variation)}
   :OtherVoteVariation               {:chars (assoc-chars :other_vote_variation)}
   :end (fn [ctx _]
          (-> ctx
              (update-in [:csv-data :party-contest :data]
                         conj (get-in ctx [:tmp :party-contest]))
              (update :tmp dissoc :party-contest)))})