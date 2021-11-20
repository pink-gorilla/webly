

 ;{luminus/ring-undertow-adapter "1.2.0"}

 ;undertow
   ;[ring.adapter.undertow :refer [run-undertow]]
   ;[shadow.cljs.devtools.server :as shadow-server]

; https://github.com/luminus-framework/ring-undertow-adapter
#_(defn run-undertow-server [ring-handler port host api]
    (require '[ring.adapter.undertow :refer [run-undertow]])
    (let [;run-undertow (resolve)
          conn (init-ws! :undertow)]
      (info "Starting Undertow web server at port " port " ..")
      (run-undertow ring-handler {:port port
                                  :host host
                            ; :websockets  {"/api/chsk" (wrap-webly (partial ws-handshake-handler conn))}
                             ;:allow-null-path-info true
                           ;:join?  false        
                                  })))

#_(defn run-shadow-server []
    (let [conn (init-ws! :undertow)]
    ;(shadow-server/start!)
    ;(shadow-server/stop!)
      ))