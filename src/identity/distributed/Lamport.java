package identity.distributed;

import java.util.UUID;

/**
 * <<Class summary>>
 *
 * @author Jaremy Creechley &lt;&gt;
 * @version $Rev$
 */
public final class Lamport {
   // msgid is a lamport time of the message
	public Long msgid;
	// we include the unique coordinator UUID for further checking
	public UUID coord;
	public Lamport(Long msgid, UUID coord)
	{
	   this.msgid = msgid;
	   this.coord = coord;
	}
	
	public boolean equals(Object other) {
	   if (other instanceof Lamport) {
	      Lamport o = (Lamport)other;
	      return ( msgid.equals(o.msgid) && coord.equals(o.coord) );
      } else {
         return false;
      }
	}
}

