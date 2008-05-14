#!/bin/zsh
# Zsh is cooler!

echo $PWD

#echo "build current classes"
#ant build
port=5236
r=$(uuidgen)
user="UserName"${r[1,6]}
echo $user

echo -e "\nCreating"
echo "=============================="
java -classpath bin/ identity.client.IdClient localhost $port \
	--create $user --password pass-$user
echo "=============================="
java -classpath bin/ identity.client.IdClient localhost $port \
	--create Other-$user --password Otherpass-$user
	
echo -e "\nModifying"	
echo "=============================="
java -classpath bin/ identity.client.IdClient localhost $port\
	--modify $user $user-mod --password pass-$user 

echo -e "\nGetting all"	
echo "=============================="
java -classpath bin/ identity.client.IdClient localhost $port \
	--get all 

echo "java -classpath bin/ identity.client.IdClient localhost $port \
	--get users"
echo -e "\nGetting users"	
echo "=============================="
java -classpath bin/ identity.client.IdClient localhost $port \
	--get users 

echo -e "\nGetting uuids"	
echo "=============================="
java -classpath bin/ identity.client.IdClient localhost $port \
	--get uuids 

echo -e "\nLookup"		
echo "=============================="
java -classpath bin/ identity.client.IdClient localhost $port \
	--lookup $user-mod
	
echo -e "\nRevLookup error"	
echo "============================== This should produce an error"
java -classpath bin/ identity.client.IdClient localhost $port \
	--reverse-lookup $user-mod


echo -e "\n\n\n####################################\n Testing Calendar functionality"

echo -e "\n --show cal"	
echo "=============================="
java -classpath bin/ identity.client.IdClient localhost $port \
 	--show cal -u $user-mod --password pass-$user	
echo "=============================="
java -classpath bin/ identity.client.IdClient localhost $port \
 	--show cal -u $user-mod -p pass-$user	


echo -e "\n --del cal "	
echo "=============================="
echo "### delete non-existing user"
java -classpath bin/ identity.client.IdClient localhost $port \
	--del cal -u $user-mod --password pass-$user -s 2


echo -e "\n --add cal "	
echo "=============================="
echo "### all options present: private"
java -classpath bin/ identity.client.IdClient localhost $port \
	--new cal -u $user-mod --password pass-$user -t 11/30/2008 2:45PM -sl private -des i ma cwdoing good today -du 40
echo "=============================="
echo "### all options present: private"
java -classpath bin/ identity.client.IdClient localhost $port \
	--new cal -u $user-mod --password pass-$user -t 10/5/2008 7:03PM -sl private -des third descr -du 40
echo "=============================="
echo "### all options present: public"
java -classpath bin/ identity.client.IdClient localhost $port \
	--new cal -u $user-mod --password pass-$user -t 1/15/2009 7:45AM -sl public -des Something long winded -du 100
echo "=============================="
echo "### no descr present: private"
echo "### should produce error base on now descr being present"
java -classpath bin/ identity.client.IdClient localhost $port \
	--new cal -u $user-mod --password pass-$user -t 1/15/2010 11:06AM -sl public -des -du 40 2>&1 | grep "Usage: java IdClient"

echo "=============================="
echo -e "\n --show cal current user"	
java -classpath bin/ identity.client.IdClient localhost $port \
 	--show cal -u $user-mod --password pass-$user
echo "=============================="
echo -e "\n --show cal logging in as other user"	
java -classpath bin/ identity.client.IdClient localhost $port \
 	--show cal -rusr $user-mod -u Other-$user --password Otherpass-$user

echo -e "\n --del cal "	
echo "=============================="
echo "### delete 1 entry "
java -classpath bin/ identity.client.IdClient localhost $port \
	--del cal -u $user-mod --password pass-$user -s 1

echo -e "\n --show cal"	
echo "=============================="
java -classpath bin/ identity.client.IdClient localhost $port \
 	--show cal -u $user-mod --password pass-$user

echo -e "\n\n====== UserInfo's"
echo -e "java -classpath bin/ identity.client.IdClient localhost $port"
echo -e "-u $user-mod --password pass-$user" 
echo -e "-u Other-$user --password Otherpass-$user"
echo "====== Resulting server calendar"
cat CALENDAR

