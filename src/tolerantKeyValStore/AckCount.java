package tolerantKeyValStore;

public class AckCount {
	int identifier;
	int count;
	
	AckCount(int id, int c)
	{
		identifier = id;
		count = c;
	}

	public void print() {
		System.out.println("Ack Store Value:" + String.valueOf(identifier) + String.valueOf(count));
		
	}

	
	

}
