import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import java.awt.*;
 
 
@ScriptManifest(name = "NomNomChocolateDust", author = "Plague Doctor", version = 1.0, info = "Turns Chocolate bars into Chocolate dust.", logo = "IMGURLINKTOLOGO(NOTNEEDED)")
public class Main extends Script { 
	private Area varrockWestBank = new Area(3180, 3447, 3190, 3433); // Creates varrockWestBank location.
	
 
    @Override
    public void onStart() {
        // This gets executed when the script first starts.
    }
    
    private enum State {
		BANK_INVENTORY, WAIT, WALK_TO_BANK, GRIND, // Declares the different states of the program.
	};

	private State getState()
	{
		if(myPlayer().isMoving() && myPlayer().isAnimating())
		{
			return State.WAIT;
		}
		if(!inventory.contains("Knife") || !inventory.contains("Chocolate bar"))
		{
			if(varrockWestBank.contains(myPlayer()))
			{
				return State.BANK_INVENTORY;
			}
			else
			{
				return State.WALK_TO_BANK;
			}
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
				inventory.getItem("Knife").interact("Use"); // You can use this instead of that other code, that other code also works but this is more efficient for the API.
				inventory.getItem("Chocolate bar").interact("Use");
        	}
    		
    		break;
    		
    	case WAIT:
    		break;    		// You should break here instead of return 700, because the return at the end of the function will be called anyway.
    		
    	case BANK_INVENTORY: // Opens a nearby bank and deposits everything in the inventory except the knife.
    		if(!getBank().isOpen())
    		{
    			getBank().open();
    		}
			else
			{
				// Have all code that handles banking in one area.
				if(!inventory.contains("Knife"))
				{
					getBank().withdraw("Knife", 1);
				}
				getBank().depositAllExcept("Knife");
				
				getBank().withdraw("Chocolate bar", 27);
				getBank().close();    
			}

    		break;
    	case WALK_TO_BANK:
    		if(!varrockWestBank.contains(myPlayer())) // Checks if the player is already in the Varrock west bank.
    		{
			// You already had the west banks location defined at the top of the page, dont define the same thing twice!!!
        		getWalking().webWalk(varrockWestBank);
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