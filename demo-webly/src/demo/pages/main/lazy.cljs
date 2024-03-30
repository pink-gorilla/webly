(ns demo.pages.main.lazy
  (:require
   [taoensso.timbre :refer-macros [debug info warn error]]
   [reagent.core :as r]
   [promesa.core :as p]
   [re-frame.core :as rf]
   [webly.build.lazy :refer-macros [wrap-lazy] :refer [available]]
   [webly.module.build :refer [load-namespace-raw]]
   [demo.helper.ui :refer [link-dispatch link-href link-fn block2]]))

(defn lazy1 []
  (let [ui-add (wrap-lazy snippets.snip/ui-add)]
    [:div.bg-red-500
     [:p "I am lazy:"]
     [ui-add 7 7]]))

(defn lazy2 []
  (let [ui-add-more (wrap-lazy snippets.snip/ui-add-more)]
    [:div
     [ui-add-more 7 7]]))

(defn load-namespace-test [& _]
  (let [rp (load-namespace-raw :bongistan)]
    (p/then rp (fn [r]
                 (println "*** NS LOAD SUCCESS: " r)))
    (p/catch rp (fn [x]
                  (println "*** NS LOAD ERROR: " x)))))

(defn load-namespace-test-bad [& _]
  (let [rp (load-namespace-raw :bongistan1234567)]
    (p/then rp (fn [r]
                 (println "*** NS LOAD SUCCESS: " r)))
    (p/catch rp (fn [x]
                  (println "*** NS LOAD ERROR: err: " x)))))

(defn load-namespace-highcharts [& _]
  (let [rp (load-namespace-raw :'ui.highcharts)]
    (p/then rp (fn [r]
                 (println "*** NS HIGHCHARTS LOAD SUCCESS: " r)))
    (p/catch rp (fn [x]
                  (println "*** NS HIGHCHARTS LOAD ERROR: err: " x)))))


(defn demo-lazy []
  (let [show-lazy1 (r/atom false)
        show-lazy2 (r/atom false)]
    (fn []
      [block2 "lazy ui"
       [link-fn #(reset! show-lazy1 true) "lazy load1"]
       [link-fn #(reset! show-lazy2 true) "lazy load2 (not working)"]
       [link-fn load-namespace-test "lazy-namespace"]
       [link-fn load-namespace-test-bad "lazy-non-existing-namespace"]
       [link-fn load-namespace-highcharts "lazy-highcharts"]
       [:div "loaded lazy renderer: " (pr-str (available))]
       (when @show-lazy1
         [lazy1])
       (when @show-lazy2
         [lazy2])])))

