package identity.election;

/**
	A simple implementation of a semaphore object in Java.
*/


import java.io.*;


public class ElectionLock
{
   boolean election = false;
   
   public synchronized 
   
	public synchronized void startElection( )
	{
	   election = true;
	}

	public synchronized void waitForElection( )
	{
		boolean interrupted = false;
		while (election) {
			try {
				wait();
			}
			catch (InterruptedException ie) {
				interrupted = true;
			}
		}
		if (interrupted) Thread.currentThread().interrupt();
		
	}

	public synchronized void endElection()
	{
      election = false;
		notifyAll();
	
	}

}

