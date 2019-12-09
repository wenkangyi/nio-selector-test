package nioselector.niodata;

import java.nio.channels.SocketChannel;

import nioselector.convertPackage.ConvertByteString;
import nioselector.convertPackage.ConvertCharByte;
import nioselector.convertPackage.ConvertCharString;

/**
 * 这个类定义了socket channel收发数据的结构
 * */
public class ClientRwData {
	/**
	 * 客户端的socket channel
	 * */
	private SocketChannel socketChannel = null;
	/**
     *	  字符数组数据
     * */
    private char[] chars = null;
    /**
     * 	十进制或十六进制数据
     * */
    private byte[] bytes = null;
    /**
     * 	字符串数据
     * */
    private String msg = null;
    
    public SocketChannel getSocketChannel() {
    	return this.socketChannel;
    }
    
    public void setSocketChannel(SocketChannel socketChannel) {
    	this.socketChannel = socketChannel;
    }
    
    public char[] getChars() {
    	return chars;
    }
    
    public void setChars(char[] chars) {
    	this.chars = chars;
    	this.msg = ConvertCharString.getString(chars);
        this.bytes = ConvertCharByte.getBytes(chars);
    }
    
    public byte[] getBytes() {
    	return bytes;
    }
    
    public void setBytes(byte[] bytes) {
    	this.bytes = bytes;
    	this.msg = ConvertByteString.getString(bytes);
        this.chars = ConvertCharByte.getChars(bytes);
    }
    
    public String getMsg() {
    	return msg;
    }
    
    public void setMsg(String msg) {
    	System.out.println("msg.length = " + msg.length());
    	this.msg = msg;
    	this.chars = ConvertCharString.getChars(msg);
        this.bytes = ConvertByteString.getBytes(msg);
    }
}
