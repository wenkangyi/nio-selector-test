package nioselector.convertPackage;

public class ConvertByteString {
    public static byte[] getBytes(String str){
        return ConvertCharByte.getBytes(ConvertCharString.getChars(str));
    }

    public static String getString(byte[] bytes){
        return ConvertCharString.getString(ConvertCharByte.getChars(bytes));
    }
}
