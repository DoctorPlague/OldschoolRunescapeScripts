package com.plaguedoctor.oldschoolscripts.herbshit;

import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import org.osbot.rs07.utility.ConditionalSleep;

import com.plaguedoctor.oldschoolscripts.cannonballsmelter.Action;

import java.awt.*;
import java.util.LinkedList; 

 
@ScriptManifest(name = "herbshit", author = "Plague Doctor", version = 1.0, info = "Cleans Toadflax.", logo = "http://i.imgur.com/DAL7Mii.png")
public class Main extends Script { 

	private long lastTimeNotAnimating;
	private boolean isSmithing;
	
    @Override
    public void onStart() {
        // This gets executed when the script first starts.
    }
    
    private enum State {
		BANK_INVENTORY, WAIT, CLEAN, // Declares the different states of the program.
	};

	private State getState()
	{
		long timeSinceLastAnimating = System.currentTimeMillis() - lastTimeNotAnimating; // This is the time in milliseconds since last animation.
    	if(timeSinceLastAnimating > 4000) // If its been more than 4 seconds.
    		isSmithing = false;
    	RS2Widget smithingLevelWidget = getWidgets().get(233, 0);

    	if(smithingLevelWidget != null && smithingLevelWidget.isVisible() && smithingLevelWidget.getMessage().contains("Herblore"))
    		return State.CLEAN;
    	if(myPlayer().isMoving() || myPlayer().isAnimating())
    	{
    		lastTimeNotAnimating = System.currentTimeMillis();
    		return State.WAIT;
    	}
		if(!inventory.contains("Harralander potion (unf)", "Goat horn dust"))
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
    	switch  (getState()) {
    	case CLEAN: 
			if(getBank().isOpen()) // If the bank is open, this closes it.
    		{
    			getBank().close();      			
    		}
			else if(inventory.getItem("Harralander potion (unf)") != null && inventory.getItem("Goat horn dust") != null) // We already know the inventory contains a knife and chocolate bars, because grind only gets sent back to onLoop if we have knife and chocolate bars in our inventory.
        	{
				RS2Widget smithWidget = getWidgets().get(309, 4);
				if(smithWidget != null)
				{
					smithWidget.interact("Make All");
					lastTimeNotAnimating = System.currentTimeMillis();		
					isSmithing = true;
				}
				else
				{
					inventory.getItem("Harralander potion (unf)").interact("Use"); // You can use this instead of that other code, that other code also works but this is more efficient for the API.
					inventory.getItem("Goat horn dust").interact("Use");	
				}
				
				
				
				getMouse().moveOutsideScreen();
				
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
						if(!inventory.contains("Harralander potion (unf)"))
						{	
							if(!inventory.onlyContains("Harralander potion (unf)", "Goat horn dust"))
								getBank().depositAll();
							getBank().withdraw("Harralander potion (unf)", 14);
							getBank().withdraw("Goat horn dust", 14);
							
						}						
						getBank().close();  
						
						return (getInventory().contains("Harralander potion (unf)")); // If inventory contains knife and inventory contains chocolate bar it will return true. and end sleep
					}
				}.sleep();
				  
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

        private long finishTime;
        private double lastingTime;

        public MousePathPoint(int x, int y, int lastingTime) {
            super(x, y);
            this.lastingTime = lastingTime;
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
