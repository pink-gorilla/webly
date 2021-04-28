## reagent / reframe


## reagent 
- https://blog.ducky.io/clojurescript/reagent/2016/05/11/reagent-dynamic-children/
- - http://pschwarz.bicycle.io/json-to-edn/   json->edn converter 

reagent context
https://gist.github.com/mhuebert/d400701f7eddbc4fffa811c70178a8c1


https://github.com/gadfly361/re-surface   reagent page structure


## reframe fx / cofx

The differences are: fxs are pushed by an event, whereas, cofxs are pulled by an event.

Examples of fx
- Push data to a database
- Push data to localStore/cookie
- Push data to a third party service
- Push message to js/alert
- Push a function to JavaScript event queue, e.g. js/setInterval, js/setTimeout, js/Promise

Example of cofx
- Pull data from a database
- Pull data from localStore/cookie
- Pull data from a third party service
- Pull current datetime from browser
- Pull a random number

