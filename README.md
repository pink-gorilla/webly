# Webly [![GitHub Actions status |pink-gorilla/webly](https://github.com/pink-gorilla/webly/workflows/CI/badge.svg)](https://github.com/pink-gorilla/webly/actions?workflow=CI)[![Clojars Project](https://img.shields.io/clojars/v/org.pinkgorilla/webly.svg)](https://clojars.org/org.pinkgorilla/webly)

**End Users** this project is not for you.

- With webly you can build clj/cljs web apps (server: clj, frontend: reagent/reframe)
- webly uses great tools such as: reagent, reframe, tailwind-css, shadow-cljs
- webly brings a things that can be hard to configure (or repetitive) when you develop a web app such as:
  - ten-x developer console
  - unit test runner
  - routing (via edn config), extensible both in frontend  and backend
  - keybindings
  - loading animation
  - notifications and dialogs
  - google analytics (can be disabled)

- webly is used in [Goldly](https://github.com/pink-gorilla/goldly).

# run the ui demo

Clone this repo and run: `bb demo-webly`

```
clj -X:demo-webly :profile :npm-install
```

The demo runs a webserver on port 8080 with shadow-cljs "watch" mode.

A simple demo is on [Webly Github Pages](https://pink-gorilla.github.io/webly/)


# features

## 10x / tracing
- are included in watch / compile
- keybinding [alt+g t] to open 10x console
- touse trace-fn see:
  - https://github.com/day8/re-frame-debux is included in watch profile
  - frontend.notifications.events
  - select :notification/add in tenx dev console


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


## [oauth2 (separate readme)](/oauth2/README.md)

# webly - how to use:

To start webly you have to pass it two parameter: **profile** and **config**

# Webly Compile Profiles

**Webly profile** can be one of the following strings:
- watch: builds with ten-x and runs shadow dev server (shadow-cljs watch)
- watch2: shadow-cljs watch without ten-x devtools
- compile: builds bundle and output bundle stats
- release: builds release bundle (for production)  no tenx. no source-maps bundle stats
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


Add npm dependencies that you want to use into a clojure deps.cljs

```
{:npm-deps
 {; font awesome
  "@fortawesome/fontawesome-free" "^5.13.0"
```

Sometimes github repo and npm module do not match. 
Check this to see what goes on:  https://unpkg.com/@ricokahler/oauth2-popup-flow@2.0.0-alpha.1/index.js

## unit tests
- clj `bb test-clj`
- cljs: `bb test-cljs`

# notes

- 2022 02 22: disabled gh pages. error in ci. 


