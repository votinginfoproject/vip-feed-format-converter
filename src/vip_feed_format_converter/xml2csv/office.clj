(ns vip-feed-format-converter.xml2csv.office
  (:require [vip-feed-format-converter.util :as util]
            [vip-feed-format-converter.xml2csv.contact-information
             :as contact-information]))

(def headers
  [:id :name :electoral_district_id :external_identifier_type
   :external_identifier_othertype :external_identifier_value :file_date
   :is_incumbent :is_top_ticket :party_id :person_id :post_election_status
   :pre_election_status])

(defn assoc-chars [key]
  (fn [ctx value]
    (util/assoc-chars :office ctx value key)))

(defn assoc-intl-text [key]
  (fn [ctx value]
    (util/assoc-intl-text :office "en" ctx value key)))

(def handlers
  {:start (fn [ctx event]
            (assoc-in ctx [:tmp :office]
                      {:id (get-in event [:attrs :id])}))
   :ContactInformation (contact-information/handlers [:tmp :office :id])
   :Name         {:Text {:chars (assoc-intl-text :name)}}
   :ElectoralDistrictId {:chars (assoc-chars :electoral_district_id)}
   ;; Note, will currently only save the LAST External Identifier in the CSV
   :ExternalIdentifiers
   {:ExternalIdentifier
    {:Type      {:chars (assoc-chars :external_identifier_type)}
     :OtherType {:chars (assoc-chars :external_identifier_othertype)}
     :Value     {:chars (assoc-chars :external_identifier_value)}}}
   :FileDate             {:chars (assoc-chars :file_date)}
   :IsIncumbent          {:chars (assoc-chars :is_incumbent)}
   :IsTopTicket          {:chars (assoc-chars :is_top_ticket)}
   :PartyId              {:chars (assoc-chars :party_id)}
   :PersonId             {:chars (assoc-chars :person_id)}
   :PostElectionStatus   {:chars (assoc-chars :post_election_status)}
   :PreElectionStatus    {:chars (assoc-chars :pre_election_status)}
   :end (fn [ctx _]
          (-> ctx
              (update-in [:csv-data :office :data]
                         conj (get-in ctx [:tmp :office]))
              (update :tmp dissoc :office)))})
