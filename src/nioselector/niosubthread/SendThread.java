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
			
			//以下进行数据发送处理
			System.out.println("准备发送数据....");
			ClientRwData cwd = serverParam.getWMsgQueue().EnQueue();
			System.out.println("数据长度为：" + cwd.getBytes().length);
			serverParam.getWriteBuff().put(cwd.getBytes());
			serverParam.getWriteBuff().flip();
			
			serverParam.getWriteBuff().rewind();
			
			try {
				cwd.getSocketChannel().write(serverParam.getWriteBuff());
				serverParam.getWriteBuff().clear();//这句必须加，否则会出错
				System.out.println("数据发送完成....");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
