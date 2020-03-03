package net.zfinfo.test;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
 
 
public class PortScan {
 
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		InetAddress inet=null;
		try {
			inet=InetAddress.getByName("127.0.0.1");
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		System.out.println("Scanning ports");
		
		for(int i=5001;i<65536;i++){
			
//			try {
//				Socket s=new Socket(inet,i);
//				
//				System.out.println("enabled port:"+i);
//				
//
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				//e.printStackTrace();
//				System.out.println("busy port:"+i);
//			}
			
			
			
			try {
				DatagramSocket ds=new DatagramSocket(i);
				System.out.println("enabled port:"+i);
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				System.out.println("busy port:"+i);
			}
			
		}
	}
}
