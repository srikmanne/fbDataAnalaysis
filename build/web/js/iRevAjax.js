
var iRevAjax = {};

iRevAjax.getXmlHttp = function () {
	var xmlhttp = false;
	try {
		//-Firefox, Opera 8.0+, Safari
		xmlhttp=new XMLHttpRequest();
	} catch(e) {
		//-IE
		try {
			xmlhttp=new ActiveXObject("Msxml2.XMLHTTP");
		} catch(e) {
			try {
				xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
			} catch(e) {
				xmlhttp = false;
			}
		}
	}
	return xmlhttp;
}
