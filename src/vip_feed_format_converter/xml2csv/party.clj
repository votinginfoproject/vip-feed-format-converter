(ns vip-feed-format-converter.xml2csv.party
  (:require [vip-feed-format-converter.util :as util]
            [vip-feed-format-converter.xml2csv.contact-information
             :as contact-information]))

(def headers
  [:id :abbreviation :color :external_identifier_type
   :external_identifier_othertype :external_identifier_value :is_write_in
   :logo_uri :name])

(defn assoc-chars [key]
  (fn [ctx value]
    (util/assoc-chars :party ctx value key)))

(defn assoc-intl-text [key]
  (fn [ctx value]
    (util/assoc-intl-text :party "en" ctx value key)))

(def handlers
  {:start (fn [ctx event]
            (assoc-in ctx [:tmp :party]
                      {:id (get-in event [:attrs :id])}))
   :Abbreviation   {:chars (assoc-chars :abbreviation)}
   :Color          {:chars (assoc-chars :color)}
   ;; Note, will currently only save the LAST External Identifier in the CSV
   :ExternalIdentifiers
   {:ExternalIdentifier
    {:Type      {:chars (assoc-chars :external_identifier_type)}
     :OtherType {:chars (assoc-chars :external_identifier_othertype)}
     :Value     {:chars (assoc-chars :external_identifier_value)}}}
   :IsWriteIn      {:chars (assoc-chars :is_write_in)}
   :LogoUri        {:chars (assoc-chars :logo_uri)}
   :Name    {:Text {:chars (assoc-intl-text :name)}}
   :end (fn [ctx _]
          (-> ctx
              (update-in [:csv-data :party :data]
                         conj (get-in ctx [:tmp :party]))
              (update :tmp dissoc :party)))})
