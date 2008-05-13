import java.io.IOException;
import java.net.Inet4Address;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;



public class ServerList implements Iterable<Inet4Address>
{
	// private Sorted<Inet4Address> servers = Collections.synchronizedSortedSet(new TreeSet<InetAddress>());
	private Set<Inet4Address> servers = Collections.synchronizedSet(new HashSet<Inet4Address>());
	private Inet4Address myAddress = null;

	ServerList (Inet4Address myAddress) {
		servers.add(myAddress);
	}

	/*
      boolean	hasNext() 
                Returns true if the iteration has more elements.
       E	next() 
                Returns the next element in the iteration.
       void	remove() 
                Removes from the underlying collection the last element returned by the iterator (optional operation).
	 */



	public static void main(String[] args) {
		System.exit(0);
	}

	@SuppressWarnings("unchecked")
	public Iterator<Inet4Address> iterator() {
		Inet4Address[] toArray = (Inet4Address[])servers.toArray();
		InetComparator intcomp = new InetComparator();
		List lst = Arrays.asList(toArray);
		Collections.sort(lst, intcomp);
		//Arrays.sort(toArray, new InetComparator());
		Iterator<Inet4Address> iter = new ServerListIterable<Inet4Address>(lst,myAddress);
		return iter;
	}

}

@SuppressWarnings("hiding")
class ServerListIterable<Inet4Address> implements Iterator<Inet4Address> {

	private List<Inet4Address> servers;
	private Inet4Address myAddress;
	private int idx;
	private int myIdx;

	public ServerListIterable(List<Inet4Address> servers, Inet4Address myAddress) {
		this.servers = servers;
		this.myAddress = myAddress;
		this.myIdx = servers.indexOf(myAddress);
		this.idx = myIdx+1;
	}

	public boolean hasNext( ) {
		if ( idx == myIdx )
			return false;
		else
			return true;
	}

	/**
	 * next
	 * this method returns a looping list of all IP addressess in the ring starting from my current address.
	 * @param  
	 * @return 
	 */
	public Inet4Address next( ) {
		// return next ip, unless come back to self?
		if (!hasNext())
			throw new NoSuchElementException();
		
		Inet4Address result = servers.get(idx);
		
		idx = ( idx+1 >= servers.size() ) ? 0 : idx+1;
		
		return null;
	}

	public void remove( ) {

	}
}

class InetComparator implements Comparator<Inet4Address> {

	public int compare(Inet4Address o1, Inet4Address o2) {
		Integer ip1;
		Integer ip2;
		try {
			ip1 = new Integer( Utility.getInt(o1.getAddress()));
			ip2 = new Integer( Utility.getInt(o2.getAddress()));
		} catch (IOException e) {
			e.printStackTrace();
			throw new ClassCastException();
		}
		return ip1.compareTo(ip2);
	}

	
}