(ns webly.user.tailwind.generator
  (:require
   [clojure.string :as str]
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [garden.core :as garden] ; side-effects!
   [girouette.garden.util :as util]
   [girouette.tw.common :refer [dot]]
   [girouette.processor]
   [webly.user.tailwind.grammar :refer [my-chosen-components  my-color-map class-name->garden]]
   [webly.user.tailwind.css :refer [composed-classes]]))

(def webly-default-classes
  ["bg-blue-300"
   "bg-red-400"
   "cursor-pointer"
   "hover:bg-red-700"
   "inline-block"
   "m-1"
   "m-5"
   "mb-5"
   "md:w-full"
   "text-4xl"
   "a"
   "below."
   "bg-blue-300"
   "cursor-pointer"
   "hover:bg-red-700"
   "cursor-pointer"
   "for"
   "hover:bg-red-700"
   "is"
   "m-1"
   "bg-yellow-300"
   "dark"
   "dark:bg-gray-800"
   "dark:text-white"
   "grid"
   "grid-cols-4"
   "text-gray-900"

   "flex"
   "flex-col"
   "bg-blue-300"
   "bg-green-600"
   "m-3"

   "p-5"

   "text-red-900"
   "border"
   "border-2"
   "border-blue-500"
   "border-round"
   "overflow-scroll"

   "bg-red-400"
   "text-3xl"
   "text-blue-500"])

(defn- kw->classes [kw]
  (->> (name kw)
       (re-seq #"\.[^\.#]+")
       (map (fn [s] (subs s 1)))))

(defn- string->classes [s]
  (->> (str/split s #"\s+")
       (remove str/blank?)))

(defn normalize-classes [x]
  (if (string? x)
    (string->classes x)
    (kw->classes x)))

(defn generate-tailwind [{:keys [output-file garden-fn class-compositions all-css-classes]}]
  (let [predef-garden  []
        _ (println "generating css..")
        all-garden-defs (-> predef-garden
                            (into (->> (keep garden-fn all-css-classes)
                                       (sort util/rule-comparator)))
                            (into (map (fn [[target-class-name classes-to-compose]]
                                         (let [classes-to-compose (mapcat normalize-classes classes-to-compose)]
                                           (util/apply-class-rules (dot target-class-name)
                                                                   (mapv garden-fn classes-to-compose)
                                                                   (mapv dot classes-to-compose)))))
                                  class-compositions))]
    (spit output-file (garden/css all-garden-defs))))

(defn generate-tailwind-webly [& _]
  (generate-tailwind
   {:output-file "target/webly/public/girouette.css"
    :garden-fn webly.user.tailwind.grammar/class-name->garden
    :class-compositions webly.user.tailwind.css/composed-classes
    :all-css-classes webly-default-classes}))
