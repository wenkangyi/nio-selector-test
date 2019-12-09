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
 * 	�������ݿɶ�ʱ��������߳��д���
 * */
public class RecvThread implements Runnable {
	private ServerParam serverParam = null;
	private ClientRwData clientRwData = null;
	private Selector readSelector = null;
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
            	//int nReady = serverParam.getReadSelector().select();
            	int nReady = this.readSelector.select();
                if(nReady <= 0) {
                	System.out.println("recv thread no data...");
                	Thread.yield();
                	continue;
                }
        	}catch(IOException ex) {}
			
			System.out.println("���յ������ݣ����ڴ��� ...");
			
			//Set<SelectionKey> keys = serverParam.getReadSelector().selectedKeys();
			Set<SelectionKey> keys = this.readSelector.selectedKeys();
			Iterator<SelectionKey> it = keys.iterator();
        	System.out.println( "keys.size:" + keys.size());
        	while (it.hasNext()) {
                SelectionKey key = it.next();
                it.remove();

                if (!key.isReadable()) continue;
                try {
                SocketChannel socketChannel = (SocketChannel) key.channel();
                serverParam.getReadBuff().clear();
                int recNum = socketChannel.read(serverParam.getReadBuff());
                
                System.out.println("���յ���ַ��" + socketChannel.getRemoteAddress() + " ���ֽ�����" + recNum);
                if(recNum <= 0) {
                	System.out.println("�ͻ��� " + socketChannel.getRemoteAddress() + " �����˳������ڹرտͻ��ˡ�����");
                	socketChannel.close();//������һ��Ϳ���ʵ�ֿ��ٻ�����
                	//key.cancel();
                	//selector.selectNow();
                	System.out.println("�ͻ���  " + socketChannel.getRemoteAddress() + " �ر���ɣ�");
                	continue;
                }
                serverParam.getReadBuff().flip();
                clientRwData = new ClientRwData();
                clientRwData.setSocketChannel(socketChannel);
            	byte[] recData = new byte[recNum];
    	        
    	        System.arraycopy(serverParam.getReadBuff().array(),0,recData,0,recNum);
                clientRwData.setBytes(recData);
                serverParam.getRMsgQueue().DeQueue(clientRwData);
                System.out.println("������д����  serverParam.getRMsgQueue()=" 
                		+ serverParam.getRMsgQueue().getQueueSize() );
                
            	}catch(IOException ex) {}
			
        	}
		}
	}

}