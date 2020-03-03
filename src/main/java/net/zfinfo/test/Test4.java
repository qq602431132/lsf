package net.zfinfo.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class Test4 {
	/**
	 * Checks to see if a specific port is available.
	 *
	 * @param port the port to check for availability
	 */
	public static boolean available(int port) {
	    if (port < 1 || port > 65535) {
	        throw new IllegalArgumentException("Invalid start port: " + port);
	    }
	
	    ServerSocket ss = null;
	    DatagramSocket ds = null;
	    try {
	        ss = new ServerSocket(port);
	        ss.setReuseAddress(true);
	        ds = new DatagramSocket(port);
	        ds.setReuseAddress(true);
	        return true;
	    } catch (IOException e) {
	    } finally {
	        if (ds != null) {
	            ds.close();
	        }
	
	        if (ss != null) {
	            try {
	                ss.close();
	            } catch (IOException e) {
	                /* should not be thrown */
	            }
	        }
	    }
	
	    return false;
	}
	public static void processBuilderCommand() throws Exception {
	      
        List<String> commands = new ArrayList<>();
        commands.add("cmd.exe");
        commands.add("/c");
        commands.add("dir");
        commands.add("E:\\flink");
        ProcessBuilder pb =new ProcessBuilder(commands);
        //可以修改进程环境变量
        pb.environment().put("DAXIN_HOME", "/home/daxin");
        System.out.println(pb.directory());
         
        Process process = pb.start();
        int status = process.waitFor();
        System.out.println(pb.environment());
         
        System.out.println(status);
        InputStream in = process.getInputStream();
         
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String line = br.readLine();
        while(line!=null) {
            System.out.println(line);
            line = br.readLine();
        }
         
    }

	public static void main(String[] args) {
		System.out.println(Test4.available(8888));
		try {
			processBuilderCommand();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}

