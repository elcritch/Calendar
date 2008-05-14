package identity.distributed;

import java.security.acl.Group;
import java.util.UUID;

import identity.calendar.CalendarEntry;
import identity.election.ElectionMonitor;
import identity.election.Groupie;
import identity.server.SharedData;
import identity.server.UserInfo;
import identity.util.PrintColor;

public class TestDH implements Types{

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		PrintColor.type = 1;

		
		SharedData data = new SharedData();
		Groupie g = new Groupie(data);
		
		int dhmport = 5294;
		DistributedHashServer dhserver = new DistributedHashServer( data, dhmport);

		dhserver.start();
		
		
		System.out.println("Starting DH test");
		
		DistributedHash dh = new DistributedHash(data);
		ElectionMonitor elecm = new ElectionMonitor("monitor",data);
		elecm.start();
		
		//
		CalendarEntry cal1 = new CalendarEntry(UUID.randomUUID(), 23, null,"stat","desc",90 );
		CalendarEntry cal2 = new CalendarEntry(UUID.randomUUID(), 23, null,"stat","desc",90 );

		UserInfo testuser0 = new UserInfo("testuser00", "mypasswd0", "No Real Name");
		UserInfo testuser1 = new UserInfo("testuser10", "mypasswd1", "Bob Jon");
		UserInfo testuser2 = new UserInfo("testuser30", "mypasswd2", "Welly Nilson");
		UserInfo testuser3 = new UserInfo("testuser40", "mypasswd3", "Mark Henry");
		UserInfo testuser2a = new UserInfo("testuser40", "mypasswd3a", "Teh Hax0R!");
		
		DHM dhm = new DHM_user(ADD, testuser0);
		dh.sendAll(dhm);
		
	}

}
