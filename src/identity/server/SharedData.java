package identity.server;


import identity.election.CoordLock;
import identity.election.ElectionLock;
import identity.election.ServerList;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * <<Class summary>>
 *
 * @author Jaremy Creechley &lt;&gt;
 * @version $Rev$
 */
public final class SharedData {
	public InetAddress selfaddress;
	public ElectionLock elock;
	public CoordLock clock;
	public ServerList servers;

	public SharedData() {
		try {
			selfaddress = InetAddress.getLocalHost();
			elock = new ElectionLock();
			clock = new CoordLock();
			servers = new ServerList(selfaddress);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
