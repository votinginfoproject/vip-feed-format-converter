(ns vip-feed-format-converter.xml2csv.department
  (:require [vip-feed-format-converter.xml2csv.contact-information
             :as contact-information]
            [vip-feed-format-converter.xml2csv.voter-service
             :as voter-service]
            [vip-feed-format-converter.util :as util]))

(def headers
  [:id :election_official_person_id :election_administration_id])

(defn assoc-chars [key]
  (fn [ctx value]
    (util/assoc-chars :department ctx value key)))

(defn handlers [path-to-parent-id]
  {:start (fn [ctx event]
            (-> ctx
                (assoc-in [:tmp :department]
                          {:id (.toString (java.util.UUID/randomUUID))
                           :election_administration_id
                           (get-in ctx path-to-parent-id)})))
   :ElectionOfficialPersonId {:chars (assoc-chars :election_official_person_id)}
   :ContactInformation       (contact-information/handlers
                              [:tmp :department :id])
   :VoterService             (voter-service/handlers
                              [:tmp :department :id])
   :end (fn [ctx _]
          (-> ctx
              (update-in [:csv-data :department :data]
                         conj (get-in ctx [:tmp :department]))
              (update :tmp dissoc :department)))})
