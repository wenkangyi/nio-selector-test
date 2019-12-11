package nioselector.niosubthread;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.Date;

import nioselector.ServerParam;
import nioselector.convertPackage.ConvertDateTime;

/**
 *  当超时后，需要更新数据库状态
 * 100毫秒运行一次
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
			 * 100ms执行一次
			 * */
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Date nowTime = new Date();
			for(int i=0;i < serverParam.getMaxConnNum();i++) {
				//检测当前设备是否被使用
				if(!serverParam.getClientConnParams()[i].getUseFlag()) 
					continue;//未被使用，即不执行检索
				
				if(serverParam.getClientConnParams()[i].getConnFlag()) {
					//需要将在线状态写入数据库
					serverParam.getClientConnParams()[i].setConnFlag(false);
					online2Database(serverParam.getClientConnParams()[i].getSocketChannel());
					continue;//刚连接上就不检测在线状态
				}
				
				//计算上次活动时间与当前时间的时间差
				int interval = ConvertDateTime.calcTime2Sec(nowTime, 
						serverParam.getClientConnParams()[i].getCommTime());
				if(interval <= serverParam.getOfflineTime()) continue;
				
				//如果设备已离线，即需要清除数据，并将下标进队
				offline2Database(serverParam.getClientConnParams()[i].getSocketChannel());
				serverParam.getClientConnParams()[i].setUseFlag(false);
				serverParam.getClientConnParams()[i].setSocketChannel(null);
				//下标进队
				serverParam.getConnNumQueue().DeQueue(i);
				System.out.println("连接号： " + i + " 的设备离线");
			}
		}
		
		System.out.println("超时监控线程退出");
	}
	
	//将设备的在线状态写入数据库
	private void online2Database(SocketChannel socket) {
		System.out.println("需要记录在线状态");
		try {
			System.out.println("设备：" + socket.getRemoteAddress() + " 上线");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 	当连接离线时，当前连接对应的所有设备均离线
	 * */
	private void offline2Database(SocketChannel socket) {
		System.out.println("需要记录离线状态");
		try {
			if(socket.isOpen()) {
				System.out.println("设备：" + socket.getRemoteAddress() + " 离线");
				socket.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
