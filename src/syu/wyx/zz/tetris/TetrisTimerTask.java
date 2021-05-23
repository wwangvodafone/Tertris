package syu.wyx.zz.tetris;

import java.util.TimerTask;

import android.os.Handler;
import android.os.Message;

public class TetrisTimerTask extends TimerTask{

	public Boolean bTimerCond = Boolean.FALSE;
	private Handler handler = null;
	
	public TetrisTimerTask(Handler handler) {
		this.handler = handler;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		synchronized(this) {
			while (!bTimerCond) {
				try {
					wait();
				} catch (InterruptedException e) {
					Thread.interrupted();
				}
			}
		}
		try {
			Message message = new Message();
			message.what = 1;
			handler.sendMessage(message);			
			Thread.sleep(10);
		} catch (InterruptedException e) {
			
		}
		//bTimerCond = Boolean.FALSE;
	}

}
