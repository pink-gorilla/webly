(function(){
'use strict';$APP.shadow.loader.set_load_start("snippets");$APP.snippets.snip={};$APP.snippets.snip.add=function(a,b){$APP.taoensso.timbre._log_BANG_.cljs$core$IFn$_invoke$arity$11($APP.taoensso.timbre._STAR_config_STAR_,$APP.cljs$cst$keyword$info,"snippets.snip",null,7,$APP.cljs$cst$keyword$p,$APP.cljs$cst$keyword$auto,new $APP.cljs.core.Delay(function(){return new $APP.cljs.core.PersistentVector(null,3,5,$APP.cljs.core.PersistentVector.EMPTY_NODE,["snippet add ",a,b],null)},null),null,2005350538,null);return a+b};
$APP.snippets.snip.ui_add=function(a,b){return new $APP.cljs.core.PersistentVector(null,3,5,$APP.cljs.core.PersistentVector.EMPTY_NODE,[$APP.cljs$cst$keyword$p,"addition result: ",a+b],null)};
$APP.snippets.snip.ui_add_more_impl=function(a,b){var c=$APP.reagent.core.atom.cljs$core$IFn$_invoke$arity$1(0);return function(d,e){$APP.cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$2(c,$APP.cljs.core.inc);return new $APP.cljs.core.PersistentVector(null,3,5,$APP.cljs.core.PersistentVector.EMPTY_NODE,[$APP.cljs$cst$keyword$p,"addition result: ",d+e+$APP.cljs.core.deref(c)],null)}};
$APP.snippets.snip.ui_add_more=function(a,b){return new $APP.cljs.core.PersistentVector(null,3,5,$APP.cljs.core.PersistentVector.EMPTY_NODE,[$APP.snippets.snip.ui_add_more_impl,a,b],null)};$APP.shadow.loader.set_loaded();
}).call(this);