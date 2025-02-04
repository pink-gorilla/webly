(ns webly.spa.default)

(def webserver
  {:http {:port 8080
          :ip "0.0.0.0"}
   :https {:port 0} ; by default https is disabled
   :letsencrypt {:domain nil
                 :email nil
                 :force-renewal false ; to test renewal
                 }})

(def spa
  {:title "webly"
   :spinner "webly/loading.svg"
   :icon "webly/icon/pinkgorilla32.png" ; "webly/icon/silver.ico"  ; gorilla is much smaller than silver
   :loading-image-url "webly/loadimage/library.jpg" ; 
   :start-user-app [:webly/start-default]  ; after config loaded}
   })


(def prefix "/r/")
