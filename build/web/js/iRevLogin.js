/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

iRevLogin = {};
iRevLogin.accessToken = "";
var json;

//========================================================
//-Init the view 
//========================================================
iRevLogin.initFbLogin = function () {
    try {
        window.location.href = "https://www.facebook.com/dialog/oauth?client_id=414232525620324&redirect_uri=http://localhost:8084/irev/";

    } catch (e) {
        eplCommon.persistentLog(sWhere, 'Error: ' + e.message);
    }
};

//========================================================
//-Display the list of portals available.
//========================================================
iRevLogin.fbLogin = function () {
    var sWhere = "iRevInit.fbLogin()";
    try {
        var xmlhttp = iRevAjax.getXmlHttp();
        var params = "";
        params += "accessToken=" + iRevLogin.accessToken;
        xmlhttp.onreadystatechange = function () {
            if (xmlhttp.readyState == 4) {
                //-Check for response issues, returns JSON
                //var json = iRevCommon.chkAjaxResponse(sWhere, xmlhttp.responseText);
                json = JSON.parse(xmlhttp.responseText);
                if (json.hasOwnProperty("data")) {
                    json = json["data"];

                    var login = document.getElementById("login");
                    if (login) {
                        login.style.display = "none";
                        login.innerHTML = "";
                    }
                    var dasboard = document.getElementById("dasboard");
                    if (dasboard) {
                        dasboard.style.display = "block";
                    }

                    var mv = json["Movies"];

                    //-Bar chart

                    //sort bars based on value
                    data = [{
                            "name": "Movies",
                            "value": mv.length,
                        },
                        {
                            "name": "Music",
                            "value": json["Music"].length,
                        },
                        {
                            "name": "Places",
                            "value": json["Places"].length,
                        },
                        {
                            "name": "TV",
                            "value": json["TV"].length
                        }];
                    data = data.sort(function (a, b) {
                        return d3.ascending(a.value, b.value);
                    })

                    //-Draw bar chart
                    iRevLogin.drawPie(data);

                    var pictures = json["Places"];
                    var slidePics = document.getElementById("slidePics");
                    var firstname = json["firstname"];

                    var imgs = " <h4 style='font-weight: bold;'>" + firstname + "'s Top Picks</h4>";
                    var reviews = "<h5 style='font-weight: bold;'>Places</h5>";
                    for (i in pictures) {
                        if (pictures[i].pic != undefined) {
                            imgs += "<div class=\"w3-display-container mySlides\"><img class=\"mainSlide\" src=\"" + pictures[i].pic + "\" style=\"width:45%;\">";
                            imgs += "<div style=\"\" class=\"w3-display-topright  w3-large w3-container w3-padding-16 w3-black\"><img style=\"width:40px;\" src=\"media/green.jpg\">" + pictures[i].name + "</div></div>";
                        }                       
                        reviews += "<span style=\" font-family: Roboto;font-size: 18px;\" >" + pictures[i].name + "<img style=\"width:40px;\" src=\"media/green.jpg\"><img style=\"width:40px;opacity:0.2\" src=\"media/red.jpg\"></span><br>";                         
                    }
                    document.getElementById("profilepic").innerHTML = "<img src=\"" + json["profilepic"] + "\" style=\"width:100%\">";
                    document.getElementById("timeLine").innerHTML = reviews;

                    imgs += "<button class=\"w3-button w3-black w3-display-left\" onclick=\"iRevCommon.plusDivs(-1)\">&#10094;</button>";
                    imgs += "<button class=\"w3-button w3-black w3-display-right\" onclick=\"iRevCommon.plusDivs(1)\">&#10095;</button>";

                    slidePics.innerHTML = imgs;

                    iRevCommon.showDivs(1);
                }

            }
        };
        xmlhttp.open("POST", "/irev/fb/login", true);
        xmlhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
        xmlhttp.send(params);
    } catch (e) {
        console.log(sWhere, 'Error: ' + e.message);
    }
    return json;
};


//========================================================
//-Init the view 
//========================================================
iRevLogin.drawBar = function (data) {
    try {
        //set up svg using margin conventions - we'll need plenty of room on the left for labels
        var margin = {
            top: 15,
            right: 25,
            bottom: 15,
            left: 60
        };

        var width = 400 - margin.left - margin.right,
                height = 300 - margin.top - margin.bottom;

        var svg = d3.select("#graphic").append("svg")
                .attr("width", width + margin.left + margin.right)
                .attr("height", height + margin.top + margin.bottom)
                .append("g")
                .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

        var x = d3.scale.linear()
                .range([0, 200])
                .domain([0, d3.max(data, function (d) {
                        return d.value;
                    })]);

        var y = d3.scale.ordinal()
                .rangeRoundBands([100, 0], .1)
                .domain(data.map(function (d) {
                    return d.name;
                }));

        //make y axis to show bar names
        var yAxis = d3.svg.axis()
                .scale(y)
                //no tick marks
                .tickSize(0)
                .orient("left");

        var gy = svg.append("g")
                .attr("class", "y axis")
                .call(yAxis)

        var bars = svg.selectAll(".bar")
                .data(data)
                .enter()
                .append("g")

        //append rects
        bars.append("rect")
                .attr("class", "bar")
                .attr("y", function (d) {
                    return y(d.name);
                })
                .attr("height", y.rangeBand())
                .attr("x", 0)
                .attr("width", function (d) {
                    return x(d.value);
                });

        //add a value label to the right of each bar
        bars.append("text")
                .attr("class", "label")
                //y position of the label is halfway down the bar
                .attr("y", function (d) {
                    return y(d.name) + y.rangeBand() / 2 + 4;
                })
                //x position is 3 pixels to the right of the bar
                .attr("x", function (d) {
                    return x(d.value) + 3;
                })
                .text(function (d) {
                    return d.value;
                });

    } catch (e) {
        eplCommon.persistentLog(sWhere, 'Error: ' + e.message);
    }
};

//========================================================
//-Init the view 
//========================================================
iRevLogin.drawPie = function (data) {
//    try {
    var width = 560;
    var height = 360;
    var r = Math.min(width, height) / 2;
    var donutWidth = 75;
    var legendRectSize = 18;                                  // NEW
    var legendSpacing = 4;
    var color = d3.scaleOrdinal(d3.schemeCategory20b);

    var vis = d3.select("#graphic")
            .append("svg:svg")              //create the SVG element inside the <body>
            .data([data])                   //associate our data with the document
            .attr("width", width)           //set the width and height of our visualization (these will be attributes of the <svg> tag
            .attr("height", height)
            .append("svg:g")                //make a group to hold our pie chart
            .attr("transform", "translate(" + r + "," + r + ")")    //move the center of the pie chart from 0, 0 to radius, radius

    var arc = d3.svg.arc()              //this will create <path> elements for us using arc data
            .outerRadius(r);

    var pie = d3.layout.pie()           //this will create arc data for us given a list of values
            .value(function (d) {
                return d.value;
            });    //we must tell it out to access the value of each element in our data array


    var arcs = vis.selectAll("g.slice")     //this selects all <g> elements with class slice (there aren't any yet)
            .data(pie)                          //associate the generated pie data (an array of arcs, each having startAngle, endAngle and value properties) 
            .enter()                            //this will create <g> elements for every "extra" data element that should be associated with a selection. The result is creating a <g> for every object in the data array
            .append("svg:g")                //create a group to hold each slice (we will have a <path> and a <text> element associated with each slice)
            .attr("class", "slice");    //allow us to style things in the slices (like text)

    arcs.append("svg:path")
            .attr("fill", function (d, i) {
                return color(i);
            }) //set the color for each slice to be chosen from the color function defined above
            .attr("d", arc)                                    //this creates the actual SVG path using the associated data (pie) with the arc drawing function

            .on("click", function (d, i) {
//-change on click color
//                $(this)
//                        .attr("fill-opacity", ".8")
//                        .css({"stroke": "blue", "stroke-width": "1px"});

                iRevLogin.showList(data[i]);
                //window.location = baseURL + bucket;
                // alert("drill down to " +  data.name);
            });

    arcs.append("svg:text")                                     //add a label to each slice
            .attr("transform", function (d) {                    //set the label's origin to the center of the arc
                //we have to make sure to set these before calling arc.centroid
                d.innerRadius = 0;
                d.outerRadius = r;
                return "translate(" + arc.centroid(d) + ")";        //this gives us a pair of coordinates like [50, 50]
            })
            .attr("text-anchor", "middle")                          //center the text on it's origin
            .text(function (d, i) {
                return data[i].name;
            });        //get the label from our original data array
//
    var legend = vis.selectAll('.legend')                     // NEW
            .data(color.domain())                                   // NEW
            .enter()                                                // NEW
            .append('g')                                            // NEW
            .attr('class', 'legend')                                // NEW
            .attr('transform', function (d, i) {                     // NEW
                var height = legendRectSize + legendSpacing;          // NEW
                var offset = height * color.domain().length / 2;     // NEW
                var horz = 13 * legendRectSize;                       // NEW
                var vert = i * height - offset;                       // NEW
                return 'translate(' + horz + ',' + vert + ')';        // NEW
            });                                                     // NEW
    legend.append('rect')                                     // NEW
            .attr('width', legendRectSize)                          // NEW
            .attr('height', legendRectSize)                         // NEW
            .style('fill', color)                                   // NEW
            .style('stroke', color);                                // NEW

    legend.append('text')                                     // NEW
            .attr('x', legendRectSize + legendSpacing)              // NEW
            .attr('y', legendRectSize - legendSpacing)              // NEW
            .text(function (d,i) {
                return  data[i].name;
            });

//    } catch (e) {
//        console.log('Error: ' + e.message);
//    }
};



//========================================================
// Display list of movies,music etc
//========================================================
iRevLogin.showList = function (data) {
    var list = json[data.name];
    var reviews = "<h5 style='font-weight: bold;'>" + data.name + "</h5><br>";
    for (i in list) {
      reviews += "<span style=\" font-family: Roboto;font-size: 18px;\" >" + list[i].name + "<img style=\"width:40px;\" src=\"media/green.jpg\"><img style=\"width:40px;opacity:0.2\" src=\"media/red.jpg\"></span><br>";      
    }
    document.getElementById("timeLine").innerHTML = reviews;
};