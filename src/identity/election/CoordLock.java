
/**
	A simple implementation of a semaphore object in Java.
*/


import java.io.*;


public class CoordLock
{
   boolean coord = false;
   
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

}

