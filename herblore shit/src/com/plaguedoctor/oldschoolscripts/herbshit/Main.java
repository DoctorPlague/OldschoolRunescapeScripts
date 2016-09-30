package com.plaguedoctor.oldschoolscripts.herbshit;

import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import org.osbot.rs07.utility.ConditionalSleep;



import java.awt.*;
import java.util.LinkedList; 

 
@ScriptManifest(name = "potmaker", author = "Plague Doctor", version = 1.0, info = "Cleans Toadflax.", logo = "http://i.imgur.com/DAL7Mii.png")
public class Main extends Script { 

	private long lastTimeNotAnimating;
	private int selectionBias = random(15,85);
	private boolean isSmithing;
	private String potion = "Harralander potion (unf)";
	private String ingredient = "Goat horn dust";
	
    @Override
    public void onStart() {
        // This gets executed when the script first starts.
    	log("selectionBias: " + selectionBias);
    	if(!getWorlds().isMembersWorld())
    	{
    		log("Not logged into a members world.");
    		stop();
    	}
    		
    }
    
    private enum State {
		BANK_INVENTORY, WAIT, CLEAN, // Declares the different states of the program.
	};

	private State getState()
	{
		long timeSinceLastAnimating = System.currentTimeMillis() - lastTimeNotAnimating; // This is the time in milliseconds since last animation.
    	if(timeSinceLastAnimating > 5000) // If its been more than 4 seconds.
    		isSmithing = false;
    	RS2Widget smithingLevelWidget = getWidgets().get(233, 0);

    	if(smithingLevelWidget != null && smithingLevelWidget.isVisible() && smithingLevelWidget.getMessage().contains("Herblore"))
    		return State.CLEAN;
    	if(myPlayer().isMoving() || myPlayer().isAnimating())
    	{
    		lastTimeNotAnimating = System.currentTimeMillis();
    		return State.WAIT;
    	}
		if(!inventory.contains(potion) || !inventory.contains(ingredient))
		{
			isSmithing = false;
			return State.BANK_INVENTORY;					
		}
		if(isSmithing)
    	{
    		return State.WAIT;
    	}
		
		return State.CLEAN;
		
	}
    
 
    @Override
    public int onLoop() throws InterruptedException {
    	State state = getState();
    	log(state);
    	switch  (state) {
    	case CLEAN:     		
			if(inventory.getItem(potion) != null && inventory.getItem(ingredient) != null) // We already know the inventory contains a knife and chocolate bars, because grind only gets sent back to onLoop if we have knife and chocolate bars in our inventory.
        	{
				RS2Widget smithWidget = getWidgets().get(309, 4);
				if(smithWidget != null)
				{
					smithWidget.interact("Make All");
					isSmithing = true;					
					lastTimeNotAnimating = System.currentTimeMillis();
					getMouse().moveRandomly();
					getMouse().moveOutsideScreen();
					
				}
				else if(inventory.contains(potion) || inventory.contains(ingredient))
				{
					int harrToUse = random(0, (int)inventory.getAmount(potion));
					int goatToUse = random(0, (int)inventory.getAmount(ingredient));
					int harrSlot = 99;
					int goatSlot = 99;
					
					int harrI = 0;
					int goatI = 0;
					for(int i = 0; i < 28; i++){
						Item item = inventory.getItemInSlot(i);
						if(item != null)
						{
							if(harrSlot == 99 &&item.getName().equals(potion))
							{
								if(harrI == harrToUse)
								{
									harrSlot = i;
								}
								harrI++;
							}
							if(goatSlot == 99 && item.getName().equals(ingredient))
							{
								if(goatI == goatToUse)
								{
									goatSlot = i;
								}
								goatI++;
							}
							if(goatSlot != 99 && harrSlot != 99)
								break;
						}
					}
										
					if(goatSlot != 99 && harrSlot != 99)
					{
						if(random(0,100) >= selectionBias)
						{
							mouse.click(inventory.getMouseDestination(harrSlot));
							mouse.click(inventory.getMouseDestination(goatSlot));
						}
						else
						{
							mouse.click(inventory.getMouseDestination(goatSlot));
							mouse.click(inventory.getMouseDestination(harrSlot));
						}
					}
					
					// Basically, all this code does,
					// is gets a random number from 0 to the number of the item we have.
					// then it will figure out which slot that random number of that item is in.
					// it will do that for both items we need
					// then once its done it will randomly choose between clicking the first item then second or second then first.

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
			{ // Changed this code because the logic was not correct. You were doing it all at once every time you did the check. You want to do it in steps.
				
				
				if(!getInventory().isEmpty()) // If the inventory is already empty, we don't need to empty it.
				{
					getBank().depositAll(); // Deposit everything
					new ConditionalSleep(3000) { // Depositing should at most take like 2 seconds, 3 seconds is a good wait to make the bot never just sit there for ages.
						@Override
						public boolean condition() throws InterruptedException {

							return (getInventory().isEmpty());  // If the inventory is empty then stop sleeping
						}
					}.sleep();
					if(!getInventory().isEmpty())
						// If the sleep timed out (got to three seconds), then break cos it means something went wrong.
						// This will cause onLoop to be called again and it will happen again.
						break;
				}
				
				getBank().withdraw(potion, 14);
				new ConditionalSleep(3000) { // Same deal, shouldn't take longer than 3s
					@Override
					public boolean condition() throws InterruptedException {

						return (getInventory().contains(potion));
					}
				}.sleep();
				if(!getInventory().contains(potion))
					// If the sleep timed out (got to three seconds), then break cos it means something went wrong.
					// This will cause onLoop to be called again and it will happen again.
					break;
			
			

				getBank().withdraw(ingredient, 14);
				new ConditionalSleep(10000) {
					@Override
					public boolean condition() throws InterruptedException {

						return (getInventory().contains(ingredient)); 
					}
				}.sleep();
				if(!getInventory().contains(ingredient))
					// If the sleep timed out (got to three seconds), then break cos it means something went wrong.
					// This will cause onLoop to be called again and it will happen again.
					break;
				
				
				getBank().close(); // Everything worked out, close the bank.
			}
			
					
		
    		break; 
    	}
    	 	
 
    	return random(600, 800); //The amount of time in milliseconds before the loop is called again
    }
    
    @Override
    public void onExit() {
        // This will get executed when the user hits the stop script button.
 
    }
    
    LinkedList<MousePathPoint> mousePath = new LinkedList<MousePathPoint>();
    public class MousePathPoint extends Point {

        /**
		 * 
		 */
		private static final long serialVersionUID = 8948307125632779948L;
		private long finishTime;

        public MousePathPoint(int x, int y, int lastingTime) {
            super(x, y);
            finishTime = System.currentTimeMillis() + lastingTime;
        }

        public boolean isUp() {
            return System.currentTimeMillis() > finishTime;
        }
    }
 
    @Override
    public void onPaint(Graphics2D g) {  
    	g.setPaint(Color.cyan);
    	
    	while (!mousePath.isEmpty() && mousePath.peek().isUp())
            mousePath.remove();
    	
        Point clientCursor = mouse.getPosition();
        
        g.drawLine(clientCursor.x, clientCursor.y, clientCursor.x, clientCursor.y);
        
        MousePathPoint mpp = new MousePathPoint(clientCursor.x, clientCursor.y, 500);
        if (mousePath.isEmpty() || !mousePath.getLast().equals(mpp))
            mousePath.add(mpp);
        MousePathPoint lastPoint = null;
        for (MousePathPoint a : mousePath) {
            if (lastPoint != null) {                
                g.drawLine(a.x, a.y, lastPoint.x, lastPoint.y);
            }
            lastPoint = a;
        }        
    }
 
 
}
