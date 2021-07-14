(ns webly.user.css.loading
  (:require
   [taoensso.timbre :refer-macros [debugf info warn warnf error]]
   [re-frame.core :as rf]))

(rf/reg-event-db
 :css/loading-add
 (fn [db [_ href]]
   (info "css/loading-add: " href)
   (let [css-loading (or (:css/loading db) [])
         css-loading (into [] (conj css-loading href))]
     (info "css/loading: " css-loading)
     (assoc-in db [:css/loading] css-loading))))

(rf/reg-event-db
 :css/loading-success
 (fn [db [_ href]]
   (info "css/loading-success: " href)
   (let [css-loading (or (:css/loading db) [])
         css-loading (into [] (remove #(= href %) css-loading))]
     (info "css/loading: " css-loading)
     (assoc-in db [:css/loading] css-loading))))

(rf/reg-sub
 :css/loading?
 (fn [db _]
   (let [css-loading (or (:css/loading db) [])]
     (> (count css-loading) 0))))