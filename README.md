# Webly [![GitHub Actions status |pink-gorilla/webly](https://github.com/pink-gorilla/webly/workflows/CI/badge.svg)](https://github.com/pink-gorilla/webly/actions?workflow=CI)[![Clojars Project](https://img.shields.io/clojars/v/org.pinkgorilla/webly.svg)](https://clojars.org/org.pinkgorilla/webly)

- This project is used in [Notebook](https://github.com/pink-gorilla/gorilla-notebook) and [Goldly](https://github.com/pink-gorilla/goldly).
- Tools to build reagent/re-frame clojurescript web apps that are served from a clojure webserver.
- **End Users** this project is not for you.

# UI demo

```
lein demo            ; The demo runs a webserver on port 8000. "shadow-cljs watch" mode
```

```
lein build           ; Build cljs bundle, and output bundle stats
lein run-web         ; Serves website from precompiled bundle.
```

```
lein test-clj        ; Run unit tests - clj
lein test-js         ; Run unit tests - js

```

**Demonstrates**
- loading animation
- navigation links to registered handlers (and error for unregistered handler)
- notifications
- dialog
- api endpoints
- oauth2

# Features


## webly build 
  - this feature is available for apps that use webly
  - compiles/watches via shadow-cljs 
  - does not require shadow-cljs.edn
  - bundle-size report at compile time

## web app
- dynamically generated app html that works with csrf
- ring middleware for api calls
- routing with bidi (this allows to have links within the web-app)
- this means a registry for ring-handlers and reagent-views
- oauth2 with github

## Bidi Routing

Bidi is not as highly starred as compojure, but it has important benefits:
- It is isomorphic (clj and cljs) (forget compojure AND secretary) 
- It has a clear separation of concerns with handlers (both for 
  clj (ring-handler) and cljs (pushy)
- It is bi-directional; this allows making links! 
- It does not use macros, this means routes can be easily tested, transformed, sent between client-server, stored, modified. 
- No linting errors due to macros.
- Easy testing of route definitions, and handler results (including wrapping routes)

# Notes

If there are problems in using vega with errors to "buffer" then `npm install shadow-cljs --save` might fix it. thheller: both buffer and process are polyfills packages that shadow-cljs will provide ... the npm package is mostly the for CLI stuff but also brings in some extra npm packages
the compiler is from the CLJ dependency you have in project.clj.
the npm stuff never does any actual compilation, just runs the java process
