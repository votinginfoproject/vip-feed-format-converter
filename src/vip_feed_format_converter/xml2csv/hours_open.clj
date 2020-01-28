(ns vip-feed-format-converter.xml2csv.hours-open
  (:require [vip-feed-format-converter.util :as util]
            [vip-feed-format-converter.xml2csv.schedule
             :as schedule]))

(def headers
  [:id])

(def handlers
  {:start (fn [ctx event]
            (assoc-in ctx [:tmp :hours_open]
                      {:id (get-in event [:attrs :id])}))
   :Schedule (schedule/handlers [:tmp :hours_open :id])
   :end (fn [ctx _]
          (-> ctx
              (update-in [:csv-data :hours-open :data]
                         conj (get-in ctx [:tmp :hours_open]))
              (update :tmp dissoc :hours_open)))})
