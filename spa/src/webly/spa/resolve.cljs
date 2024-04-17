(ns webly.spa.resolve
  (:require
   [webly.module.build :refer [webly-resolve]]))

(def resolver-a (atom webly-resolve))

(defn set-resolver! [resolver-fn]
  (reset! resolver-a resolver-fn))

(defn get-resolver []
  @resolver-a)

