


# 2024-04-14

- webly uses clip (easier to create an app)
- ring-handlers get required on startup (app will not start with invalid handlers)
- cljs-build uses extensions (can use all ui-extensions without sci)
- runtime services using extensions (api-routes, cljs-routes, theme)
- webly profile: "static": writes all resources neded for a static page
- static build working.


2024-04-02T00:23:25.862Z INFO [frontend.routes.events:13] - 

bidi routes-frontend are:  ["/static/index.html" 
{"" :demo/main, 
"help" demo.page/help, 
["party/" :location] 
demo.page/party, 
"job" :demo/job, 
"job2" :demo/job, 
"save" :demo/save-non-existing}]

 starting pushy
url did change to:  /static/index.html
adjusted path:  /static/index.html
reset-current!  {:handler :demo/main, :query-params {}, :route-params {}, :tag nil} 
trigger:  pushy/goto