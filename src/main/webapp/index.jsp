<%--
  Created by IntelliJ IDEA.
  User: Thomas Aulich
  Date: 09.10.2024
  Time: 06:46
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="org.aulich.model.Configuration" %>
<%@ page import="org.aulich.model.ConfigurationModel" %>
<%
    ConfigurationModel cfgM =
            Configuration.getConfiguration().getConfigurationModel();
    String apiKey = cfgM.getMapsApiKey();
%>
<html>
<head>
    <title>Vertiefungsarbeit WBH - Start</title>
    <script type="text/javascript"
            src="https://www.gstatic.com/charts/loader.js"></script>
    <script>
        // Global variables
        var chartsloaded = false;

        // Load relevant javascript from Google
        google.charts.load('current', {
            'packages': ['map'],
            'mapsApiKey': "<%=apiKey%>"
        });

        google.charts.setOnLoadCallback(waitForLoaded);
        fetchData();

        function waitForLoaded() {
            chartsloaded = true;
        }

        async function fetchData() {
            // URL of the JSON data
            const url = 'http://localhost:8080/weatherstation/rest/mapsdata/stations';
            try {
                const response = await fetch(url);
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                const jsonData = await response.json();
                await waitUntil(() => console.log('Google Charts now loaded!'));
                const data = new google.visualization.DataTable(jsonData);
                const options = {
                    showTooltip: true,
                    showInfoWindow: true,
                    mapType: 'terrain',
                    useMapTypeControl: true
                };
                const map = new
                google.visualization.Map(document.getElementById('chart_div'));
                map.draw(data, options);
                google.visualization.events.addListener(map, 'select',
                    selectHandler);
            } catch (error) {
                console.error('Fetch error:', error);
            }
        }

        async function waitUntil() {
            while (!chartsloaded) {
                await new Promise((resolve) => setTimeout(resolve, 100));
            }
        }

        function selectHandler(e) {
            console.log('A table row was selected');
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
<div id="chart_div" class="full-height"></div>
</body>
</html>
