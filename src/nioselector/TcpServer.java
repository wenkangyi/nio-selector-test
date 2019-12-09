package nioselector;

import nioselector.niosubthread.BusinessThread;
import nioselector.niosubthread.ListingThread;
import nioselector.niosubthread.RecvThread;
import nioselector.niosubthread.SendThread;
import nioselector.niosubthread.TimeOutThread;

/**
 * 	通过模拟简单的 Reactor模型实现TcpServer
 * 	UdpServer也可通过此思路实现
 * 	当然也可以直接使用netty库
 * */
public class TcpServer implements Runnable {
	/**
	 * serverParam实例将由各线程引用，里面包含了各线程所需要的参数
	 * */
	public ServerParam serverParam = null;
	
	private Thread listingThread = null;
	private Thread recvThread = null;
	private Thread timeOutThread = null;
	private Thread sendThread = null;
	private Thread businessThread = null;
	
	public TcpServer(Integer serverPort) {
		serverParam = new ServerParam(serverPort);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public TcpServer(Integer serverPort,Integer offlineTime) {
		serverParam = new ServerParam(serverPort,offlineTime);
	}
	
	public TcpServer(Integer serverPort,Integer offlineTime,Integer maxConnNum) {
		serverParam = new ServerParam(serverPort,offlineTime,maxConnNum);
	}
	
	@Override
	public void run() {
		if(serverParam.getRunStatu()) return;
		serverParam.setRunStatu(true);
		
		System.out.println("Starting tcpServer thread...");
		// TODO Auto-generated method stub
		//启动监听线程
		Runnable listing = new ListingThread(serverParam);
		listingThread = new Thread(listing);
		listingThread.setDaemon(true);
		listingThread.setName("listing");
		listingThread.start();
		
		/*Runnable recv = new RecvThread(serverParam);
		recvThread = new Thread(recv);
		recvThread.setDaemon(true);
		recvThread.setName("recv");
		recvThread.start();*/
		
		Runnable timeOut = new TimeOutThread(serverParam);
		timeOutThread = new Thread(timeOut);
		timeOutThread.setDaemon(true);
		timeOutThread.setName("timeOut");
		timeOutThread.start();
		
		Runnable business = new BusinessThread(serverParam);
		businessThread = new Thread(business);
		businessThread.setDaemon(true);
		businessThread.setName("business");
		businessThread.start();
		
		Runnable send = new SendThread(serverParam);
		sendThread = new Thread(send);
		sendThread.setDaemon(true);
		sendThread.setName("send");
		sendThread.start();
		
		while(true) {
			Thread.yield();
		}
	}
	
	/**
	 * 用于停止服务
	 * */
	public void stop() {
		if(!serverParam.getRunStatu()) return;
		serverParam.setRunStatu(false);
		
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		listingThread = null;
		recvThread = null;
		timeOutThread = null;
		sendThread = null;
	}

}
