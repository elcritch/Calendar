package identity.election;


/**
	A simple implementation of a semaphore object in Java.
 */


import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;


public class CoordLock
{
	private boolean coord = false;
	private InetAddress coordip;
	private static UUID coordsession = UUID.randomUUID();
	private static AtomicLong lamport = new AtomicLong(0);
	private ElectionLock coordWait = new ElectionLock();
	
	public CoordLock() {
		coordWait.startElection();
	}
	
	public synchronized void notCoord( )
	{
		coord = false;
	}

	public synchronized void amCoordinator( )
	{
		boolean interrupted = false;
		while (!coord) {
			try {
				wait();
			}
			catch (InterruptedException ie) {
				interrupted = true;
			}
		}
		if (interrupted) Thread.currentThread().interrupt();
	}

	public synchronized void becomeCoordinator( )
	{
		coord = true;
		notifyAll();
	}

	public synchronized void setCoordInetAddress(Integer coordip) {
		try {
		   System.out.println("got from groupie");
			setCoordInetAddress( InetAddress.getByAddress(Utility.getBytes(coordip)) );
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized void setCoordInetAddress(InetAddress coordip) {
		this.coordip = coordip;
		InetAddress myaddr = null;
		coordWait.endElection();
		// set becomeCoordinator if my ip address equals that of the new coordip.
		try {
			myaddr = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if ( (coordip != null) && coordip.equals(myaddr)) {
			becomeCoordinator();
			CoordLock.coordsession = UUID.randomUUID();
			CoordLock.lamport = new AtomicLong(0);
		} else {
			this.coord = false;
			CoordLock.coordsession = null;
			CoordLock.lamport = null;
		}

	}

	public synchronized InetAddress getCoordInetAddress() {
		return coordip;
	}

	public static synchronized UUID getCoordsession() {
		return coordsession;
	}
	
	public static synchronized Long getLamport() {
		return CoordLock.lamport.incrementAndGet();
	}
	
	public synchronized boolean checkCoordinator() {
	   return coord;
	}

	public void waitForCoord() {
		// TODO Auto-generated method stub
		coordWait.waitForElection();
	}

}

