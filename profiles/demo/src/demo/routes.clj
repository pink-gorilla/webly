(ns demo.routes
  (:require
   [bidi.bidi :as bidi]
  ))

(def routes
  {:api {"time"   {:get :api/time}
         "timejava"   {:get :api/time-java}
         "biditest"   {:get :api/bidi-test}
         "test"   {:get :api/test
                   :post :api/test  ; used in unit-test
                   }
         "snippet"   {:get :api/snippet}}
   :app  {""        :demo/main
          "help"    :demo/help
          "prose"   :demo/prose
          ["party/" :location] :demo/party
          "job"     (bidi/tag :demo/job :wunderbar)
          "job2"     (bidi/tag :demo/job {:flags 3 
                                          :context :wunderbar})
          "save"    :demo/save-non-existing ; there is no handler defined for this on purpose
          }})










