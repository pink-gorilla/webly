# Ping Gorilla Webly [![GitHub Actions status |pink-gorilla/webly](https://github.com/pink-gorilla/webly/workflows/CI/badge.svg)](https://github.com/pink-gorilla/webly/actions?workflow=CI)[![Clojars Project](https://img.shields.io/clojars/v/org.pinkgorilla/webly.svg)](https://clojars.org/org.pinkgorilla/webly)

- This project is used in [Notebook](https://github.com/pink-gorilla/gorilla-notebook) and [Goldly](https://github.com/pink-gorilla/goldly).
- Tools to build reagent web apps.

# UI demo

The demo runs a webserver on port 8019.

```
lein demo
```

# Features
- routing with bidi (this allows to have links within the web-app)
- oauth2 with github
- dynamically generated app html that works with csrf
- ring middleware for api calls


# Architecture

## Routing: Bidi

Bidi is not as highly starred as compojure, but it has important benefits:
- It is isomorphic (clj and cljs) (forget compojure AND secretary) 
- It has a clear separation of concerns with handlers (both for 
  clj (ring-handler) and cljs (pushy)
- It is bi-directional; this allows making links! 
- It does not use macros, this means routes can be easily tested, transformed, sent between client-server, stored, modified. 
- No linting errors due to macros.
- Easy testing of route definitions, and handler results (including wrapping routes)
- TODO: use bidi for RPC routing on websocket.

