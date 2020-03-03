package net.zfinfo.test;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Test1 implements Runnable {
	// private static String host="210.39.3.164";
	private static int i = 0;
	static Date one, two;

	public synchronized int get() {

		if (i >= 1024) {
			notifyAll();
		}
		return i++;
	}

	public static void main(String argv[]) {
		one = new Date();
		// long time=one.getTime()-two.getTime();
		// System.out.println(time);
		ExecutorService pool = Executors.newFixedThreadPool(30);
		for (int j = 0; j < 15; j++) {
			pool.submit(new Thread(new Test1()));
		}
		pool.shutdown();
		while (true) {
			if (pool.isTerminated()) {
				System.out.println(two.getTime() - one.getTime());
				break;
			}
		}
	}

	public void run() {
		int temp;
		while ((temp = get()) < 1024) {
			try {
				Socket s = new Socket(InetAddress.getLocalHost(), temp);
				System.out.println("服务端口:   " + temp);
				s.close();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ConnectException e) {
//				System.out.println(temp + "不是服务端口");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (temp == 1023) {
				two = new Date();
			}
		}
	}
}