(ns demo.routes
  (:require
   [bidi.bidi :as bidi]))

(def routes
  {:api {"time"   {:get 'demo.handler/time-handler}
         "timejava"   {:get :api/time-java}
         "biditest"   {:get :api/bidi-test}
         "test"   {:get 'demo.handler.test/test-handler
                   :post 'demo.handler.test/test-handler  ; used in unit-test
                   }
         "snippet"   {:get :api/snippet}
         "bindata" :api/binary
         ; todo: remove oauth2 route once oauth2 works as goldly extension
         "oauth2/" {["start/" :provider] {:get 'modular.oauth2.handler/handler-oauth2-start-wrapped}
                    "token"  {:get 'modular.oauth2.handler/token-handler-wrapped}
                    "save-token" {:post 'modular.oauth2.handler/handler-oauth2-save-wrapped}
                                  ; redirect -- oauth2 config needs to be updated (previously thsi was under /oauth2/redirect, but now /api/oauth2/redirect)
                    ["redirect/" :provider] 'modular.oauth2.handler/handler-oauth2-redirect-wrapped}  ;  either client OR server side
         }
   :app  {""        :demo/main
          "help"    :demo/help
          "prose"   :demo/prose
          ["party/" :location] :demo/party
          "job"     (bidi/tag :demo/job :wunderbar)
          "job2"     (bidi/tag :demo/job {:flags 3
                                          :context :wunderbar})
          "save"    :demo/save-non-existing ; there is no handler defined for this on purpose
          }})










