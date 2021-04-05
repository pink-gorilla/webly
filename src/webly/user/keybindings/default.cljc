(ns webly.user.keybindings.default)

(def keybindings-app
  [;{:kb "t"       :handler [:settings/show]          :desc "TEST: User Settings Edit"}
   {:kb "alt-g k" :handler [:palette/show]           :desc "Keybindings dialog"}

   {:kb "esc"     :handler [:modal/close]            :desc "Dialog Close"} ; for ALL dialogs!
   ;{:kb "esc"     :handler [:notebook/close-dialog-or-exit-edit] :desc "Dialog Close"} ; for ALL dialogs!

   {:kb "alt-g t" :handler [:reframe10x-toggle] :desc "10x visibility toggle"}])

