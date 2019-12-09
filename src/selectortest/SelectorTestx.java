package selectortest;


/*import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;*/

/*import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;*/

import nioselector.TcpServer;

public class SelectorTestx {
    public static void main(String[] args) {
    	
    	Runnable tcpServer = new TcpServer(54321);
    	Thread thread = new Thread(tcpServer);
    	thread.setDaemon(true);
    	thread.setName("tcpServer");
    	//System.out.println("Starting tcpServer thread...");
    	thread.start();
    	
    	do {
    		try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}while(((TcpServer)tcpServer).serverParam.getRunStatu());
    	
    	try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	/*Selector selector = null;
    	ServerSocketChannel ssc = null;
    	
        try {
            ssc = ServerSocketChannel.open();
            ssc.socket().bind(new InetSocketAddress(8000));
            ssc.configureBlocking(false);

            selector = Selector.open();
            // 注册 channel，并且指定感兴趣的事件是 Accept
            ssc.register(selector, SelectionKey.OP_ACCEPT);
        }catch(IOException ex) {}
        
            ByteBuffer readBuff = ByteBuffer.allocate(1024);
            ByteBuffer writeBuff = ByteBuffer.allocate(128);
            writeBuff.put("received".getBytes());
            writeBuff.flip();
            System.out.println("服务端已启动。。。");
            while (true) {
            	try {
                int nReady = selector.select();
                if(nReady <= 0) {
                	Thread.yield();
                	continue;
                }
            	}catch(IOException ex) {}
            	
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> it = keys.iterator();
            	System.out.println( "keys.size:" + keys.size());
            	
                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    it.remove();

                    if (key.isAcceptable()) {
                        // 创建新的连接，并且把连接注册到selector上，而且，
                        // 声明这个channel只对读操作感兴趣。
                    	try {
                        SocketChannel socketChannel = ssc.accept();
                        socketChannel.configureBlocking(false);
                        socketChannel.register(selector, SelectionKey.OP_READ);
                    	}catch(IOException ex) {}
                    }
                    else if (key.isReadable()) {
                    	try {
                        SocketChannel socketChannel = (SocketChannel) key.channel();
                        readBuff.clear();
                        int recNum = socketChannel.read(readBuff);
                        
                        System.out.println("接收到地址：" + socketChannel.getRemoteAddress() + " 的字节数：" + recNum);
                        if(recNum <= 0) {
                        	System.out.println("客户端退出，正在清除客户端。。。");
                        	socketChannel.close();//就用这一句就可以实现快速回收了
                        	//key.cancel();
                        	//selector.selectNow();
                        	System.out.println("清除客户端完成！");
                        	continue;
                        }
                        readBuff.flip();
                        //上次的数据还在，没有清除，要配合接收字节数进行处理
                        System.out.println("receivedx : " + new String(readBuff.array()));
                        key.interestOps(SelectionKey.OP_WRITE);
                    	}catch(IOException ex) {}
                    }
                    else if (key.isWritable()) {
                    	try {
                        writeBuff.rewind();
                        SocketChannel socketChannel = (SocketChannel) key.channel();
                        socketChannel.write(writeBuff);                        
                        key.interestOps(SelectionKey.OP_READ);
                    	}catch(IOException ex) {}
                    }
                }
            }
        /*} catch (IOException e) {
            //e.printStackTrace();
        }*/
    }
}
