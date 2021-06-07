# Webly [![GitHub Actions status |pink-gorilla/webly](https://github.com/pink-gorilla/webly/workflows/CI/badge.svg)](https://github.com/pink-gorilla/webly/actions?workflow=CI)[![Clojars Project](https://img.shields.io/clojars/v/org.pinkgorilla/webly.svg)](https://clojars.org/org.pinkgorilla/webly)

**End Users** this project is not for you.

- With webly you can build clj/cljs web apps (server: clj, frontend: reagent/reframe)
- webly uses great tools such as: reagent, reframe, tailwind-css, shadow-cljs
- webly brings a things that can be hard to configure (or repetitive) when you develop a web app such as:
  - ten-x developer console
  - configuration management
  - unit test runner
  - routing (via edn config), extensible both in frontend  and backend
  - keybindings
  - loading animation
  - notifications and dialogs
  - api endpoints
  - oauth2
  - google analytics (can be disabled)

- webly is used in [Goldly](https://github.com/pink-gorilla/goldly) and [Notebook](https://github.com/pink-gorilla/notebook).


# run the ui demo

Clone this repo and run:

```
lein webly npm-install
lein prep-res
lein webly watch     ; The demo runs a webserver on port 8000. "shadow-cljs watch" mode
```

The source code of the demo is in **profiles/demo**.

If this was your app, after you are finished developing it, you might compile it:

```
lein webly compile  ; Build cljs bundle, and output bundle stats
lein webly jetty     ; Serves website from precompiled bundle.
```

```
lein webly release   ; Build cljs bundle, and output bundle stats. no tenx. no source-maps
lein webly jetty     ; Serves website from precompiled bundle.
```

# features:

## 10x / tracing
- are included in watch / compile
- keybinding [alt+g t] to open 10x console
- touse trace-fn see:
  - https://github.com/day8/re-frame-debux is included in watch profile
  - webly.user.notifications.events
  - select :notification/add in tenx dev consol

## Bidi Routing

Bidi is not as highly starred as compojure, but it has important benefits:
- It is isomorphic (clj and cljs) (forget compojure AND secretary) 
- It has a clear separation of concerns with handlers (both for 
  clj (ring-handler) and cljs (pushy)
- It is bi-directional; this allows making links! 
- It does not use macros, this means routes can be easily tested, transformed, sent between client-server, stored, modified. 
- No linting errors due to macros.
- Easy testing of route definitions, and handler results (including wrapping routes)

## web app
- dynamically generated app html that works with csrf
- ring middleware for api calls
- routing with bidi (this allows to have links within the web-app)
- this means a registry for ring-handlers and reagent-views
- oauth2 with github

## webly build 
  - this feature is available for apps that use webly
  - compiles/watches via shadow-cljs 
  - does not require shadow-cljs.edn
  - bundle-size report at compile time


## [oauth2 (separate readme)](/README-oauth2.md)

# webly - how to use:

To start webly you have to pass it two parameter: **profile** and **config**

**Webly profile** can be one of the following strings:
- watch: builds with ten-x and runs shadow dev server
- compile: builds bundle
- release: builds release bundle (for production)
- jetty: runs app, with bundle compiled via compile or release 
- ci: builds bundle for unit tests
- npm-install: just installs npm dependencies (based on deps.cljs)

**Webly config**:
- is a clojure datastructure 
- is used in backend (clj) and frontend (cljs)
- can be passed as a string (link to a edn file or resource) - mandatory if webly is used in a leiningen project
- can be passed as a clojure datastructure - for quicker configuration in deps.edn projects.

## npm dependencies
- keep your npm depeendencies *only* in deps.cljs
- do NOT create a package.json file!
- package.json will be auto generated
- run `lein webly npm-install` and `npm outdated` to see if there are newer versions available


Add npm dependencies that you want to use into a clojure deps.cljs

```
{:npm-deps
 {; font awesome
  "@fortawesome/fontawesome-free" "^5.13.0"
```

Sometimes github repo and npm module do not match. 
Check this to see what goes on:  https://unpkg.com/@ricokahler/oauth2-popup-flow@2.0.0-alpha.1/index.js



## cljs unit tests
- run `lein webly ci`
- run `npm test` (this script is auto created when your package.json get auto created)

```
lein test-clj        ; Run unit tests - clj
lein test-js         ; Run unit tests - js

```



