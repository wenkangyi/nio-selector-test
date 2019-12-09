package nioselector.convertPackage;

public class ConvertCharString {
    public static char[] getChars(String str){
        return str.toCharArray();
    }

    public static String getString(char[] chars){
        return String.valueOf(chars);
    }
}
