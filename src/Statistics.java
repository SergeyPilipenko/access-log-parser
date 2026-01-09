import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;

public class Statistics {

    private int totalTraffic;
    private LocalDateTime minTime;
    private LocalDateTime maxTime;
    private HashSet existingPages;
    private HashSet notExistingPages;
    private HashMap<String, Integer> operatingSystemCounter;
    private HashMap<String, Integer> browserCounter;

    public Statistics() {
        this.totalTraffic = 0;
        this.minTime = LocalDateTime.MAX;
        this.maxTime = LocalDateTime.MIN;
        this.existingPages = new HashSet<>();
        this.notExistingPages = new HashSet();
        this.operatingSystemCounter = new HashMap<>();
        this.browserCounter = new HashMap<>();
    }

    public void addEntry(LogEntry logEntry) {

        totalTraffic += logEntry.getResponseSize();

        if (logEntry.getTime().isBefore(minTime)) minTime = logEntry.getTime();
        if (logEntry.getTime().isAfter(maxTime)) maxTime = logEntry.getTime();

        if (logEntry.getResponseCode() == 200) existingPages.add(logEntry.getPath());
        if (logEntry.getResponseCode() == 404) notExistingPages.add(logEntry.getPath());

        if (logEntry.getUserAgent() != null) {
            if (logEntry.getUserAgent().getOperatingSystem() != null) {
                String os = logEntry.getUserAgent().getOperatingSystem();
                if (operatingSystemCounter.containsKey(os)) {
                    operatingSystemCounter.replace(os, operatingSystemCounter.get(os) + 1);
                } else {
                    operatingSystemCounter.put(os, 0);
                }
            }

            if (logEntry.getUserAgent().getBrowser() != null) {
                String browser = logEntry.getUserAgent().getBrowser();
                if (browserCounter.containsKey(browser)) {
                    browserCounter.replace(browser, browserCounter.get(browser) + 1);
                } else {
                    browserCounter.put(browser, 0);
                }
            }
        }
    }

    public HashSet getExistingPages() {
        return existingPages;
    }

    public HashSet getNotExistingPages() {
        return notExistingPages;
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

}
