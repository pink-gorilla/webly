

console.log("test 2 was loaded.");



function add5 (a) {
    return a + 5;
};


function bongoshow (domid, c) {
    console.log("Adding bongoshow to dom node: " + domid)
    var paragraph = document.getElementById(domid);
    var t = c;
    if (t === null) t = "ERROR! no text!";
    var text = document.createTextNode(t);
    paragraph.appendChild(text);

};


window.add5 = add5;
window.bongoshow = bongoshow;