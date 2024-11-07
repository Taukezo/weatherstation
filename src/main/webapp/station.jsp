<%--
  Created by IntelliJ IDEA.
  User: Thomas Aulich
  Date: 19.10.2024
  Time: 13:39
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="org.aulich.model.Configuration" %>
<%@ page import="org.aulich.model.ConfigurationModel" %>
<%@ page import="org.aulich.utilities.JspInjectorUtil" %>
<%
    ConfigurationModel cfgM =
            Configuration.getConfiguration().getConfigurationModel();
    String apiKey = cfgM.getMapsApiKey();
    String hostAddress = cfgM.getHostAddress();
    String stationId = request.getParameter("stationid");
%>
<html>
<head>
    <title>Vertiefungsarbeit WBH - Anzeige Station <%=JspInjectorUtil.getStationName(stationId)%></title>
    <link rel="stylesheet" href="css/main.css">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <script type="text/javascript"
            src="https://www.gstatic.com/charts/loader.js"></script>
    <script type="text/javascript"
            src="javascript/weatherstation.js"></script>
    <script>
        // Global variables
        var chartsloaded = false;
        var map;
        var data;
        var options;
        const urlParams = new URLSearchParams(window.location.search);
        const stationId = urlParams.get('stationid');
        const measurementTypes =<%=JspInjectorUtil.getMeasurementTypes()%>;
        const actTempInC =<%=JspInjectorUtil.getActualValue(stationId,"TempInC")%>;
        const actTempOutC =<%=JspInjectorUtil.getActualValue(stationId,"TempOutC")%>;
        const actAirPressureInHpa =<%=JspInjectorUtil.getActualValue(stationId,"AirPressureInHpa")%>;
        const hostAddress = '<%=hostAddress%>';
        var endTime = getTimeStampValue(new Date());
        var startTime = getTimeStampValue(new Date(new Date() - 7 * 24 * 60 *
            60 *
            1000));
        var measureType = 'TempOutC';
        // Load relevant javascript from Google
        google.charts.load('current', {
            'packages': ['line', 'gauge'],
            'mapsApiKey': "<%=apiKey%>"
        });
        // Register callback, when google is ready
        google.charts.setOnLoadCallback(waitForLoaded);
        // Call async diagram data without waiting here
        fetchData();
        window.onload = function () {
            document.getElementById("startTime").value = startTime;
            document.getElementById("endTime").value = endTime;
            populateSelect();
            populateActualStatus();
        }
        window.onresize = function () {
            map = new
            google.charts.Line(document.getElementById('chart_div'));
            map.draw(data, options);
        };

    </script>
</head>
<body>
<div id="header" class="header row">
    <img class="col-3" src="images/wbhlogo.jpeg"
         alt="Logo der Hochschule">
    <div class="col-9"><h1>Vertiefung Architektur und Gestaltung von
        Web-Anwendungen</h1></div>
</div>
<div id="actualInfoline">
    <p>Station
        '<%=JspInjectorUtil.getStationName(stationId)%>'
        gemessen <%=JspInjectorUtil.getLastMeasureDateFormatted(stationId)%>
    </p>
</div>
<div id="actualInstruments" class="row">
    <div class="col-1">
        <img class="trend" src="<%=JspInjectorUtil.getTrendImageUrl(stationId,
        "TempInC")%>" alt=""/>
    </div>
    <div class="instrument col-3">
        <div id="chart_tempinc"></div>
    </div>
    <div class="col-1">
        <img class="trend"
             src="<%=JspInjectorUtil.getTrendImageUrl(stationId, "TempOutC")%>"
             alt=""/>
    </div>
    <div class="instrument col-3">
        <div id="chart_tempoutc"></div>
    </div>
    <div class="col-1">
        <img class="trend"
             src="<%=JspInjectorUtil.getTrendImageUrl(stationId, "AirPressureInHpa")%>"
             alt=""/>
    </div>
    <div class="instrument col-3">
        <div id="chart_preassure"></div>
    </div>
</div>
<form name="selectSeriesForm" class="row">
    <div class="instrument col-3">
        <label for="startTime">Von</label>
        <input type="datetime-local" id="startTime"/>
    </div>
    <div class="instrument col-3">
        <label for="endTime">Bis</label>
        <input type="datetime-local" id="endTime"/>
    </div>
    <div class="instrument col-3">
        <label for="measurementType">Wertereihe</label>
        <select id="measurementType">
            <option value="">-- Select an option --</option>
        </select>
    </div>
    <div class="instrument col-3">
        <label>Neu aufbauen</label>
        <button type="button" onclick="seriesFormSelected()">Aktualisieren
        </button>
    </div>
</form>
<div class="row">
    <div id="chart_div" class="col-12"></div>
</div>

</body>
</html>