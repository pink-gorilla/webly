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
         "bindata" :api/binary}
   :app  {""        :demo/main
          "help"    :demo/help
          "prose"   :demo/prose
          ["party/" :location] :demo/party
          "job"     (bidi/tag :demo/job :wunderbar)
          "job2"     (bidi/tag :demo/job {:flags 3
                                          :context :wunderbar})
          "save"    :demo/save-non-existing ; there is no handler defined for this on purpose
          }})










