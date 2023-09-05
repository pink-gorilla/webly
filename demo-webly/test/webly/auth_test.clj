(ns webly.auth-test
  (:require
   [taoensso.timbre :as timbre :refer [debug info error]]
   [clojure.test :refer [deftest is testing]]
   [ring.mock.request :refer [request] :rename {request mock-request}]
   [bidi.bidi]
   [bidi.ring]
   [modular.config :as config]
   [modular.oauth2.local.pass :as pass]
   [webly.web.handler :refer [make-handler]]
   [modular.permission.service :refer [add-permissioned-services]]
   [modular.permission.role :as role]
   [modular.permission.websocket :refer [set-user! service-authorized?]]
   [modular.permission.user :refer [print-users]]))

(deftest pwd-hash-test []
  (is (= (pass/pwd-hash "1234") "a231498f6c1f441aa98482ea0b224ffa")))

; set the required config for the auth test

(config/set! :oauth2
             {:local {:client-secret "123456789"}})

(config/set! :users
             {:demo {:roles #{:admin :logistic}
                     :password "a231498f6c1f441aa98482ea0b224ffa" ; "1234"
                     :email ["john@doe.com"]}})

(print-users)

(deftest auth-test []
  (info "config: " (pr-str (config/get-in-config [])))
  (let [{:keys [user token] :as t} (pass/get-token "demo" "1234")]
    (info "get-token result token: " (pr-str t))
    (is (= user :demo))
    (is (= {:user :demo} (pass/verify-token token)))))

(deftest perm-test []
  (is (role/authorized? nil nil)) ; no user for route without permission requirement is authorized
  (is (role/authorized? nil :demo)) ; user for route with permssion is authorized

  (is (= (role/authorized? #{} :demo) true)) ; authorized user required
  (is (= (role/authorized? #{} nil) false)) ; no user for route with permission does not pass

  (is (role/authorized? #{:admin} :demo))
  (is (not (role/authorized? #{:heroic-warrior} :demo)))
  (is (not (role/authorized? #{:admin} :crazy-ponny)))

  ;
  )
(add-permissioned-services
 {:public2 nil
  :hello #{}
  :add #{:math}
  :calc #{:logistic}})

(deftest service-test []
  (is (= (service-authorized? :public2 "2") true)) ; public2 does not need any permission => authorized
  (is (= (service-authorized? :public1 "1") false)) ; public1 is not defined => not authorized.

  (is (= (service-authorized? :hello "3") false)) ; :hello needs login. but "3" is not logged in => not authorized
  (is (= (service-authorized? :add "4") false)) ; hello needs role :math, but "4" is not logged in ==> not authorized
  (set-user! "3" :demo)
  (set-user! "4" :demo)
  (is (= (service-authorized? :hello "3") true))
  ; "3" => :demo [:admin :logistic}
  ; :hello does not need any role ==> authorized    
  (is (not (service-authorized? :add "4")))
  (is (service-authorized? :calc "3")))





