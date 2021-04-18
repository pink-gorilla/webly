(ns webly.user.keybindings.events
  (:require
   [clojure.string :as str]
   [taoensso.timbre :refer-macros [debug info error]]
   [re-frame.core :refer [reg-event-db reg-sub dispatch]]
   [webly.user.notifications.core :refer [add-notification]]
   [webly.user.keybindings.init :refer [init-keybindings!]]
   [webly.user.keybindings.component :refer [keybindings-dialog]]))

(def clean-search
  {:highlight     0
   :visible-items nil
   :query        ""})

(defn- kb-from-config [db]
  (or (get-in db [:config :keybindings]) []))

(reg-event-db
 :keybindings/init
 (fn [db [_]]
   (let [db (or db {})
         keybindings (kb-from-config db)]
     (info "keybinding init ..")
     (init-keybindings! keybindings)
     (assoc-in db
               [:keybindings]
               {:search clean-search}))))

; dialog visibility

(reg-event-db
 :palette/show
 (fn [db [_]]
   (info "showing keybindings dialog")
   (dispatch [:palette/filter-changed ""])
   (dispatch [:modal/open [keybindings-dialog] :medium])
   (assoc-in db [:keybindings :search] clean-search)))

(reg-event-db
 :palette/hide
 (fn [db _]
   (dispatch [:modal/close])
   db))

; palette

(reg-sub
 :palette
 (fn [db _]
   (get-in db [:keybindings :search])))

(defn make-regex
  [val]
  (let [res (str/join ".*" (str/split val " "))
        pattern (str "(?i)" res ".*") ; case insensitive
        _ (error "regex: " pattern)
        re (re-pattern pattern)]
    (fn [item]
      (let [desc (or (:desc item) "")
            match (re-find re desc)] ; don't use re-match, it is mutating
        match))))

(reg-event-db
 :palette/filter-changed
 (fn [db [_ query]]
   (let [all (kb-from-config db)
         search-old (get-in db [:keybindings :search])
         visible (if (or (nil? query) (= query ""))
                   all
                   (filter (make-regex query) all))]
     (assoc-in db
               [:keybindings :search]
               (merge search-old {:visible-items visible
                                  :query         query})))))

(defn highlight-move [db direction]
  (let [palette (get-in db [:keybindings :search])
        {:keys [highlight visible-items]} palette
        maxidx (- (count visible-items) 1)
        highlight-new (case direction
                        :up (if (> highlight 0)
                              (- highlight 1)
                              highlight)
                        :down (if (< highlight maxidx)
                                (+ highlight 1)
                                highlight))]
    (debug "highlight: " highlight-new)
    (assoc-in db [:keybindings :search :highlight] highlight-new)))

(defn highlight-action [db]
  (let [palette (get-in db [:keybindings :search])
        {:keys [highlight visible-items]} palette
        _ (info "action highlight: " highlight " count:" (count visible-items))
        item (when
              (not-empty visible-items)
               (nth visible-items highlight))]
    (dispatch [:palette/action item])
    db))

(reg-event-db
 :palette/filter-keydown
 (fn [db [_ keycode]]
   (case keycode
     38 (highlight-move db :up)
     40 (highlight-move db :down)
     27 (do (dispatch [:palette/hide])
            db)
     13 (highlight-action db)
     db)))

(reg-event-db
 :palette/action
 (fn [db [_ item]]
   (info "palette/action!" item)
   (let [handler (:handler item)]
     (if handler
       (do (info "dispatching" handler)
           (dispatch [:palette/hide]) ; if dispatch opens another dialog we first have to hide it
           (dispatch handler))
       (do
         (error "handler not found!")
         (add-notification :danger "keybining didn't have a handler!")
         (dispatch [:palette/hide])))
     db)))

