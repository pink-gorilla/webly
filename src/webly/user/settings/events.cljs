(ns webly.user.settings.events
  "events related to the settings"
  (:require
   [taoensso.timbre :refer-macros [info warn]]
   [re-frame.core :refer [reg-event-fx inject-cofx]]
   [akiroz.re-frame.storage :refer [reg-co-fx!]] ; https://github.com/akiroz/re-frame-storage
   ))

(reg-co-fx! :webly         ;; local storage key
            {:fx :store     ;; re-frame fx ID
             :cofx :store}) ;; re-frame cofx ID

(reg-event-fx
 :settings/init
 [(inject-cofx :store)]
 (fn [{:keys [db store]}]
   (info "settings/localstorage init")
   (let [store (or store {})
         settings-default (get-in db [:config :settings])
         use-localstorage (:use-localstorage settings-default)
         settings-default (dissoc settings-default :use-localstorage)
         settings (if use-localstorage
                    (merge settings-default store)
                    settings-default)]
     (if use-localstorage
       (warn ":db :settings can be changed by user via localstorage")
       (warn "using static settings supplied via config"))
     {:db (assoc db :settings settings)})))

;; Settings Change

(reg-event-fx
 :settings/set
 [(inject-cofx :store)]
 (fn [{:keys [db store]} [_ k v]]
   (info "changing setting " k " to: " v)
   (let [store (or store {})]
     {:db (assoc-in db [:settings k] v)
      :store (assoc store k v)})))

(reg-event-fx
 :settings/unset
 [(inject-cofx :store)]
 (fn [{:keys [db store]} [_ k]]
   (info "unsetting setting " k)
   (let [store (or store {})
         settings (dissoc (:settings db) k)]
     {:db (assoc-in db [:settings] settings)
      :store (dissoc store k)})))
