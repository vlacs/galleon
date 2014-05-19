(ns gangway.validation
  (:require [clojure.data.json :as json]
            [hatch]
            [navigator.validation :as navigator]))

(defn construct-data
  "Constructs a namespaced map out of an incoming json message"
  [msg]
  (let [entity-type (get-in msg [:header :entity-type])]
    (if (get-in msg [:header :entity-id])
      (let [id-key (keyword (str entity-type "-id"))
            id-sk-key (keyword (str entity-type "/id-sk"))
            id (str (get-in msg [:header :entity-id id-key]))]
        (merge {id-sk-key id}
               (hatch/slam-all (get-in msg [:payload :entity]) (keyword entity-type))))
      (hatch/slam-all (get-in msg [:payload :entity]) (keyword entity-type)))))

(def validation-dispatch
  {:assert
   {:task           navigator/task-in
    :comp           navigator/comp-in
    :comp-tag       navigator/comp-tag-in
    :perf-asmt      navigator/perf-asmt-in
    :user2comp      navigator/user2comp-in
    :user2perf-asmt navigator/user2perf-asmt-in}})

(defn valid-json?
  "Evaluates given message string to determine if it's valid JSON.
  Returns true or false."
  [msg]
  (let [valid-msg
        (try
          (let [parsed-msg (json/read-str msg :key-fn keyword)]
            true)
          (catch Exception e
            (not true)))]
    valid-msg))

(defn valid?
  "Runs a validation function to check if a message is valid.
  Returns true or false."
  [msg]
  (if (valid-json? msg)
    (let [parsed-msg (first (json/read-str msg :key-fn keyword))
          header (:header parsed-msg)
          op (keyword (:operation header))
          entity-type (keyword (:entity-type header))
          validation-fn (get-in validation-dispatch [op entity-type])]
      (if (nil? validation-fn)
        true
        (if (nil? (first (validation-fn (construct-data parsed-msg))))
          true
          false)))
    false))
