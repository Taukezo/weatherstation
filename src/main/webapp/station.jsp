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
%>
<html>
<head>
    <title>Vertiefungsarbeit WBH - Start</title>
    <script type="text/javascript"
            src="https://www.gstatic.com/charts/loader.js"></script>
    <script>
        // Global variables
        var chartsloaded = false;
        var map;
        var data;

        // Get stationid from url-parameter
        const urlParams = new URLSearchParams(window.location.search);
        const stationId = urlParams.get('stationid');
        const measurementTypes=<%=JspInjectorUtil.getMeasurementTypes()%>;
        console.log(measurementTypes);
        var endTime = new Date();
        var startTime = new Date(endTime - 24  * 60 * 60 *
            1000);

        // Load relevant javascript from Google
        google.charts.load('current', {
            'packages': ['line'],
            'mapsApiKey': "<%=apiKey%>"
        });

        google.charts.setOnLoadCallback(waitForLoaded);
        fetchData();
        window.onload = function () {
            setTimeStampValue(document.getElementById("startTime"), startTime);
            setTimeStampValue(document.getElementById("endTime"), endTime);
            populateSelect();
        }

        function waitForLoaded() {
            chartsloaded = true;
        }

        async function fetchData() {
            // URL of the JSON data
            const url = '<%=hostAddress%>/rest/measurementdata/series?' +
                'stationid=' + stationId + '&to=' + endTime;
            console.log(url);
            try {
                const response = await fetch(url);
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                const jsonData = await response.json();
                await waitUntil(() => console.log('Google Charts now loaded!'));
                data = new google.visualization.DataTable(jsonData);
                var options = {
                    chart: {
                        title: 'Verlauf',
                        subtitle: '... auf der Zeitachse'
                    },
                    width: 900,
                    height: 500,
                    axes: {
                        x: {
                            0: {side: 'top'}
                        }
                    }
                };
                map = new
                google.charts.Line(document.getElementById('chart_div'));
                map.draw(data, options);
            } catch (error) {
                console.error('Fetch error:', error);
            }
        }

        async function waitUntil() {
            while (!chartsloaded) {
                await new Promise((resolve) => setTimeout(resolve, 100));
            }
        }

        function setTimeStampValue(element, date) {
            var isoString = date.toISOString()
            element.value = isoString.substring(0, (isoString.indexOf("T") | 0) + 6 | 0);
        }

        function populateSelect() {
            const selectElement = document.getElementById('measurementtype');
            measurementTypes.forEach(item => {
                const option = document.createElement('option');
                option.value = item.measurementId;
                option.text = item.measurementName;
                selectElement.appendChild(option);
            });
        }

    </script>
    <style>
        html, body {
            height: 100%;
            margin: 0;
        }

        .full-height {
            height: 100%;
        }
    </style>
</head>
<body>
<form name="selectSeriesForm" onsubmit="return validateSeriesForm()">
    <label for="startTime">Von</label>
    <input type="datetime-local" id="startTime"/>
    <label for="endTime">Bis</label>
    <input type="datetime-local" id="endTime"/>
    <select id="measurementtype">
        <option value="">-- Select an option --</option>
    </select>
    <input type="submit" value="Anzeigen">
</form>
<script>
    function validateSeriesForm() {
        var form = document.selectSeriesForm;
        return false;
    }
</script>
<div id="chart_div" class="full-height"></div>
</body>
</html>