package identity.client;

import identity.server.IdentityUUID;
import identity.server.UserInfo;
import identity.server.UserInfoException;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * @author jaremy
 *
 */
public class IdentityClient {

	private IdentityClient()
	{}

	public static void main(String[] args)
	{
		if (args.length < 1) {
			System.err.println("Usage: java IdentityClient <host> [<registry-port>]");
			System.exit(1);
		}
		String host = null;
		int registryPort = 1099;
		if (args.length == 1) {
			host = args[0];
		}
		else {
			host = args[0];
			registryPort = Integer.parseInt(args[1]);
		}

		try {
			Registry registry = LocateRegistry.getRegistry(host, registryPort);
			IdentityUUID userdb = (IdentityUUID) registry.lookup("IdentityServer");
			
			//debuggin
			//userdb.empty();
			
			String response = userdb.sayHello();
			System.out.println("response: " + response);
			UserInfo[] testuser = new UserInfo[5];

			testuser[0] = new UserInfo("user00","password4","ccc");
			testuser[1] = new UserInfo("user10","password1","Real Name");
			testuser[2] = new UserInfo("user20","password2","aaa");
			testuser[3] = new UserInfo("user30","password3","bbb");
			testuser[4] = new UserInfo("user10","password1","Real Name");
			UserInfo moduser = new UserInfo(null,"password1111","Real Name v1.1");

			boolean stat;
			UserInfo result;
			for (UserInfo user : testuser) {
				try {
					result = userdb.createUUID(user);
					System.out.println("Local User: "+user);
					
				} catch (UserInfoException uie) {
					System.err.println("UIE: "+uie);
				}
			}
			System.out.println("lookupUUID: "+ userdb.lookupUUID("user2"));
			System.out.println("lookupUUID: "+ userdb.lookupUUID("user0"));

			System.out.println("revlookupUUID: "+ userdb.revlookupUUID(testuser[2].uuid));
			System.out.println("revlookupUUID: "+ userdb.revlookupUUID(testuser[0].uuid));
			
			try {
				System.err.println("Testing modify now");
				System.out.println("orig revlookupUUID: "+ userdb.revlookupUUID(testuser[1].uuid));
				result = userdb.modifyUUID(testuser[1], moduser);
				System.out.println("Modify user: "+result);
				System.out.println("lookupUUID: "+ userdb.lookupUUID("newuser1"));
				System.out.println("revlookupUUID: "+ userdb.revlookupUUID(testuser[1].uuid));
			} catch (UserInfoException uie) {
				System.err.println("UIE: "+uie);
			}
			
			UserInfo[] dump = (UserInfo[]) userdb.dumpContents();
			System.out.println("\nDumping... len: "+dump.length+" dump: "+dump);
			for (int i=0; i<dump.length; ++i ) {
				System.out.println("dump: "+dump[i]);
			}
			
			System.out.println("Done.");
		}
		catch (Exception e) {
			System.err.println("Client exception: " + e.toString());
			e.printStackTrace();
		}
	}//main
}