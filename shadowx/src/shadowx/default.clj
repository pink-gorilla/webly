(ns shadowx.default)

(def shadow
  {:ring-handler "webly.app.app/ring-handler" ; no need to change 
   :dev-http {:port 8000}
   :http {:port 8001 :host "0.0.0.0"}; "localhost"}
   :nrepl {:port 8002}})

(def build
  {:ns-cljs []
   :modules {}
   :module-loader-init false ; important, because we need to load on different paths
   :output-dir "target/webly/public"})

(def prefix "/r/")
