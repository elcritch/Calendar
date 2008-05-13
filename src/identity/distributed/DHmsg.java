package identity.distributed;

import java.io.Serializable;
import java.util.UUID;

/**
 * <<Class summary>>
 *
 * @author Jaremy Creechley &lt;&gt;
 * @version 0.1
 */
abstract class DHmsg implements Serializable, Types
{

   protected int type;
   protected Long lamport;
   protected UUID coord;

   public DHmsg() 
    {}
   
	public DHmsg(int type, Long lamport)
	{
	   this.type = type;
	   this.lamport = lamport;
	}
	
	public String toString() {
	   String tostring = "";
	   tostring += "type: "+type;
	   tostring += " ";
	   tostring += "lamport "+lamport;
	   return tostring;
	}
}

