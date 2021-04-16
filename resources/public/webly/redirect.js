

var bc = new BroadcastChannel('webly_oauth2_redirect_channel');

var url = window.location.href;

console.log ("url: " + url);

function sendcallback (provider)
{
  var msg = JSON.stringify({provider: provider, url: url});
  console.log ("sending back: " + msg)
  bc.postMessage(msg); 
}

// window.location.hash = '';