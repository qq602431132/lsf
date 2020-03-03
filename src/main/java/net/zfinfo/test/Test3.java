package net.zfinfo.test;

import java.io.IOException;
import java.net.ServerSocket;

public class Test3 {
	/**
	 * 获取可用的tcp端口号
	 * 
	 * @return
	 */
	public static int getAvailableTcpPort() {
	    // 指定范围10000到65535
	    for (int i = 10000; i <= 65535; i++) {
	        try {
	            new ServerSocket(i).close();
	            System.out.println(i);
	            return i;
	        } catch (IOException e) { // 抛出异常表示不可以，则进行下一个
	            continue;
	        }
	    }
		return 0;
	}
	public static void main(String[] args) {
		getAvailableTcpPort();
	}
}

