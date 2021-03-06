package tolerantKeyValStore;

import java.util.ArrayList;

public class AckHandler {
	
	ArrayList<AckCount> AckList = new ArrayList<AckCount>();
	int quorumLevel;
	
	
	public AckHandler(int quorumLevel) {
		this.quorumLevel = quorumLevel; 
	}


	public int increaseCount(int id)
	{
		int val=0;
		boolean flag = false;
		if(quorumLevel == 1)
		{
			val = id;
			flag=true;
		}
		else if(quorumLevel == 2)
		{
			for(int i=0; i<AckList.size(); i++)
			{
				if(AckList.get(i).identifier == id)
				{
					flag = true;
					if(AckList.get(i).count == 1)
					{
						val = AckList.get(i).identifier;
						AckList.remove(i);
						return val;
						
					}
										
				}
			}
		}
		else if(quorumLevel == 3)
		{
			for(int i=0; i<AckList.size(); i++)
			{
				if(AckList.get(i).identifier == id)
				{
					flag = true;
					if(AckList.get(i).count == 2)
					{
						val = AckList.get(i).identifier;
						AckList.remove(i);
						return val;
						
					}
					if(AckList.get(i).count == 1)
					{
						AckList.get(i).count++;
						
						return val;
					}
						
				}
			}
		}
		if(!flag)
		{
			AckList.add(new AckCount(id, 1));
			return val;
		}
		return val;
	}


	public void print() {
		for(int i=0; i<AckList.size(); i++)
		{
			AckList.get(i).print();
		}
		
	}
	

}
