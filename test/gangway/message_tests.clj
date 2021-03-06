(ns gangway.message-tests
  (:require [clojure.test.check :as tc]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [clojure.data.json :as json])) 

;; from test.check README.md
(defspec first-element-is-min-after-sorting ;; the name of the test
         100 ;; the number of iterations for test.check to test
         (prop/for-all [v (gen/such-that not-empty (gen/vector gen/int))]
           (= (apply min v)
              (first (sort v)))))

;; VLACS data exchange messages are defined as a vector of maps.
;; Disabling this example as Travic CI is now config'ed to run
;; Immutant, etc. 
#_(defspec abstract-message
  ;; testing that reading EDN data structure into JSON and converting
  ;; back yields the same data structure.
  ;; Note: This test fails. Committing failing test to show how
  ;; test.check reports failures.
  (prop/for-all [ab-message (gen/vector (gen/map gen/keyword gen/string))]
    (= ab-message (-> ab-message
                       json/json-str
                       json/read-str))))

(defspec fixed-abstract-message
  ;; testing that reading EDN data structure into JSON and converting
  ;; back yields the same data structure.
  ;; Note: Fixed by adding :key-fn keyword arg to json/read-str.
  (prop/for-all [ab-message (gen/vector (gen/map gen/keyword gen/string))]
    (= ab-message (-> ab-message
                      (json/json-str)
                      (json/read-str :key-fn keyword))))

)
