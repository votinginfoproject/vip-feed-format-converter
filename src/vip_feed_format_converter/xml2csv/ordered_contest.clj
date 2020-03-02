(ns vip-feed-format-converter.xml2csv.ordered-contest
  (:require [vip-feed-format-converter.util :as util]))

(def headers
  [:id :contest_id :ordered_ballot_selection_ids])

(defn assoc-chars [key]
  (fn [ctx value]
    (util/assoc-chars :ordered-contest ctx value key)))

(defn handlers [path-to-parent-id]
  {:start (fn [ctx event]
            (-> ctx
                (assoc-in [:tmp :ordered-contest]
                          {:id (get-in event [:attrs :id])})))
   :ContestId                   {:chars (assoc-chars :contest_id)}
   :OrderedBallotSelectionIds   {:chars (assoc-chars :ordered_ballot_selection_ids)}
   :end (fn [ctx _]
          (-> ctx
              (update-in [:csv-data :ordered-contest :data]
                         conj (get-in ctx [:tmp :ordered-contest]))
              (update :tmp dissoc :ordered-contest)))})
