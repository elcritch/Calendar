package identity.election;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;



public class ServerList implements Iterable<InetAddress>
{
	// private Sorted<InetAddress> servers = Collections.synchronizedSortedSet(new TreeSet<InetAddress>());
	private Set<InetAddress> servers = Collections.synchronizedSet(new HashSet<InetAddress>());
	private InetAddress myAddress = null;

	public ServerList (InetAddress myAddress) {
		servers.add(myAddress);
		this.myAddress = myAddress;
	}

	/*
      boolean	hasNext() 
                Returns true if the iteration has more elements.
       E	next() 
                Returns the next element in the iteration.
       void	remove() 
                Removes from the underlying collection the last element returned by the iterator (optional operation).
	 */

	public boolean add(InetAddress newaddr) {
		return servers.add(newaddr);
	}

	public boolean remove(InetAddress remaddr) {
		return servers.remove(remaddr);
	}
	

	public void removeAll(InetAddress remaddr) {
		servers.clear();
	}
	
	public InetAddress[] toArray() {
		return servers.toArray(new InetAddress[0] );
	}
	
	public int hashCode() {
		return servers.hashCode();
	}
	
	@SuppressWarnings("unchecked")
	public Iterator<InetAddress> iterator() {
		InetAddress[] toarray = servers.toArray(new InetAddress[0]);
		InetComparator intcomp = new InetComparator();
		List lst = Arrays.asList(toarray);
		Collections.sort(lst, intcomp);
		//Arrays.sort(toArray, new InetComparator());
		Iterator<InetAddress> iter = new ServerListIterable<InetAddress>(lst,myAddress);
		return iter;
	}


	public static void main(String[] args) throws Exception {
		String ip1 = "192.168.0.1";
		String ip2 = "192.168.48.2";
		String ip3 = "191.1.1.1";
		String ip4 = "192.168.12.2";
		InetAddress me = InetAddress.getLocalHost();
		ServerList ips = new ServerList(me);
		ips.add(InetAddress.getByName(ip1));
		ips.add(InetAddress.getByName(ip2));
		ips.add(InetAddress.getByName(ip3));
		ips.add(InetAddress.getByName(ip4));

		Iterator<InetAddress> iter = ips.iterator();
		
		System.out.println("Me: "+me);
		int i = 0;
		while (iter.hasNext()) {
			InetAddress ip = iter.next();
			System.out.println("ip: "+ip+" hash: "+ip.hashCode());
		}
		
		System.exit(0);
	}
}

@SuppressWarnings("hiding")
class ServerListIterable<InetAddress> implements Iterator<InetAddress> {

	private List<InetAddress> servers;
	private InetAddress myAddress;
	private int idx;
	private int myIdx;

	public ServerListIterable(List<InetAddress> servers, InetAddress myAddress) {
		this.servers = servers;
		this.myAddress = myAddress;
		this.myIdx = servers.indexOf(myAddress);
		
		this.idx = myIdx;
	}

	public boolean hasNext( ) {
		if ( (( idx+1 >= servers.size() ) ? 0 : idx+1) == myIdx )
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
	public InetAddress next( ) {
		// return next ip, unless come back to self?
		if (!hasNext())
			throw new NoSuchElementException();
		
		idx = ( idx+1 >= servers.size() ) ? 0 : idx+1;
		InetAddress result = servers.get(idx);
		return result;
	}

	public void remove( ) {

	}
}

class InetComparator implements Comparator<InetAddress> {

	public int compare(InetAddress o1, InetAddress o2) {
		Integer ip1;
		Integer ip2;
		ip1 = new Integer( o1.hashCode());
		ip2 = new Integer( o2.hashCode());
		return ip1.compareTo(ip2);
	}

	
}