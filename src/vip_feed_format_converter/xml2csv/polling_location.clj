(ns vip-feed-format-converter.xml2csv.polling-location
  (:require [vip-feed-format-converter.util :as util]))

(def headers
  [:id :name :address_line :directions :hours :hours_open_id :is_drop_box
   :is_early_voting :latitude :longitude :latlng_source :photo_uri])

(defn assoc-chars [key]
  (fn [ctx value]
    (util/assoc-chars :polling-location ctx value key)))

(defn append-chars [key]
  (fn [ctx value]
    (util/append-chars :polling-location ctx value key " ")))

(defn assoc-intl-text [key]
  (fn [ctx value]
    (util/assoc-intl-text :polling-location "en" ctx value key)))

(def handlers
  {:start (fn [ctx event]
            (assoc-in ctx [:tmp :polling-location]
                      {:id (get-in event [:attrs :id])}))
   :Name              {:chars (assoc-chars :name)}
   :AddressLine       {:chars (append-chars :address_line)}
   :Directions        {:Text {:chars (assoc-intl-text :directions)}}
   :Hours             {:Text {:chars (assoc-intl-text :hours)}}
   :HoursOpenId       {:chars (assoc-chars :hours_open_id)}
   :IsDropBox         {:chars (assoc-chars :is_drop_box)}
   :IsEarlyVoting     {:chars (assoc-chars :is_early_voting)}
   :LatLng {:Latitude  {:chars (assoc-chars :latitude)}
            :Longitude {:chars (assoc-chars :longitude)}
            :Source    {:chars (assoc-chars :latlng_source)}}
   :PhotoUri          {:chars (assoc-chars :photo_uri)}
   :end (fn [ctx _]
          (-> ctx
              (update-in [:csv-data :polling-location :data]
                         conj (get-in ctx [:tmp :polling-location]))
              (update :tmp dissoc :polling-location)))})
