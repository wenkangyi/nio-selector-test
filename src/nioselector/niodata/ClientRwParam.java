package nioselector.niodata;

import java.nio.channels.SocketChannel;
import java.util.Date;

/**
 * 	�������Ķ��壬��Ҫ����ʵ���������
 * */
public class ClientRwParam {
	/**
	 *	 ʵ����ʹ�ñ�־��ֻ�б�־Ϊtrue�ż�����ʵ��
	 * */
	private boolean useFlag = false;
	/**
	 * 	�ͻ������ӱ�־�����˱�־Ϊtrueʱ����Ҫ�����ݿ�д������״̬
	 * */
	private boolean connFlag = false;
	/**
	 * 	�ͻ��˵�socket channel
	 * */
	private SocketChannel socketChannel = null;
    /**
     * 	��ǰʱ��
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
