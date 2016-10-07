package com.plaguedoctor.oldschoolscripts.pstringspinner;

import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import org.osbot.rs07.utility.ConditionalSleep;



import java.awt.*;
import java.util.LinkedList; 

 
@ScriptManifest(name = "Diamond bolt maker", author = "Plague Doctor", version = 1.0, info = "Cleans Toadflax.", logo = "http://i.imgur.com/DAL7Mii.png")
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
		return null;
		
	}
    
 
    @Override
    public int onLoop() throws InterruptedException {
    	State state = getState();
    	log(state);
    	switch  (state) {
    	case CLEAN:   
    		
    		
    		break;
    		
    	case WAIT:
    		break;    		// You should break here instead of return 700, because the return at the end of the function will be called anyway.
    		
    	case BANK_INVENTORY: // Opens a nearby bank and deposits everything in the inventory except the knife.		
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
