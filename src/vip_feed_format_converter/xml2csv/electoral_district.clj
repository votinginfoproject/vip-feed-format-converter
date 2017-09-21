(ns vip-feed-format-converter.xml2csv.electoral-district
  (:require [clojure.string :as str]
            [vip-feed-format-converter.util :as util]))

(def headers
  [:id :external_identifier_type :external_identifier_othertype
   :external_identifier_value :name :number :type :other_type])

(defn assoc-chars [key]
  (fn [ctx event]
    (util/assoc-chars ctx event :electoral-district)))

(def handlers
  {:start (fn [ctx event]
            (assoc-in ctx [:tmp :electoral-district]
                      {:id (get-in event [:attrs :id])}))
   :ExternalIdentifiers
   {:ExternalIdentifier
    {:Type {:chars (assoc-chars :external_identifier_type)}
     :OtherType {:chars (assoc-chars :external_identifier_othertype)}
     :Value {:chars (assoc-chars :external_identifier_value)}}}
   :Name {:chars (assoc-chars :name)}
   :Number {:chars (assoc-chars :number)}
   :Type {:chars (assoc-chars :type)}
   :OtherType {:chars (assoc-chars :other_type)}
   :end (fn [ctx _]
          (-> ctx
              (update-in [:csv-data :electoral-district :data]
                         conj (get-in ctx [:tmp :electoral-district]))
              (update :tmp dissoc :electoral-district)))})
