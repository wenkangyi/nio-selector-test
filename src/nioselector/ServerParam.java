package nioselector;

import java.io.IOException;
import java.net.InetSocketAddress;
//import java.nio.ByteBuffer;
//import java.nio.channels.SelectionKey;
//import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

import nioselector.niodata.ClientConnParam;
import nioselector.niodata.ClientRwData;
import nioselector.niodata.TQueue;

/**
 * 	�������߳����õ��Ĳ�����ȫ��������������
 * */
public class ServerParam {
	/**
	 * 	����״̬����Ϊfalse�����߳̾�Ҫ�˳�
	 * */
 	private boolean runStatu = false;
 	/**
 	 * 	�������˿�
 	 * */
 	private Integer serverPort = 56789;
 	/**
 	 * 	����ʱ�� ����λ����
 	 * */
 	private Integer offlineTime = 30;
 	/**
 	 * 	��������������
 	 * */
 	private Integer maxConnNum = 256;
 	/**
 	 * 	�ͻ��˵Ĳ�����д����
 	 * */
 	private ClientConnParam[] clientConnParams = null;
 	/**
 	 * 	ͨ���ַ�����ClientConnParam[]������±���й���
 	 * */
 	private Map<SocketChannel ,Integer> clientConnMap = new HashMap<SocketChannel ,Integer>();
 	/**
 	 * 	���������У����ڱ�עClientRwParam���±�
 	 * */
 	private TQueue<Integer> connNumQueue = new TQueue<Integer>();
 	/**
 	 *	 �����±����
 	 * */
 	private TQueue<Integer> recvIndexQueue = new TQueue<Integer>();
 	/**
 	 * 	����Ϣ����
 	 * */
 	private TQueue<ClientRwData> rMsgQueue = new TQueue<ClientRwData>();
 	/**
 	 * 	д��Ϣ����
 	 * */
 	private TQueue<ClientRwData> wMsgQueue = new TQueue<ClientRwData>();
 	/**
 	 * 	�����ͨ��
 	 * */
 	private ServerSocketChannel serverSocketChannel = null;
 	
 	public ServerParam(Integer serverPort) {
 		this.serverPort = serverPort;

 		try {
	 		serverSocketChannel = ServerSocketChannel.open();
	 		serverSocketChannel.socket().bind(new InetSocketAddress(serverPort));
	 		serverSocketChannel.configureBlocking(false);
 			System.out.println("����ˣ�" + serverSocketChannel.getLocalAddress());
	 		
	        System.out.println("ServerParam init...");
 		}catch(IOException ex) {}
 		
 		clientConnParams = new ClientConnParam[maxConnNum];
 		for(int i=0;i<maxConnNum;i++) {
 			clientConnParams[i] = new ClientConnParam();
 			connNumQueue.DeQueue(i);
 		}
 		System.out.println("connNumQueue��ʼ����ɣ�size:" 
 					+ connNumQueue.getQueueSize());;
 	}
 	
 	public ServerParam(Integer serverPort,Integer offlineTime) {
 		this(serverPort);
 		this.offlineTime = offlineTime;
 	}
 	
 	public ServerParam(Integer serverPort,Integer offlineTime,Integer maxConnNum) {
 		this(serverPort,offlineTime);
 		this.maxConnNum = maxConnNum;
 	}
 	
 	
 	//////////////////////////////////////////////
 	//  			����
 	//////////////////////////////////////////////
 	
 	public boolean getRunStatu() {
 		return runStatu;
 	}
 	
 	public void setRunStatu(boolean runStatu) {
 		this.runStatu = runStatu;
 	}
 	
 	public Integer getServerPort() {
 		return serverPort;
 	}
 	
 	public void setServerPort(Integer serverPort) {
 		this.serverPort = serverPort;
 	}
 	
 	public Integer getOfflineTime() {
 		return offlineTime;
 	}
 	
 	public void setOfflineTime(Integer offlineTime) {
 		this.offlineTime = offlineTime;
 	}
 	
 	//maxConnNum��ʱ����
 	
 	public Integer getMaxConnNum() {
 		return maxConnNum;
 	}
 	
 	public ClientConnParam[] getClientConnParams() {
 		return clientConnParams;
 	}
 	
 	public Map<SocketChannel,Integer> getClientConnMap(){
 		return clientConnMap;
 	}
 	
 	public TQueue<Integer> getConnNumQueue(){
 		return connNumQueue;
 	}
 	
 	public TQueue<Integer> getRecvIndexQueue(){
 		return recvIndexQueue;
 	}
 	
 	public TQueue<ClientRwData> getRMsgQueue(){
 		return rMsgQueue;
 	}
 	
 	public TQueue<ClientRwData> getWMsgQueue(){
 		return wMsgQueue;
 	}
 	
 	public ServerSocketChannel getServerSocketChannel() {
 		return serverSocketChannel;
 	}
 	
}
