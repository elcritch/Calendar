/**
 * 
 */
package identity.distributed;

/**
 * @author jaremy
 *
 */
public class DHM_checkpoint extends DHM {
	public int checksum;
	/**
	 * 
	 */
	private static final long serialVersionUID = -8098519733248856130L;

	/**
	 * @param type
	 * @param lamport
	 */
	public DHM_checkpoint(int type, int checksum) {
		super(type);
		this.checksum = checksum;
	}

}
