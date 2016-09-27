import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import org.osbot.rs07.utility.ConditionalSleep;



import java.awt.*; 

 
@ScriptManifest(name = "NomNomChocolateDust", author = "Plague Doctor", version = 1.0, info = "Turns Chocolate bars into Chocolate dust.", logo = "http://i.imgur.com/OmO9g77.png")
public class Main extends Script { 

	
    @Override
    public void onStart() {
        // This gets executed when the script first starts.
    }
    
    private enum State {
		BANK_INVENTORY, WAIT, GRIND, // Declares the different states of the program.
	};

	private State getState()
	{
		if(myPlayer().isMoving() && myPlayer().isAnimating())
		{
			return State.WAIT;
		}
		if(!inventory.contains("Knife") || !inventory.contains("Chocolate bar"))
		{
			return State.BANK_INVENTORY;					
		}
		if(inventory.contains("Chocolate bar")) // If inventory contains any Chocolate bars, it will 'GRIND'.
		{
			return State.GRIND;
		}
		return null;
	}
    
 
    @Override
    public int onLoop() throws InterruptedException {    		
    	switch  (getState()) {
    	case GRIND: 
			if(getBank().isOpen()) // If the bank is open, this closes it.
    		{
    			getBank().close();      			
    		}
			else // We already know the inventory contains a knife and chocolate bars, because grind only gets sent back to onLoop if we have knife and chocolate bars in our inventory.
        	{
				if(inventory.getItem("Knife") != null && inventory.getItem("Chocolate bar") != null)
				{
					inventory.getItem("Knife").interact("Use"); // You can use this instead of that other code, that other code also works but this is more efficient for the API.
					inventory.getItem("Chocolate bar").interact("Use");
				}
        	}
    		
    		break;
    		
    	case WAIT:
    		break;    		// You should break here instead of return 700, because the return at the end of the function will be called anyway.
    		
    	case BANK_INVENTORY: // Opens a nearby bank and deposits everything in the inventory except the knife.
    		if ((map.isWithinRange(objects.closest("Bank booth"), 10) || map.isWithinRange(objects.closest("Grand exchange booth"), 10) )&& !bank.isOpen())  // Checks if a bank booth, or grand exchange booth is within range and the bank isnt open
			{
				bank.open(); //attempts to open the bank
				new ConditionalSleep(10000) { //how many ms to wait for condition
					@Override

					public boolean condition() throws InterruptedException {
						return bank.isOpen(); //ends conditional sleep if bank is open
					}
				}.sleep();
			}
			else
			{
				new ConditionalSleep(10000) { //how many ms to wait for condition
					@Override

					public boolean condition() throws InterruptedException {
						if(!inventory.contains("Knife"))
						{
							getBank().withdraw("Knife", 1);
						}
						else if(!getBank().contains("Chocolate bar")) // or  || getBank().getAmount(ITEM) &lt; MIN)
						{
							stop();
						}
						getBank().depositAllExcept("Knife");
						getBank().withdraw("Chocolate bar", 27);
						getBank().close();  
						
						return (getInventory().contains("Knife") && getInventory().contains("Chocolate bar")); // If inventory contains knife and inventory contains chocolate bar it will return true. and end sleep
					}
				}.sleep();
				  
			}
			
					
		
    		break; 
    	}
    	 	
 
        return 800; //The amount of time in milliseconds before the loop is called again
    }
    
    @Override
    public void onExit() {
        // This will get executed when the user hits the stop script button.
 
    }          
 
    @Override
    public void onPaint(Graphics2D g) {  
    	// Adds a graphic around the mouse to make it more obvious what is happening/where the mouse is.
	Point mP = getMouse().getPosition();
        g.setPaint(Color.white);
        g.drawLine(mP.x - 1, 0, mP.x - 1, 500); // Above X
        g.drawLine(mP.x + 1, 0, mP.x + 1, 500); // Below X
        g.drawLine(0, mP.y - 1, 800, mP.y - 1); // Left Y
        g.drawLine(0, mP.y + 1, 800, mP.y + 1); // Right Y
        g.setPaint(Color.black);
        g.drawLine(mP.x, 0, mP.x, 500); // At X
        g.drawLine(0, mP.y, 800, mP.y); // At Y 
    }
 
 
}
