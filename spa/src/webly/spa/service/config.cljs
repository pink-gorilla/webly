(ns webly.spa.service.config
  (:require
   [taoensso.timbre :refer-macros [debug info infof error]]
   [promesa.core :as p]
   [ajax.core] ; https://github.com/JulianBirch/cljs-ajax
   [transit.io :refer [read-opts]]
   [modular.encoding.edn :refer [read-edn]]
   [webly.spa.mode :refer [get-resource-path get-mode]]))

(defn GET
  [url params]
  (p/create
   (fn [resolve reject]
     (ajax.core/GET url
       (merge params
              {:handler (fn [response]
                          (resolve response))
               :error-handler (fn [error]
                                (reject error))})))))

; load configuration

(defn get-config-static []
  (let [url (str (get-resource-path) "config.edn")
        _  (infof "loading static config from url: %s"  url)
        p-get (GET url {:timeout         10000                        ;; optional see API docs
                        :response-format  (ajax.core/text-response-format) ;; IMPORTANT!: You must provide this.
                        })]
    (p/then p-get
            (fn [config-txt]
              (-> config-txt
                  (read-edn) ; (cljs.reader/read-string config)
                  (assoc :static? true))))))

(defn get-config-api []
  (let [url "/api/config"
        _  (infof "loading api config from url: %s"  url)
        p-get (GET url {:timeout         10000                        ;; optional see API docs
                        :response-format (ajax.core/transit-response-format :json (read-opts)) ;; IMPORTANT!: You must provide this.
                        })]
    (-> p-get
        (p/then
         (fn [config]
           (-> config
               (assoc :static? false))))
        (p/catch
         (fn [err]
           (error "could not load config from " url " error: " err))))))

(defn get-config []
  (let [static? (= (get-mode) :static)]
    (if static?
      (get-config-static)
      (get-config-api))))

;(defn read-str [s]
;  (read-string
;   {:readers config} s))