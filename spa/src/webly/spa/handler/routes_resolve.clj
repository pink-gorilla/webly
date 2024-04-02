(ns webly.spa.handler.routes-resolve
   (:require
    [clojure.string]
    [taoensso.timbre :refer [debug info warn error]]
    [com.rpl.specter :as specter]))

 (defn get-handler-backend-symbol [s]
   (try
     (warn "resolving handler symbol: " s)
     (requiring-resolve s)
     (catch Exception ex
       (error "api-handler-symbol resolve exception: " ex)
       (throw ex))))


 (defn path-check [x]
   ;(warn "path: " x)
   (or (vector? x) (map? x)))

 (def TREE-VALUES
   (specter/recursive-path [] p
                           (specter/if-path path-check
                                            [specter/ALL p]
                                            specter/STAY)))


 (defn resolve-handler [routes]
   (warn "resolving handlers on startup.. ")
   (specter/transform
    [TREE-VALUES symbol?]
    get-handler-backend-symbol routes))


 (comment

   (defn res [s]
     [:H s])

   (defn resolve-handler3 [routes]
     (warn "resolving handler: " routes)
     ;(specter/transform [specter/ALL symbol?] res routes)  
     ;(specter/transform [TreeWalker symbol?] res routes)
     (specter/transform [TREE-VALUES symbol?] res routes))

   (specter/transform [TreeWalker symbol?] res
                      ["/"  {"/a" 'some.handler/super
                             "/b" 'another.handler/super}])

   (resolve-handler3
    ["/a" 'some.handler/super
     "/b" 'another.handler/super])

   (resolve-handler3
    {"/a" 'some.handler/super
     "/b" 'another.handler/super})

   (resolve-handler3
    {"/a" :good
     "/b" 'another.handler/super
     "/c" {"/d" 'xxx.xxx/xxx
           "/e" 'yyy/yyy}})



   (resolve-handler3
    {:get 'demo.handler.test/test-handler,
     :post 'demo.handler.test/test-handler})

   (resolve-handler3
    {"chsk" {:get 'demo.handler.test/test-handler,
             :post 'demo.handler.test/test-handler}
     "bindata" 'demo.handler.binary/binary-handler,
     "config" :config-handler-fn,
     "chsk2" {:get 'demo.handler.test/test-handler,
              :post 'demo.handler.test/test-handler}})

   (resolve-handler3
    {"token" :token-handler-fn,
     "bindata" 'demo.handler.binary/binary-handler,
     "biditest" {:get 'demo.handler/bidi-test-handler-wrapped},
     "config" :config-handler-fn,
     "snippet" {:get 'demo.handler/snippet-handler-wrapped},
     "timejava" {:get 'time-java-handler-wrapped},
     "time" {:get 'demo.handler/time-handler},
     #_"oauth2/" #_{["start/" :provider]  {:get 'modular.oauth2.handler/handler-oauth2-start-wrapped},
                    "token" {:get 'modular.oauth2.handler/token-handler-wrapped},
                    "save-token" {:post 'modular.oauth2.handler/handler-oauth2-save-wrapped},
                    ["redirect/" :provider] 'modular.oauth2.handler/handler-oauth2-redirect-wrapped},
     "chsk" {:get :websocket-chsk,
             :post :websocket-post},
     "test" {:get 'demo.handler.test/test-handler,
             :post 'demo.handler.test/test-handler}})


 ;   
   )








