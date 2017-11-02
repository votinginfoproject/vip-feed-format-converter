(ns vip-feed-format-converter.util-test
  (:require [clojure.test :refer :all]
            [vip-feed-format-converter.util :refer :all]))

(deftest assoc-chars-test
  (testing "assoc's :str value from event arg into ctx arg at [:tmp top-level-key key]"
    (is (= {:tmp {:tlk {:key "the thing with the deal"}}}
           (assoc-chars :tlk {} {:str "the thing with the deal"} :key))))
  (testing "trims the :str value from event arg"
    (is (= {:tmp {:tlk {:key "  the thing with the deal "}}})
        (assoc-chars :tlk {} {:str "the thing with the deal"} :key))))

(deftest append-chars-test
  (testing "assoc's :str value when currently empty"
    (is (= {:tmp {:tlk {:key "the thing with the deal"}}}
           (append-chars :tlk {} {:str "the thing with the deal"} :key " "))))
  (testing "concatenates with separator when something is there"
    (is (= {:tmp {:tlk {:key " the thing with the deal"}}})
        (append-chars :tlk {:tmp {:tlk {:key "the thing with"}}}
                      {:str "the deal"} :key " "))))

(deftest assoc-intl-text-test
  (testing "assoc-char's when language matches"
    (is (= {:tmp {:tlk {:key "moar things"}}}
           (assoc-intl-text :tlk "en" {} {:str "moar things"
                                          :prior {:attrs {:language "en"}}}
                            :key))))
  (testing "returns unmodified ctx when language doesn't match"
    (is (= {:other :thingy})
        (assoc-intl-text :tlk "en" {:other :thingy}
                         {:str "más cosas"
                          :prior {:attrs {:language "es"}}}
                         :key))))
