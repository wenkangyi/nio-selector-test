package nioselector.niosubthread;

import java.io.IOException;
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
			/**
			 * 100msִ��һ��
			 * */
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
					continue;//δ��ʹ�ã�����ִ�м���
				
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
				offline2Database(serverParam.getClientConnParams()[i].getSocketChannel());
				serverParam.getClientConnParams()[i].setUseFlag(false);
				serverParam.getClientConnParams()[i].setSocketChannel(null);
				//�±����
				serverParam.getConnNumQueue().DeQueue(i);
				System.out.println("���Ӻţ� " + i + " ���豸����");
			}
		}
		
		System.out.println("��ʱ����߳��˳�");
	}
	
	//���豸������״̬д�����ݿ�
	private void online2Database(SocketChannel socket) {
		System.out.println("��Ҫ��¼����״̬");
		try {
			System.out.println("�豸��" + socket.getRemoteAddress() + " ����");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 	����������ʱ����ǰ���Ӷ�Ӧ�������豸������
	 * */
	private void offline2Database(SocketChannel socket) {
		System.out.println("��Ҫ��¼����״̬");
		try {
			if(socket.isOpen()) {
				System.out.println("�豸��" + socket.getRemoteAddress() + " ����");
				socket.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
