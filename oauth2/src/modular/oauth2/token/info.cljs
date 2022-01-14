(ns modular.oauth2.token.info)

(defn token-expired? [token]
  (if-let [exp (:exp token)]
    (let [time-current (.getTime js/Date.)   ; if (new Date () .getTime () <= exp * 1000) return false;
          time-exp (* exp 1000)]
      (println "exp: " time-exp " cur:" time-current)
      (<= time-current time-exp))
    false))