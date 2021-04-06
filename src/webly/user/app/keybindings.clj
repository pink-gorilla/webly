(ns webly.user.app.keybindings)

(def default
  ; https://github.com/piranha/keybind
  ; shift, ctrl, alt, win, cmd, defmod, "left" "right"
  ; "meta-shift-l" "alt-shift-p" "ctrl-shift-left" "ctrl-shift-right"
  [{:kb "alt-g k" :handler [:palette/show]           :desc "Keybindings dialog"}
   {:kb "esc"     :handler [:modal/close]            :desc "Dialog Close"} ; for ALL dialogs!
   {:kb "alt-g t" :handler [:reframe10x-toggle] :desc "10x visibility toggle"}])

