(ns vip-feed-format-converter.xml
  (:require [clojure.data.xml :as xml])
  (:import (clojure.data.xml.event StartElementEvent CharsEvent EndElementEvent)))

(defn process-event [{:keys [handlers] :as ctx} event prior-event]
  (let [event-tag (:tag event)
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
                                       event))]
        (assoc handler-ctx :tag-path next-tag-path))
      (assoc ctx :tag-path next-tag-path))))

(defn parse-file [{:keys [input] :as ctx}]
  (println "Parsing xml file")
  (let [events (xml/event-seq input {})]
    (loop [event (first events)
           prior-event nil
           remaining-events (rest events)
           ctx ctx
           iteration 0]
      (when (= 0 (mod iteration 10000))
        (print ". ") (flush))
      (let [next-ctx (process-event ctx event prior-event)]
        (if (seq remaining-events)
          (recur (first remaining-events)
                 event
                 (rest remaining-events)
                 next-ctx
                 (inc iteration))
          (do
            (println "\nDone parsing xml file")
            next-ctx))))))
