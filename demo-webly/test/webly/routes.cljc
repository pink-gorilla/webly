(ns webly.routes)

(def routes-api
  {"time"   {:get 'demo.handler/time-handler}
   "test"   {:get 'demo.handler.test/test-handler
             :post 'demo.handler.test/test-handler  ; used in unit-test
             }
   "snippet"   {:get :api/snippet}})

(def routes-app
  {""        :demo/main
   "help"    :demo/help
   "prose"   :demo/prose
   ["party/" :location] :demo/party
   "save"    :demo/save-non-existing ; there is no handler defined for this on purpose
   })
