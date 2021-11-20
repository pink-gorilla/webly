(ns demo.https
  (:require
   [modular.webserver.jetty :refer [run-jetty-server]]
   [modular.webserver.handler.not-found :refer [not-found-handler]]
   [modular.webserver.handler.files :refer [->FilesMaybe]]
   [modular.webserver.handler.config :refer [config-handler]]
   [modular.webserver.middleware.bidi :refer [wrap-bidi]]
   [modular.webserver.middleware.exception :refer [wrap-fallback-exception]]
   [modular.webserver.middleware.api :refer [wrap-api-handler]]
   [modular.config :as config]))

(config/set! :demo {:mode 3 :message "testing"})

(def routes
  ["/" {"config" {:get (wrap-api-handler config-handler)}
        #{"r" "public"} (->FilesMaybe {:dir "public"})
        true not-found-handler}])

(defn run-webserver [& _]
  (let [ring-handler (-> (wrap-bidi routes)
                         (wrap-fallback-exception))]
    (run-jetty-server ring-handler nil
                      {:port 8080
                       :host "0.0.0.0"
                       :join? true
                       :ssl-port 8443
                       :keystore "./certs/keystore.p12"
                       :key-password "password"; Password you gave when creating the keystore
                       })))
;; https://danielflower.github.io/2017/04/08/Lets-Encrypt-Certs-with-embedded-Jetty.html

