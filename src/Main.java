import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        int correctPathNum = 1;
        Scanner scanner = new Scanner(System.in);
        String path;

        while (true) {
            System.out.print("Введите путь к файлу: ");
            path = scanner.nextLine();
            File file = new File(path);
            boolean fileExists = file.exists();
            boolean isDirectory = file.isDirectory();

            if (!fileExists) {
                System.out.println("Указан путь к несуществующему файлу");
                continue;
            }

            if (isDirectory) {
                System.out.println("Указан путь к папке, а не к файлу");
                continue;
            }

            System.out.println("Путь указан верно. Это файл номер " + correctPathNum++);
            scanner.close();
            break;
        }

        try {
            int countLines = 0;
            int countGoogleBot = 0;
            int countYandexBot = 0;

            FileReader fileReader = new FileReader(path);
            BufferedReader reader =
                    new BufferedReader(fileReader);
            String line;
            while ((line = reader.readLine()) != null) {
                int length = line.length();
                validateLineLength(length);

                countLines++;
                String bot = findBotInUserAgent(line);
                if (bot != null) {
                    if (bot.equals("Googlebot")) countGoogleBot++;
                    if (bot.equals("YandexBot")) countYandexBot++;
                }

            }

            double googleBotPercent = (double) countGoogleBot * 100 / countLines;
            double yandexBotPercent = (double) countYandexBot * 100 / countLines;
            System.out.printf("Общее количество строк: %s%n", countLines);
            System.out.printf("Количество Googlebot: %s%n", countGoogleBot);
            System.out.printf("Количество YandexBot: %s%n", countYandexBot);
            System.out.printf("Доля запросов Googlebot: %f%n", googleBotPercent);
            System.out.printf("Доля запросов YandexBot: %f%n", yandexBotPercent);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void validateLineLength(int lineLength) throws AccessLogParserException {
        final int LINE_LENGHT_LIMIT = 1024;

        if (lineLength > LINE_LENGHT_LIMIT) throw new
                AccessLogParserException(String.format("Длина строки больше %s символов. Длина строки: %s символа", LINE_LENGHT_LIMIT, lineLength));
    }

    public static String findUserAgent(String line) {
        //Находим последний набор двойных кавычек, считаем его инфой UserAgent
        int lastQuoteIndex = line.lastIndexOf('"');
        if (lastQuoteIndex == -1) return null;

        int preLastQuoteIndex = line.lastIndexOf('"', lastQuoteIndex - 1);
        if (preLastQuoteIndex == -1) return null;

        return line.substring(preLastQuoteIndex + 1, lastQuoteIndex);
    }

    public static String findBotInUserAgent(String line) {

        String userAgent = findUserAgent(line);
        if (userAgent == null || userAgent.equals("-")) return null;

        String bot = "None";
        // Находим пару скобок, где открывающая скобка со словом compatible
        int openBracketIndex = userAgent.indexOf("(compatible");
        if (openBracketIndex == -1) return null;

        int closeBracketIndex = userAgent.indexOf(')', openBracketIndex);
        if (closeBracketIndex == -1) return null;

        String contentInBrackets = userAgent.substring(openBracketIndex + 1, closeBracketIndex);

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
}