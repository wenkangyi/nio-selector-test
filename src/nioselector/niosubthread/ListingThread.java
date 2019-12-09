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
 * ���пͻ��˷�������ʱ��������̴߳���
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
		}//�ͻ��������ϣ���ע��
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("Starting listing thread...");
		while(serverParam.getRunStatu()) {
			try {
            	int nReady = serverParam.getAcceptSelector().select();//��������Ҫռʱ���
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
		// �����µ����ӣ����Ұ�����ע�ᵽselector�ϣ����ң�
        // �������channelֻ�Զ���������Ȥ��
    	try {
    		socketChannel = serverParam.getServerSocketChannel().accept();
            if(socketChannel == null) {
            	System.out.println("û���µ����ӣ�");
            	return;
            }
            System.out.println("�豸 " + socketChannel.getRemoteAddress() + "  ���ӳɹ� ��");
            socketChannel.configureBlocking(false);
            socketChannel.register(readSelector,SelectionKey.OP_READ);
            System.out.println("��Ӷ����");
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
            
            System.out.println("���յ���ַ��" + socketChannel.getRemoteAddress() + " ���ֽ�����" + recNum);
            if(recNum <= 0) {
            	System.out.println("�ͻ��� " + socketChannel.getRemoteAddress() + " �����˳������ڹرտͻ��ˡ�����");
            	socketChannel.close();//������һ��Ϳ���ʵ�ֿ��ٻ�����
            	//key.cancel();
            	//selector.selectNow();
            	System.out.println("�ͻ���  " + socketChannel.getRemoteAddress() + " �ر���ɣ�");
            	return;
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
            //�ϴε����ݻ��ڣ�û�������Ҫ��Ͻ����ֽ������д���
            //System.out.println("receivedx : " + new String(serverParam.getReadBuff().array()));
            //key.interestOps(SelectionKey.OP_WRITE);
            
        	}catch(IOException ex) {}
	}
	
}

