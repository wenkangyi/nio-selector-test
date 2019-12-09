package nioselector;

import nioselector.niosubthread.BusinessThread;
import nioselector.niosubthread.ListingThread;
import nioselector.niosubthread.RecvThread;
import nioselector.niosubthread.SendThread;
import nioselector.niosubthread.TimeOutThread;

/**
 * 	ͨ��ģ��򵥵� Reactorģ��ʵ��TcpServer
 * 	UdpServerҲ��ͨ����˼·ʵ��
 * 	��ȻҲ����ֱ��ʹ��netty��
 * */
public class TcpServer implements Runnable {
	/**
	 * serverParamʵ�����ɸ��߳����ã���������˸��߳�����Ҫ�Ĳ���
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
		//���������߳�
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
	 * ����ֹͣ����
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
