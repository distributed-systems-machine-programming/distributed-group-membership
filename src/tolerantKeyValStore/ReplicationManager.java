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
	int[] oldRing;			
	int[] newRing;			
	
	ReplicationManager(MemberList memberList,Gossiper gos_obj, int identifier)
	{
		localMemTable = memberList;
		mess = gos_obj.messenger;
		localIdentifier = identifier;
		
		oldRing = new int[]{1, localIdentifier};
		newRing = new int[]{1};
		start();
	}
	
	public void run()
	{
		while(true)
		{
		updateRing();
		//updatePD();
		findFailures();
		findJoins();
		/*System.out.println("OldRing: " + oldRing);
		for(int i=0; i<oldRing.length; i++)
			System.out.println(oldRing[i] + " ");
		
		System.out.println("NewRing: " + newRing);
		for(int i=0; i<newRing.length; i++)
			System.out.println(newRing[i] + " ");*/
		
		try {
			Thread.sleep(100);				//CONSTANT USE. BEWARE
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
	}
	
	private void findJoins() {
		if(newRing[0] == oldRing[0]+1)
		{
			int joinMachineID = findCrashMachine(newRing, oldRing);
			int relation = findcrashMachineRelation(joinMachineID, localIdentifier, newRing);
			int join_1 = findCrashRelativeMachine(joinMachineID, newRing, -1);
			
		}
		
	}

	private void findFailures() {
		if(newRing[0] == oldRing[0]-1)
		{
			//someone crashed
			try {
				Thread.sleep(1000); //wait for second crash CONSTANT USE. BEWARE
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			} 
			int[] savedOldRing = oldRing;
			int[] savedNewRing = newRing;
			updateRing();
			if(newRing[0] == oldRing [0]-1)
			{
				start2crashRecovery(savedOldRing, newRing);
			}
			else
			{
				start1crashRecovery(savedOldRing, savedNewRing);
			}
		}
		else if(newRing[0] == oldRing[0]-2)
		{
			start2crashRecovery(oldRing, newRing);
		}
		
	}

	private void start2crashRecovery(int[] oldRing2, int[] newRing2) {
		try {
			Thread.sleep(1000);	//WAIT FOR THE OTHER MACHINES TO REALIZE THAT CRASH HAS HAPPENED
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("STARTED RECOVERY PROCESS");
		int [] crashMachineIDs = findCrashMachines(oldRing2, newRing2);
		boolean isSimul = findCrashRelation(oldRing2, crashMachineIDs);
		int relation1 = findcrashMachineRelation(crashMachineIDs[0], localIdentifier, oldRing2); 
		int relation2 = findcrashMachineRelation(crashMachineIDs[1], localIdentifier, oldRing2);
		System.out.println("crashMachineID1 " + crashMachineIDs[0]);
		System.out.println("Relation1: " + relation1);
		System.out.println("crashMachineID2 " + crashMachineIDs[1]);
		System.out.println("Relation2: " + relation2);
		if(isSimul)
		{
			int crash1 = findCrashRelativeMachine(crashMachineIDs[1], oldRing2, 1);
			int crash2 = findCrashRelativeMachine(crashMachineIDs[1], oldRing2, 2);
			int crash3 = findCrashRelativeMachine(crashMachineIDs[1], oldRing2, 3);
			
			if(relation2 == 1)
			{
				System.out.println("Getting keys from crash+3");
				mess.getAuthenticKeys(crash3);
				System.out.println("Keys received from crash+3");
				System.out.println("Getting keys from crash+2");
				mess.getAuthenticKeys(crash2);
				System.out.println("Keys received from crash+2");
			}
			else if(relation2 == 2)
			{
				System.out.println("Getting keys from crash+1");
				mess.getAuthenticKeys(crash1);
				System.out.println("Keys received from crash+1");
				System.out.println("Getting keys from crash+3");
				mess.getAuthenticKeys(crash3);
				System.out.println("Keys received from crash+3");
			}
			else if(relation2 == 3)
			{
				System.out.println("Getting keys from crash+1");
				mess.getAuthenticKeys(crash1);
				System.out.println("Keys received from crash+1");
			}
			
		}
		else
		{
			int crash1 = findCrashRelativeMachine(crashMachineIDs[1], oldRing2, 1);
			int crash2 = findCrashRelativeMachine(crashMachineIDs[1], oldRing2, 2);
			int caughtInTheMiddle = findCrashRelativeMachine(crashMachineIDs[0], oldRing2, 1);
			
			if(relation1 == 1)
			{
				System.out.println("Getting keys from crash+1");
				mess.getAuthenticKeys(crash1);
				System.out.println("Keys received from crash+1");
			}
			else if(relation2 == 1)
			{
				System.out.println("Getting keys from crash+2");
				mess.getAuthenticKeys(crash2);
				System.out.println("Keys received from crash+2");
				System.out.println("Getting keys from middle crash machine");
				mess.getAuthenticKeys(caughtInTheMiddle);
				System.out.println("Keys received from middle crash machine");
			}
			else if(relation2 == 2)
			{
				System.out.println("Getting keys from middle crash machine");
				mess.getAuthenticKeys(caughtInTheMiddle);
				System.out.println("Keys received from middle crash machine");
			}
		}
		System.out.println("RECOVERY PROCESS ENDED");
		
	}

	private boolean findCrashRelation(int[] oldRing2, int[] crashMachineIDs) {
		boolean isSim = false;
		int id1 = -1;
		int id2 = -1;
		boolean flag1 = false;
		boolean flag2 = false;
		int k=0;
		for(int i=1; i<oldRing2.length && k<2; i++)
		{
			if(oldRing2[i] == crashMachineIDs[0])
			{
				flag1 = true;
				id1 = i;
				k++;
			}
			if(oldRing2[i] == crashMachineIDs[1])
			{
				flag2 = true;
				id2 = i;
				k++;
			}
		
		}
		
		if(((id2 - id1) == 1) || ((id1 - id2) == (oldRing2[0]-1)))
		{
			isSim = true;
		}
		
		return isSim;
	}

	private int[] findCrashMachines(int[] oldRing2, int[] newRing2) {
		int [] ret =  {-1, -1};
		int [] id =   {-1, -1};
		int k=0;
		for(int i=1; i<oldRing2.length && k<2; i++)
		{
			boolean flag = false;
			for(int j=1; j<newRing2.length; j++)
			{
				if(oldRing2[i] == newRing2[j])
				{
					flag = true;
				}
			}
			if(!flag)
			{
				ret[k] = oldRing2[i];
				id[k] = i;
				
				k++;
			}
		}
		
		if(id[1] - id[0] <= 2)
			return ret;
		else
		{
			int a = ret[0];
			ret[0] = ret[1];
			ret[1] = a;
			return ret;
		}
		
		
		
	}

	private void start1crashRecovery(int[] oldRing2, int[] newRing2) {
		System.out.println("RECOVERY PROCESS STARTED");
		int crashMachineID = findCrashMachine(oldRing2, newRing2);
		int relation = findcrashMachineRelation(crashMachineID, localIdentifier, oldRing2); 
		int crash_1 = findCrashRelativeMachine(crashMachineID, oldRing2, -1);
		int crash_2 = findCrashRelativeMachine(crashMachineID, oldRing2, -2);
		int crash1 = findCrashRelativeMachine(crashMachineID, oldRing2, 1);
		int crash2 = findCrashRelativeMachine(crashMachineID, oldRing2, 2);
		int crash3 = findCrashRelativeMachine(crashMachineID, oldRing2, 3);
		System.out.println("crashMachineID " + crashMachineID);
		System.out.println("Relation: " + relation);
		System.out.println("crash_1 " + crash_1);
		System.out.println("crash_2 " + crash_2);
		System.out.println("crash1 " + crash1);
		System.out.println("crash2 " + crash2);
		System.out.println("crash3 " + crash3);
		
		
		if(relation == 1)
		{
			System.out.println("Getting keys from crash-2");
			mess.getAuthenticKeys(crash_2);
			System.out.println("Keys received from crash-2");
		}
		else if (relation == 2)
		{
			System.out.println("Getting keys from crash-1");
			mess.getAuthenticKeys(crash_1);
			System.out.println("Keys received from crash-1");
		}
		else if(relation == 3)
		{
			System.out.println("Getting keys from crash+1");
			mess.getAuthenticKeys(crash1);
			System.out.println("Keys received from crash+1");
		}
		System.out.println("RECOVERY PROCESS ENDED");
	}

	private int findCrashRelativeMachine(int crashMachineID, int[] oldRing2, int distance) {
		int crashIndex=-1;
		for (int i=1; i<oldRing2.length; i++)
		{
			if(oldRing2[i] == crashMachineID)
			{
				crashIndex = i;
				break;
			}
		}
		
		crashIndex--;
		
		int temp = crashIndex + distance;
		
		if(temp < 0)
		{
			return oldRing2[oldRing2[0]+temp+1];
		}
		else if(temp >= oldRing2[0])
		{
			return oldRing2[temp-oldRing2[0]+1];
		}
		else
		{
			return oldRing2[temp+1];
		}
		
	}

	private int findcrashMachineRelation(int crashMachineID, int localIdentifier, int[] oldRing2) {
		int crashIndex = -1;
		int localIndex = -1;
		for(int i=1; i<oldRing2.length; i++)
		{
			if(oldRing2[i] == localIdentifier)
			{
				localIndex = i;
			}
			if(oldRing2[i] == crashMachineID)
			{
				crashIndex = i;
			}
			
		}
		if(crashIndex == localIndex)
		{
			return 0;
		}
		else if(localIndex > crashIndex)
		{
			return localIndex - crashIndex;
		}
		else if(localIndex < crashIndex)
		{
			return oldRing2[0] + localIndex - crashIndex;
		}
		else
		{
			return -1;
		}
	
	}

	private int findCrashMachine(int[] oldRing2, int[] newRing2) {
		
		for(int i=1; i<oldRing2.length; i++)
		{
			boolean flag = false;
			for(int j=1; j<newRing2.length; j++)
			{
				if(oldRing2[i] == newRing2[j])
				{
					flag = true;
				}
			}
			if(!flag)
				return oldRing2[i];
		}
		return -1;
	}

	private void updateRing()
	{
		oldRing = newRing;
		newRing = fetchMembers(localMemTable);
		
	}
	private int[] fetchMembers(MemberList localMemT) {
		int[] retVal = new int[localMemT.getSize()+1];
		int[] temp = new int[localMemT.getSize()];
		retVal[0] = localMemT.getSize();
		for(int i=0; i<localMemT.getSize(); i++)
		{
			temp[i] = localMemT.getFullList().get(i).getIdentifier();
		}
		if(retVal[0] > 1)
		{
			Arrays.sort(temp);
		}
		for(int i=0; i<localMemT.getSize(); i++)
		{
			retVal[i+1] = temp[i];
		}
		
			return retVal;
	}

	public void findPredecessor(int identifier) {
		
		/*Integer[] allIdentifiers = new Integer[localMemberList.getSize()];
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
*/	}
	
	
}
