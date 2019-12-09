package nioselector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

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
	 * 	���ڼ���
	 * 	Ŀǰ���Selector�����ã�����accept��read���������selector
	 * */
 	public Selector acceptSelector = null;
	/**
	 * 	���ڽ���
	 * 	�ͻ��������ϣ���ע��
	 * */
 	public Selector readSelector = null;
 	/**
 	 * 	�ͻ��˵Ĳ�����д����
 	 * */
 	private ClientConnParam[] clientConnParams = null;
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
 	/**
 	 * 	���ջ�����
 	 * */
 	private ByteBuffer readBuff = ByteBuffer.allocate(8192);
 	/**
 	 * 	���ͻ�����
 	 * */
 	private ByteBuffer writeBuff = ByteBuffer.allocate(8192);
 	
 	/*public ServerParam() {
 		try {
	 		serverSocketChannel = ServerSocketChannel.open();
	 		serverSocketChannel.socket().bind(new InetSocketAddress(serverPort));
	 		serverSocketChannel.configureBlocking(false);
 			System.out.println("����ˣ�" + serverSocketChannel.getLocalAddress());
	 		acceptSelector = Selector.open();
	        // ע�� channel������ָ������Ȥ���¼��� Accept
	        serverSocketChannel.register(acceptSelector, SelectionKey.OP_ACCEPT);
	        
	        readSelector = Selector.open();//�ͻ��������ϣ���ע��
	        
	        System.out.println("ServerParam init...");
 		}catch(IOException ex) {}
 		
 		clientConnParams = new ClientConnParam[maxConnNum];
 		for(int i=0;i<maxConnNum;i++) {
 			clientConnParams[i] = new ClientConnParam();
 			connNumQueue.DeQueue(i);
 		}
 		System.out.println("connNumQueue��ʼ����ɣ�size:" 
 					+ connNumQueue.getQueueSize());
 		
        //writeBuff.put("received".getBytes());
        //writeBuff.flip();
 	}*/
 	
 	public ServerParam(Integer serverPort) {
 		this.serverPort = serverPort;

 		try {
 			readSelector = Selector.open();//�ͻ��������ϣ���ע��
 			
	 		serverSocketChannel = ServerSocketChannel.open();
	 		serverSocketChannel.socket().bind(new InetSocketAddress(serverPort));
	 		serverSocketChannel.configureBlocking(false);
 			System.out.println("����ˣ�" + serverSocketChannel.getLocalAddress());
	 		acceptSelector = Selector.open();
	        // ע�� channel������ָ������Ȥ���¼��� Accept
	        serverSocketChannel.register(acceptSelector, SelectionKey.OP_ACCEPT);
	        
	        
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
 	
 	public Selector getAcceptSelector() {
 		return acceptSelector;
 	}
 	
 	//��ʱ����set
 	//public void setAcceptSelector()
 	
 	public Selector getReadSelector() {
 		return readSelector;
 	}
 	
 	public ClientConnParam[] getClientConnParams() {
 		return clientConnParams;
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
 	
 	public ByteBuffer getReadBuff() {
 		return readBuff;
 	}
 	
 	public ByteBuffer getWriteBuff() {
 		return writeBuff;
 	}
}
