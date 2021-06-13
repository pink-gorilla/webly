(ns demo.routes)

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
          "save"    :demo/save-non-existing ; there is no handler defined for this on purpose
          }})










