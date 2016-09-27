import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.Entity;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.input.mouse.RectangleDestination;
import org.osbot.rs07.script.MethodProvider;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import org.osbot.rs07.utility.ConditionalSleep;

import java.awt.*;

@ScriptManifest(author = "Plague Doctor", info = "My first script", name = "Smelter", version = 0, logo = "")
public class main extends Script {	
	
	private long lastTimeNotAnimating;	

	@Override
	public void onStart() {
		log("Welcome to JSmelter by Harley Daun & PlagueDoctor.");		
		log("Enjoy the script.");
	}

	private enum State {
		SMELT, BANK, WAIT, WALK_TO_FURNACE, WALK_TO_BANK
	};

	private State getState() {
		long timeSinceLastAnimating = System.currentTimeMillis() - lastTimeNotAnimating;
        if(myPlayer().isMoving() || myPlayer().isAnimating() || timeSinceLastAnimating > 2000)
            return State.WAIT;
	    if(inventory.isFull()) // If our inventory is full, dont do anything, bank instead
	    {
	        return State.WALK_TO_BANK;
	    }
	    if(Locations.LumbridgeCastleBank.contains(myPlayer()))
	    {
	        return State.BANK;
	    }
	    if(Locations.lumbridgeFurnaceRoom.contains(myPlayer())) // If we are in the lumbridge furance room, smelt
	    {
	        return State.SMELT;
	    }
	    else // Otherwise walk to the lumbridge furnace room
	    {
	        return State.WALK_TO_FURNACE;
	    }
	}

	@Override
	public int onLoop() throws InterruptedException {
	    switch  (getState()) {
	    case SMELT:
            Entity furnace = objects.closest("Furnace");
            if(furnace != null)
            {
                if(getWidgets().get(311, 2) != null) // If its not null it means the smelt menu is open
                {
                    // So we know that the menu is open, now we must find the button for bronze.
                    // We could do this by going through all the children of 311, and look for ones named Bronze or we can go hard coded.
                    // For now we go hard coded.
 
                    // So looking in the osbot with widgets ticked we see that Bronze is 311, 14
                    // So we do
                    RS2Widget bronzeButton = getWidgets().get(311, 13); // Gets the Widget instance for the bronze bar.
                    RS2Widget enterAmountWidget = getWidgets().get(162, 33); // Gets the Widget instance for the enter Ammount text field
                    String amountToSmith = "5"; // The amount of bronze bar we want to smith
                    if(bronzeButton != null && !enterAmountWidget.isVisible()) // Check that its not null, because if it was null and we tried to interact with it we would get errors
                    {
                        if(getMenuAPI().isOpen()) // If the right click menu for smithing is open, click the Smith X button.
                        {
                            Rectangle smithRectX = getMenuAPI().getOptionRectangle(3);
                            RectangleDestination dest = new RectangleDestination(bot, smithRectX);
                            getMouse().click(dest);
                        }
                        else // If it is not, go to the bronze button and right click it.
                        {
                            bronzeButton.hover();
                            getMouse().click(true);
                        }
                    }
                    else if(enterAmountWidget != null && enterAmountWidget.isVisible()) // Now, if the enter amount text widget is not null and is visible, we should enter how many to smith
                    {
                        if(getMenuAPI().isOpen()) // If we have a menu open still (I noticed the bot would sometimes right click twice causing it to open again.) then we close it
                            getMenuAPI().selectAction("Cancel");
                        else if(enterAmountWidget.getMessage().equals("*")) // If the text field is empty (just contains the default *) then we should type the amount to smith and press enter
                        {
                            // Putting true into this function will make it press enter after typing;
                            getKeyboard().typeString(amountToSmith, true);
                        }
                        else // The text field is not empty, so we want to empty it.
                        {
                            new ConditionalSleep(10000) // Creates a conditional sleep that will keep calling condition until condition returns true, or 10 seconds is  up.
                            {
 
                                @Override
                                public boolean condition() throws InterruptedException {
                                        if(enterAmountWidget.getMessage().equals("*")) // If the test field is empty, we should return true for the condition meaning the conditionalsleep will finish.
                                            return true;
                                        else // If it is not empty, we should press back space once, and the conditionalsleep will keep doing that until it is empty then stop.
                                        {
                                            getKeyboard().pressKey(8);
                                            MethodProvider.sleep(50);
                                            getKeyboard().releaseKey(8);
                                        }
                                    return false;
                                }
                               
                            }.sleep(); // Call the actual conditional sleep to start sleeping here.
                           
                        }
                    }
                   
                }
                else // If it is null, then we haven't got it open so we want to open it
                {
                    furnace.interact("Smelt");
                }
            }
            break;
	       
	        case BANK:
	        break;
	       
	        case WALK_TO_FURNACE:	            		
	    		getWalking().webWalk(Locations.LumbridgeFurnace);
	    		break;	        	
	        
	        case WAIT:
	        break;
		
	    }
	
		return random(200, 300);
	}

	@Override
	public void onExit() {
		log("Thanks for running my Smelter!");
	}

	@Override
	public void onPaint(Graphics2D g) {
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