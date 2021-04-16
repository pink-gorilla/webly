

; AUTHENTICATION for web apps
; https://github.com/cemerick/friend
; http://ahungry.com/blog/2018-12-26-Clojure-is-Capable.html


;; stolen from
;; https://github.com/timgilbert/haunting-refrain-posh/blob/develop/src/cljs/haunting_refrain/fx/auth.cljs


;; also good:
;; https://github.com/ricokahler/oauth2-popup-flow/tree/master/src


;; stolen from:
;; https://github.com/timgilbert/haunting-refrain-posh/blob/develop/src/cljs/haunting_refrain/views/foursquare.cljs



token expiry
 if (new Date().getTime() <= exp * 1000) return false;

 const popup = window.open(
      `${this.authorizationUri}?${OAuth2PopupFlow.encodeObjectToUri({
        client_id: this.clientId,
        response_type: this.responseType,
        redirect_uri: this.redirectUri,
        scope: this.scope,
        ...additionalParams,
      })}`,
    );
    if (!popup) return 'POPUP_FAILED';

    await this.authenticated();
    popup.close();