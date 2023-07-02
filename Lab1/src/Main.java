import java.util.Locale;
import java.util.Scanner;

public class Main {

    public static Scanner in = new Scanner(System.in);

    public static String rusExcept = "[^А-Яа-я]";
    public static String engExcept = "[^A-Za-z]";

    public static int rusShift = Math.abs('а' - 'А');
    public static int engShift = Math.abs('a' - 'A');

    public static int rusCount = 'я' - 'а' + 1;
    public static int engCount = 'z' - 'a' + 1;

    public static char rusFloor = 'а';
    public static char rusCeil = 'я';
    public static char engFloor = 'a';
    public static char engCeil = 'z';

    public static void main(String[] args) {
        Variant4.main(null);
        System.out.println("Enter plaintext:");
        String plaintext = in.nextLine();
        printVigenereMethod(plaintext);
        printDecimacyMethod(plaintext);
        printColumnMethod(plaintext);
    }

    private static boolean isRus(char c) {
        return c >= 'а' && c <= 'я' || c >= 'А' && c <= 'Я';
    }

    private static boolean isEng(char c) {
        return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z';
    }

    // МЕТОД ВИЖЕНЕРА
    public static void printVigenereMethod(String plaintext) {
        System.out.println("Enter key:");
        StringBuilder key = new StringBuilder(in.nextLine().replaceAll(rusExcept, ""));
        plaintext = plaintext.toLowerCase(Locale.ROOT);
        StringBuilder answer = new StringBuilder();

        int keyIndex = 0;
        for(int i = 0; i < plaintext.length(); i++) {
            char toAnswer = plaintext.charAt(i);
            if(isEng(toAnswer)) {
                char keyChar = key.charAt(keyIndex);
                toAnswer = (char) ((keyChar + toAnswer - 2 * rusFloor) % rusCount + rusFloor);
                key.setCharAt(keyIndex, (char) ((keyChar + 1 - rusFloor) % rusCount + rusFloor));
                keyIndex = (keyIndex + 1) % key.length();
            }
            answer.append(toAnswer);
        }
        System.out.println(answer);
/*
CRYPTOGRAPHY AND DATA SECURITY
mouse

*/
    }

    // МЕТОД ДЕЦИМАЦИЯ
    public static int gcd(int a, int b) {
        while (b != 0) {
            int tmp = a % b;
            a = b;
            b = tmp;
        }
        return a;
    }

    private static void printDecimacyMethod(String plaintext) {
        System.out.println("Enter key:");
        String key = in.nextLine().replaceAll(engExcept, "");
        plaintext = plaintext.toLowerCase(Locale.ROOT);
        StringBuilder answer = new StringBuilder();
        for (int i = 0; i < plaintext.length(); i++) {
            char keyChar = key.charAt(i % key.length());
            char textChar = plaintext.charAt(i);
            if (textChar >= 'a' && textChar <= 'z') {
                if (gcd(keyChar, engCeil - engFloor) == 1 && isEng(textChar)) {
                    answer.append((char) (textChar * keyChar % (engCeil - engFloor) + 'a'));
                } else {
                    answer.append(textChar);
                }
            }
        }
        System.out.println("Answer:" + answer);
/*
Loremipsumdolorsitamet
crypto

 */
    }

    // СТОЛБЦОВЫЙ МЕТОД
    private static void printColumnMethod(String plaintext) {
        System.out.println("Enter key:");
        String key = in.nextLine().replaceAll(engExcept, "");
        StringBuilder answer = new StringBuilder();
        for (int i = engFloor; i <= engCeil; i++) {
            for (int j = 0; j < key.length(); j++) {
                if (key.charAt(j) == i || key.charAt(j) == i + engShift) {
                    for (int k = 0; k < Math.ceil((float) plaintext.length() / key.length()); k++) {
                        int index = j + k * key.length();
                        if (index < plaintext.length())
                            answer.append(plaintext.charAt(index));
                    }
                }
            }
        }
/*
перваялабораторнаяработапокиоки
криптография

Lorem ipsum dolor sit amet
crypto

*/
        System.out.println("Answer: " + answer);
    }
}
