(ns vip-feed-format-converter.xml2csv.party-selection
  (:require [vip-feed-format-converter.util :as util]))

(def headers
  [:id :sequence_order :party_ids])

(defn assoc-chars [key]
  (fn [ctx value]
    (util/assoc-chars :party-selection ctx value key)))

(def handlers
  {:start (fn [ctx event]
            (assoc-in ctx [:tmp :party-selection]
                      {:id (get-in event [:attrs :id])}))
   :SequenceOrder    {:chars (assoc-chars :sequence_order)}
   :PartyIds         {:chars (assoc-chars :party_ids)}
   :end (fn [ctx _]
          (-> ctx
              (update-in [:csv-data :party-selection :data]
                         conj (get-in ctx [:tmp :party-selection]))
              (update :tmp dissoc :party-selection)))})
