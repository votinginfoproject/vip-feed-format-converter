(ns vip-feed-format-converter.xml2csv.retention-contest
  (:require [vip-feed-format-converter.util :as util]))

(def headers
  [:id :abbreviation :ballot_selection_ids :ballot_sub_title :ballot_title
   :elecoral_district_id :candidate_id :electorate_specification :external_identifier_type
   :external_identifier_othertype :external_identifier_value :has_rotation
   :name :office_id :sequence_order :vote_variation :other_vote_variation
   :con_statement :effect_of_abstain :full_text :info_uri :passage_threshold
   :pro_statement :summary_text :type :other_type])

(defn assoc-chars [key]
  (fn [ctx value]
    (util/assoc-chars : ctx value key)))

(defn assoc-intl-text [key]
  (fn [ctx value]
    (util/assoc-intl-text :retention-contest "en" ctx value key)))

(def handlers
  {:start (fn [ctx event]
            (assoc-in ctx [:tmp :retention-contest]
                      {:id (get-in event [:attrs :id])}))
   :Abbreviation            {:chars (assoc-chars :abbreviation)}
   :BallotSelectionIds      {:chars (assoc-chars :ballot_selection_ids)}
   :BallotSubTitle          {:chars (assoc-chars :ballot_sub_title)}
   :BallotTitle             {:chars (assoc-chars :ballot_title)}
   :CandidateId             {:chars (assoc-chars :candidate_id)}
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
   :OfficeId                {:chars (assoc-chars :office_id)}
   :SequenceOrder           {:chars (assoc-chars :sequence_order)}
   :VoteVariation           {:chars (assoc-chars :vote_variation)}
   :OtherVoteVariation      {:chars (assoc-chars :other_vote_variation)}
   :ConStatement     {:Text {:chars (assoc-intl-text :con_statement)}}
   :EffectOfAbstain  {:Text {:chars (assoc-intl-text :effect_of_abstain)}}
   :FullText         {:Text {:chars (assoc-intl-text :full_text)}}
   :InfoUri                 {:chars (assoc-chars :info_uri)}
   :PassageThreshold {:Text {:chars (assoc-intl-text :passage_threshold)}}
   :ProStatement     {:Text {:chars (assoc-intl-text :pro_statement)}}
   :SummaryText      {:Text {:chars (assoc-intl-text :summary_text)}}
   :Type                    {:chars (assoc-chars :ballot_measure_type)}
   :OtherType               {:chars (assoc-chars :other_type)}
   :end (fn [ctx _]
          (-> ctx
              (update-in [:csv-data :retention-contest :data]
                         conj (get-in ctx [:tmp :retention-contest]))
              (update :tmp dissoc :retention-contest)))})
