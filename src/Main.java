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
            int minLineLength = Integer.MAX_VALUE;
            int maxLineLength = Integer.MIN_VALUE;

            FileReader fileReader = new FileReader(path);
            BufferedReader reader =
                    new BufferedReader(fileReader);
            String line;
            while ((line = reader.readLine()) != null) {
                int length = line.length();
                validateLineLength(length);

                countLines++;
                if (length < minLineLength) minLineLength = length;
                if (length > maxLineLength) maxLineLength = length;
            }

            System.out.printf("Общее количество строк: %s%n", countLines);
            System.out.printf("Минимальная длина строки: %s символа%n", minLineLength);
            System.out.printf("Максимальнмя длина строки: %s символа%n", maxLineLength);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void validateLineLength(int lineLength) throws AccessLogParserException {
        final int LINE_LENGHT_LIMIT = 1024;

        if (lineLength > LINE_LENGHT_LIMIT) throw new
                AccessLogParserException(String.format("Длина строки больше %s символов. Длина строки: %s символа", LINE_LENGHT_LIMIT, lineLength));
    }
}