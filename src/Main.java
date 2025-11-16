import java.io.File;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        int correctPathNum = 1;
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Введите путь к файлу: ");
            String path = scanner.nextLine();
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
        }
    }
}