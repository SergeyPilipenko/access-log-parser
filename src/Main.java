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

        int countLines = 0;

        try {

            int countGoogleBot = 0;
            int countYandexBot = 0;
            Statistics statistics = new Statistics();

            FileReader fileReader = new FileReader(path);
            BufferedReader reader =
                    new BufferedReader(fileReader);
            String line;
            while ((line = reader.readLine()) != null) {

                LogEntry logEntry = new LogEntry(line);
                statistics.addEntry(logEntry);

                countLines++;

                if (logEntry.getUserAgent() != null) {
                    String bot = logEntry.getUserAgent().getBot();
                    if (bot != null) {
                        if (bot.equals("Googlebot")) countGoogleBot++;
                        if (bot.equals("YandexBot")) countYandexBot++;
                    }
                }
            }

            System.out.println("Cредний объём трафика сайта за час:" + statistics.getTrafficRate());
            double googleBotPercent = (double) countGoogleBot * 100 / countLines;
            double yandexBotPercent = (double) countYandexBot * 100 / countLines;
            System.out.printf("Общее количество строк: %s%n", countLines);
            System.out.printf("Количество Googlebot: %s%n", countGoogleBot);
            System.out.printf("Количество YandexBot: %s%n", countYandexBot);
            System.out.printf("Доля запросов Googlebot: %f%n", googleBotPercent);
            System.out.printf("Доля запросов YandexBot: %f%n", yandexBotPercent);

        } catch (Exception ex) {
            System.err.println("Ошибка в строке файла " + countLines+1);
            ex.printStackTrace();
        }
    }
}