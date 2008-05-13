/**
 * 
 */
package identity.distributed;

/**
 * @author jaremy
 *
 */
public class DHM_vote extends DHmsg {

	/**
	 * @param type
	 * @param lamport
	 */
	public DHM_vote(int type, Long lamport, int vote_msg) {
		super(type, lamport);
		this.type = vote_msg;
	}

}
