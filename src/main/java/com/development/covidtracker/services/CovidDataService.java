package com.development.covidtracker.services;

import com.development.covidtracker.models.LocationInfo;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class CovidDataService {

    private static final String COVID_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";

    public List<LocationInfo> getAllinfo() {
        return allinfo;
    }

    private List<LocationInfo> allinfo = new ArrayList<>();

    @PostConstruct
    @Scheduled(cron = "* * 1 * * *")
    public void getCovidData() throws IOException, InterruptedException {
        List<LocationInfo> newInfo = new ArrayList<>();
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(COVID_DATA_URL))
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        StringReader csvBodyReader = new StringReader(response.body());

        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);
        for (CSVRecord record : records) {
            LocationInfo locationInfo = new LocationInfo();
            locationInfo.setState(record.get("Province/State"));
            locationInfo.setCountry(record.get("Country/Region"));
            int latestCases = Integer.parseInt(record.get(record.size() - 1));
            int prevDayCases = Integer.parseInt(record.get(record.size() - 2));
            locationInfo.setLatestTotalCases(latestCases);
            locationInfo.setDiffFromPrevDay(prevDayCases);
            newInfo.add(locationInfo);
        }
        this.allinfo = newInfo;
    }
}
