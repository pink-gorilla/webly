(ns webly.date
  "Time calculation and formatting helpers"
  (:require
   #?(:clj [java-time]
      :cljs [cljs-time.core :as cljs-time])
   #?(:clj [java-time.format])))


#?(:cljs
   (def date-time cljs-time/date-time)
   :clj
   (defn date-time [& args]
     (java-time/with-zone-same-instant
       (apply java-time/zoned-date-time args)
       "UTC")))


#?(:clj (defn now []
          (java-time/with-zone-same-instant
            (java-time/local-date)
           "UTC"))
   :cljs (def now cljs-time/now))

#?(:clj  (defn tostring [dt]
           (let [fmt (java-time.format/formatter "YYYY-MM-dd HH:mm:ss")] ;
             (java-time.format/format fmt dt)))

   :cljs (defn tostring [dt]
           (let [fmt (cljs-time/formatter "yyyy-MM-dd HH:mm:ss")]
             (cljs-time/unparse fmt dt))))

(defn now-str []
  (tostring (now))
  )

(now)
(tostring 
 (date-time 2021 01 23))

(tostring (now))

;; #?(:clj
;;    (s/fdef time-ago
;;      :args (s/cat :instant (s/alt :instant java-time/instant?
;;                                   :str string?))
;;      :ret string?))

(def ^:const units [{:name "second" :limit 60 :in-second 1}
                    {:name "minute" :limit 3600 :in-second 60}
                    {:name "hour" :limit 86400 :in-second 3600}
                    {:name "day" :limit 604800 :in-second 86400}
                    {:name "week" :limit 2629743 :in-second 604800}
                    {:name "month" :limit 31556926 :in-second 2629743}
                    {:name "year" :limit nil :in-second 31556926}])

(defn diff-to-now [instant]
  #?(:clj
     (java-time/as (java-time/duration instant (java-time/instant)) :seconds)
     :cljs
     (cljs-time/in-seconds (cljs-time/interval instant (cljs-time/now)))))

(defn time-ago
  "Returns the difference of `instant` and now formated as words"
  [instant]
  (let [diff (diff-to-now instant)]
    (if (< diff 5)
      "just now"
      (let [unit (first (drop-while #(and (:limit %)
                                          (>= diff (:limit %)))
                                    units))]
        (-> (/ diff (:in-second unit))
            Math/floor
            int
            (#(str % " " (:name unit) (when (> % 1) "s") " ago")))))))

(comment
  #?(:clj
     (do
       (time-ago (java-time/instant "2018-07-02T10:20:30.00Z"))
       (time-ago #inst "2018-07-04T11:14:49.738-00:00")
       (time-ago #inst "2017-07-04T11:14:49.738-00:00"))

     :cljs
     (do
       (in-minutes (interval (date-time 1986 10 2) (date-time 1986 10 14)))
       (time-ago (cljs-time/minus (cljs-time/now) (cljs-time/hours 5))))))