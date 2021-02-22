package sample;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;
import java.util.Scanner;

public class Variant4 {
    public static String rusExcept = "[^А-Яа-я]";
    public static String engExcept = "[^A-Za-z]";

    private static boolean isInvalidKey(String key, String alphabet) {
        for(int i = 0; i < key.length(); i++) {
            if(!alphabet.contains(key.charAt(i) + "")) return true;
        }
        return false;
    }

    public static void encrypt(File file, String key, int encryptionMethod) throws Exception {
        String plaintext = inputPlaintext(file).toUpperCase(Locale.ROOT);
        key = key.toUpperCase(Locale.ROOT);

        String output = "";
        switch (encryptionMethod) {
            case 1:
                if(isInvalidKey(key, rusAlphabet)) throw new Exception("Key must contain only russian symbols.");
                output = vigenereMethod(plaintext, key);
                break;
            case 2:
                output = pleupherMethod(plaintext);
                break;
            case 3:
                if(isInvalidKey(key, "0123456789")) throw new Exception("Key must contain only digits.");
                output = railFenceMethod(plaintext, key);
                break;
        }
        createFile(file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf("."))
                + "_locked.txt", output);
    }

    public static String encryptText(String plaintext, String key, int encryptionMethod) throws Exception {
        plaintext = plaintext.toUpperCase(Locale.ROOT);
        key = key.toUpperCase(Locale.ROOT);

        switch (encryptionMethod) {
            case 1:
                if(isInvalidKey(key, rusAlphabet)) throw new Exception("Key must contain only russian symbols.");
                return vigenereMethod(plaintext, key);
            case 2:
                return pleupherMethod(plaintext);
            case 3:
                if(isInvalidKey(key, "0123456789")) throw new Exception("Key must contain only digits.");
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

    public static void decrypt(File file, String key, int encryptionMethod) throws Exception {
        String plaintext = inputPlaintext(file).toUpperCase(Locale.ROOT);
        key = key.toUpperCase(Locale.ROOT);
        String output = "";
        switch (encryptionMethod) {
            case 1:
                if(isInvalidKey(key, rusAlphabet)) throw new Exception("Key must contain only russian symbols.");
                output = deVigenereMethod(plaintext, key);
                break;
            case 2:
                output = dePleupherMethod(plaintext);
                break;
            case 3:
                if(isInvalidKey(key, "0123456789")) throw new Exception("Key must contain only digits.");
                output = deRailFenceMethod(plaintext, key);
                break;
        }
        createFile(file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf("."))
                + "_unlocked.txt", output);
    }

    public static String decryptText(String plaintext, String key, int encryptionMethod) throws Exception {
        plaintext = plaintext.toUpperCase(Locale.ROOT);
        key = key.toUpperCase(Locale.ROOT);
        switch (encryptionMethod) {
            case 1:
                if(isInvalidKey(key, rusAlphabet)) throw new Exception("Key must contain only russian symbols.");
                return deVigenereMethod(plaintext, key);
            case 2:
                return dePleupherMethod(plaintext);
            case 3:
                if(isInvalidKey(key, "0123456789")) throw new Exception("Key must contain only digits.");
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
            answer.append(textChar);
            index = index + key * 2 - 2;
        }
        index = 1;
        int level = 1;
        while (level < key - 1) {
            char textChar = plaintext.charAt(index);
            answer.append(textChar);
            index = index + (key - level) * 2 - 2;
            if (index >= plaintext.length()) {
                index = ++level;
            } else {
                textChar = plaintext.charAt(index);
                answer.append(textChar);
                index = index + level * 2;
                if (index >= plaintext.length()) {
                    index = ++level;
                }
            }
        }

        while (index < plaintext.length()) {
            char textChar = plaintext.charAt(index);
            answer.append(textChar);
            index = index + key * 2 - 2;
        }

        if (answer.length() == 0) return plaintext;
        return answer.toString();
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
            answer[index] = textChar;
            index = index + (key - level) * 2 - 2;
            if (index >= cypherText.length()) {
                index = ++level;
            } else {
                textChar = cypherText.charAt(i++);
                answer[index] = textChar;
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

        if (str.length() == 0) return cypherText;
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

    private static String pleupherMethod(String plaintext) throws Exception { // АНГЛИЙСКИЙ
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

            if(cFirst == null || cSecond == null) throw new Exception("Invalid symbols.");

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

        if (answer.length() == 0) return plaintext;
        return answer.toString();
    }

    private static String dePleupherMethod(String plaintext) throws Exception {
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

            if (cFirst == null || cSecond == null) throw new Exception("Invalid symbols.");

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

        if (answer.length() == 0) return plaintext;
        return answer.toString();
    }

    public static String rusAlphabet = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ";

    // МЕТОД ВИЖЕНЕРА
    public static String vigenereMethod(String plaintext, String inputKey) { // РУССКИЙ
        StringBuilder key = new StringBuilder(inputKey.replaceAll(rusExcept, "").toUpperCase(Locale.ROOT));
        plaintext = plaintext.toUpperCase(Locale.ROOT).replaceAll(rusExcept, "");
        StringBuilder answer = new StringBuilder();

        int keyIndex = 0;
        for (int i = 0; i < plaintext.length(); i++) {
            char toAnswer = plaintext.charAt(i);
            char keyChar = key.charAt(keyIndex);
            toAnswer = rusAlphabet.charAt((rusAlphabet.indexOf(keyChar) + rusAlphabet.indexOf(toAnswer))
                    % rusAlphabet.length());
            key.setCharAt(keyIndex, rusAlphabet.charAt((rusAlphabet.indexOf(keyChar) + 1) % rusAlphabet.length()));
            keyIndex = (keyIndex + 1) % key.length();
            answer.append(toAnswer);
        }

        if (answer.length() == 0) return plaintext;
        return answer.toString();
    }

    private static String deVigenereMethod(String plaintext, String inputKey) {
        StringBuilder key = new StringBuilder(inputKey.replaceAll(rusExcept, "").toUpperCase(Locale.ROOT));
        plaintext = plaintext.toUpperCase(Locale.ROOT);
        StringBuilder answer = new StringBuilder();

        int keyIndex = 0;
        for (int i = 0; i < plaintext.length(); i++) {
            char toAnswer = plaintext.charAt(i);
            char keyChar = key.charAt(keyIndex);
            toAnswer = rusAlphabet.charAt((rusAlphabet.indexOf(toAnswer) - rusAlphabet.indexOf(keyChar)
                    + rusAlphabet.length()) % rusAlphabet.length());
            key.setCharAt(keyIndex, rusAlphabet.charAt((rusAlphabet.indexOf(keyChar) + 1) % rusAlphabet.length()));
            keyIndex = (keyIndex + 1) % key.length();
            answer.append(toAnswer);
        }

        if (answer.length() == 0) return plaintext;
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
