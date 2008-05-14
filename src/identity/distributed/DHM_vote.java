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
	 * 
	 */
	private static final long serialVersionUID = -1017197010755188589L;

	/**
	 * @param type
	 * @param lamport
	 */
	public DHM_vote(int vote_msg) {
		super(vote_msg);
	}

}
