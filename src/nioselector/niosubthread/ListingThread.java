package nioselector.niosubthread;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import nioselector.ServerParam;
import nioselector.niodata.ClientRwData;



/**
 * 当有客户端发起连接时，在这个线程处理
 * */
public class ListingThread implements Runnable {
	private ServerParam serverParam = null;
	private ClientRwData clientRwData = null;
	private SocketChannel socketChannel = null;
	private Thread recvThread = null;
	private Selector readSelector = null;
	
	public ListingThread(ServerParam sp) {
		this.serverParam = sp;
		try {
			readSelector = Selector.open();
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
            	int nReady = serverParam.getAcceptSelector().select();//这里是需要占时间的
            	if(nReady <= 0) {
                	Thread.yield();
                	//Thread.sleep(100);
                	System.out.println("listing thread no connection...");
                	continue;
                }
        	}
			catch(IOException ex) {
				
			} 
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
            Set<SelectionKey> keys = serverParam.getAcceptSelector().selectedKeys();
			Iterator<SelectionKey> it = keys.iterator();
        	System.out.println( "keys.size:" + keys.size());
        	
            while (it.hasNext()) {
                SelectionKey key = it.next();

                if (key.isAcceptable()) {
                	doAccept();
                }
                else if(key.isReadable()) {
                	doRead(key);
                }
            	it.remove();
            }
		}
		stopRecvThread();
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
            System.out.println("设备 " + socketChannel.getRemoteAddress() + "  连接成功 ！");
            socketChannel.configureBlocking(false);
            socketChannel.register(readSelector,SelectionKey.OP_READ);
            System.out.println("添加读完成");
            startRecvThread();
    	}catch(IOException ex) {
    		System.out.println("listing thread exception...");
    	}
	}
	
	private void startRecvThread() {
		if(recvThread != null) return;
		
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
            serverParam.getReadBuff().clear();
            int recNum = socketChannel.read(serverParam.getReadBuff());
            
            System.out.println("接收到地址：" + socketChannel.getRemoteAddress() + " 的字节数：" + recNum);
            if(recNum <= 0) {
            	System.out.println("客户端 " + socketChannel.getRemoteAddress() + " 主动退出，正在关闭客户端。。。");
            	socketChannel.close();//就用这一句就可以实现快速回收了
            	//key.cancel();
            	//selector.selectNow();
            	System.out.println("客户端  " + socketChannel.getRemoteAddress() + " 关闭完成！");
            	return;
            }
            serverParam.getReadBuff().flip();
            clientRwData = new ClientRwData();
            clientRwData.setSocketChannel(socketChannel);
        	byte[] recData = new byte[recNum];
	        
	        System.arraycopy(serverParam.getReadBuff().array(),0,recData,0,recNum);
            clientRwData.setBytes(recData);
            serverParam.getRMsgQueue().DeQueue(clientRwData);
            System.out.println("数据已写存入  serverParam.getRMsgQueue()=" 
            		+ serverParam.getRMsgQueue().getQueueSize() );
            //上次的数据还在，没有清除，要配合接收字节数进行处理
            //System.out.println("receivedx : " + new String(serverParam.getReadBuff().array()));
            //key.interestOps(SelectionKey.OP_WRITE);
            
        	}catch(IOException ex) {}
	}
	
}

