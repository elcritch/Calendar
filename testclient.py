#!/opt/jaremy/jython/jython
# import our packages and code
import identity.client as ic;
from java.io import File
from java.io import UnsupportedEncodingException
from java.rmi.registry import LocateRegistry
from java.rmi.registry import Registry
from java.rmi import *
from java.security import MessageDigest
from java.security import NoSuchAlgorithmException
from java.text import DateFormat
from java.text import ParseException
from java.text import SimpleDateFormat
from java.util import ArrayList
from java.util import Arrays
from java.util import Calendar
from java.util import Date
from java.util import Hashtable
from java.util import Iterator
from java.util import List
from java.util import UUID

import sun.misc.BASE64Encoder;

import identity.calendar.CalendarDB;
import identity.calendar.CalendarEntry;
from identity.server import *

# setup python java imports
from java import *
from java.lang import System
import os,sys

os.environ['PYTHONINSPECT'] = '1'

d = dir


print "Setting files"
client_trust = "./resources/Client_Truststore";
security_policy = "./resources/mysecurity.policy";
client_trust_file = File(client_trust);
security_policy_file = File(security_policy);


print "Setting system properties"
System.setProperty("javax.net.ssl.trustStore", client_trust);
System.setProperty("java.security.policy", security_policy);

# our arguement list
host = "localhost"
port = 5299
args = ["--lookup","user1"]
args1 = ["--get", "all"]

argh = []
argh.append(["--create","user","--password","pass-user"])
argh.append(["--create","Other-user","--password","Otherpass-user"])
argh.append(["--modify","user","user-mod","--password","pass-user"])
argh.append(["--get","all"])
argh.append(["--lookup","user-mod"])
argh.append(["--reverse-lookup","user-mod"])
argh.append(["--show","cal","-u","user-mod","--password","pass-user"])
argh.append(["--show","cal","-u","user-mod","-p","pass-user"])
argh.append(["--del","cal","-u","user-mod","--password","pass-user","-s","2"])
argh.append(["--new","cal","-u","user-mod","--password","pass-user","-t","11/30/2008","2:45PM","-sl","private","-des","i","ma","cwdoing","good","today","-du","40"])
argh.append(["--new","cal","-u","user-mod","--password","pass-user","-t","10/5/2008","7:03PM","-sl","private","-des","third","descr","-du","40"])
argh.append(["--new","cal","-u","user-mod","--password","pass-user","-t","1/15/2009","7:45AM","-sl","public","-des","Something","long","winded","-du","100"])
argh.append(["--show","cal","-u","user-mod","--password","pass-user"])
argh.append(["--show","cal","-rusr","user-mod","-u","Other-user","--password","Otherpass-user"])
argh.append(["--del","cal","-u","user-mod","--password","pass-user","-s","1"])
argh.append(["--show","cal","-u","user-mod","--password","pass-user"])

print "creating new client"
c = ic.IdClient()

print "Try parsing input"

def set_client():
	c.parseInput(args)
	c.setServerName(host, port)
	c.parse_switches(args, numinputs)
	c.perform()
