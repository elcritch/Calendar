package identity.distributed;

import identity.server.UserInfo;

/**
 * <<Class summary>>
 *
 * @author Jaremy Creechley &lt;&gt;
 * @version 0.1
 */
public final class DHM_user extends DHM {    
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 2914328767380352429L;
	
	protected UserInfo user;
    /**
     * 
     */
	public DHM_user(int type, UserInfo user) {
		super(type);
		this.user = user;
	}
}
