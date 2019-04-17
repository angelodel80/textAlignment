package lico.ilc.cnr.it.utils;

public class HebrewUtils {

    public static String devocalizeText(String text) {
        StringBuilder devocalized = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == ' ' || (text.charAt(i) >= 0x05d0 && text.charAt(i) <= 0x05f4)) {
                devocalized.append(text.charAt(i));
            }
        }
        return devocalized.toString().trim().replaceAll(" +", " ");
    }

    public static String devocalizeWord(String word) {
        StringBuilder devocalized = new StringBuilder();
        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) >= 0x05d0 && word.charAt(i) <= 0x05f4) {
                devocalized.append(word.charAt(i));
            }
        }
        return devocalized.toString();
    }

    public static boolean isVocalized(String text) {
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) >= 0x0591 && text.charAt(i) <= 0x05c7) {
                return true;
            }
        }
        return false;
    }

    public static boolean isHebrew(int c) {
        return (c >= 0x0591 && c <= 0x05f4);
    }

    public static boolean isVocal(int c) {
        return (c >= 0x0591 && c <= 0x05c7);
    }

    public static boolean isLetter(int c) {
        return (c >= 0x05d0 && c <= 0x05ea);
    }

    public static boolean containsHebrew(String text) {
        for (int i = 0; i < text.length(); i++) {
            if (isHebrew(text.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    public static int compare(String string1, String string2) {
        string1 = devocalizeText(string1);
        string2 = devocalizeText(string2);
        return string1.compareTo(string2);
    }

    public static String[] devocalizeAndSplit(String string) {
        string = devocalizeText(string);
        return string.isEmpty() ? new String[]{} : string.split(" ");
    }

    public static String getVocalizedHebrewText(String text) {
        StringBuilder hebrew = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == ' ' || isHebrew(text.charAt(i))) {
                hebrew.append(text.charAt(i));
            }
        }
        return hebrew.toString().trim().replaceAll(" +", " ");
    }
}