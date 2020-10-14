(ns vip-feed-format-converter.xml2csv.candidate-selection
  (:require [vip-feed-format-converter.util :as util]))

(def headers
  [:id :sequence_order :candidate_ids :endorsement_party_ids :is_write_in])

(defn assoc-chars [key]
  (fn [ctx value]
    (util/assoc-chars :candidate-selection ctx value key)))

(defn assoc-intl-text [key]
  (fn [ctx value]
    (util/assoc-intl-text :candidate-selection "en" ctx value key)))

(def handlers
  {:start (fn [ctx event]
            (assoc-in ctx [:tmp :candidate-selection]
                      {:id (get-in event [:attrs :id])}))
   :SequenceOrder       {:chars (assoc-chars :sequence_order)}
   :CandidateIds        {:chars (assoc-chars :candidate_ids)}
   :EndorsementPartyIds {:chars (assoc-chars :endorsement_party_ids)}
   :IsWriteIn           {:chars (assoc-chars :is_write_in)}
   :end (fn [ctx _]
          (-> ctx
              (update-in [:csv-data :candidate-selection :data]
                         conj (get-in ctx [:tmp :candidate-selection]))
              (update :tmp dissoc :candidate-selection)))})
