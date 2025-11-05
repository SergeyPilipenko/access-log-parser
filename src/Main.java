import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.print("Введите первое число: ");
        int firstNumber = new Scanner(System.in).nextInt();

        System.out.print("Введите второе число: ");
        int secondNumber = new Scanner(System.in).nextInt();

        int sum = firstNumber + secondNumber;
        System.out.println("Сумма чисел: " + sum);

        int difference = firstNumber - secondNumber;
        System.out.println("Разность чисел: " + difference);

        int composition = firstNumber * secondNumber;
        System.out.println("Произведение чисел: " + composition);

        if (secondNumber == 0) {
            System.out.println("Частное чисел не посчитать, на 0 делить нельзя");
        } else {
            double quotient = (double) firstNumber / secondNumber;
            System.out.println("Частное чисел: " + quotient);
        }
    }
}