(ns webly.log
  (:require
   [taoensso.timbre :as timbre]))

#?(:clj
   (def color-appender
     (let [colors {:info :green, :warn :yellow, :error :red, :fatal :purple, :report :blue}]
       {:enabled?   true
        :async?     false
        :min-level  nil
        :rate-limit nil
        :output-fn  :inherit
        :fn (fn [{:keys [error? level output-fn] :as data}]
              (binding [*out* (if error? *err* *out*)]
                (if-let [color (colors level)]
                  (println (timbre/color-str color (output-fn data)))
                  (println (output-fn data)))))})))

#?(:clj (def appender-config {:appenders {:color-appender color-appender}})
   :cljs (def appender-config {}))

(defn timbre-config! [config]
  (let [timbre-loglevel (:timbre-loglevel config)]
    (println "timbre config: " timbre-loglevel)
    (when timbre-loglevel
      (timbre/set-config!
       (merge timbre/default-config
              appender-config
              {;:output-fn default-output-fn
               :min-level timbre-loglevel})))))


