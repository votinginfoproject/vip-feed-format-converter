(ns vip-feed-format-converter.xml2csv.candidate-contest
  (:require [vip-feed-format-converter.util :as util]))

(def headers
  [:id :abbreviation :ballot_selection_ids :ballot_sub_title :ballot_title
   :elecoral_district_id :electorate_specification :external_identifier_type
   :external_identifier_othertype :external_identifier_value :has_rotation
   :name :sequence_order :vote_variation :other_vote_variation
   :number_elected :office_ids :primary_party_ids :votes_allowed])

(defn assoc-chars [key]
  (fn [ctx value]
    (util/assoc-chars :candidate-contest ctx value key)))

(defn assoc-intl-text [key]
  (fn [ctx value]
    (util/assoc-intl-text :candidate-contest "en" ctx value key)))

(def handlers
  {:start (fn [ctx event]
            (assoc-in ctx [:tmp :candidate-contest]
                      {:id (get-in event [:attrs :id])}))
   :Abbreviation            {:chars (assoc-chars :abbreviation)}
   :BallotSelectionIds      {:chars (assoc-chars :ballot_selection_ids)}
   :BallotSubTitle          {:chars (assoc-chars :ballot_sub_title)}
   :BallotTitle             {:chars (assoc-chars :ballot_title)}
   :ElectoralDistrictId     {:chars (assoc-chars :electoral_district_id)}
   :ElectorateSpecification {:chars (assoc-chars :electorate_specification)}
   ;; Note, will currently only save the LAST External Identifier in the CSV
   :ExternalIdentifiers
   {:ExternalIdentifier
    {:Type      {:chars (assoc-chars :external_identifier_type)}
     :OtherType {:chars (assoc-chars :external_identifier_othertype)}
     :Value     {:chars (assoc-chars :external_identifier_value)}}}
   :HasRotation             {:chars (assoc-chars :has_rotation)}
   :Name                    {:chars (assoc-chars :name)}
   :SequenceOrder           {:chars (assoc-chars :sequence_order)}
   :VoteVariation           {:chars (assoc-chars :vote_variation)}
   :OtherVoteVariation      {:chars (assoc-chars :other_vote_variation)}
   :NumberElected           {:chars (assoc-chars :number_elected)}
   :OfficeIds               {:chars (assoc-chars :office_ids)}
   :PrimaryPartyIds         {:chars (assoc-chars :primary_party_ids)}
   :VotesAllowed            {:chars (assoc-chars :votes_allowed)}
   :end (fn [ctx _]
          (-> ctx
              (update-in [:csv-data :candidate-contest :data]
                         conj (get-in ctx [:tmp :candidate-contest]))
              (update :tmp dissoc :candidate-contest)))})
