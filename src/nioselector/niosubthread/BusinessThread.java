package nioselector.niosubthread;

import nioselector.ServerParam;
import nioselector.niodata.ClientRwData;

/**
 * 	业务处理线程
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
		while(serverParam.getRunStatu()) {//处理接收的数据
			if(serverParam.getRMsgQueue().getQueueSize() <= 0) {
				Thread.yield();
				continue;
			}
			ClientRwData crd = serverParam.getRMsgQueue().EnQueue();
			System.out.println("接收到的数据为：" + crd.getMsg());
			
			cwd = new ClientRwData();
			cwd.setSocketChannel(crd.getSocketChannel());
			cwd.setMsg("Hello," + crd.getMsg());
			serverParam.getWMsgQueue().DeQueue(cwd);
		}
	}

}
