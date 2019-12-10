package nioselector.niosubthread;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import nioselector.ServerParam;
import nioselector.niodata.ClientRwData;



/**
 * 	当有数据可读时，在这个线程中处理
 * */
public class RecvThread implements Runnable {
	private ServerParam serverParam = null;
	private ClientRwData clientRwData = null;
	private Selector readSelector = null;
	private ByteBuffer readBuff = ByteBuffer.allocate(8192);
	/**
	 * 	静态全局变量
	 * */
	public static Selector gReadSelector = null;
	
	public RecvThread(ServerParam sp) {
		this.serverParam = sp;
	}
	
	public RecvThread(ServerParam sp,Selector readSelector) {
		this(sp);
		this.readSelector = readSelector;
	}
	
	@Override
	public void run() {
		System.out.println("Starting recv thread...");
		// TODO Auto-generated method stub
		while(serverParam.getRunStatu()) {
			try {
            	int nReady = gReadSelector.select();
                if(nReady <= 0) {
                	System.out.println("recv thread no data...");
                	Thread.yield();
                	continue;
                }
        	}catch(IOException ex) {}
			
			System.out.println("接收到了数据，正在处理 ...");
			
			Set<SelectionKey> keys = gReadSelector.selectedKeys();
			Iterator<SelectionKey> it = keys.iterator();
        	System.out.println( "keys.size:" + keys.size());
        	
        	while (it.hasNext()) {
                SelectionKey key = it.next();
                it.remove();

                if (!key.isReadable()) continue;
                
                //处理读事件
                doRead(key);
        	}
		}
		System.out.println("读线程退出");
	}
	
	private void doRead(SelectionKey key) {
		try {
            SocketChannel socketChannel = (SocketChannel) key.channel();
            readBuff.clear();
            int recNum = socketChannel.read(readBuff);
            
            System.out.println("接收到地址：" + socketChannel.getRemoteAddress() + " 的字节数：" + recNum);
            if(recNum <= 0) {
            	System.out.println("客户端 " + socketChannel.getRemoteAddress() + " 主动退出，正在关闭客户端。。。");
            	socketChannel.close();//就用这一句就可以实现快速回收了
            	//key.cancel();
            	//selector.selectNow();
            	System.out.println("客户端  " + socketChannel.getRemoteAddress() + " 关闭完成！");
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
            
        	}catch(IOException ex) {}
	}

}
