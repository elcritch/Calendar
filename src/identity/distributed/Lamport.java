package identity.distributed;

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
}

