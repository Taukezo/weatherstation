function waitForLoaded() {
    chartsloaded = true;
}

async function fetchData() {
    // URL of the JSON data
    const url =
        encodeURI(hostAddress + '/rest/measurementdata/series?' +
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
        options = {
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
    selectElement.value = measureType;
}

function populateActualStatus() {
    var data0 = google.visualization.arrayToDataTable([
        ['Label', 'Value'],
        ['Innen °C', actTempInC],
]);
    var options0 = {
        redFrom: 35, redTo: 45,
        yellowFrom: 25, yellowTo: 35,
        greenColor: '#58ACFA',
        greenFrom: -15, greenTo: 0,
        max: 45,
        min: -15,
        minorTicks: 2
    };
    var chart0 = new
    google.visualization.Gauge(document.getElementById('chart_tempinc'));
    chart0.draw(data0, options0);

    var data1 = google.visualization.arrayToDataTable([
        ['Label', 'Value'],
        ['Aussen °C',
            actTempOutC],
]);
    var chart1 = new
    google.visualization.Gauge(document.getElementById('chart_tempoutc'));
    chart1.draw(data1, options0);

    var data2 = google.visualization.arrayToDataTable([
        ['Label', 'Value'],
        ['Luftdruck hpa', actAirPressureInHpa],
]);
    var options2 = {
        max: 1100,
        min: 300,
        minorTicks: 50
    };
    var chart2 = new
    google.visualization.Gauge(document.getElementById('chart_preassure'));
    chart2.draw(data2, options2);
}

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