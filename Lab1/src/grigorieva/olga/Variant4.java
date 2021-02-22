package grigorieva.olga;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
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

    public static char rusFloor = 'А';
    public static char rusCeil = 'Я';
    public static char engFloor = 'A';
    public static char engCeil = 'Z';

    public static void encrypt(File file, String key, int encryptionMethod) throws IOException {
        String plaintext = inputPlaintext(file).toUpperCase(Locale.ROOT);
        key = key.toUpperCase(Locale.ROOT);

        String output = "";
        switch (encryptionMethod) {
            case 1:
                output = pleupherMethod(plaintext);
                break;
            case 2:
                output = vigenereMethod(plaintext, key);
                break;
            case 3:
                output = railFenceMethod(plaintext, key);
                break;
        }
        createFile(file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf("."))
                + "_locked.txt", output);
    }

    public static String encryptText(String plaintext, String key, int encryptionMethod) throws IOException {
        plaintext = plaintext.toUpperCase(Locale.ROOT);
        key = key.toUpperCase(Locale.ROOT);

        switch (encryptionMethod) {
            case 1:
                return pleupherMethod(plaintext);
            case 2:
                return vigenereMethod(plaintext, key);
            case 3:
                return railFenceMethod(plaintext, key);
        }
        return null;
    }

    private static void createFile(String path, String output) throws IOException {
        FileWriter fw = new FileWriter(path);
        fw.write(output);
        fw.close();
    }

    private static String inputPlaintext(File file) throws FileNotFoundException {
        StringBuilder plaintext = new StringBuilder();
        Scanner fileScanner = new Scanner(file);
        while (fileScanner.hasNextLine()) {
            plaintext.append(fileScanner.nextLine()).append("\n");
        }
        fileScanner.close();
        return plaintext.toString();
    }

    private static boolean isRus(char c) {
        return c >= 'а' && c <= 'я' || c >= 'А' && c <= 'Я';
    }

    private static boolean isEng(char c) {
        return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z';
    }

    public static void decrypt(File file, String key, int encryptionMethod) throws IOException {
        String plaintext = inputPlaintext(file).toUpperCase(Locale.ROOT);
        key = key.toUpperCase(Locale.ROOT);
        String output = "";
        switch (encryptionMethod) {
            case 1:
                output = dePleupherMethod(plaintext);
                break;
            case 2:
                output = deVigenereMethod(plaintext, key);
                break;
            case 3:
                output = deRailFenceMethod(plaintext, key);
                break;
        }
        createFile(file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf("."))
                + "_unlocked.txt", output);
    }

    public static String decryptText(String plaintext, String key, int encryptionMethod) throws IOException {
        plaintext = plaintext.toUpperCase(Locale.ROOT);
        key = key.toUpperCase(Locale.ROOT);
        switch (encryptionMethod) {
            case 1:
                return dePleupherMethod(plaintext);
            case 2:
                return deVigenereMethod(plaintext, key);
            case 3:
                return deRailFenceMethod(plaintext, key);
        }
        return null;
    }

    // ЖЕЛЕЗНОДОРОЖНЫЙ МЕТОД
    private static String railFenceMethod(String plaintext, String inputKey) { // АНГЛИЙСКИЙ
        plaintext = plaintext.replaceAll(engExcept, "").toUpperCase(Locale.ROOT);
        int key = Integer.parseInt(inputKey);

        StringBuilder answer = new StringBuilder();
        int index = 0;
        while (index < plaintext.length()) {
            char textChar = plaintext.charAt(index);
            if (isEng(textChar)) {
                answer.append(textChar);
            }
            index = index + key * 2 - 2;
        }
        index = 1;
        int level = 1;
        while (level < key - 1) {
            char textChar = plaintext.charAt(index);
            if (isEng(textChar)) {
                answer.append(textChar);
            }
            index = index + (key - level) * 2 - 2;
            if (index >= plaintext.length()) {
                index = ++level;
            } else {
                textChar = plaintext.charAt(index);
                if (isEng(textChar)) {
                    answer.append(textChar);
                }
                index = index + level * 2;
                if (index >= plaintext.length()) {
                    index = ++level;
                }
            }
        }

        while (index < plaintext.length()) {
            char textChar = plaintext.charAt(index);
            if (isEng(textChar)) {
                answer.append(textChar);
            }
            index = index + key * 2 - 2;
        }

        if(answer.length() == 0) return plaintext;
        return answer.toString();
/*
это лабораторная работа по киоки
4

Lorem ipsum dolor sit amet
crypto

*/
    }

    private static String deRailFenceMethod(String cypherText, String inputKey) {
        cypherText = cypherText.toUpperCase(Locale.ROOT).trim();
        int key = Integer.parseInt(inputKey);

        char[] answer = new char[cypherText.length()];

        int index = 0;
        int i = 0;
        while (index < cypherText.length()) {
            answer[index] = cypherText.charAt(i++);
            index = index + key * 2 - 2;
        }
        int level = 1;
        index = 1;
        while (level < key - 1) {
            char textChar = cypherText.charAt(i++);
            if (isEng(textChar)) {
                answer[index] = textChar;
            }
            index = index + (key - level) * 2 - 2;
            if (index >= cypherText.length()) {
                index = ++level;
            } else {
                textChar = cypherText.charAt(i++);
                if (isEng(textChar)) {
                    answer[index] = textChar;
                }
                index = index + level * 2;
                if (index >= cypherText.length()) {
                    index = ++level;
                }
            }
        }

        while (index < cypherText.length()) {
            answer[index] = cypherText.charAt(i++);
            index = index + key * 2 - 2;
        }

        StringBuilder str = new StringBuilder();
        for (char c : answer) {
            str.append(c);
        }

        if(str.length() == 0) return cypherText;
        return str.toString();
    }

    // МЕТОД ПЛЕЙФЕРА
    static class Coords {
        int row, col;

        Coords(int row, int col) {
            this.row = row;
            this.col = col;
        }
    }

    private static String pleupherMethod(String plaintext) { // АНГЛИЙСКИЙ
        plaintext = plaintext.toUpperCase(Locale.ROOT).replaceAll(engExcept, "");

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
            char first;
            char second;

            first = plaintext.charAt(i);
            if (i + 1 == plaintext.length()) {
                second = 'X';
            } else {
                second = plaintext.charAt(i + 1);
            }

            if (first == second) {
                second = 'X';
                i--;
            }
            Coords cFirst = findCoords(table, first);
            Coords cSecond = findCoords(table, second);

            if (cFirst.row == cSecond.row) {
                answer.append(table[cFirst.row][(cFirst.col + 1) % colCount].charAt(0))
                        .append(table[cSecond.row][(cSecond.col + 1) % colCount].charAt(0));
                continue;
            }
            if (cFirst.col == cSecond.col) {
                answer.append(table[(cFirst.row + 1) % rowCount][cFirst.col].charAt(0))
                        .append(table[(cSecond.row + 1) % rowCount][cSecond.col].charAt(0));
                continue;
            }

            answer.append(table[cFirst.row][cSecond.col].charAt(0)).append(table[cSecond.row][cFirst.col].charAt(0));
        }

        if(answer.length() == 0) return plaintext;
        return answer.toString();
/*
Ciphertext

 */
    }

    private static String dePleupherMethod(String plaintext) {
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
            char first;
            char second;

            first = plaintext.charAt(i);
            if (i + 1 == plaintext.length()) {
                second = 'X';
            } else {
                second = plaintext.charAt(i + 1);
            }

            if (first == second) {
                second = 'X';
                i--;
            }
            Coords cFirst = findCoords(table, first);
            Coords cSecond = findCoords(table, second);

            if (cFirst.row == cSecond.row) {
                answer.append(table[cFirst.row][(cFirst.col - 1 + colCount) % colCount].charAt(0))
                        .append(table[cSecond.row][(cSecond.col - 1 + colCount) % colCount].charAt(0));
                continue;
            }
            if (cFirst.col == cSecond.col) {
                answer.append(table[(cFirst.row - 1 + rowCount) % rowCount][cFirst.col].charAt(0))
                        .append(table[(cSecond.row - 1 + rowCount) % rowCount][cSecond.col].charAt(0));
                continue;
            }

            answer.append(table[cFirst.row][cSecond.col].charAt(0)).append(table[cSecond.row][cFirst.col].charAt(0));
        }

        if(answer.length() == 0) return plaintext;
        return answer.toString();
    }

    // МЕТОД ВИЖЕНЕРА
    public static String vigenereMethod(String plaintext, String inputKey) { // РУССКИЙ
        StringBuilder key = new StringBuilder(inputKey.replaceAll(rusExcept, "").toUpperCase(Locale.ROOT));
        plaintext = plaintext.toUpperCase(Locale.ROOT).replaceAll(rusExcept, "");
        StringBuilder answer = new StringBuilder();

        int keyIndex = 0;
        for (int i = 0; i < plaintext.length(); i++) {
            char toAnswer = plaintext.charAt(i);
            char keyChar = key.charAt(keyIndex);
            toAnswer = (char) ((keyChar + toAnswer - 2 * rusFloor) % rusCount + rusFloor);
            key.setCharAt(keyIndex, (char) ((keyChar + 1 - rusFloor) % rusCount + rusFloor));
            keyIndex = (keyIndex + 1) % key.length();
            answer.append(toAnswer);
        }

        if(answer.length() == 0) return plaintext;
        return answer.toString();
/*
CRYPTOGRAPHY AND DATA SECURITY
mouse

*/
    }

    private static String deVigenereMethod(String plaintext, String inputKey) {
        StringBuilder key = new StringBuilder(inputKey.replaceAll(rusExcept, "").toUpperCase(Locale.ROOT));
        plaintext = plaintext.toUpperCase(Locale.ROOT);
        StringBuilder answer = new StringBuilder();

        int keyIndex = 0;
        for (int i = 0; i < plaintext.length(); i++) {
            char toAnswer = plaintext.charAt(i);
            char keyChar = key.charAt(keyIndex);
            toAnswer = (char) ((toAnswer - keyChar + rusCount) % rusCount + rusFloor);
            key.setCharAt(keyIndex, (char) ((keyChar + 1 - rusFloor) % rusCount + rusFloor));
            keyIndex = (keyIndex + 1) % key.length();
            answer.append(toAnswer);
        }

        if(answer.length() == 0) return plaintext;
        return answer.toString();
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
