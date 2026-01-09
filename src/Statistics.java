import java.time.LocalDateTime;

public class Statistics {

    private int totalTraffic;
    private LocalDateTime minTime;
    private LocalDateTime maxTime;

    public Statistics() {
        this.totalTraffic = 0;
        this.minTime = LocalDateTime.MAX;
        this.maxTime = LocalDateTime.MIN;
    }

    public void addEntry(LogEntry logEntry) {

        totalTraffic += logEntry.getResponseSize();

        if (logEntry.getTime().isBefore(minTime)) minTime = logEntry.getTime();
        if (logEntry.getTime().isAfter(maxTime)) maxTime = logEntry.getTime();
    }

    public double getTrafficRate() {
        int divTimeHours = maxTime.getHour() - minTime.getHour();

        return (double) totalTraffic / divTimeHours;

    }
}
