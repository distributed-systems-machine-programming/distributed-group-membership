JAVAC = javac
JAVA = java

all: node client give_permissions

node: src/tolerantKeyValStore/runner.java src/tolerantKeyValStore/MemberListEntry.java src/tolerantKeyValStore/Heart.java src/tolerantKeyValStore/Messenger.java src/tolerantKeyValStore/FaultRateCalculator.java src/tolerantKeyValStore/LogFormatter.java src/tolerantKeyValStore/MemberList.java src/tolerantKeyValStore/LogWriter.java src/tolerantKeyValStore/Gossiper.java src/tolerantKeyValStore/KeyValEntry.java src/tolerantKeyValStore/Value.java src/tolerantKeyValStore/MapStore.java src/tolerantKeyValStore/SenderThreadParameter.java src/tolerantKeyValStore/ReplicationManager.java src/tolerantKeyValStore/AckHandler.java src/tolerantKeyValStore/AckCount.java
	$(JAVAC) src/tolerantKeyValStore/runner.java src/tolerantKeyValStore/MemberListEntry.java src/tolerantKeyValStore/Heart.java src/tolerantKeyValStore/Messenger.java src/tolerantKeyValStore/FaultRateCalculator.java src/tolerantKeyValStore/LogFormatter.java src/tolerantKeyValStore/MemberList.java src/tolerantKeyValStore/LogWriter.java src/tolerantKeyValStore/Gossiper.java src/tolerantKeyValStore/KeyValEntry.java src/tolerantKeyValStore/Value.java src/tolerantKeyValStore/MapStore.java src/tolerantKeyValStore/SenderThreadParameter.java src/tolerantKeyValStore/ReplicationManager.java src/tolerantKeyValStore/AckHandler.java src/tolerantKeyValStore/AckCount.java -d make_bin

client: src/tolerantKeyValStore/Client.java src/tolerantKeyValStore/KeyValEntry.java src/tolerantKeyValStore/Value.java src/tolerantKeyValStore/MapStore.java src/tolerantKeyValStore/analysis.java
	$(JAVAC) src/tolerantKeyValStore/Client.java src/tolerantKeyValStore/KeyValEntry.java src/tolerantKeyValStore/Value.java src/tolerantKeyValStore/MapStore.java src/tolerantKeyValStore/analysis.java -d make_bin

give_permissions: node.sh contact.sh client.sh now.sh
	chmod 755 node.sh
	chmod 755 contact.sh 
	chmod 755 client.sh 
	chmod 755 now.sh 


clean:
	rm -f make_bin/*.class





