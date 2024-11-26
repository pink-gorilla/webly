(ns webly.spa.default)

(def webserver
  {:port 8080
   :host "0.0.0.0"
   :ssl-port 8443
   :keystore "../webserver/certs/keystore.p12"
   :key-password "password"; Password you gave when creating the keystore
   })

(def spa
  {:title "webly"
   :spinner "webly/loading.svg"
   :icon "webly/icon/pinkgorilla32.png" ; "webly/icon/silver.ico"  ; gorilla is much smaller than silver
   :loading-image-url "webly/loadimage/library.jpg" ; 
   :start-user-app [:webly/start-default]  ; after config loaded}
   })

(def settings {:use-localstorage  false})

(def shadow
  {:ring-handler "webly.app.app/ring-handler" ; no need to change 
   :dev-http {:port 8000}
   :http {:port 8001 :host "0.0.0.0"}; "localhost"}
   :nrepl {:port 8002}})

(def build
  {:ns-cljs []
   :modules {}
   :module-loader-init false ; important, because we need to load on different paths
   :output-dir "target/webly/public"})

(def prefix "/r/")
