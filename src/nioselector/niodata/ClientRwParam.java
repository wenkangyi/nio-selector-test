package nioselector.niodata;

import java.nio.channels.SocketChannel;
import java.util.Date;

/**
 * 	这个类里的定义，需要根据实际情况而定
 * */
public class ClientRwParam {
	/**
	 *	 实例被使用标志，只有标志为true才检索此实例
	 * */
	private boolean useFlag = false;
	/**
	 * 	客户端连接标志，当此标志为true时，需要向数据库写主在线状态
	 * */
	private boolean connFlag = false;
	/**
	 * 	客户端的socket channel
	 * */
	private SocketChannel socketChannel = null;
    /**
     * 	当前时间
     * */
    private Date currTime;

    public boolean getUseFlag() {
    	return useFlag;
    }
    
    public void setUseFlag(boolean useFlag) {
    	this.useFlag = useFlag;
    }
    
    public boolean getConnFlag() {
    	return connFlag;
    }
    
    public void setConnFlag(boolean connFlag) {
    	this.connFlag = connFlag;
    }

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    public void setSocketChannel(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    public Date getCommTime() {
        return currTime;
    }

    public void setCurrTime(Date currTime) {
        this.currTime = currTime;
    }
}
