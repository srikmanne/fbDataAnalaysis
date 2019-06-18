var iRevInit = {};


//========================================================
//-Init the view by loading HTML from file
//========================================================
iRevInit.init = function () {
    var sWhere = "iRevInit.init()";
//    try {
//      
             var login = document.getElementById("login");
                    if(login){
                        login.style.display= "block";
                      //  login.innerHTML= "";
                    }
                    var dasboard = document.getElementById("dasboard");
                    if(dasboard){
                        dasboard.style.display= "none";                       
                    } 
                    console.log("sd::"+iRevCommon.getUrlParams());
            //-This should be changed in future
            if(iRevCommon.getUrlParams()!== "" ){
               // iRevLogin.fbLogin(iRevCommon.getUrlParams());
            }

//    } catch (e) {
//        iRevCommon.persistentLog(sWhere, 'Error: ' + e.message);
//    }
};

//========================================================
//-Display the list of portals available.
//========================================================
iRevInit.display = function () {
    var sWhere = "iRevInit.display()";
    var json;
    try {
        var xmlhttp = iRevAjax.getXmlHttp();
        xmlhttp.onreadystatechange = function () {
            if (xmlhttp.readyState == 4) {
                //-Check for response issues, returns JSON
                //var json = iRevCommon.chkAjaxResponse(sWhere, xmlhttp.responseText);
                json = JSON.parse(xmlhttp.responseText);                
                if (json.hasOwnProperty("data")) {
                    json = json["data"];

                }
                
                var con = json["content"]; //console.log("cn::"+con);
                document.getElementById("content").innerHTML ="<h2>'+'ve reviews</h2>" +con;
                delete json.content;

                google.charts.load('current', {'packages': ['corechart']});
                google.charts.setOnLoadCallback(drawChart);

                function drawChart() {

                    var data = google.visualization.arrayToDataTable([
                        ['Item', 'Count'],
                        ['Movies', json["movies"]], // Example of specifying actual and formatted values.
                        ['Music', json["music"]], // More typically this would be done using a
                        ['Places', json["places"]], // formatter.
                        ['TV Shows', json["TV"]]
                    ],
                            false);                    

                    var options = {
                        title: 'My Reviews'
                    };

                    var chart = new google.visualization.PieChart(document.getElementById('piechart'));
                    chart.draw(data, options);

                }

            }
        };
        xmlhttp.open("POST", "/irev/init", true);
        xmlhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
        xmlhttp.send(null);
    } catch (e) {
        console.log(sWhere, 'Error: ' + e.message);
    }
    return json;
};