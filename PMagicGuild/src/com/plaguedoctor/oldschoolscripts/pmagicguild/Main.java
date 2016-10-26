package com.plaguedoctor.oldschoolscripts.pmagicguild;

import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import org.osbot.rs07.utility.ConditionalSleep;



import java.awt.*;
import java.util.LinkedList; 

 
@ScriptManifest(name = "PMagicGuild", author = "Plague Doctor", version = 1.0, info = "Buys runes and battlestaves", logo = "")
public class Main extends Script { 
	
	// Variables
	
	Area magicGuild = new Area(new Position(2584, 3094, 1), new Position(2596, 3081, 1));
	Area yanilleBank = new Area(2616, 3088, 2609, 3097);
	NPC Store = npcs.closest("Magic Store owner");
	
	
    @Override
    public void onStart() {
        // This gets executed when the script first starts.
    	
    		
    }
    
    private enum State {
		BANK_INVENTORY, WAIT, SHOP, HOP, LOGOUT// Declares the different states of the program.
	};

	private State getState()
	{	
		if(getStore().isOpen()) {
			if(getStore().getAmount("Battlestaff") <= 0 && getStore().getAmount("Nature rune") <= 0 && getStore().getAmount("Death rune") >= 0) {
				return State.HOP;
			}
		}
		if(getInventory().getAmount("Coins") <= 10000) {
			return State.LOGOUT;
		}
		if(getStore().isOpen()) {
			if(getStore().getAmount("Battlestaff") <= 0 && getStore().getAmount("Nature rune") <= 0 && getStore().getAmount("Death rune") >= 0 && inventory.isFull()) {
				return State.BANK_INVENTORY;
			}
		}
		
		return State.SHOP;
	}	
    
 
    @Override
    public int onLoop() throws InterruptedException {
    	State state = getState();
    	log(state);
    	switch  (state) {
    	case SHOP:
    		if(magicGuild.contains(myPlayer())) {    			
    			if(getStore().isOpen()) {
    				if(getStore().getAmount("Death rune") >= 1) {
    					getStore().buy("Death rune", 10);
    				}
    				if(getStore().getAmount("Nature rune") >= 1) {
    					getStore().buy("Nature rune", 10);
    				}
    				if(getStore().getAmount("Battlestaff") >= 1) {
    					getStore().buy("Battlestaff", 10);
    				}
    				
    			}
    			if(!getStore().isOpen()) {
    				Store.interact("Trade");
    			}
    		}
    		else {    		
    			getWalking().webWalk(magicGuild);
    		}
    		break;          
    	
    	case HOP:      	
    		getWorlds().hopToP2PWorld();
    		new ConditionalSleep(20000, 25000) {
				@Override
				
				public boolean condition() throws InterruptedException {
					return Main.this.myPlayer().exists();
				}
			}.sleep();
			
    		break;  
		
    	case WAIT:
    		break;    		
    		
    	case BANK_INVENTORY: // Opens a nearby bank and deposits everything in the inventory except the knife.	
    		if(yanilleBank.contains(myPlayer()))
			{
				if(!getBank().isOpen())
				{					
					getBank().open();
					new ConditionalSleep(10000) {
						@Override
						
						public boolean condition() throws InterruptedException {
							return bank.isOpen();
						}
					}.sleep();
				}
				else
				{				
					getBank().depositAllExcept("Coins");									
				}	
				
			}
			else
			{				
				getWalking().webWalk(yanilleBank);
			}
    		break; 
    		
    	case LOGOUT:
    		stop();
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
