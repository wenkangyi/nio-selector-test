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
            // ע�� channel������ָ������Ȥ���¼��� Accept
            ssc.register(selector, SelectionKey.OP_ACCEPT);
        }catch(IOException ex) {}
        
            ByteBuffer readBuff = ByteBuffer.allocate(1024);
            ByteBuffer writeBuff = ByteBuffer.allocate(128);
            writeBuff.put("received".getBytes());
            writeBuff.flip();
            System.out.println("�����������������");
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
                        // �����µ����ӣ����Ұ�����ע�ᵽselector�ϣ����ң�
                        // �������channelֻ�Զ���������Ȥ��
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
                        
                        System.out.println("���յ���ַ��" + socketChannel.getRemoteAddress() + " ���ֽ�����" + recNum);
                        if(recNum <= 0) {
                        	System.out.println("�ͻ����˳�����������ͻ��ˡ�����");
                        	socketChannel.close();//������һ��Ϳ���ʵ�ֿ��ٻ�����
                        	//key.cancel();
                        	//selector.selectNow();
                        	System.out.println("����ͻ�����ɣ�");
                        	continue;
                        }
                        readBuff.flip();
                        //�ϴε����ݻ��ڣ�û�������Ҫ��Ͻ����ֽ������д���
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
