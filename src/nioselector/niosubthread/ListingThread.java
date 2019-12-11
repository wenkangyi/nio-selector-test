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
 * 	���пͻ��˷�������ʱ��������̴߳���
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
		}//�ͻ��������ϣ���ע��
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("Starting listing thread...");
		while(serverParam.getRunStatu()) {
			try {
            	int nReady = acceptSelector.select();//��������Ҫռʱ���
            	if(nReady <= 0) {
                	Thread.yield();
                	System.out.println("listing thread no connection...");
                	continue;
                }
        	}
			catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("�����߳��쳣!");
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
			System.out.println("�����쳣��");
		}
		stopRecvThread();
		System.out.println("�����߳��˳�");
	}

	private void doAccept() {
		// �����µ����ӣ����Ұ�����ע�ᵽselector�ϣ����ң�
        // �������channelֻ�Զ���������Ȥ��
    	try {
    		socketChannel = serverParam.getServerSocketChannel().accept();
            if(socketChannel == null) {
            	System.out.println("û���µ����ӣ�");
            	return;
            }
            
            if(serverParam.getConnNumQueue().getQueueSize() <= 0) {
            	socketChannel.close();
            	System.out.println("�Ѵﵽ���������");
            	return ;
            }
            
            System.out.println("�豸 " + socketChannel.getRemoteAddress() + "  ���ӳɹ� ��");
            socketChannel.configureBlocking(false);
            //socketChannel.register(RecvThread.gReadSelector,SelectionKey.OP_READ);
            socketChannel.register(acceptSelector,SelectionKey.OP_READ);
            System.out.println("��Ӷ����");
            
            //��Щ������Ҫ���ڳ�ʱ���߼��
            addTimeoutDetection(socketChannel);
            
            //startRecvThread();
    	}catch(IOException ex) {
    		System.out.println("listing thread exception...");
    	}
	}
	
	private void startRecvThread() {
		if(recvThread != null) return;
		System.out.println("׼���������̣߳�");
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
            
            System.out.println("���յ���ַ��" + socketChannel.getRemoteAddress() + " ���ֽ�����" + recNum);
            if(recNum <= 0) {
            	System.out.println("�ͻ��� " + socketChannel.getRemoteAddress() + " �����˳������ڹرտͻ��ˡ�����");
            	updateClostTime(socketChannel);
            	socketChannel.close();//������һ��Ϳ���ʵ�ֿ��ٻ�����
            	//key.cancel();
            	//selector.selectNow();
            	//System.out.println("�ͻ���   �ر���ɣ�");
            	return;
            }
            readBuff.flip();
            clientRwData = new ClientRwData();
            clientRwData.setSocketChannel(socketChannel);
            
        	byte[] recData = new byte[recNum];
	        System.arraycopy(readBuff.array(),0,recData,0,recNum);
            clientRwData.setBytes(recData);
            serverParam.getRMsgQueue().DeQueue(clientRwData);
            System.out.println("������д����  serverParam.getRMsgQueue()=" 
            		+ serverParam.getRMsgQueue().getQueueSize() );
            
            updateOnlineTime(socketChannel);
    	}catch(IOException ex) {
    		System.out.println("�������쳣!" );
    	}
	}
	
	/**
	 * 	��ӳ�ʱ���
	 * */
	private void addTimeoutDetection(SocketChannel socketChannel) {
		Integer index = serverParam.getConnNumQueue().EnQueue();
		System.out.println("���Ӻţ� " + index + " ���豸����");
        serverParam.getClientConnParams()[index].setCurrTime(new Date());
        serverParam.getClientConnParams()[index].setConnFlag(true);
        serverParam.getClientConnParams()[index].setUseFlag(true);
        serverParam.getClientConnParams()[index].setSocketChannel(socketChannel);
        //���ڿ��ٲ��ҵ�ClientConnParams������±�
        serverParam.getClientConnMap().put(socketChannel, index);
    }
	
	/**
	 * 	��������ʱ��
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

