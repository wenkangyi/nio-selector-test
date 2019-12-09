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
 * 	在所有线程中用到的参数，全放在这里，方便调用
 * */
public class ServerParam {
	/**
	 * 	运行状态，当为false所有线程均要退出
	 * */
 	private boolean runStatu = false;
 	/**
 	 * 	服务器端口
 	 * */
 	private Integer serverPort = 56789;
 	/**
 	 * 	离线时间 ，单位：秒
 	 * */
 	private Integer offlineTime = 30;
 	/**
 	 * 	允许的最大连接数
 	 * */
 	private Integer maxConnNum = 256;
	/**
	 * 	用于监听
	 * 	目前多个Selector不能用，所以accept与read都采用这个selector
	 * */
 	public Selector acceptSelector = null;
	/**
	 * 	用于接收
	 * 	客户端连接上，才注册
	 * */
 	public Selector readSelector = null;
 	/**
 	 * 	客户端的参数读写数组
 	 * */
 	private ClientConnParam[] clientConnParams = null;
 	/**
 	 * 	连接数队列，用于标注ClientRwParam的下标
 	 * */
 	private TQueue<Integer> connNumQueue = new TQueue<Integer>();
 	/**
 	 *	 接收下标队列
 	 * */
 	private TQueue<Integer> recvIndexQueue = new TQueue<Integer>();
 	/**
 	 * 	读消息队列
 	 * */
 	private TQueue<ClientRwData> rMsgQueue = new TQueue<ClientRwData>();
 	/**
 	 * 	写消息队列
 	 * */
 	private TQueue<ClientRwData> wMsgQueue = new TQueue<ClientRwData>();
 	/**
 	 * 	服务端通道
 	 * */
 	private ServerSocketChannel serverSocketChannel = null;
 	/**
 	 * 	接收缓冲区
 	 * */
 	private ByteBuffer readBuff = ByteBuffer.allocate(8192);
 	/**
 	 * 	发送缓冲区
 	 * */
 	private ByteBuffer writeBuff = ByteBuffer.allocate(8192);
 	
 	/*public ServerParam() {
 		try {
	 		serverSocketChannel = ServerSocketChannel.open();
	 		serverSocketChannel.socket().bind(new InetSocketAddress(serverPort));
	 		serverSocketChannel.configureBlocking(false);
 			System.out.println("服务端：" + serverSocketChannel.getLocalAddress());
	 		acceptSelector = Selector.open();
	        // 注册 channel，并且指定感兴趣的事件是 Accept
	        serverSocketChannel.register(acceptSelector, SelectionKey.OP_ACCEPT);
	        
	        readSelector = Selector.open();//客户端连接上，才注册
	        
	        System.out.println("ServerParam init...");
 		}catch(IOException ex) {}
 		
 		clientConnParams = new ClientConnParam[maxConnNum];
 		for(int i=0;i<maxConnNum;i++) {
 			clientConnParams[i] = new ClientConnParam();
 			connNumQueue.DeQueue(i);
 		}
 		System.out.println("connNumQueue初始化完成，size:" 
 					+ connNumQueue.getQueueSize());
 		
        //writeBuff.put("received".getBytes());
        //writeBuff.flip();
 	}*/
 	
 	public ServerParam(Integer serverPort) {
 		this.serverPort = serverPort;

 		try {
 			readSelector = Selector.open();//客户端连接上，才注册
 			
	 		serverSocketChannel = ServerSocketChannel.open();
	 		serverSocketChannel.socket().bind(new InetSocketAddress(serverPort));
	 		serverSocketChannel.configureBlocking(false);
 			System.out.println("服务端：" + serverSocketChannel.getLocalAddress());
	 		acceptSelector = Selector.open();
	        // 注册 channel，并且指定感兴趣的事件是 Accept
	        serverSocketChannel.register(acceptSelector, SelectionKey.OP_ACCEPT);
	        
	        
	        System.out.println("ServerParam init...");
 		}catch(IOException ex) {}
 		
 		clientConnParams = new ClientConnParam[maxConnNum];
 		for(int i=0;i<maxConnNum;i++) {
 			clientConnParams[i] = new ClientConnParam();
 			connNumQueue.DeQueue(i);
 		}
 		System.out.println("connNumQueue初始化完成，size:" 
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
 	//  			属性
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
 	
 	//maxConnNum暂时不用
 	
 	public Integer getMaxConnNum() {
 		return maxConnNum;
 	}
 	
 	public Selector getAcceptSelector() {
 		return acceptSelector;
 	}
 	
 	//暂时不用set
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
