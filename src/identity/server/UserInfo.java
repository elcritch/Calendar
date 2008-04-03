package identity.server;

import java.net.InetAddress;
import java.util.Date;

/*
   UUID.java
   IdentityUUID

   Created by Jaremy Creechley on 2008-03-16.
   Copyright 2008 __MyCompanyName__. All rights reserved.
*/
import java.util.UUID;

/**
    Create everything with final for reasons of consistency. This could be useful and we know that each object has to be the same (aka objectstream chaching issues should be avoided).
*/
public class UserInfo implements java.io.Serializable, Comparable<UUID>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4997597599080737742L;
	public final UUID uuid;
	public final String username;
	public final String md5passwd;
	public final String realname;
	public final String ipaddr;
	public final Date lastdate;

	/**
	* Useful for creating a requestUserInfo process.
	* @throws UserInfoException Use this to throw UserInfoException.
	*/	
	public UserInfo(final UUID uuid, final String username, final String md5passwd, 
				final String realname, final String ipaddr, final Date lastdate) 
	{
		this.uuid = uuid;
		this.username = username;
		this.md5passwd = md5passwd;
		this.realname = realname;
		this.ipaddr = ipaddr;
		this.lastdate = lastdate;
	}

	/**
	* Useful for creating a requestUserInfo process.
	* @throws UserInfoException Use this to throw UserInfoException.
	*/
	public UserInfo(String username, String passwd, String realname) {
		this.uuid = UUID.randomUUID();
		this.username = username;
		this.md5passwd = passwd; 
		this.realname = realname;
		this.lastdate = new Date();
		this.ipaddr = null;
	}

	/**
	* Useful for creating a requestUserInfo process.
	* @throws UserInfoException Use this to throw UserInfoException.
	*/		
	public UserInfo(UserInfo recreate)
	{
		this.uuid = recreate.uuid;
		this.username = recreate.username;
		this.md5passwd = recreate.md5passwd;
		this.realname = recreate.realname;
		this.ipaddr = recreate.ipaddr;
		this.lastdate = recreate.lastdate;
	}	

	/**
	* Useful for creating a requestUserInfo process.
	* @throws UserInfoException Use this to throw UserInfoException.
	*/	
	public UserInfo(UserInfo recreate, String ip)
	{
		this.uuid = recreate.uuid;
		this.username = recreate.username;
		this.md5passwd = recreate.md5passwd;
		this.realname = recreate.realname;
		this.ipaddr = ip;
		this.lastdate = new Date();
	}

	/**
	 * setRandomUUID creates a new UserInfo with a randomized UUID
	 *@return new UserInfo with random UUID but with all other fields set the same as current UserInfo
	 */
	public UserInfo setRandomUUID() {
		UserInfo tmp = null;
		tmp = new UserInfo(UUID.randomUUID(), username, md5passwd, realname, ipaddr, lastdate);
		return tmp;
	}
	
	/**
	 * setRandomUUID creates a new UserInfo with a randomized UUID
	 *@return new UserInfo with random UUID but with all other fields set the same as current UserInfo
	 */
	public UserInfo setUUID(UUID uid) {
		UserInfo tmp = null;
		tmp = new UserInfo(uid, username, md5passwd, realname, ipaddr, lastdate);
		return tmp;
	}
	
	
	/**
	 * Setter for md5passwd.
	 * @param newMd5passwd new value for md5passwd
	 */
	public UserInfo setMd5passwd(String newMd5passwd) {
		UserInfo tmp = null;
		tmp = new UserInfo(uuid, username, newMd5passwd, realname, ipaddr, lastdate);
		return tmp;
	}

	/**
	 * Setter for username.
	 * @param newUsername new value for username
	 */
	public UserInfo setUsername(String newUsername) {
		UserInfo tmp = null;
		tmp = new UserInfo(uuid, newUsername, newUsername, realname, ipaddr, lastdate);
		return tmp;
	}
	
	/**
	 * Setter for realname.
	 * @param newRealname new value for realname
	 */
	public UserInfo setRealname(String newRealname) {
		UserInfo tmp = null;
		tmp = new UserInfo(uuid, username, newRealname, newRealname, ipaddr, lastdate);
		return tmp;
	}


	/**
	 * scrubPassword scrubs a given UserInfo of a password and returns a new UserInfo
	 */
	public static UserInfo scrubPassword(UserInfo u) {
		if (u==null) return null;
		return new UserInfo(u.uuid, u.username, null, u.realname, u.ipaddr, u.lastdate);
	}
	
	public int compareTo(UUID o) {
		// TODO Auto-generated method stub
		return this.uuid.compareTo(o);
	}
	
	public String toString() {
		return "UUID: "+uuid+" Name: "+username+" Passwd: "+md5passwd+" IP: "+ipaddr;
	}
// Note: This doesn't seem to work the way my test file did.
/*
	public boolean equals(Object obj) {
		if (obj.getClass() == String.class) {
			System.err.println("EQUATING USERNAME");
			return this.username.equals((String)obj);
		} else if (obj.getClass() == UUID.class) {
			System.err.println("EQUATING UUID");
			return this.uuid.equals((UUID)obj);
		} else {
			System.out.println("EQUATING OBJ");
			return true;
		}
	}
*/
}
