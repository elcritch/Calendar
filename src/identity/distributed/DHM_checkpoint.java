/**
 * 
 */
package identity.distributed;

/**
 * @author jaremy
 *
 */
public class DHM_checkpoint extends DHmsg {

	/**
	 * @param type
	 * @param lamport
	 */
	public DHM_checkpoint(int type, Long lamport, int vote_msg) {
		super(type, lamport);
		this.type = vote_msg;
	}

}
