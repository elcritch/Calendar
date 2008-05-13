import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;


public class Test
{
	public static void main(String[] args)
	{
		try
		{
			ElectionLock lock = new ElectionLock();
			InetAddress inet = InetAddress.getLocalHost();
			
			//String localHost = inet.getHostAddress();
			System.out.println("Local ip addr :"+inet);
			int pid =Integer.parseInt(args[1]);
			ElectionListener listen = new ElectionListener(inet,pid,"ElectionListener",lock);
			ElectionMonitor monitor = new ElectionMonitor(inet,pid,"ElectionMonitor",lock);
			monitor.nextIp = args[0];
			monitor.start();
			listen.nextIp = args[0];
			listen.runListener();
			

		}
		catch (SocketException e)
		{
			e.printStackTrace();
		}
		catch (UnknownHostException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
