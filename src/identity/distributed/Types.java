package identity.distributed;

interface Types 
{
   public static final int GET = 0;
   public static final int ADD = 1;
   public static final int DEL = 2;
   
   public static final int VOTE_BEGIN   = 110;
   public static final int VOTE_REQUEST = 120;
   public static final int VOTE_COMMIT  = 130;
   public static final int GET_MSG      = 140;
   public static final int DO_COMMIT    = 150;
}
