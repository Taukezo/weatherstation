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
        const measurementTypes =<%=JspInjectorUtil.getMeasurementTypes()%>;
        var endTime = getTimeStampValue(new Date());
        var startTime = getTimeStampValue(new Date(new Date() - 7 * 24 * 60 *
            60 *
            1000));
        var measureType = 'TempOutC';

        // Load relevant javascript from Google
        google.charts.load('current', {
            'packages': ['line'],
            'mapsApiKey': "<%=apiKey%>"
        });

        google.charts.setOnLoadCallback(waitForLoaded);
        fetchData();
        window.onload = function () {
            document.getElementById("startTime").value = startTime;
            document.getElementById("endTime").value = endTime;
            populateSelect();
        }

        function waitForLoaded() {
            chartsloaded = true;
        }

        async function fetchData() {
            // URL of the JSON data
            const url = encodeURI('<%=hostAddress%>/rest/measurementdata/series?' +
                'stationid=' + stationId + '&starttime=' + startTime +
                '&endtime=' + endTime + '&measuretype=' + measureType);
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
                    hAxis: {
                        title: 'Time of day',
                        format: 'hh:mm a'
                    },
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
            element.value = getTimeStampValue(date);
        }

        function getTimeStampValue(date) {
            var isoString = date.toISOString()
            return isoString.substring(0, (isoString.indexOf("T") | 0) + 6 | 0);
        }

        function populateSelect() {
            const selectElement = document.getElementById('measurementType');
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
    <label for="measurementType">Wertereihe</label>
    <select id="measurementType">
        <option value="">-- Select an option --</option>
    </select>
    <button type="button" onclick="seriesFormSelected()">Aufrufen</button>
</form>
<script>
    async function seriesFormSelected() {
        var form = document.selectSeriesForm;
        startTime = document.getElementById('startTime').value;
        endTime = document.getElementById('endTime').value;
        measureType = document.getElementById('measurementType').value;
        if (measurementType === "") {
            alert('Wählen Sie bitte eine Wertereihe aus!');
        } else {
            console.log('Form-Values: ' + startTime + ' ' + endTime + ' ' +
                measureType);
            await fetchData();
        }
    }
</script>
<div id="chart_div" class="full-height"></div>
</body>
</html>