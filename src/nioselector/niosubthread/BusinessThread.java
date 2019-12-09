package nioselector.niosubthread;

import nioselector.ServerParam;
import nioselector.niodata.ClientRwData;

/**
 * 	ҵ�����߳�
 * */
public class BusinessThread implements Runnable {
	private ServerParam serverParam = null;
	private ClientRwData cwd = null;
	
	public BusinessThread(ServerParam sp) {
		this.serverParam = sp;
	}
	
	@Override
	public void run() {
		System.out.println("Starting business thread...");
		// TODO Auto-generated method stub
		while(serverParam.getRunStatu()) {//������յ�����
			if(serverParam.getRMsgQueue().getQueueSize() <= 0) {
				Thread.yield();
				continue;
			}
			ClientRwData crd = serverParam.getRMsgQueue().EnQueue();
			System.out.println("���յ�������Ϊ��" + crd.getMsg());
			
			cwd = new ClientRwData();
			cwd.setSocketChannel(crd.getSocketChannel());
			cwd.setMsg("Hello," + crd.getMsg());
			serverParam.getWMsgQueue().DeQueue(cwd);
		}
	}

}
