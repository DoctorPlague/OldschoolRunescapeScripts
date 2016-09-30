package com.plaguedoctor.oldschoolscripts.herbshit;

import org.osbot.rs07.api.model.Entity;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import org.osbot.rs07.utility.ConditionalSleep;
<<<<<<< HEAD
import java.awt.*; 
=======

import com.plaguedoctor.oldschoolscripts.cannonballsmelter.Action;

import java.awt.*;
import java.util.LinkedList; 
>>>>>>> parent of 70134f3... +error fix

 
@ScriptManifest(name = "HerbloreIsHardToCode", author = "Plague Doctor", version = 1.0, info = "herblore.", logo = "http://i.imgur.com/OmO9g77.png")
public class Main extends Script { 
	
	 private boolean isHerbing;
	 private long lastTimeNotAnimating;

	
    @Override
    public void onStart() {
        // This gets executed when the script first starts.
    }
    
    private enum State {
		BANK_INVENTORY, WAIT, GRIND, // Declares the different states of the program.
	};

	private State getState()
	{
		long timeSinceLastAnimating = System.currentTimeMillis() - lastTimeNotAnimating; // This is the time in milliseconds since last animation.
    	if(timeSinceLastAnimating > 4000) // If its been more than 4 seconds.
    		isHerbing = false;
    	if(myPlayer().isMoving() || myPlayer().isAnimating())
    	{
    		lastTimeNotAnimating = System.currentTimeMillis();
    		return State.WAIT;
    	}
		if(!hasRequiredItems())
		{
			isHerbing = false;
			return State.BANK_INVENTORY;					
		}		
		if(isHerbing)
    	{
    		return State.WAIT;
    	}
		return State.GRIND;
	}
    
	 private boolean hasRequiredItems() {
	    	if(!getInventory().contains("Harralander potion (unf)") || !getInventory().contains("Goat horn dust"))
	    	{
	    		return false;
	    	}
	  
			return true;
		}
	
	
    @Override
    public int onLoop() throws InterruptedException {    		
    	switch  (getState()) {
    	case GRIND: 
			if(getBank().isOpen()) // If the bank is open, this closes it.
    		{
    			getBank().close();      			
    		}
<<<<<<< HEAD
			RS2Widget herbloreWidget = getWidgets().get(309, 4);
			if(herbloreWidget != null)
			{				
				herbloreWidget.interact("Make All");
				lastTimeNotAnimating = System.currentTimeMillis();								
				isHerbing = true;
				getMouse().moveOutsideScreen();
			}
			else
			{
				if(getInventory().getItem("Harralander potion (unf)") != null && (getInventory().getItem("Goat horn dust")) != null)
=======
			
			if(inventory.getItem("Harralander potion (unf)") != null && inventory.getItem("Goat horn dust") != null) // We already know the inventory contains a knife and chocolate bars, because grind only gets sent back to onLoop if we have knife and chocolate bars in our inventory.
        	{
				RS2Widget smithWidget = getWidgets().get(309, 4);
				if(smithWidget != null)
				{
					smithWidget.interact("Make All");
					lastTimeNotAnimating = System.currentTimeMillis();		
					isSmithing = true;
				}
				else
>>>>>>> parent of 70134f3... +error fix
				{
					new ConditionalSleep(3000) {

						@Override
						public boolean condition() throws InterruptedException {							
							getInventory().getItem("Harralander potion (unf)").interact("Use");								
							return getInventory().isItemSelected();
						}
						
					}.sleep();				
					
					if(getInventory().getItem("Goat horn dust") != null)
					{
						getInventory().getItem("Goat horn dust").interact("Use");
						lastTimeNotAnimating = System.currentTimeMillis();						
					}
					
				}
			}	
		      
    		
    		break;
    		
    	case WAIT:
    		break;    		// You should break here instead of return 700, because the return at the end of the function will be called anyway.
    		
    	case BANK_INVENTORY: // Opens a nearby bank and deposits everything in the inventory except the knife.
    		if ((map.isWithinRange(objects.closest("Bank booth"), 10) || map.isWithinRange(objects.closest("Grand exchange booth"), 10) )&& !bank.isOpen())  // Checks if a bank booth, or grand exchange booth is within range and the bank isnt open
			{
    			if(!getBank().isOpen())
				{					
					getBank().open();
				}
				else
				{					
					if(!inventory.contains("Harralander potion (unf)"))
					{
						getBank().withdraw("Harralander potion (unf)", 14);
					}
					getBank().depositAllExcept("Harralander potion (unf)");					
					getBank().withdraw("Goat horn dust", 14);
				}				
			}
    		break; 
    	} 
    		
    	return random(600, 800);
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
