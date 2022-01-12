(ns webly.auth-test
(:require
   [taoensso.timbre :as timbre :refer [debug info error]]
   [clojure.test :refer [deftest is testing]]
   [ring.mock.request :refer [request] :rename {request mock-request}]
   [bidi.bidi]
   [bidi.ring]
   [modular.config :as config]
   [modular.oauth2.local.pass :as pass]
   [modular.oauth2.local.permission :as perm]
   [modular.oauth2.local.handler :as h]
   [webly.web.handler :refer [make-handler]]
))

(deftest pwd-hash-test []
  (is (= (pass/pwd-hash "1234") "a231498f6c1f441aa98482ea0b224ffa")))


; set the required config for the auth test

(config/set! :oauth2 
            {:local {:client-secret "123456789"}})

(config/set! :users
 {:demo {:roles #{:admin :logistic}
         :password "a231498f6c1f441aa98482ea0b224ffa" ; "1234"
         :email ["john@doe.com"]}})


(deftest auth-test []
  (info "config: " (pr-str (config/get-in-config [])))
  (let [{:keys [user token] :as t} (pass/get-token "demo" "1234")]
    (info "auth token: " (pr-str t))
    (is (= user "demo"))
    (is (= {:user "demo"} (pass/verify-token token)))))
  


(deftest perm-test []
  (is (perm/authorized? nil nil )) ; no user for route without permission requirement is authorized
  (is (perm/authorized? nil "demo" )) ; user for route with permssion is authorized
  
  (is (perm/authorized? #{} "demo" )) ; authorzed user required
  (is (not (perm/authorized? #{} nil ))) ; no user for route with permission does not pass

  (is (perm/authorized? #{:admin} "demo" ))
  (is (not (perm/authorized? #{:heroic-warrior} "demo" )))
  (is (not (perm/authorized? #{:admin} "crazy-ponny" )))
  
  ;
  )

(config/set! :permission
 {:default nil
  :service {:public2 nil
            :hello #{}
            :add #{:math}
            :calc #{:logistic}
            }
     })

(deftest service-test []
  (is (h/service-authorized? :public1 "1"))
  (is (h/service-authorized? :public2 "2"))
  (is (not (h/service-authorized? :hello "3")))
  (is (not (h/service-authorized? :add "4")))
  (h/set-user! "3" "demo")
  (h/set-user! "4" "demo")
  (is (h/service-authorized? :hello "3"))
  (is (not (h/service-authorized? :add "4")))
  (is (h/service-authorized? :calc "3"))



)





