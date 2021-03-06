(ns webly.profile
  (:require
   [taoensso.timbre :as timbre :refer [info error]]
   [webly.config :refer [config-atom]]
   [webly.prefs :refer [prefs-atom]]
   [webly.writer :refer [write-target]]))

(defonce profiles
  {:ci          {:prefs   {:tenx false}
                 :bundle {:shadow-verbose false
                          :cljs-build :ci
                          :shadow-mode :release
                          :size-report false}}

   :release     {:prefs   {:tenx false}
                 :bundle {:shadow-verbose false
                          :cljs-build :webly
                          :shadow-mode :release ; production build - no source maps
                          :size-report true}}

   :release-adv  {:prefs   {:tenx false}
                  :bundle {:shadow-verbose false
                           :cljs-build :webly
                           :shadow-mode :release ; production build - no source maps
                           :advanced true
                           :size-report true}}

   :npm-install  {:prefs   {:tenx false}
                  :bundle {:shadow-verbose false
                           :cljs-build :webly
                           :shadow-mode nil ; no build, just npm install
                           :size-report false
                           :npm-install true}}

   :compile     {:prefs   {:tenx true}
                 :bundle {:shadow-verbose false
                          :cljs-build :webly
                          :shadow-mode :compile  ; compile has source maps
                          :size-report true}}

   :watch       {:prefs   {:tenx true}
                 :bundle {:shadow-verbose true
                          :cljs-build :webly
                          :shadow-mode :watch
                          :size-report false}
                 :server {:type :jetty
                          :wrap-handler-reload false
                          :api true  ; 
                          }}

   :watch2       {:prefs   {:tenx false}
                  :bundle {:shadow-verbose true
                           :cljs-build :webly
                           :shadow-mode :watch
                           :size-report false}
                  :server {:type :jetty
                           :wrap-handler-reload false
                           :api true  ; 
                           }}

   :jetty       {:prefs   {:tenx true}
                 :server {:type :jetty
                          :wrap-handler-reload false}}

   :httpkit      {:prefs   {:tenx true}
                  :server {:type :httpkit
                           :wrap-handler-reload false}}

   :undertow    {:server {:type :undertow
                          :wrap-handler-reload false}}})

(defn str->profile [profile-str]
  (let [p (keyword profile-str)]
    (get profiles p)))

(defn server? [profile-str]
  (let [p (str->profile profile-str)]
    (:server p)))

(defn compile? [profile-str]
  (let [p (str->profile profile-str)]
    (:bundle p)))

(defn get-build-prefs [profile]
  (let [p (get-in profile [:prefs])]
    (or p {})))

(defn profiles-available []
  (->> profiles
       keys
       (map name)
       (into [])))

(defn setup-profile
  [profile-name]
  (let [profile (str->profile profile-name)]
    (if (or (nil? profile-name) (not profile))
      (error "no profile. valid profiles are: " (profiles-available))
      (do
        (info "webly profile-name: " profile-name " profile: " profile)
        (swap! config-atom merge {:profile profile})
        (swap! prefs-atom merge (get-build-prefs profile))
        (info "prefs: " @prefs-atom)
        (write-target "prefs" (assoc @prefs-atom :profile profile-name))))
    profile))
