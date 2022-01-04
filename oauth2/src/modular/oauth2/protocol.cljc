(ns modular.oauth2.protocol)

#_(def providers
    {:github github/config
     :google google/config
     :xero xero/config})

#_(defn get-provider-config [p]
    (or (p providers) {}))

#_(defn known-providers []
    (keys providers))

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
