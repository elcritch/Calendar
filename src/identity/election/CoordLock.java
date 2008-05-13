package identity.election;


/**
	A simple implementation of a semaphore object in Java.
 */


import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;


public class CoordLock
{
	private boolean coord = false;
	private InetAddress coordip;

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
			this.coordip = InetAddress.getByAddress(Utility.getBytes(coordip));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public synchronized void setCoordInetAddress(InetAddress coordip) {
		this.coordip = coordip;
	}

	public synchronized InetAddress getCoordInetAddress() {
		return coordip;
	}
}

