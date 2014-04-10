(ns gangway.worker
  (:require [clojure.data.json :as json]
            [immutant.messaging :as msg]))

#_(def entity-functions
  "Maps incoming 'operation' value with a function that implements
   analagous operation via Korma db functions."
  {:put k-resource/set-data
   :get k-resource/get-data})

(defn do-work [message]
  (let [parsed-message (json/read-str message :key-fn keyword)
        ;;header (:header parsed-message)
        ;;payload (:payload parsed-message)
        ;;entity (keyword (:entity-type header))
        ;;operation (keyword (:operation header))
        ]
    (spit "/tmp/do-work.txt" (str parsed-message))
    #_((operation entity-functions) entity (get-in header [:entity-id :user-id]) payload))
  true
  )
