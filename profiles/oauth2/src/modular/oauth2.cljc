(ns modular.oauth2
  (:require
   #?(:clj  [taoensso.timbre :refer [debug info warn error]])
   #?(:cljs [taoensso.timbre :refer-macros [debug info warn error]])
   #?(:clj  [nano-id.core :refer [nano-id]])
   #?(:clj  [modular.config :as config])
   #?(:clj  [modular.persist.protocol :refer [save loadr]])
   #?(:cljs [modular.oauth2.request])
   #?(:cljs [modular.oauth2.subscriptions])
   #?(:cljs [modular.oauth2.authorize.start])
   #?(:cljs [modular.oauth2.authorize.redirect-events])
   #?(:cljs [modular.oauth2.authorize.userinfo])
   #?(:cljs [modular.oauth2.authorize.events-parse])))

#?(:clj
   (defn get-config-server [server-profile-kw]
     (config/get-in-config [:oauth2 :server server-profile-kw])))

#?(:clj
   (defn get-config-user
     ([]
      (get-config-user :default))
     ([user-id-kw]
      (config/get-in-config [:oauth2 :user user-id-kw])))

;
   )

; (nano-id 6)

(defn start-authorize-workflow [{:keys [provider scopes]}])

