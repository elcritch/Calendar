
/**
	A simple implementation of a semaphore object in Java.
*/


import java.io.*;


public class ElectionLock
{
   boolean election = false;
   public static Coordinator_Message obj ;
   
	public synchronized void startElection( )
	{
	   election = true;
	}

	public synchronized Coordinator_Message waitForElection( )
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
		return obj;
	}

	public synchronized void endElection(Coordinator_Message objct )
	{
      election = false;
		notifyAll();
		obj = objct;
		
	}

}

