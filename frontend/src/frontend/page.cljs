(ns frontend.page
  (:require
   [frontend.routes :refer [current]])
  )

(defmulti reagent-page
  (fn [x] (get x :handler)))

(defn available-pages []
  (->> (methods reagent-page)
       keys
       (remove #(= :default %))
       (into [])))

(defn not-found-page []
  [:div.bg-red-500.m-5
   [:h1 (str "page " (:handler @current) " - not found!")]
   [:p "Available pages: " (pr-str (available-pages))]
   [:p "Current Page:" (str @current)]])

(defmethod reagent-page :default [& args]
  [not-found-page])