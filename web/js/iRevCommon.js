/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
var iRevCommon = {};
iRevCommon.slideIndex = 1;

//========================================================
// Get the URL parameters
//========================================================
iRevCommon.getUrlParams = function () {
    var vars = [], hash;
   var sCode = "";
    var hashes = window.location.search.slice(window.location.search.indexOf('?') + 1).split('&');
    for (var i = 0; i < hashes.length; i++)
    {
        hash = hashes[0].split('=');
        vars.push(hash[1]);
        vars[hash[0]] = hash[1];
        if(typeof hash[1] !== "undefined"){  
        sCode = hash[1];
        }
    }
    return sCode;
};

////========================================================
//// Original - should be used later Get the URL parameters
////========================================================
//iRevCommon.getUrlParams = function () {
//	var vars = [], hash;
//    var hashes = window.location.search.slice(window.location.search.indexOf('?') + 1).split('&');
//    for(var i = 0; i < hashes.length; i++)
//    {
//	hash = hashes[i].split('=');
//	vars.push(hash[1]); 
//	vars[hash[0]] = hash[1];
//    }
//    return vars;
//};


//showDivs(iRevCommon.slideIndex);

iRevCommon.plusDivs = function(n) {
  iRevCommon.showDivs(iRevCommon.slideIndex += n);
};

iRevCommon.showDivs= function(n) {
  var i;
  var x = document.getElementsByClassName("mySlides");
  if (n > x.length) {iRevCommon.slideIndex = 1}    
  if (n < 1) {iRevCommon.slideIndex = x.length}
  for (i = 0; i < x.length; i++) {
     x[i].style.display = "none";  
  }
  x[iRevCommon.slideIndex-1].style.display = "block";  
};