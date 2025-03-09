# 2024-04-14

- webly uses clip (easier to create an app)
- ring-handlers get required on startup (app will not start with invalid handlers)
- cljs-build uses extensions (can use all ui-extensions without sci)
- runtime services using extensions (api-routes, cljs-routes, theme)
- webly profile: "static": writes all resources neded for a static page
- static build working.


# 2024-11-25
- sente updated, so now on jetty 11.


# 2025-01-09
- webserver updated, now with letsencrypt-proxy as an included option


# 2025-02-20
- webserver updated:
  - now with reitit router and middleware to inject services to handlers
  - routing moved 100% to webserver
  - namespace change from "modular.webserver" to just "webserver"
- 0.9.xxx because of breaking changes  

# 2025-03-09
- sente websocket is now debundled.
- ui-pprint-frisk is debundled
- devtools is debundled

