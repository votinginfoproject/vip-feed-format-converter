(ns vip-feed-format-converter.util
  (:require [clojure.string :as str]))

(defn assoc-chars [top-level-key ctx event key]
  (assoc-in ctx [:tmp top-level-key key]
            (str/trim (:str event))))

(defn assoc-intl-text [top-level-key lang ctx event key]
  (if (= lang (get-in event [:prior :attrs :language]))
    (assoc-chars top-level-key ctx event key)))