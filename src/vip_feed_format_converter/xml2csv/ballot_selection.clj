(ns vip-feed-format-converter.xml2csv.ballot-selection
  (:require [vip-feed-format-converter.util :as util]))

(def headers
  [:id :sequence_order])

(defn assoc-chars [key]
  (fn [ctx value]
    (util/assoc-chars :ballot-selection ctx value key)))

(def handlers
  {:start (fn [ctx event]
            (assoc-in ctx [:tmp :ballot-selection]
                      {:id (get-in event [:attrs :id])}))
   :SequenceOrder    {:chars (assoc-chars :sequence_order)}
   :end (fn [ctx _]
          (-> ctx
              (update-in [:csv-data :ballot-selection :data]
                         conj (get-in ctx [:tmp :ballot-selection]))
              (update :tmp dissoc :ballot-selection)))})
