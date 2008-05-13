package identity.server;

import java.net.Inet4Address;

/**
 * <<Class summary>>
 *
 * @author Jaremy Creechley &lt;&gt;
 * @version $Rev$
 */
public final class SharedData {
   public Inet4Address coordinator;
   public Inet4Address selfaddress;
   public ElectionLock elock;
   public CoordLock clock;
   public ServerList servers;
   
}
