/**
 * 
 */
package identity.distributed;

/**
 * @author jaremy
 *
 */
public class DHM_error extends DHmsg {

   String error_msg;
	/**
	 * 
	 */
	public DHM_error() {
		// TODO Auto-generated constructor stub
	}

   /**
    * @param type
    * @param lamport
    */
   public DHM_error(String error_msg) {
      super(ERROR, ERROR);
      this.error_msg = error_msg;
   }

   /**
    * @param type
    * @param lamport
    */
   public DHM_error(DHmsg msg, String error_msg) {
      super(msg.type, msg.lamport);
      this.error_msg = error_msg;
   }

}
