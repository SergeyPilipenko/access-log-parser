public class UserAgent {

    private final String browser;
    private final String operatingSystem;
    private final String botName;
    private final boolean isBot;

    public UserAgent(String userAgentStr) {
        this.browser = parseBrowser(userAgentStr);
        this.operatingSystem = parseOperatingSystem(userAgentStr);
        this.botName = parseBot(userAgentStr);
        this.isBot = parseIsBot(userAgentStr);
    }

    public String getBrowser() {
        return browser;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public String getBotName() {
        return botName;
    }

    @Override
    public String toString() {
        return "UserAgent{" +
                "browser='" + browser + '\'' +
                ", operatingSystem='" + operatingSystem + '\'' +
                ", bot='" + botName + '\'' +
                '}';
    }

    public boolean isBot(){
        return isBot;
    }

    private String parseBrowser(String userAgentStr) {

        if (userAgentStr.contains("Firefox/")) return "Mozilla Firefox";
        if (userAgentStr.contains("OPR/") || userAgentStr.contains("OPT/")) return "Opera";
        if (userAgentStr.contains("Edg")) return "Microsoft Edge";
        if (userAgentStr.contains("Chrome/")) return "Google Chrome";
        if (userAgentStr.contains("Safari/")) return "Safari";

        return null;
    }

    private String parseOperatingSystem(String userAgentStr){

        if (userAgentStr.contains("Linux")) return "Linux";
        if (userAgentStr.contains("Windows")) return "Windows";
        if (userAgentStr.contains("Macintosh") || userAgentStr.contains("Mac OS")) return "Mac OS";

        return null;
    }

    private String parseBot(String userAgentStr){

        String bot = null;

        // Находим пару скобок, где открывающая скобка со словом compatible
        int openBracketIndex = userAgentStr.indexOf("(compatible");
        if (openBracketIndex == -1) return null;

        int closeBracketIndex = userAgentStr.indexOf(')', openBracketIndex);
        if (closeBracketIndex == -1) return null;

        String contentInBrackets = userAgentStr.substring(openBracketIndex + 1, closeBracketIndex);

        String[] parts = contentInBrackets.split(";");
        if (parts.length >= 2) {
            for (int i = 0; i < parts.length; i++) {
                parts[i] = parts[i].trim();
            }
            String fragment = parts[1];
            int slashIndex = fragment.indexOf('/');
            if (slashIndex == -1) return null;
            bot = fragment.substring(0, slashIndex);
        }

        return bot;
    }

    private boolean parseIsBot(String userAgentStr){
        return userAgentStr.contains("bot") || userAgentStr.contains("Bot");
    }
}