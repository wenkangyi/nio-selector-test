package nioselector.niosubthread;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import nioselector.ServerParam;
import nioselector.convertPackage.ConvertDateTime;
import nioselector.niodata.ClientRwData;



/**
 * 	当有客户端发起连接时，在这个线程处理
 * */
public class ListingThread implements Runnable {
	private ServerParam serverParam = null;
	private SocketChannel socketChannel = null;
	private Thread recvThread = null;
	private Selector readSelector = null;
	private Selector acceptSelector = null;
	private ByteBuffer readBuff = ByteBuffer.allocate(8192);
	private ClientRwData clientRwData = null;
	
	public ListingThread(ServerParam sp) {
		this.serverParam = sp;
		try {
			acceptSelector = Selector.open();
			readSelector = Selector.open();
			serverParam.getServerSocketChannel().register(
					acceptSelector, SelectionKey.OP_ACCEPT);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//客户端连接上，才注册
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("Starting listing thread...");
		while(serverParam.getRunStatu()) {
			try {
            	int nReady = acceptSelector.select();//这里是需要占时间的
            	if(nReady <= 0) {
                	Thread.yield();
                	System.out.println("listing thread no connection...");
                	continue;
                }
        	}
			catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("监听线程异常!");
			}
        	
            Set<SelectionKey> keys = acceptSelector.selectedKeys();
			Iterator<SelectionKey> it = keys.iterator();
        	System.out.println( "keys.size:" + keys.size());
        	
            while (it.hasNext()) {
                SelectionKey key = it.next();
                it.remove();
                
                if (key.isAcceptable()) {
                	doAccept();
                }
                else if(key.isReadable()) {
                	doRead(key);
                }
            	
            }
		}
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			System.out.println("休眠异常！");
		}
		stopRecvThread();
		System.out.println("监听线程退出");
	}

	private void doAccept() {
		// 创建新的连接，并且把连接注册到selector上，而且，
        // 声明这个channel只对读操作感兴趣。
    	try {
    		socketChannel = serverParam.getServerSocketChannel().accept();
            if(socketChannel == null) {
            	System.out.println("没有新的连接！");
            	return;
            }
            
            if(serverParam.getConnNumQueue().getQueueSize() <= 0) {
            	socketChannel.close();
            	System.out.println("已达到最大连接数");
            	return ;
            }
            
            System.out.println("设备 " + socketChannel.getRemoteAddress() + "  连接成功 ！");
            socketChannel.configureBlocking(false);
            //socketChannel.register(RecvThread.gReadSelector,SelectionKey.OP_READ);
            socketChannel.register(acceptSelector,SelectionKey.OP_READ);
            System.out.println("添加读完成");
            
            //这些数据主要用于超时离线检测
            addTimeoutDetection(socketChannel);
            
            //startRecvThread();
    	}catch(IOException ex) {
    		System.out.println("listing thread exception...");
    	}
	}
	
	private void startRecvThread() {
		if(recvThread != null) return;
		System.out.println("准备创建读线程！");
		Runnable recv = new RecvThread(serverParam,readSelector);
		recvThread = new Thread(recv);
		recvThread.setDaemon(true);
		recvThread.setName("recv");
		recvThread.start();
	}
	
	private void stopRecvThread() {
		recvThread = null;
	}
	
	
	private void doRead(SelectionKey key) {
		try {
            SocketChannel socketChannel = (SocketChannel) key.channel();
            readBuff.clear();
            int recNum = socketChannel.read(readBuff);
            
            System.out.println("接收到地址：" + socketChannel.getRemoteAddress() + " 的字节数：" + recNum);
            if(recNum <= 0) {
            	System.out.println("客户端 " + socketChannel.getRemoteAddress() + " 主动退出，正在关闭客户端。。。");
            	updateClostTime(socketChannel);
            	socketChannel.close();//就用这一句就可以实现快速回收了
            	//key.cancel();
            	//selector.selectNow();
            	//System.out.println("客户端   关闭完成！");
            	return;
            }
            readBuff.flip();
            clientRwData = new ClientRwData();
            clientRwData.setSocketChannel(socketChannel);
            
        	byte[] recData = new byte[recNum];
	        System.arraycopy(readBuff.array(),0,recData,0,recNum);
            clientRwData.setBytes(recData);
            serverParam.getRMsgQueue().DeQueue(clientRwData);
            System.out.println("数据已写存入  serverParam.getRMsgQueue()=" 
            		+ serverParam.getRMsgQueue().getQueueSize() );
            
            updateOnlineTime(socketChannel);
    	}catch(IOException ex) {
    		System.out.println("读数据异常!" );
    	}
	}
	
	/**
	 * 	添加超时检测
	 * */
	private void addTimeoutDetection(SocketChannel socketChannel) {
		Integer index = serverParam.getConnNumQueue().EnQueue();
		System.out.println("连接号： " + index + " 的设备上线");
        serverParam.getClientConnParams()[index].setCurrTime(new Date());
        serverParam.getClientConnParams()[index].setConnFlag(true);
        serverParam.getClientConnParams()[index].setUseFlag(true);
        serverParam.getClientConnParams()[index].setSocketChannel(socketChannel);
        //用于快速查找到ClientConnParams数组的下标
        serverParam.getClientConnMap().put(socketChannel, index);
    }
	
	/**
	 * 	更新在线时间
	 * */
	private void updateOnlineTime(SocketChannel socketChannel) {
		Integer index = serverParam.getClientConnMap().get(socketChannel);
		serverParam.getClientConnParams()[index].setCurrTime(new Date());
	}
	
	private void updateClostTime(SocketChannel socketChannel) {
		Integer index = serverParam.getClientConnMap().get(socketChannel);
		serverParam.getClientConnParams()[index]
				.setCurrTime(ConvertDateTime.addSec2Datd(-(serverParam.getOfflineTime() + 3)));
	}
}

