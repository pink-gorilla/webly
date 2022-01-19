(ns modular.rest.paging
  (:require
   [taoensso.timbre :refer [debug info warn error]]
   [martian.core :as martian]))

; https://clojure.atlassian.net/plugins/servlet/mobile?originPath=%2Fbrowse%2FCLJ-2555#issue/CLJ-2555
; https://github.com/clojure/clojure/blob/e45e47882597359aa2adce9f244ecdba730e6c76/src/clj/clojure/core.clj

(defn iteration
  "creates a seqable/reducible given step!,
   a function of some (opaque continuation data) k
   step! - fn of k/nil to (opaque) 'ret'
   :some? - fn of ret -> truthy, indicating there is a value
           will not call vf/kf nor continue when false
   :vf - fn of ret -> v, the values produced by the iteration
   :kf - fn of ret -> next-k or nil (will not continue)
   :initk - the first value passed to step!
   vf, kf default to identity, some? defaults to some?, initk defaults to nil
   it is presumed that step! with non-initk is unreproducible/non-idempotent
   if step! with initk is unreproducible, it is on the consumer to not consume twice"
  {:added "1.11"}
  [step! & {:keys [vf kf some? initk]
            :or {vf identity
                 kf identity
                 some? some?
                 initk nil}}]
  (reify
    clojure.lang.Seqable
    (seq [_]
      ((fn next [ret]
         (when (some? ret)
           (cons (vf ret)
                 (when-some [k (kf ret)]
                   (lazy-seq (next (step! k)))))))
       (step! initk)))
    clojure.lang.IReduceInit
    (reduce [_ rf init]
      (loop [acc init
             ret (step! initk)]
        (if (some? ret)
          (let [acc (rf acc (vf ret))]
            (if (reduced? acc)
              @acc
              (if-some [k (kf ret)]
                (recur acc (step! k))
                acc)))
          acc)))))

(defn make-paginate [m handler params result-kw]
  (fn [page]
    (let [page (or page 1)
           ;_ (info "requesting page: " page)
          params (assoc params :page page)
          result (->> (martian/response-for m handler params)
                      :body
                      result-kw)
          result? (when (> (count result) 0) true)]
       ;(info "page size: " (count result))
      (when result?
        {:page (inc page)
         :result result}))))

(defn request-paginated [m handler params result-kw]
  (let [api (make-paginate m handler params result-kw)]
    (mapcat identity (iteration api :kf :page :vf :result))))