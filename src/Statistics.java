import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Statistics {

    private int totalTraffic;
    private LocalDateTime minTime;
    private LocalDateTime maxTime;
    private HashSet existingPages;
    private HashSet notExistingPages;
    private HashMap<String, Integer> operatingSystemCounter;
    private HashMap<String, Integer> browserCounter;
    private int realUsersCounter;
    private int errorRequestsCounter;
    private HashMap<String, Integer> uniqueUsersIp;
    private HashSet refererDomainList;
    private  HashMap<LocalDateTime, Integer> usersVisitsPerSecondCounter;

    public Statistics() {
        this.totalTraffic = 0;
        this.minTime = LocalDateTime.MAX;
        this.maxTime = LocalDateTime.MIN;
        this.existingPages = new HashSet<>();
        this.notExistingPages = new HashSet();
        this.operatingSystemCounter = new HashMap<>();
        this.browserCounter = new HashMap<>();
        this.realUsersCounter = 0;
        this.errorRequestsCounter = 0;
        this.uniqueUsersIp = new HashMap<>();
        this.refererDomainList = new HashSet<>();
        this.usersVisitsPerSecondCounter = new HashMap<>();

    }

    public void addEntry(LogEntry logEntry) {

        totalTraffic += logEntry.getResponseSize();

        if (logEntry.getTime().isBefore(minTime)) minTime = logEntry.getTime();
        if (logEntry.getTime().isAfter(maxTime)) maxTime = logEntry.getTime();

        if (logEntry.getResponseCode() == 200) existingPages.add(logEntry.getPath());
        if (logEntry.getResponseCode() == 404) notExistingPages.add(logEntry.getPath());
        // Считаем кол-во ошибочных запросов
        if (logEntry.getResponseCode() >= 400) errorRequestsCounter++;

        //Список доменов Referer
        if (logEntry.getReferer() != null) {
            refererDomainList.add(parseDomainReferer(logEntry.getReferer()));
        }

        if (logEntry.getUserAgent() != null) {
            if (logEntry.getUserAgent().getOperatingSystem() != null) {
                String os = logEntry.getUserAgent().getOperatingSystem();
                if (operatingSystemCounter.containsKey(os)) {
                    operatingSystemCounter.replace(os, operatingSystemCounter.get(os) + 1);
                } else {
                    operatingSystemCounter.put(os, 1);
                }
            }

            if (logEntry.getUserAgent().getBrowser() != null) {
                String browser = logEntry.getUserAgent().getBrowser();
                if (browserCounter.containsKey(browser)) {
                    browserCounter.replace(browser, browserCounter.get(browser) + 1);
                } else {
                    browserCounter.put(browser, 1);
                }
            }

            // Считаем количество реальных пользователей и кол-во уникальных пользователей
            if (!logEntry.getUserAgent().isBot()) {
                realUsersCounter++;
                String uniqueUserIp = logEntry.getIpAddr();
                if (uniqueUsersIp.containsKey(uniqueUserIp)) {
                    uniqueUsersIp.replace(uniqueUserIp, uniqueUsersIp.get(uniqueUserIp) + 1);
                } else {
                    uniqueUsersIp.put(uniqueUserIp, 1);
                }
            }

            //Считаем нагрузку на каждую секунду времени
            if (!logEntry.getUserAgent().isBot()) {
                LocalDateTime ldm = logEntry.getTime();
                if (usersVisitsPerSecondCounter.containsKey(ldm)){
                    usersVisitsPerSecondCounter.replace(ldm,usersVisitsPerSecondCounter.get(ldm)+1);
                } else{
                    usersVisitsPerSecondCounter.put(ldm,1);
                }
            }

        }
    }

    public HashSet getRefererDomainList() {
        return refererDomainList;
    }

    public HashSet getExistingPages() {
        return existingPages;
    }

    public HashSet getNotExistingPages() {
        return notExistingPages;
    }

    public HashMap<LocalDateTime, Integer> getUsersVisitsPerSecondCounter() {
        return usersVisitsPerSecondCounter;
    }

    public HashMap<String, Integer> getOperatingSystemCounter() {
        return operatingSystemCounter;
    }

    public HashMap<String, Integer> getBrowserCounter() {
        return browserCounter;
    }

    public double getTrafficRate() {
        int divTimeHours = maxTime.getHour() - minTime.getHour();

        return (double) totalTraffic / divTimeHours;

    }

    public double getAvgRealUsersVisitsPerHour() {
        int divTimeHours = maxTime.getHour() - minTime.getHour();

        return (double) realUsersCounter / divTimeHours;
    }

    public double getAvgVisitsPerRealUser() {
        if (uniqueUsersIp.isEmpty()) {
            return 0.0;
        }
        return (double) realUsersCounter / uniqueUsersIp.size();
    }

    //Максимальное количество посещений одним юзером
    public Map.Entry<String, Integer> getMaxVisitsPerRealUser() {
        if (uniqueUsersIp.isEmpty()) return null;

        return Collections.max(uniqueUsersIp.entrySet(), Map.Entry.comparingByValue());
    }

    public double getAvgErrorRequestsPerHour() {
        int divTimeHours = maxTime.getHour() - minTime.getHour();

        return (double) divTimeHours / errorRequestsCounter;
    }

    public HashMap<String, Double> getOperatingSystemRate() {

        if (operatingSystemCounter.isEmpty()) return null;

        String[] keys = operatingSystemCounter.keySet().toArray(new String[0]);
        Integer[] values = operatingSystemCounter.values().toArray(new Integer[0]);

        int osTotalCount = 0;
        for (int value : values) {
            osTotalCount += value;
        }

        HashMap<String, Double> operatingSystemRate = new HashMap<>();
        for (int i = 0; i < keys.length; i++) {
            double part = (double) values[i] / osTotalCount;
            operatingSystemRate.put(keys[i], Math.round(part * 10000d) / 10000d);
        }

        return operatingSystemRate;
    }

    public HashMap<String, Double> getBrowserRate() {

        if (browserCounter.isEmpty()) return null;

        String[] keys = browserCounter.keySet().toArray(new String[0]);
        Integer[] values = browserCounter.values().toArray(new Integer[0]);

        int browserTotalCount = 0;
        for (int value : values) {
            browserTotalCount += value;
        }

        HashMap<String, Double> browserRate = new HashMap<>();
        for (int i = 0; i < keys.length; i++) {
            double part = (double) values[i] / browserTotalCount;
            browserRate.put(keys[i], Math.round(part * 10000d) / 10000d);
        }

        return browserRate;
    }

    private String parseDomainReferer(String referer) {
        try {
            URI uri = new URI(referer);
            return uri.getHost();
        } catch (URISyntaxException e) {
            return null;
        }
    }
}