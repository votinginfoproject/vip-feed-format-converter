(ns vip-feed-format-converter.xml2csv.contact-information
  (:require [clojure.string :as str]
            [vip-feed-format-converter.util :as util]))

(def headers
  [:id :address_line_1 :address_line_2 :address_line_3 :directions :email
   :fax :hours :hours_open_id :latitude :longitude :latlng_source :name
   :phone :uri :parent_id])

(defn assoc-chars [key]
  (fn [ctx value]
    (util/assoc-chars :contact_information ctx value key)))

(defn assoc-intl-text [key]
  (fn [ctx value]
    (util/assoc-intl-text :contact_information "en" ctx value key)))

(defn maybe-add-address-line
  "This is a little confusing, so here's some context. We want to convert
  *at most* 3 of the unbounded AddressLine elements into :address_line_1,
  :address_line_2, and :address_line_3 respectively. So at the start of
  processing a ContactInformation we put a list of these 3 spots into
  the tmp context, and each time we process an AddressLine we grab the next
  one until we are all out."
  [ctx event]
  (let [address-lines (get-in ctx [:tmp :contact_information :address_lines])]
    (if (seq address-lines)
      (let [next-line (first address-lines)
            value (str/trim (:str event))
            remaining-lines (rest address-lines)]
        (-> ctx
            (assoc-in [:tmp :contact_information next-line] value)
            (assoc-in [:tmp :contact_information :address_lines] remaining-lines)))
      ctx)))

(defn handlers [path-to-parent-id]
  {:start (fn [ctx event]
            (assoc-in ctx [:tmp :contact_information]
                      {:id (get-in event [:attrs :id])
                       :parent_id (get-in ctx path-to-parent-id)
                       :address_lines [:address_line_1
                                       :address_line_2
                                       :address_line_3]}))
   ;;Note we only take up to the first 3 address lines
   :AddressLine {:chars maybe-add-address-line}
   :Directions  {:chars (assoc-chars :directions)}
   ;;Note we only take the LAST email
   :Email       {:chars (assoc-chars :email)}
   ;;Note we only take the LAST fax
   :Fax         {:chars (assoc-chars :fax)}
   :Hours       {:chars (assoc-chars :hours)}
   :HoursOpenId {:chars (assoc-chars :hours_open_id)}
   :LatLng      {:Latitude  {:chars (assoc-chars :latitude)}
                 :Longitude {:chars (assoc-chars :longitude)}
                 :Source    {:chars (assoc-chars :latlng_source)}}
   :Name        {:chars (assoc-chars :name)}
   ;;Note we only take the LAST phone
   :Phone       {:chars (assoc-chars :phone)}
   :Uri         {:chars (assoc-chars :uri)}
   :end (fn [ctx _]
          (-> ctx
              (update-in [:tmp :contact_information] dissoc :address_lines)
              (update-in [:csv-data :contact_information :data]
                         conj (get-in ctx [:tmp :contact_information]))
              (update :tmp dissoc :contact_information)))})
