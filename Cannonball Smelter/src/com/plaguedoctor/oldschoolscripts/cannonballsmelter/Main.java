package com.plaguedoctor.oldschoolscripts.cannonballsmelter;
import org.osbot.rs07.api.model.Entity;
import org.osbot.rs07.api.ui.Message;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import org.osbot.rs07.utility.ConditionalSleep;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
 
@ScriptManifest(author = "Plague Doctor", info = "Smelts cannonballs and banks them", name = "Cannonball Smelter", version = 0, logo = "")
public class Main extends Script {
	
	private Furnace selectedFurnace;	
    private boolean isSmithing;
    private long lastTimeNotAnimating;
    private int ammoSmelted;
    private String status = "Starting bot"; 
    private Font runescape_chat_font; // Font used by paint
	private BufferedImage paintBG; // Background image used for paint, loaded when GUI is done.	
	private long startTime;	 	
	// private boolean botStarted = false;
	private boolean drawPaint = false;
	private int cannonballValue = 0;
	
	
    @Override
    public void onStart() {
    	startTime = System.currentTimeMillis();
        selectedFurnace = Furnace.EDGEVILLE;
        try
        {
            paintBG = ImageIO.read(Main.class.getResourceAsStream("/resources/paint_bg.png"));

            runescape_chat_font = Font.createFont(Font.TRUETYPE_FONT, Main.class.getResourceAsStream("/resources/runescape_chat.ttf"));
            runescape_chat_font = runescape_chat_font.deriveFont(16.0f);
            drawPaint = true;
        }
        catch (Exception e)
        {
            log(e);
            errorBox("The script failed to load the Paint images. This is often fixed by restarting OSBot. If not, post on forum thread with the error printed to Logger, and be sure to PM me on forum so I know about it.", "Failed to load paint images");
            return;
        }
        cannonballValue = Exchange.getPrice(2);
        
        
    }
    
    private Action getState() {
    	long timeSinceLastAnimating = System.currentTimeMillis() - lastTimeNotAnimating; // This is the time in milliseconds since last animation.
    	if(timeSinceLastAnimating > 4000) // If its been more than 4 seconds.
    		isSmithing = false;
    	if(!hasRequiredItems()) // If our inventory is full, dont do anything, bank instead
    	{
    		//log("We are out of ore, or our ring of forging has disintegrated, or need to bank");
    		isSmithing = false;
    		return Action.BANK;   		
    	}
    	
    	RS2Widget smithingLevelWidget = getWidgets().get(233, 0);

    	if(smithingLevelWidget != null && smithingLevelWidget.isVisible() && smithingLevelWidget.getMessage().contains("Smithing"))
    		return Action.SMELT;
    	if(myPlayer().isMoving() || myPlayer().isAnimating())
    	{
    		lastTimeNotAnimating = System.currentTimeMillis();
    		return Action.WAIT;
    	}
    	if(isSmithing)
    	{
    		return Action.WAIT;
    	}
    	
    	return Action.SMELT;
    }
    
    private boolean hasRequiredItems() {
    	if(!getInventory().contains("Steel bar") || !getInventory().contains("Ammo mould"))
    	{
    		return false;
    	}
  
		return true;
	}
 
    @Override
    public int onLoop() throws InterruptedException {    	
    	Action action = getState(); 
    	log(action);
    	switch(action)
    	{
		case BANK:
			if(selectedFurnace.getBankArea().contains(myPlayer()))
			{
				if(!getBank().isOpen())
				{
					status = "Opening bank";
					getBank().open();
				}
				else
				{
					status = "Depositing Cannonballs";
					getBank().depositAllExcept("Ammo mould");
					status = "Withdrawing Steel bars";
					getBank().withdraw("Steel bar", 27);
				}	
				
			}
			else
			{
				status = "Walking to bank";
				getWalking().webWalk(selectedFurnace.getBankArea());
			}	
			
			break;
		case SMELT:
			if(selectedFurnace.getFurnaceArea().contains(myPlayer()))
			{
				RS2Widget smithWidget = getWidgets().get(309, 4);
				if(smithWidget != null)
				{
					status = "Selecting Make All";
					smithWidget.interact("Make All");
					status = "Smelting Cannonballs";
					lastTimeNotAnimating = System.currentTimeMillis();
					isSmithing = true;
					getMouse().moveOutsideScreen();
				}
				else
				{
					if(getInventory().getItem("Steel bar") != null)
					{
						new ConditionalSleep(3000) {

							@Override
							public boolean condition() throws InterruptedException {
								status = "Selecting Steel bar";
								getInventory().getItem("Steel bar").interact("Use");								
								return getInventory().isItemSelected();
							}
							
						}.sleep();
						
						Entity furnace = getObjects().closest("Furnace");
						if(furnace != null)
						{
							status = "Using Steel bar on Furnace";
							furnace.interact("Use");							
						}
					}
				}
				
			}
			else
			{
				status = "Walking to Furnace";
				getWalking().webWalk(selectedFurnace.getFurnaceArea());
			}	
			break;
		case WAIT:
			break;		
    	
    	}
        return random(600, 800);
    }
 
    @Override
    public void onExit() {
        log("Thanks for running my Tea Thiever!");
    }
    
    private String ft(long duration) {
        String res = "";
        long days = TimeUnit.MILLISECONDS.toDays(duration);
        long hours = TimeUnit.MILLISECONDS.toHours(duration)
        - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(duration));
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration)
        - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
        .toHours(duration));
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration)
        - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
        .toMinutes(duration));
        if (days == 0) {
        res = (String.format("%02d", hours) + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds));
        } else {
        res = (String.format("%02d", days) + ":" + String.format("%02d", hours) + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds));
        }
        return res;
    }
 
    @Override
    public void onPaint(Graphics2D g) { 
    	if(!drawPaint)
    		return;
    	long runTime = System.currentTimeMillis() - startTime; // mili seconds been running
    	
    	int profit = ammoSmelted * cannonballValue;
    	int ammoProfitPerHour = (int)(profit / ((System.currentTimeMillis() - startTime) / 3600000.0D));
        int ammoPerHour = (int)(ammoSmelted / ((System.currentTimeMillis() - startTime) / 3600000.0D));
    	
    	Point mP = getMouse().getPosition();
        g.setPaint(Color.white);
        g.drawLine(mP.x - 1, 0, mP.x - 1, 500); // Above X
        g.drawLine(mP.x + 1, 0, mP.x + 1, 500); // Below X
        g.drawLine(0, mP.y - 1, 800, mP.y - 1); // Left Y
        g.drawLine(0, mP.y + 1, 800, mP.y + 1); // Right Y
        g.setPaint(Color.black);
        g.drawLine(mP.x, 0, mP.x, 500); // At X
        g.drawLine(0, mP.y, 800, mP.y); // At Y
        
        float opacity = 1f;
 
        Rectangle paintRect = new Rectangle(1, 338, 518, 140);

        if(paintRect.contains(mouse.getPosition()))
        {
            opacity = 0.2f;
        }
        else
        {
            opacity = 1f;
        }
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
        g.setFont(runescape_chat_font);
        g.drawImage(paintBG, null, 1, 338);        
        g.setPaint(Color.blue);
        g.drawString("Plague Doctor's Cannonball Smelter", 145 ,370);
        g.setPaint(Color.black);
        g.drawString("Time Running: " + ft(runTime), 110, 425);
        g.drawString("Cannonballs Smelted: " + ammoSmelted + " Cannonballs/h: " + ammoPerHour, 110, 440);
        g.drawString("Profit: " + profit + " Profit/h: " + ammoProfitPerHour, 110, 455);
        g.drawString("Status: " + status + ".", 110, 470);
        
    }
    
    @Override
    public void onMessage(Message message)
    {
    	String text = message.getMessage();
    	if(text.contains("remove the cannonballs"))
    		ammoSmelted += 4;
    }
    
    public static void errorBox(String infoMessage, String titleBar)
    {
        JOptionPane.showMessageDialog(null, infoMessage, "Error: " + titleBar, JOptionPane.ERROR_MESSAGE);
    }
 
}