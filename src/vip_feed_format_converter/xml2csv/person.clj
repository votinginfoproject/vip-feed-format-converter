(ns vip-feed-format-converter.xml2csv.person
  (:require [vip-feed-format-converter.util :as util]
            [vip-feed-format-converter.xml2csv.contact-information
             :as contact-information]))

(def headers
  [:id :date_of_birth :external_identifier_type :external_identifier_othertype
   :first_name :gender :last_name :middle_name :nickname :party_id :prefix
   :profession :suffix :title])

(defn assoc-chars [key]
  (fn [ctx value]
    (util/assoc-chars :person ctx value key)))

(defn assoc-intl-text [key]
  (fn [ctx value]
    (util/assoc-intl-text :person "en" ctx value key)))

(def handlers
  {:start (fn [ctx event]
            (assoc-in ctx [:tmp :person]
                      {:id (get-in event [:attrs :id])}))
   :ContactInformation (contact-information/handlers [:tmp :person :id])
   :DateOfBirth       {:chars (assoc-chars :date_of_birth)}
   ;; Note, will currently only save the LAST External Identifier in the CSV
   :ExternalIdentifiers
   {:ExternalIdentifier
    {:Type      {:chars (assoc-chars :external_identifier_type)}
     :OtherType {:chars (assoc-chars :external_identifier_othertype)}
     :Value     {:chars (assoc-chars :external_identifier_value)}}}
   :FirstName         {:chars (assoc-chars :first_name)}
   :Fullname          {:chars (assoc-chars :full_name)}
   :Gender            {:chars (assoc-chars :gender)}
   :LastName          {:chars (assoc-chars :last_name)}
   ;;NOTE, will currently only save the LAST middle name in the CSV
   :MiddleName        {:chars (assoc-chars :middle_name)}
   :Nickname          {:chars (assoc-chars :nickname)}
   :PartyId           {:chars (assoc-chars :party_id)}
   :Prefix            {:chars (assoc-chars :prefix)}
   :Profession {:Text {:chars (assoc-intl-text :profession)}}
   :Suffix            {:chars (assoc-chars :suffix)}
   :Title      {:Text {:chars (assoc-intl-text :title)}}
   :end (fn [ctx _]
          (-> ctx
              (update-in [:csv-data :person :data]
                         conj (get-in ctx [:tmp :person]))
              (update :tmp dissoc :person)))})
