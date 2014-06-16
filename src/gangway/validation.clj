(ns gangway.validation
  (:require [clojure.data.json :as json]
            [hatch]
            [schema.core :as s]
            [navigator.validation :as nav-val]
            [oarlock.validation :as oar-val]))

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

(defn validator
  [entity-type validation-map data]
  (let [validation (entity-type validation-map)]
    (if validation
      (try
        (s/validate
         validation
         data)
        (catch Exception e (.getMessage e)))
      data)))

(def validation-maps
  {:assert
   {:task           oar-val/validations
    :perf-asmt      oar-val/validations
    :user2perf-asmt oar-val/validations
    :user2comp      nav-val/validations}})

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
  (let [op (keyword (:operation msg))
        entity-type (keyword (:entity-type msg))
        validation-map (get-in validation-maps [op entity-type])]
    (if (nil? validation-map)
      true
      ;;We need some new validation here
      #_(if (nil? (first (validation-fn (construct-data msg))))
        true
        false))))
