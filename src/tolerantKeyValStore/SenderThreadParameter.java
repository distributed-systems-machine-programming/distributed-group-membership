package tolerantKeyValStore;

public class SenderThreadParameter implements Runnable {
	int keyIdentifier;
	KeyValEntry kV; 
	int recipient1;
	int recipient2;
	int recipient3;
	String action;
	
	SenderThreadParameter(String action, int r1, int r2, int r3, int Data)
	{
		keyIdentifier = Data;
		this.action = action;
		recipient1 = r1;
		recipient2 = r2;
		recipient3 = r3;
		kV= null;
	}
	SenderThreadParameter(String action, int r1, int r2, int r3, KeyValEntry data)
	{
		keyIdentifier = 0;
		this.action = action;
		recipient1 = r1;
		recipient2 = r2;
		recipient3 = r3;
		kV= data;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
	
}
