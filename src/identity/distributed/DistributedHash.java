package identity.distributed;

import identity.server.UserInfo;

public class DistributedHash
{
   DistributedDatabase () {
      
   }
   // private UserInfoDataBase userDBwrapper;
   // private ConcurrentHashMap<UUID, UserInfo> userdb;
   
   // methods for UserInfo entries
   public UserInfo UserInfoPut (UUID uuid, UserInfo user) {}
   public UserInfo UserInfoGet (UUID uuid) {}
   public UserInfo UserInfoQuickUUID (String username) {}
   public UserInfo UserInfoDel (UUID uuid) {}
   
   // methods for calendar entries
	public boolean addEntry(CalendarEntry entry) {}
	
	public boolean delEntry(CalendarEntry entry) {}
	public boolean delEntry(UUID uuid, Integer keyid) {}
	
	public CalendarEntry getEntry(CalendarEntry entry) {}
	public CalendarEntry getEntry(UUID uid, Integer key) {}
   
	
}
