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
    <title>Vertiefungsarbeit WBH - Start</title>
    <script type="text/javascript"
            src="https://www.gstatic.com/charts/loader.js"></script>
    <script type="text/javascript"
            src="javascript/weatherstation.js"></script>
    <script>
        // Global variables
        var chartsloaded = false;
        var map;
        var data;
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
    </script>
</head>
<body>
<div id="header"><img src="images/wbhlogo.jpeg"
                      alt="Logo der Hochschule">
    <h1>Vertiefung Architektur und Gestaltung von Web-Anwendungen</h1></div>
Letzte Messung an der Station
'<%=JspInjectorUtil.getStationName(stationId)%>' (Id
=<%=stationId%>) <%=JspInjectorUtil.getLastMeasureDateFormatted(stationId)%>
<br/>
<br/>
<div id="tempinccomplete">
    <div id="chart_tempinc"></div>
    <img src="<%=JspInjectorUtil.getTrendImageUrl(stationId, "TempInC")%>">
</div>
<div id="tempoutccomplete">
    <div id="chart_tempoutc"></div>
    <img src="<%=JspInjectorUtil.getTrendImageUrl(stationId, "TempOutC")%>">
</div>
<div id="preassurecomplete">
    <div id="chart_preassure"></div>
    <img src="<%=JspInjectorUtil.getTrendImageUrl(stationId, "AirPressureInHpa")%>">
</div>

<form name="selectSeriesForm" onsubmit="return validateSeriesForm()">
    <label for="startTime">Von</label>
    <input type="datetime-local" id="startTime"/>
    <label for="endTime">Bis</label>
    <input type="datetime-local" id="endTime"/>
    <label for="measurementType">Wertereihe</label>
    <select id="measurementType">
        <option value="">-- Select an option --</option>
    </select>
    <button type="button" onclick="seriesFormSelected()">Aufrufen</button>
</form>
<div id="chart_div" class="full-height"></div>
</body>
</html>