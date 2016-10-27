package com.plaguedoctor.oldschoolscripts.pmagicguild;

import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import org.osbot.rs07.utility.ConditionalSleep;



import java.awt.*;
import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom; 

 
@ScriptManifest(name = "PMagicGuild", author = "Plague Doctor", version = 1.0, info = "Buys runes and battlestaves", logo = "")
public class Main extends Script { 
	
	// Variables
	
	Area magicGuild = new Area(new Position(2584, 3094, 1), new Position(2596, 3081, 1));
	Area yanilleBank = new Area(2616, 3088, 2609, 3097);	
	String items[] = new String[] {"Death rune", "Nature rune", "Battlestaff",  };
	int rand = ThreadLocalRandom.current().nextInt(1,10);
	int randResponse = ThreadLocalRandom.current().nextInt(1,10);
	
	
    @Override
    public void onStart() {
    }
    
    private enum State {
		BANK_INVENTORY, SHOP, HOP, CHAT1// Declares the different states of the program.
	};

	private State getState()
	{	
		RS2Widget response = getWidgets().get(162, 41);
		if(getStore().isOpen()) {
			if(getStore().getAmount(items) <= 0) {
				return State.HOP;
			}
		}
		
		
		if(inventory.isFull()) {
			return State.BANK_INVENTORY;
		}
		
		if(response.getMessage().contains("hi") || response.getMessage().contains("hey") || response.getMessage().contains("sup") ||
		   response.getMessage().contains("yo") || response.getMessage().contains("hello") || response.getMessage().contains("heya")) {
			return State.CHAT1;
		}
		
		return State.SHOP;
	}	
    
 
    @Override
    public int onLoop() throws InterruptedException {
    	State state = getState();
    	log(state);
    	switch  (state) {
    	case SHOP: 
    		RS2Widget chatBox  = getWidgets().get(162, 43, 0); 
    		rand = ThreadLocalRandom.current().nextInt(1,10);
    		if(rand == 5) {
    			getMouse().moveSlightly();
    		}
    		if(rand == 10) {
    			getMouse().moveOutsideScreen();
    			sleep(random(2000-7000));
    		}
    		NPC Store = npcs.closest("Magic Store owner");
    		if(chatBox.getMessage().contains("coins")) {
    			stop();
    		}
    		if(magicGuild.contains(myPlayer())) {    			
    			if(Store != null && getStore().isOpen()) {
    				if(getStore().getAmount("Death rune") >= 1) {
    					getStore().buy("Death rune", 10); 
    					if(getStore().getAmount("Chaos rune") >= 1) {
    						getStore().buy("Chaos rune", 10);
    					}
    				}
    				if(getStore().getAmount("Nature rune") >= 1) {
    					getStore().buy("Nature rune", 10);
    					if(getStore().getAmount("Chaos rune") >= 1) {
    						getStore().buy("Chaos rune", 10);
    					}
    				}
    				if(getStore().getAmount("Battlestaff") >= 1) {
    					getStore().buy("Battlestaff", 10);
    					if(getStore().getAmount("Chaos rune") >= 1) {
    						getStore().buy("Chaos rune", 10);
    					}
    				}   				
    			}
    			if(!getStore().isOpen() && Store != null) {
    				Store.interact("Trade");
    				new ConditionalSleep(7000, 10000) {
    					@Override
    					
    					public boolean condition() throws InterruptedException {
    						return getStore().isOpen();
    					}
    				}.sleep();
    			}
    		}
    		else {    		
    			getWalking().webWalk(magicGuild);
    		}
    		break;          
    	
    	case HOP:   
    		rand = ThreadLocalRandom.current().nextInt(1,10);
    		if(rand == 5) {
    			getMouse().moveSlightly();
    		}
    		if(rand == 10) {
    			getMouse().moveOutsideScreen();
    			sleep(random(2000-7000));
    		}
    		if(rand == 2) {
    			getMouse().moveOutsideScreen();
    			sleep(random(7000-13000));
    		}
    		if(rand == 7) {
    			tabs.open(Tab.IGNORES);
    			sleep(random(200,500));    			
    		}
    		if(getStore().isOpen()) {
				getStore().close();
				new ConditionalSleep(20000, 25000) {
					@Override
					
					public boolean condition() throws InterruptedException {
						return !getStore().isOpen();
					}
				}.sleep();
			}
    		sleep(random(1000,4000));
    		getWorlds().hopToP2PWorld();
    		new ConditionalSleep(20000, 25000) {
				@Override
				
				public boolean condition() throws InterruptedException {
					return Main.this.myPlayer().exists();
				}
			}.sleep();
			
    		break;  	
    	   		
    		
    	case BANK_INVENTORY: 
    		rand = ThreadLocalRandom.current().nextInt(1,10);
    		if(rand == 5) {
    			getMouse().moveSlightly();
    		}
    		if(rand == 10) {
    			getMouse().moveOutsideScreen();
    			sleep(random(2000-7000));
    		}
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
    		
    	case CHAT1:
    		
    		randResponse = ThreadLocalRandom.current().nextInt(1,10);
    		if(randResponse == 1)
    			getKeyboard().typeString("hi");
    		if(randResponse == 2)
    			getKeyboard().typeString("heya");
    		if(randResponse == 3)
    			getKeyboard().typeString("sup");
    		if(randResponse == 4)
    			getKeyboard().typeString("yo");
    		if(randResponse == 5)
    			getKeyboard().typeString("hihi");
    		if(randResponse == 6)
    			getKeyboard().typeString("hallo");
    		if(randResponse == 7)
    			getKeyboard().typeString("hello");
    		if(randResponse == 8)
    			getKeyboard().typeString("hiiiii");
    		if(randResponse == 9)
    			getKeyboard().typeString("howdy");
    		if(randResponse == 10)
    			getKeyboard().typeString("heeey");
    		else
    			getKeyboard().typeString("que?");
    		
    		
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
