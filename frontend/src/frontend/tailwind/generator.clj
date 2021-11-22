(ns frontend.tailwind.generator
  (:require
   [clojure.string :as str]
   [clojure.java.io :as io]
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [garden.core :as garden] ; side-effects!
   [girouette.garden.util :as util]
   [girouette.tw.common :refer [dot]]
   [girouette.processor]
   [frontend.tailwind.grammar :refer [my-chosen-components  my-color-map class-name->garden]]
   [frontend.tailwind.css :refer [composed-classes]]))

(def webly-default-classes
  ["inline-block"
   "cursor-pointer"
   "overflow-scroll"

   "w-full"
   "md:w-full"

    ; margin padding
   "m-1"
   "m-5"
   "mb-5"
   "pb-5"
   "gap-1"
   "gap-2"
   "gap-3"
   "gap-4"
   "gap-5"
   "m-3"
   "p-5"
   "p-4"
   "p-4"
   "mr-3"
   "lg:p-5"

   ; border
   "border"
   "border-2"
   "border-blue-500"
   "border-round"
   "border-l-4"
   "border-red-500"
   "border-l-4"
   "border-yellow-500"
   "border-l-4"
   "border-blue-500"
   ; color

   "bg-blue-300"
   "bg-red-400"
   "bg-yellow-100"
   "bg-yellow-300"
   "bg-blue-300"
   "bg-green-600"
   "bg-red-100"
   "bg-red-400"
   "bg-red-500"
   "bg-blue-100"
   "bg-blue-400"
   "bg-gray-300"
   "hover:bg-red-700"
   "dark:bg-gray-800"
   "dark:text-white"

  ;; text
   "text-3xl"
   "text-blue-500"
   "text-4xl"
   "text-red-700"
   "text-red-900"
   "text-gray-900"
   "text-yellow-700"
   "text-blue-700"

   ; grid
   "grid"
   "grid-cols-1"
   "grid-cols-2"
   "grid-cols-3"
   "grid-cols-4"
   "md:grid-cols-1"
   "md:grid-cols-2"
   "lg:grid-cols-2"
   "lg:grid-cols-3"
   "xl:grid-cols-3"
   "xl:grid-cols-4"

   ; flex
   "flex"
   "flex-col"

   "dark"
   "notification"
   "fas"
   "fa-trash"
   "button"

   "cursor-pointer"
   "button"
   "i"
   "fas"

   "hover:text-blue-700 mr-1"
   "hover:text-blue-700 mr-1"
   "fas fa-trash mr-3"
   "fas fa-trash pr-3"
   "inline-block"
   "fa-lg pl-1 fab fa-google-plus"
   "absolute bg-blue-300 p-2 top-3 border-0 mr-3 block z-50 font-normal leading-normal text-sm max-w-xs text-left no-underline break-words rounded-lg"
   "absolute bg-blue-300 p-2 top-3 border-0 mr-3 block z-50 font-normal leading-normal text-sm max-w-xs text-left no-underline break-words rounded-lg"
   "text-white p-3"
   "fa-lg pl-1"
   "bg-blue-300 cursor-pointer hover:bg-red-700 m-1"
   "text-green-800"
   "bg-blue-300 cursor-pointer hover:bg-red-700 m-1"
   ; keybindings
   "m-2 border border-round border-blue-400 w-1/4"
   "w-full ml-2 bg-blue-200 text-lg"
    "bg-red-300"
   "flex flex-row items-stretch mb-0 border border-solid border-blue-800 p-0"
   "flex flex-row items-stretch"

;
   ])

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

(defn- ensure-directory [path]
  (when-not (.exists (io/file path))
    (.mkdir (java.io.File. path))))

(defn generate-tailwind-webly [& _]
  (ensure-directory "target")
  (ensure-directory "target/webly")
  (ensure-directory "target/webly/public")
  (generate-tailwind
   {:output-file "target/webly/public/girouette.css"
    :garden-fn frontend.tailwind.grammar/class-name->garden
    :class-compositions frontend.tailwind.css/composed-classes
    :all-css-classes webly-default-classes}))
