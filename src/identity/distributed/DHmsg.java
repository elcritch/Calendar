package identity.distributed;

import java.io.Serializable;
import java.util.UUID;

/**
 * <<Class summary>>
 *
 * @author Jaremy Creechley &lt;&gt;
 * @version 0.1
 */
public class DHmsg implements Serializable, Types
{

	protected int type;
	protected Long lamport;
	protected UUID uuid;

   public DHmsg() 
    {}
   
	public DHmsg(int type, Long lamport)
	{
	   this.type = type;
	   this.lamport = lamport;
	}
}

