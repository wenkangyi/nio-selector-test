package nioselector.niosubthread;

import java.nio.channels.SocketChannel;
import java.util.Date;

import nioselector.ServerParam;
import nioselector.convertPackage.ConvertDateTime;

/**
 *  ����ʱ����Ҫ�������ݿ�״̬
 * 100��������һ��
 * */
public class TimeOutThread implements Runnable {
	private ServerParam serverParam = null;
	
	public TimeOutThread(ServerParam sp) {
		this.serverParam = sp;
	}

	@Override
	public void run() {
		System.out.println("Starting timeOut thread...");
		// TODO Auto-generated method stub
		while(serverParam.getRunStatu()) {
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Date nowTime = new Date();
			for(int i=0;i < serverParam.getMaxConnNum();i++) {
				//��⵱ǰ�豸�Ƿ�ʹ��
				if(!serverParam.getClientConnParams()[i].getUseFlag()) 
					continue;
				
				if(serverParam.getClientConnParams()[i].getConnFlag()) {
					//��Ҫ������״̬д�����ݿ�
					serverParam.getClientConnParams()[i].setConnFlag(false);
					online2Database(serverParam.getClientConnParams()[i].getSocketChannel());
					continue;//�������ϾͲ��������״̬
				}
				
				//�����ϴλʱ���뵱ǰʱ���ʱ���
				int interval = ConvertDateTime.calcTime2Sec(nowTime, 
						serverParam.getClientConnParams()[i].getCommTime());
				if(interval <= serverParam.getOfflineTime()) continue;
				
				//����豸�����ߣ�����Ҫ������ݣ������±����
				offlineDatabase(serverParam.getClientConnParams()[i].getSocketChannel());
				serverParam.getClientConnParams()[i].setUseFlag(false);
				serverParam.getClientConnParams()[i].setSocketChannel(null);
				//�±����
				serverParam.getConnNumQueue().DeQueue(i);
			}
		}
		
		System.out.println("��ʱ����߳��˳�");
	}
	
	//���豸������״̬д�����ݿ�
	private void online2Database(SocketChannel socket) {
		
	}
	
	/**
	 * 	����������ʱ����ǰ���Ӷ�Ӧ�������豸������
	 * */
	private void offlineDatabase(SocketChannel socket) {
		
	}
}
