(ns demo.routes)

; copy for cloverage

(def routes-api
  {"time"   {:get :api/time}
   "test"   {:get :api/test
             :post :api/test  ; used in unit-test
             }
   "snippet"   {:get :api/snippet}})

(def routes-app
  {""        :demo/main
   "help"    :demo/help
   ["party/" :location] :demo/party
   "save"    :demo/save-non-existing ; there is no handler defined for this on purpose
   })







