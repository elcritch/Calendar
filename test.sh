#!/bin/zsh
# Zsh is cooler!

echo $PWD
if [[ ${PWD:t} != "IdentityUUID" ]]; then
	exit
fi

#echo "build current classes"
#ant build

r=$(uuidgen)
user="UserName"${r[1,6]}
echo $user

echo -e "\nCreating"
echo "==============="
java -classpath bin/ identity.client.IdClient localhost 5299 \
	--create $user --password pass-$user

echo -e "\nModifying"	
echo "==============="
java -classpath bin/ identity.client.IdClient localhost \
	--modify $user $user-mod --password pass-$user

echo -e "\nGetting all"	
echo "==============="
java -classpath bin/ identity.client.IdClient localhost 5299 \
	--get all | grep $user-mod

echo -e "\nLookup"		
echo "==============="
java -classpath bin/ identity.client.IdClient localhost 5299 \
	--lookup $user-mod
	
echo -e "\nRevLookup"	
echo "==============="
java -classpath bin/ identity.client.IdClient localhost 5299 \
	--reverse-lookup $user-mod
	
echo -e "\nRevLookup"	
echo "==============="
java -classpath bin/ identity.client.IdClient localhost 5299 \
	-r $(java -classpath bin/ identity.client.IdClient localhost 5299 -l $user-mod | 
			grep UUID: | awk '{print $2}')
	
