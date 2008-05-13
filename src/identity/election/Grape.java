package identity.election;


public class Grape extends Thread
{
	public ElectionLock lock;

   public Grape(String s, ElectionLock lock ) {
		super(s);
		this.lock = lock ;
	}

   public void run() {
		// for (int i = 0; i<100; ++i) {
		//    System.out.println("Looping Election and Waiting...");
		//          try {sleep(4000);} catch (Exception e) {System.out.println("sleep error");}
		//          System.out.println("waitForElection");
		//    lock.waitForElection();
		//    System.out.println("election is over\n");
		// }

		if (this.getName().equals("pinot")) {
			for (int i = 0; i < 3; i++) {
				System.out.println("Pinot: starting election.");
				lock.startElection();
				System.out.println("Pinot: Waiting...");
				try {
					sleep(5000);
				}
				catch (Exception e) {
					System.out.println("sleep error");
				}
				System.out.println("Pinot: Waiting for election");
				lock.waitForElection();
				System.out.println("Pinot: Finishing Election");
			}
		}
		else if (this.getName().equals("cabernet")) {
			for (int i = 0; i < 10; i++) {
				System.out.println("cabernet: " + this.getName() + " thread.");
				try {
					sleep(1000);
				}
				catch (Exception e) {
					System.out.println("sleep error");
				}
				System.out.println("cabernet: calling wait");
				lock.waitForElection();
				System.out.println("cabernet: finishing work");
			}
		}
		else {
			for (int i = 0; i < 20; i++) {
				System.out.println("merlot: " + this.getName() + " thread.");
				try {
					sleep(1000);
				}
				catch (Exception e) {
					System.out.println("sleep error");
				}
				System.out.println("merlot: end election!");
				lock.endElection();
				System.out.println("merlot: finishing work");
			}
		}
	}

	public static void main (String args[])
	{
		ElectionLock lock = new ElectionLock();

		Grape g1 = new Grape("merlot", lock );
		g1.start();
		new Grape("pinot", lock ).start();
		lock.startElection();
		new Grape("cabernet", lock ).start();
	}
}


/*
import ElectionLock
import Grape

lock = ElectionLock()

g1 = Grape("merlot",lock)
g1.start()
g2 = Grape("pinot",lock).start()
g3 = Grape("cabernet",lock).start()

lock.startElection()
lock.endElection()
*/



