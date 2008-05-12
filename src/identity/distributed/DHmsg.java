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
	protected Integer lamport;
	protected UUID uuid;

	/**
	 * 
	 */
	public DHmsg()
	{
	}
}

