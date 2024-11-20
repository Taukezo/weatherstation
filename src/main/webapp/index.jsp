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
    String hostAddress = cfgM.getHostAddress();
%>
<html>
<head>
    <title>Vertiefungsarbeit WBH - Start</title>
    <link rel="stylesheet" href="css/main.css">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <script type="text/javascript"
            src="https://www.gstatic.com/charts/loader.js"></script>
    <script>
        // Global variables
        var chartsloaded = false;
        var map;
        var data;

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
            //const url = 'http://localhost:8080/weatherstation/rest/mapsdata/stations';
            const url = '<%=hostAddress%>/rest/mapsdata/stations';
            try {
                const response = await fetch(url);
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                const jsonData = await response.json();
                await waitUntil(() => console.log('Google Charts now loaded!'));
                data = new google.visualization.DataTable(jsonData);
                const options = {
                    showTooltip: true,
                    showInfoWindow: true,
                    mapType: 'terrain',
                    useMapTypeControl: true
                };
                map = new
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

        function selectHandler() {
            console.log('A map-marker was selected');
            var selection = map.getSelection();
            for (var i = 0; i < selection.length; i++) {
                var item = selection[i];
                if (item.row != null) {
                    var stationId = data.getValue(item.row, 3);
                    console.log('Station ' + stationId + ' was selected');
                    const stationPage = '<%=hostAddress%>/station.jsp?' +
                        'stationid=' +
                        stationId;
                    window.location.assign(stationPage);
                }
            }
        }
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
    <p>Bitte wählen Sie eine Wetterstation aus!</p>
</div>
<div id="chart_div" class="mapsstyle"></div>
<!-- <div id="chart_div"></div> -->
</body>
</html>
