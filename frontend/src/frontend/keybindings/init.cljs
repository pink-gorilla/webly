(ns frontend.keybindings.init
  (:require
   [taoensso.timbre :refer-macros [debug debugf info infof error]]
   [re-frame.core :as r :refer [reg-event-db dispatch]]
   [keybind.core :as key]))

; https://github.com/piranha/keybind
(def current (atom 0))
(defn id []
  (swap! current inc)
  (keyword (str @current)))

(def current-scope (atom nil))

(defn reframe-handler [{:keys [kb handler scope] :as keybinding} evt]
  (info "keybinding triggered scope: " scope "handler: " handler " evt: " evt)
  (when (or (nil? scope)
            (= scope @current-scope))
    (dispatch handler)
    (.preventDefault evt))) ; important, so that the "s" in "alt-g s" does not end in editor

(def BINDINGS (atom {}))

(defn bind-key
  "Binds a sequence of button presses, specified by `spec`, to `cb` when
  pressed. Keys must be unique per `spec`, and can be used to remove keybinding
  with `unbind!`.
  `spec` format is emacs-like strings a-la \"ctrl-c k\", \"meta-shift-k\", etc."
  [{:keys [kb handler scope] :as keybinding}]
  (debugf "binding %s to %s" kb handler)
  #_(swap! BINDINGS
           key/bind kb (id) (partial reframe-handler keybinding))
  (key/bind! kb (id) (partial reframe-handler keybinding)))

(defn ^:export init-keybindings!
  [keybindings]
  (infof "registering %s keybindings.." (count keybindings))
  (doall (for [kb keybindings]
           (bind-key kb))))

