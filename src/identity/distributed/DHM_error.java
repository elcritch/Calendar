/**
 * 
 */
package identity.distributed;

/**
 * @author jaremy
 *
 */
public class DHM_error extends DHM {

   /**
	 * 
	 */
	private static final long serialVersionUID = -1255012547679559188L;
    String error_msg;
  
   /**
    * @param type
    * @param lamport
    */
   public DHM_error(String error_msg) {
      super(ERROR);
      this.error_msg = error_msg;
   }

   /**
    * @param type
    * @param lamport
    */
   public DHM_error(String error_msg, int err_type) {
      super(err_type);
      this.error_msg = error_msg;
   }

   /**
    * @param type
    * @param lamport
    */
   public DHM_error(DHM msg, String error_msg) {
      super(msg.type);
      this.error_msg = error_msg;
   }

}
