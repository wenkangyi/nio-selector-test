package nioselector.niosubthread;

import java.io.IOException;

import nioselector.ServerParam;
import nioselector.niodata.ClientRwData;

public class SendThread implements Runnable {
	private ServerParam serverParam = null;
	
	public SendThread(ServerParam sp) {
		this.serverParam = sp;
	}
	
	@Override
	public void run() {
		System.out.println("Starting send thread...");
		// TODO Auto-generated method stub
		while(serverParam.getRunStatu()) {
			
			if(serverParam.getWMsgQueue().getQueueSize() <= 0) {
				try {
	            	Thread.sleep(20);
	            	continue;
	        	}
				catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			//���½������ݷ��ʹ���
			System.out.println("׼����������....");
			ClientRwData cwd = serverParam.getWMsgQueue().EnQueue();
			System.out.println("���ݳ���Ϊ��" + cwd.getBytes().length);
			serverParam.getWriteBuff().put(cwd.getBytes());
			serverParam.getWriteBuff().flip();
			
			serverParam.getWriteBuff().rewind();
			
			try {
				cwd.getSocketChannel().write(serverParam.getWriteBuff());
				serverParam.getWriteBuff().clear();//������ӣ���������
				System.out.println("���ݷ������....");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
