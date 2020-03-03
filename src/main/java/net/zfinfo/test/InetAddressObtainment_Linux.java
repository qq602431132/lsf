package net.zfinfo.test;
 
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
/**
 * 程序实现了Linux平台下获得本机ip地址。由于和Windows平台不同，不能用经典的方式查看。
 * <p>
 * 但是可以通过查询网络接口（NetworkInterface）的方式来实现。
 * <p>
 * 本程序中同样运用了JAVA核心技术：枚举类型。
 * <p>
 * 枚举类型不仅可以使程序员少写某些代码，主要还提供了编译时的安全检查，可以很好的解决类安全问题。
 * 我们写JAVA代码时应该有意识去使用。
 * @author HAN
 *
 */
class InetAddressObtainment_Linux{
	public static void main(String[] args){
		Enumeration<NetworkInterface> allNetInterfaces;  //定义网络接口枚举类
		try {
			allNetInterfaces = NetworkInterface.getNetworkInterfaces();  //获得网络接口
 
			InetAddress ip = null; //声明一个InetAddress类型ip地址
			while (allNetInterfaces.hasMoreElements()) //遍历所有的网络接口
			{
				NetworkInterface netInterface = allNetInterfaces.nextElement();
				System.out.println(netInterface.getName());  //打印网端名字
				Enumeration<InetAddress> addresses = netInterface.getInetAddresses(); //同样再定义网络地址枚举类
				while (addresses.hasMoreElements())
				{
					ip = addresses.nextElement();
					if (ip != null && (ip instanceof Inet4Address)) //InetAddress类包括Inet4Address和Inet6Address
					{
						System.out.println("本机的IP = " + ip.getHostAddress());
					} 
				}
			}
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 
	}
}