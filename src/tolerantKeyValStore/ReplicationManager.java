package tolerantKeyValStore;

import java.util.Arrays;

public class ReplicationManager extends Thread {
	
	int localIdentifier;
	Messenger mess;
	MemberList localMemTable;
	int oldPD1;
	int oldPD2;
	int oldPD3;
	int newPD1;
	int newPD2;
	int newPD3;
	Integer[] oldRing = new Integer[6];
	Integer[] newRing = new Integer[6];
	
	ReplicationManager(MemberList memberList,Gossiper gos_obj, int identifier)
	{
		localMemTable = memberList;
		mess = gos_obj.messenger;
		localIdentifier = identifier;
		
		start();
	}
	
	public void run()
	{
		updateRing();
		updatePD();
		findFailures();
		
	}
	
	private void findFailures() {
		if(newRing[0] == oldRing[0]-1)
		{
			//someone crashed
			try {
				Thread.sleep(1000); //wait for second crash
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			} 
			updateRing();
			if(newRing[0] == oldRing [0]-1)
			{
				//double crash
			}
			else
			{
				//single crash
			}
		}
		else if(newRing[0] == oldRing[0]-2)
		{
			//double crash
		}
		
	}

	private void updateRing()
	{
		oldRing = newRing;
		newRing = fetchMembers(localMemTable);
		
	}
	private Integer[] fetchMembers(MemberList localMemT) {
		Integer[] retVal = new Integer[6];
		retVal[0] = localMemT.getSize();
		for(int i=0; i<localMemT.getSize(); i++)
		{
			retVal[i+1] = localMemT.getFullList().get(i).getIdentifier();
		}
		return retVal;
	}

	public void findPredecessor(int identifier) {
		
		Integer[] allIdentifiers = new Integer[localMemberList.getSize()];
		int i=0;
		for(; i< localMemberList.getSize()-1; i++)
		{
			allIdentifiers[i] = localMemberList.getFullList().get(i+1).getIdentifier();
			
		}
		allIdentifiers[i] = identifier;
		Arrays.sort(allIdentifiers);
		int index = Arrays.asList(allIdentifiers).indexOf(identifier);
		int successorIndex;
		if(index == allIdentifiers.length-1)
			successorIndex = 0;
		else
			successorIndex = index+1;
		
		localSuccessor = allIdentifiers[successorIndex];
		localSuccessor1 = allIdentifiers[(successorIndex+1)%(localMemberList.getSize())];
		
		//System.out.println(localSuccessor);
	}
}
