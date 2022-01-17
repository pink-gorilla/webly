(ns modular.oauth2.protocol)

(defmulti provider-config
  (fn [type] ;file-name data (avoid lint warnings)
    type))

(defmethod provider-config :default [_]
  {})

(defn known-providers []
  (->> (methods provider-config)
       keys
       (remove #(= :default %))
       (into [])))
