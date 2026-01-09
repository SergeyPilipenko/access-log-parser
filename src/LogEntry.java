import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class LogEntry {

    private final int LINE_LENGHT_LIMIT = 1024;
    private String logLineTmp;

    private final String ipAddr;
    private final LocalDateTime time;
    private final HttpMethod method;
    private final String path;
    private final int responseCode;
    private final int responseSize;
    private final String referer;
    private final UserAgent userAgent;


    public LogEntry(String logLine) throws AccessLogParserException {
        if (logLine.length() > LINE_LENGHT_LIMIT) throw new
                AccessLogParserException(String.format("Длина строки больше %s символов. Длина строки: %s символа",
                LINE_LENGHT_LIMIT, logLine.length()));

        this.logLineTmp = logLine;
        this.ipAddr = parseIpAddr(logLineTmp);
        this.time = parseTime(logLineTmp);
        this.method = parseMethod(logLineTmp);
        this.path = parsePath(logLineTmp);
        this.responseCode = parseResponseCode(logLineTmp);
        this.responseSize = parseResponseSize(logLineTmp);
        this.referer = parseReferer(logLineTmp);
        this.userAgent = parseUserAgent(logLineTmp);
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public HttpMethod getMethod() {
        return method;
    }

    //
    public String getPath() {
        return path;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public int getResponseSize() {
        return responseSize;
    }

    public String getReferer() {
        return referer;
    }

    public UserAgent getUserAgent() {
        return userAgent;
    }

    private String parseIpAddr(String logLine) {
        String ipAddr = logLine.substring(0, logLine.indexOf(' '));
        removeParamFromLogLineTmp(ipAddr);

        return ipAddr;
    }

    private LocalDateTime parseTime(String logLine) {
        String dateTimeStr = logLine.substring(logLine.indexOf('[') + 1,
                logLine.indexOf(']'));

        removeParamFromLogLineTmp("[" + dateTimeStr + "]");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);

        return LocalDateTime.parse(dateTimeStr, formatter);
    }

    private String parseUrlStr(String logLine) throws AccessLogParserException {
        String urlStr;

        int firstQuote = logLine.indexOf('"');
        int secondQuote = logLine.indexOf('"', firstQuote + 1);

        if (firstQuote==-1 || secondQuote == -1) throw new
                AccessLogParserException("Ошибка парсинга: неполная информация об url метода");
        urlStr = logLine.substring(firstQuote + 1, secondQuote);

        String[] urlStrArr = urlStr.split(" ");
        if (urlStrArr.length<2) throw new
                AccessLogParserException("Ошибка парсинга: неполная информация об url метода");

        return urlStr;
    }

    private HttpMethod parseMethod(String logLine) throws AccessLogParserException {
        String[] url = parseUrlStr(logLine).split(" ");
        return HttpMethod.valueOf(url[0]);
    }

    private String parsePath(String logLine) throws AccessLogParserException {
        String[] url = parseUrlStr(logLine).split(" ");

        removeParamFromLogLineTmp('"' + parseUrlStr(logLine) + '"');
        return url[1];
    }

    private int parseResponseCode(String logLine) {

        String responseCode = logLine.substring(0, logLine.indexOf(' '));
        removeParamFromLogLineTmp(responseCode);

        return Integer.parseInt(responseCode);
    }

    private int parseResponseSize(String logLine) {
        String responseSize = logLine.substring(0, logLine.indexOf(' '));
        removeParamFromLogLineTmp(responseSize);

        return Integer.parseInt(responseSize);
    }

    private String parseReferer(String logLine) {
        int indexFirstQuote = logLine.indexOf('"');
        String referer = logLine.substring(indexFirstQuote + 1,
                logLine.indexOf('"', indexFirstQuote + 1));

        removeParamFromLogLineTmp('"' + referer + '"');
        if (referer.equals("-")) return null;

        return referer;
    }

    private UserAgent parseUserAgent(String logLine) {

        if (logLine.isEmpty() || logLine.equals("-")) return null;

        UserAgent userAgent = new UserAgent(logLine);

        return userAgent;
    }

    private void removeParamFromLogLineTmp(String param) {
        String substring = logLineTmp.substring(logLineTmp.indexOf(param));
        logLineTmp = substring.replace(param, "").trim();
    }

    @Override
    public String toString() {
        return "LogEntry{" +
                "ipAddr='" + ipAddr + '\'' +
                ", time=" + time +
                ", method=" + method +
                ", path='" + path + '\'' +
                ", responseCode=" + responseCode +
                ", responseSize=" + responseSize +
                ", referer='" + referer + '\'' +
                ", userAgent=" + userAgent +
                '}';
    }
}