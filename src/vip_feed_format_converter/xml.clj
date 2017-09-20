(ns vip-feed-format-converter.xml
  (:require [clojure.data.xml :as xml])
  (:import (clojure.data.xml.event StartElementEvent CharsEvent EndElementEvent)))

(defn parse-file [{:keys [input handlers] :as ctx}]
  (let [events (xml/event-seq input {})]
    (loop [event (first events)
           prior-event nil
           remaining-events (rest events)
           ctx ctx
           iteration 0]
      (when (= 0 (mod iteration 10000))
        (print ". ") (flush))
      (let [event-tag (:tag event)
            event-str (:str event)
            ctx-tag-path (:tag-path ctx)
            tag-path (condp instance? event
                       StartElementEvent
                       (-> ctx-tag-path
                           (conj event-tag)
                           (conj :start))

                       CharsEvent
                       (conj ctx-tag-path :chars)

                       EndElementEvent
                       (conj ctx-tag-path :end)

                       ctx-tag-path)
            ctx (assoc ctx :tag-path tag-path)
            next-tag-path (if (instance? EndElementEvent event)
                            (-> tag-path pop pop)
                            (pop tag-path))]
        (if-let [handler (get-in handlers tag-path)]
          (let [handler-ctx (handler ctx (if (instance? CharsEvent event)
                                           (assoc event :prior prior-event)
                                           event))
                next-ctx (assoc handler-ctx :tag-path next-tag-path)]
            (if (seq remaining-events)
              (recur (first remaining-events)
                     event
                     (rest remaining-events)
                     next-ctx
                     (inc iteration))
              next-ctx))
          (let [next-ctx (assoc ctx :tag-path next-tag-path)]
            (if (seq remaining-events)
              (recur (first remaining-events)
                     event
                     (rest remaining-events)
                     next-ctx
                     (inc iteration))
              next-ctx)))))))
