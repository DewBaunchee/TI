import java.util.Locale;
import java.util.Scanner;

public class Variant4 {
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
        System.out.println("Enter plaintext:");
        String plaintext = in.nextLine();
        rollingGrid(plaintext.substring(0, 16));
        printPleupherMethod(plaintext);
        printRailFenceMethod(plaintext);
        printVigenereMethod(plaintext);
    }

    private static boolean isRus(char c) {
        return c >= 'а' && c <= 'я' || c >= 'А' && c <= 'Я';
    }

    private static boolean isEng(char c) {
        return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z';
    }

    // СТОЛБЦОВЫЙ МЕТОД
    private static void printRailFenceMethod(String plaintext) {
        System.out.println("It's rail fence.");
        if (plaintext.matches(engExcept + "| ")) {
            System.out.println("Invalid plaintext.");
        }
        plaintext = plaintext.replace(" ", "").toUpperCase(Locale.ROOT);
        System.out.println("Enter key:");
        int key = Integer.parseInt(in.nextLine());

        StringBuilder answer = new StringBuilder();
        int level = 0;
        int index = 0;
        while (index < plaintext.length()) {
            answer.append(plaintext.charAt(index));
            index = index + key * 2 - 2;
        }
        index = 1;
        level = 1;
        while (level < key - 1) {
            answer.append(plaintext.charAt(index));
            index = index + (key - level) * 2 - 2;
            if (index >= plaintext.length()) {
                index = ++level;
            } else {
                answer.append(plaintext.charAt(index));
                index = index + level * 2;
                if (index >= plaintext.length()) {
                    index = ++level;
                }
            }
        }

        while (index < plaintext.length()) {
            answer.append(plaintext.charAt(index));
            index = index + key * 2 - 2;
        }
        System.out.println("Answer: " + answer);
/*
это лабораторная работа по киоки
4

Lorem ipsum dolor sit amet
crypto

*/
    }

    // МЕТОД ВИЖЕНЕРА
    public static void printVigenereMethod(String plaintext) {
        System.out.println("Enter key:");
        StringBuilder key = new StringBuilder(in.nextLine().replaceAll(rusExcept, ""));
        plaintext = plaintext.toLowerCase(Locale.ROOT);
        StringBuilder answer = new StringBuilder();

        int keyIndex = 0;
        for (int i = 0; i < plaintext.length(); i++) {
            char toAnswer = plaintext.charAt(i);
            if (isEng(toAnswer)) {
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

    // МЕТОД ПЛЕЙФЕРА
    static class Coords {
        int row, col;

        Coords(int row, int col) {
            this.row = row;
            this.col = col;
        }
    }

    private static void printPleupherMethod(String plaintext) {
        System.out.println("It's pleupher method.");
        if (plaintext.matches(engExcept + "| ")) {
            System.out.println("Invalid plaintext.");
        }
        plaintext = plaintext.replace(" ", "").toUpperCase(Locale.ROOT);

        String[][] table = {
                {"C", "R", "Y", "P", "T"},
                {"O", "G", "A", "H", "B"},
                {"D", "E", "F", "IJ", "K"},
                {"L", "M", "N", "Q", "S"},
                {"U", "V", "W", "X", "Z"}
        };
        int colCount = table[0].length;
        int rowCount = table.length;
        StringBuilder answer = new StringBuilder();

        for (int i = 0; i < plaintext.length(); i += 2) {
            char first = plaintext.charAt(i);
            char second;
            if (i + 1 == plaintext.length()) {
                second = (char) ((first + 1) % engCeil + engFloor);
            } else {
                second = plaintext.charAt(i + 1);
            }

            if (first == second) {
                second = (char) ((first + 1) % engCeil + engFloor);
                i--;
            }
            Coords cFirst = findCoords(table, first);
            Coords cSecond = findCoords(table, second);

            if (cFirst.row == cSecond.row) {
                answer.append(table[cFirst.row][(cFirst.col + 1) % colCount])
                        .append(table[cSecond.row][(cSecond.col + 1) % colCount]);
                continue;
            }
            if (cFirst.col == cSecond.col) {
                answer.append(table[(cFirst.row + 1) % rowCount][cFirst.col])
                        .append(table[(cSecond.row + 1) % rowCount][cSecond.col]);
                continue;
            }

            answer.append(table[cFirst.row][cSecond.col]).append(table[cSecond.row][cFirst.col]);
        }

        System.out.println("Answer:" + answer);
/*
Ciphertext

 */
    }

    private static Coords findCoords(String[][] table, char first) {
        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table[0].length; j++) {
                if (table[i][j].contains(first + "")) {
                    return new Coords(i, j);
                }
            }
        }
        return null;
    }
}
