(ns vip-feed-format-converter.xml2csv.voter-service
  (:require [vip-feed-format-converter.xml2csv.contact-information
             :as contact-information]
            [vip-feed-format-converter.util :as util]))

(def headers
  [:id :description :election_official_person_id
   :type :other_type :department_id])

(defn assoc-chars [key]
  (fn [ctx value]
    (util/assoc-chars :voter-service ctx value key)))

(defn assoc-intl-text [key]
  (fn [ctx value]
    (util/assoc-intl-text :voter-service "en" ctx value key)))

(defn handlers [path-to-parent-id]
  {:start (fn [ctx event]
            (-> ctx
                (assoc-in [:tmp :voter-service]
                          {:id (.toString (java.util.UUID/randomUUID))
                           :department_id
                           (get-in ctx path-to-parent-id)})))
   :Description       {:Text {:chars (assoc-intl-text :description)}}
   :ElectionOfficialPersonId {:chars (assoc-chars :election_official_person_id)}
   :Type                     {:chars (assoc-chars :type)}
   :OtherType                {:chars (assoc-chars :other_type)}
   :ContactInformation       (contact-information/handlers
                              [:tmp :voter-service :id])
   :end (fn [ctx _]
          (-> ctx
              (update-in [:csv-data :voter-service :data]
                         conj (get-in ctx [:tmp :voter-service]))
              (update :tmp dissoc :voter-service)))})
