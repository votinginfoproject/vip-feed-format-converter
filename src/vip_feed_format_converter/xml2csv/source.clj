(ns vip-feed-format-converter.xml2csv.source
  (:require [clojure.string :as str]
            [vip-feed-format-converter.util :as util]))

(def headers
  [:id :date_time :description :name :organization_uri
   :terms_of_use_uri :vip_id :version])

(defn assoc-chars [key]
  (fn [ctx event]
    (util/assoc-chars :source ctx event key)))

(defn assoc-intl-text [key]
  (fn [ctx value]
    (util/assoc-intl-text :source "en" ctx value key)))

(def handlers
  {:start (fn [ctx event]
            (assoc-in ctx [:tmp :source]
                      {:id (get-in event [:attrs :id])}))
   :DateTime        {:chars (assoc-chars :date_time)}
   :Description     {:Text {:chars (assoc-intl-text :description)}}
   :Name            {:chars (assoc-chars :name)}
   :OrganizationUri {:chars (assoc-chars :organization_uri)}
   :TouUri          {:chars (assoc-chars :terms_of_use_uri)}
   :VipId           {:chars (assoc-chars :vip_id)}
   :Version         {:chars (assoc-chars :version)}
   :end (fn [ctx _]
          (-> ctx
              (update-in [:csv-data :source :data]
                         conj (get-in ctx [:tmp :source]))
              (update :tmp dissoc :source)))})
