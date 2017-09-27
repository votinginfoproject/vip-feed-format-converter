(ns vip-feed-format-converter.xml2csv.street-segment
  (:require [clojure.string :as str]
            [vip-feed-format-converter.util :as util]
            [clojure.core.async :as async]))

(def headers
  [:id :address_direction :city :includes_all_addresses :includes_all_streets
   :odd_even_both :precinct_id :start_house_number :end_house_number :state
   :street_direction :street_name :street_suffix :unit_number :zip])

(defn assoc-chars [key]
  (fn [ctx event]
    (util/assoc-chars :street-segment ctx event key)))

(defn handlers
  [channel]
  {:start (fn [ctx event]
            (assoc-in ctx [:tmp :street-segment]
                      {:id (get-in event [:attrs :id])}))
   :AddressDirection        {:chars (assoc-chars :address_direction)}
   :City                    {:chars (assoc-chars :city)}
   :IncludesAllAddresses    {:chars (assoc-chars :includes_all_addresses)}
   :IncludesAllStreets      {:chars (assoc-chars :includes_all_streets)}
   :OddEvenBoth             {:chars (assoc-chars :odd_even_both)}
   :PrecinctId              {:chars (assoc-chars :precinct_id)}
   :StartHouseNumber        {:chars (assoc-chars :start_house_number)}
   :EndHouseNumber          {:chars (assoc-chars :end_house_number)}
   :State                   {:chars (assoc-chars :state)}
   :StreetDirection         {:chars (assoc-chars :street_direction)}
   :StreetName              {:chars (assoc-chars :street_name)}
   :StreetSuffix            {:chars (assoc-chars :street_suffix)}
   ;; Note, will currently only store the LAST Unit Number if there are multiple
   :UnitNumber              {:chars (assoc-chars :unit_number)}
   :Zip                     {:chars (assoc-chars :zip)}
   :end (fn [ctx _]
          (async/>!! channel (get-in ctx [:tmp :street-segment]))
          (update ctx :tmp dissoc :street-segment))})
