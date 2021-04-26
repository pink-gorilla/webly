# develop with webly

You can use webly to build custom clj/cljs apps


You trigger webly run via webly.user.app.app/webly-run!
It only expects one parameter, the profile.

Webly profiles:
- watch: builds with ten-x and runs shadow dev server
- compile: builds bundle
- release: builds release bundle (for production)
- jetty: runs app, with bundle compiled via compile or release 
- ci: builds bundle for unit tests
- npm-install: just installs npm dependencies (based on deps.cljs)


## 10x / tracing
- are included in watch / compile
- keybinding [alt+g t] to open 10x console
- touse trace-fn see:
  - https://github.com/day8/re-frame-debux is included in watch profile
  - webly.user.notifications.events
  - select :notification/add in tenx dev console

## npm dependencies
- keep your npm depeendencies *only* in deps.cljs
- do NOT create a package.json file!
- package.json will be auto generated
- run `lein webly npm-install` and `npm outdated` to see if there are newer versions available

## cljs unit tests
- run `lein webly ci`
- run `npm test` (this script is auto created when your package.json get auto created)

## Using NPM dependencies

Add npm dependencies that you want to use into a clojure deps.cljs

```
{:npm-deps
 {; font awesome
  "@fortawesome/fontawesome-free" "^5.13.0"
```

Sometimes github repo and npm module do not match. 
Check this to see what goes on:  https://unpkg.com/@ricokahler/oauth2-popup-flow@2.0.0-alpha.1/index.js


# Notes

If there are problems in using vega with errors to "buffer" then `npm install shadow-cljs --save` might fix it. thheller: both buffer and process are polyfills packages that shadow-cljs will provide ... the npm package is mostly the for CLI stuff but also brings in some extra npm packages
the compiler is from the CLJ dependency you have in project.clj.
the npm stuff never does any actual compilation, just runs the java process
