(ns webly.user.oauth2.request-login
  (:require
   [taoensso.timbre :refer-macros [info error]]
   [clojure.string :as str]
   [ajax.core :as ajax]
   [re-frame.core :refer [reg-event-db reg-event-fx dispatch]]))

(def user-uri
  {:github "https://api.github.com/user"
   :google "https://www.googleapis.com/oauth2/v2/userinfo"})

(reg-event-db
 :oauth2/get-user
 (fn [{:keys [db]} [_ provider]]
   (info "oauth/after-login:" provider)
   (let [uri (provider user-uri)]
     (dispatch [:request provider uri :oauth2/set-user]))
   db))

(defn extract-google [data]
  (info "google-user: " data)
  {:user (:id data)
   :email (:email data)})

  ; :github {:email "name@domain.com"
;          :login "masterbuilder99"
;          :id 6767676
;          :public_gists 4
;          :public_repos 1
;          :created_at "2015-05-27T14:46:29Z"
;          :avatar_url "https://avatars.githubusercontent.com/u/82429483?v=4"}

(defn extract-github [data]
  (info "github data " data)
  {:user (:login data)
   :email (:email data)})

(reg-event-db
 :oauth2/set-user
 (fn [db [_ provider data]]
   (let [{:keys [user email]} (case provider
                                :google (extract-google data)
                                :github (extract-github data)
                              ; {:user "unknown" :email "unknown"}  
                                )]
     (info "oauth2/set-user:" provider user email)
     (dispatch [:oauth2/logged-in provider])
     (-> db
         (assoc-in  [:token provider :user] user)
         (assoc-in  [:token provider :email] email)))))