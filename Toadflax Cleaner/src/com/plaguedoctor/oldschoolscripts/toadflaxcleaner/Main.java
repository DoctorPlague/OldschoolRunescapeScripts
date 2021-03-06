package com.plaguedoctor.oldschoolscripts.toadflaxcleaner;

import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.input.mouse.InventorySlotDestination;
import org.osbot.rs07.input.mouse.MouseDestination;
import org.osbot.rs07.input.mouse.MouseEvent;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import org.osbot.rs07.utility.ConditionalSleep;
import java.awt.*;
import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom; 

 
@ScriptManifest(name = "Grimy snapdragon Cleaner", author = "Plague Doctor", version = 1.0, info = "Cleans snapdragon.", logo = "http://i.imgur.com/DAL7Mii.png")
public class Main extends Script { 

	
    @Override
    public void onStart() {
        // This gets executed when the script first starts.
    }
    
    private enum State {
		BANK_INVENTORY, WAIT, CLEAN, // Declares the different states of the program.
	};

	private State getState()
	{
		if(myPlayer().isMoving() && myPlayer().isAnimating())
		{
			return State.WAIT;
		}
		if(!inventory.contains("Grimy snapdragon"))
		{
			return State.BANK_INVENTORY;					
		}
		if(inventory.contains("Grimy snapdragon")) // If inventory contains any Chocolate bars, it will 'CLEAN'.
		{
			return State.CLEAN;
		}
		return null;
	}
    
 
    @Override
    public int onLoop() throws InterruptedException {    		
    	switch  (getState()) {
    	case CLEAN: 
			if(getBank().isOpen()) // If the bank is open, this closes it.
    		{
    			getBank().close();      			
    		}
			else // We already know the inventory contains a knife and chocolate bars, because grind only gets sent back to onLoop if we have knife and chocolate bars in our inventory.
        	{
				if(inventory.getItem("Grimy snapdragon") != null)
				{					
					for(int i = 0; i<28; i++) 
					{						
						inventory.interact(i, "Clean");	
						sleep(random(50,100));
					}
					sleep(100);
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
						if(!inventory.contains("Grimy snapdragon"))
						{	
							if(!inventory.onlyContains("Grimy snapdragon"))
								getBank().depositAll();
							getBank().withdraw("Grimy snapdragon", 28);
							
						}
						else if(!getBank().contains("Grimy snapdragon")) // or  || getBank().getAmount(ITEM) &lt; MIN)
						{
							stop();
						}					
						  
						
						return (getInventory().contains("Grimy snapdragon")); // If inventory contains knife and inventory contains chocolate bar it will return true. and end sleep
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
