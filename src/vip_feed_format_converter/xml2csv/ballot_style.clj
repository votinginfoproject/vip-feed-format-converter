(ns vip-feed-format-converter.xml2csv.ballot-style
  (:require [vip-feed-format-converter.util :as util]))

(def headers
  [:id :image_uri :ordered_contest_ids :party_ids])

(defn assoc-chars [key]
  (fn [ctx value]
    (util/assoc-chars :ballot-style ctx value key)))

(def handlers
  {:start (fn [ctx event]
            (-> ctx
                (assoc-in [:tmp :ballot-style]
                          {:id (get-in event [:attrs :id])})))
   :ImageUri            {:chars (assoc-chars :image_uri)}
   :OrderedContestIds   {:chars (assoc-chars :ordered_contest_ids)}
   :PartyIds            {:chars (assoc-chars :party_ids)}
   :end (fn [ctx _]
          (-> ctx
              (update-in [:csv-data :ballot-style :data]
                         conj (get-in ctx [:tmp :ballot-style]))
              (update :tmp dissoc :ballot-style)))})
