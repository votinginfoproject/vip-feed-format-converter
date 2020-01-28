(ns vip-feed-format-converter.xml2csv.ballot-measure-selection
  (:require [vip-feed-format-converter.util :as util]))

(def headers
  [:id :sequence_order :selection])

(defn assoc-chars [key]
  (fn [ctx value]
    (util/assoc-chars :ballot-measure-selection ctx value key)))

(defn assoc-intl-text [key]
  (fn [ctx value]
    (util/assoc-intl-text :ballot-measure-selection "en" ctx value key)))

(def handlers
  {:start (fn [ctx event]
            (assoc-in ctx [:tmp :ballot-measure-selection]
                      {:id (get-in event [:attrs :id])}))
   :SequenceOrder    {:chars (assoc-chars :sequence_order)}
   :Selection {:Text {:chars (assoc-intl-text :selection)}}
   :end (fn [ctx _]
          (-> ctx
              (update-in [:csv-data :ballot-measure-selection :data]
                         conj (get-in ctx [:tmp :ballot-measure-selection]))
              (update :tmp dissoc :ballot-measure-selection)))})
