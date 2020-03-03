package net.zfinfo.test;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Test2 {
	public static void main(String argv[]) {
		int i;
		Date d = new Date();
		ExecutorService pool = Executors.newFixedThreadPool(1204);
		for (i = 1; i <= 1024; i++) {
			pool.submit(new Thread(new thread(i)));
		}
		pool.shutdown();
		while (true) {
			if (pool.isTerminated()) {
				Date last = new Date();
				System.out.println(last.getTime() - d.getTime());
				break;
			}
		}

	}
}

class thread implements Runnable {
	int i;

	thread(int i) {
		this.i = i;
	}

	public void run() {
		try {
			Socket s = new Socket(InetAddress.getLocalHost(), i);
			System.out.println("服务端口:   " + i);
			s.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConnectException e) {
			// System.out.println(i+"不是服务端口");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}