package identity.server;
public class UserInfoException extends Exception implements java.io.Serializable
{
   /**
	 * 
	 */
	private static final long serialVersionUID = -7849475725008453376L;
/**
	 * 
	 */
// This public class delineates a basic class to report errors with.
   String errorMessage;
   int errorType;
   public final String [] errorTypes = {
                              "Unknown Error",
                              "UserName Error",
                              "UUID Error"
                              };
   public UserInfoException (String errorMessage, int errorType) {
      this.errorMessage = errorMessage;
      if ( errorType >0 && errorType < errorTypes.length )
         this.errorType = errorType;
      else
         this.errorType = 0;
      
   }
   
   UserInfoException (int errorType) {
      if ( errorType >0 && errorType < errorTypes.length )
         this.errorType = errorType;
      else
         this.errorType = 0;
      this.errorMessage = errorTypes[errorType];
      
   }
   
   /**
    * toString
    */
   public String toString() {
   	return "UserInfoException: "+errorType+" message: "+errorMessage;
   }
   
}
