package nioselector.niodata;

import java.nio.channels.SocketChannel;

import nioselector.convertPackage.ConvertByteString;
import nioselector.convertPackage.ConvertCharByte;
import nioselector.convertPackage.ConvertCharString;

/**
 * ����ඨ����socket channel�շ����ݵĽṹ
 * */
public class ClientRwData {
	/**
	 * �ͻ��˵�socket channel
	 * */
	private SocketChannel socketChannel = null;
	/**
     *	  �ַ���������
     * */
    private char[] chars = null;
    /**
     * 	ʮ���ƻ�ʮ����������
     * */
    private byte[] bytes = null;
    /**
     * 	�ַ�������
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
