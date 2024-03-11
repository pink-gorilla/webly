(ns webly.spa.default)


(def webserver
  {:port 8080
   :host "0.0.0.0"
   :ssl-port 8443
   :keystore "../webserver/certs/keystore.p12"
   :key-password "password"; Password you gave when creating the keystore
   :jetty-ws ["/api/chsk"]})

(def spa
  {:title "webly"
   :spinner "webly/loading.svg"
   :icon "webly/icon/pinkgorilla32.png" ; "webly/icon/silver.ico"  ; gorilla is much smaller than silver
   :loading-image-url "webly/loadimage/library.jpg" ; 
   :start-user-app [:webly/start-default]  ; after config loaded}
   })

(def theme
  {:available {:webly-dialog {true ["webly/dialog.css"]
                              false []}}
   :current {:webly-dialog true}})

(def google-analytics
  {:enabled false} ; set to false to disable google-analytics tracking. 
  )