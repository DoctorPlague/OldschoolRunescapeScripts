package com.plaguedoctor.oldschoolscripts.pmagicguild;

import org.osbot.rs07.api.Client.LoginState;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import org.osbot.rs07.utility.ConditionalSleep;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane; 


 
@ScriptManifest(name = "PMagicGuild", author = "Plague Doctor", version = 1.0, info = "Buys runes and battlestaves", logo = "")
public class Main extends Script { 
	
	// Variables
	
	Area magicGuild = new Area(new Position(2584, 3094, 1), new Position(2596, 3081, 1));
	Area yanilleBank = new Area(2616, 3088, 2609, 3097);	
	String items[] = new String[] {"Death rune", "Nature rune", "Battlestaff",  };
	int rand = ThreadLocalRandom.current().nextInt(1,10);
	int randResponse = ThreadLocalRandom.current().nextInt(1,10);	
	int timesBought = 0;
	int hopQuanitity = 0;
	public int X;
	private String status = "Starting bot";
	private long startTime;	
	private Font runescape_chat_font; // Font used by paint
	private BufferedImage paintBG; // Background image used for paint, loaded when GUI is done.			
	private boolean drawPaint = false;
	private long deathRunesBought;
	private long natureRunesBought;
	private long chaosRunesBought;
	private long battlestaffBought;
	private int deathRuneValue = 0;
	private int natureRuneValue = 0;
	private int chaosRuneValue = 0;
	private int battlestaffValue = 0;
	
	
	
    @Override
    public void onStart() {      	
    	startTime = System.currentTimeMillis();
    	deathRuneValue = Exchange.getPrice(560);
    	natureRuneValue = Exchange.getPrice(561);
    	chaosRuneValue = Exchange.getPrice(562);
    	battlestaffValue = Exchange.getPrice(1391);
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
            errorBox("The script has failed to load paint images.", "Failed to load paint images");
            return;
        }
    }
    
    private enum State {
		BANK_INVENTORY, SHOP, HOP // Declares the different states of the program.
	};	
	

	private State getState()
	{			
		if(getStore().isOpen()) {
			if(getStore().getAmount(items) <= 0) {				
				return State.HOP;
			}
		}		
		
		if(inventory.isFull()) {
			return State.BANK_INVENTORY;
		}
		
		
		return State.SHOP;
	}	
	
	
    @Override
    public int onLoop() throws InterruptedException {
    	State state = getState();
    	log(state);
    	switch  (state) {
    	case SHOP:    		
    		RS2Widget chatBox = getWidgets().get(162,43,0);
    		if(chatBox.getMessage().contains("coins")) {
    			status = "Logging out."; 
    			if(!getStore().isOpen()) {
    				getStore().close();
    			}
    			logoutTab.logOut();    			
				stop();
    		}
    		hopQuanitity = 0;    		
    		rand = ThreadLocalRandom.current().nextInt(1,10);
    		if(rand == 5) {
    			status = "Anti-ban.";
    			getMouse().moveSlightly();
    		}
    		if(rand == 10) {
    			status = "Anti-ban.";
    			getMouse().moveOutsideScreen();
    			sleep(random(2000-7000));
    		}
    		NPC Store = npcs.closest("Magic Store owner");    		
    		if(magicGuild.contains(myPlayer())) {    			
    			if(Store != null && getStore().isOpen()) {    				
    				if(getStore().getAmount("Death rune") >= 1) {
    					status = "Shopping.";
    					getStore().buy("Death rune", 10);     					
    					timesBought++;
    				}
    				if(getStore().getAmount("Nature rune") >= 1) {
    					status = "Shopping.";
    					getStore().buy("Nature rune", 10);    					
    					timesBought++;
    				}
    				if(getStore().getAmount("Battlestaff") >= 1) {
    					status = "Shopping.";
    					getStore().buy("Battlestaff", 10);
    					if(getStore().getAmount("Chaos rune") >= 1) {
    						getStore().buy("Chaos rune", 10);
    					}
    					timesBought++;
    				}   				
    			}
    			if(!getStore().isOpen() && Store != null) {
    				status = "Opening store.";
    				Store.interact("Trade");
    				new ConditionalSleep(10000) {
    					@Override
    					
    					public boolean condition() throws InterruptedException {
    						return getStore().isOpen();
    					}
    				}.sleep();
    			}
    		}
    		else {    		
    			status = "Walking to store.";
    			getWalking().webWalk(magicGuild);
    		}
    		break;          
    	
    	case HOP:      		
    		if(getStore().isOpen()) {
    			status = "Closing store.";
				getStore().close();
				new ConditionalSleep(10000) {
					@Override
					
					public boolean condition() throws InterruptedException {
						return !getStore().isOpen();
					}
				}.sleep();
			}
    		if(hopQuanitity != 0) {
    			status = "Delaying hop.";
    			sleep(random(3000,4000));
    		}
    		if(timesBought <= 9) {
    			status = "Delaying hop.";
    			sleep(random(3000,4000));
    		}
    		timesBought = 0;
    		X = getWorlds().getCurrentWorld();
    		status = "World hopping.";
    		getWorlds().hopToP2PWorld();

    		new ConditionalSleep(10000) {
    		@Override
    		public boolean condition() throws InterruptedException {
    		return getWorlds().getCurrentWorld()!= X;
    		}
    		}.sleep();    			
			hopQuanitity++;
    		break;  	
    	   		
    		
    	case BANK_INVENTORY:
    		
    		hopQuanitity = 0;
    		rand = ThreadLocalRandom.current().nextInt(1,10);
    		if(rand == 5) {
    			status = "Anti-ban.";
    			getMouse().moveSlightly();
    		}
    		if(rand == 10) {
    			status = "Anti-ban.";
    			getMouse().moveOutsideScreen();
    			sleep(random(2000-4000));
    		}
    		if(yanilleBank.contains(myPlayer()))
			{
				if(!getBank().isOpen())
				{					
					status = "Opening bank.";
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
					status = "Depositing inventory.";
					getBank().depositAllExcept("Coins");
					deathRunesBought = getBank().getAmount("Death rune");
					natureRunesBought = getBank().getAmount("Nature rune");
					chaosRunesBought = getBank().getAmount("Chaos rune");
					battlestaffBought = getBank().getAmount("Battlestaff");
				}	
				
			}
			else
			{	
				status = "Walking to bank.";
				getWalking().webWalk(yanilleBank);
				
			}
    		break;  	
    	
    	}
    	if (getClient().getLoginState() == LoginState.LOADING || getClient().getLoginState() == LoginState.LOADING_MAP) {
    		status = "Loading.";
    		return 300;
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
    	if(!drawPaint)
    		return;    	
    	long runTime = System.currentTimeMillis() - startTime; // mili seconds been running
    	int deathRuneProfit = (int) ((deathRuneValue - 223) * deathRunesBought);
    	int natureRuneProfit = (int) ((natureRuneValue - 223) * natureRunesBought);
    	int chaosRuneProfit = (int) ((chaosRuneValue - 90) * chaosRunesBought);
    	int battlestaffProfit = (int) ((battlestaffValue - 7007) * battlestaffBought);
    	int profit = (int) deathRuneProfit + natureRuneProfit + chaosRuneProfit + battlestaffProfit;
    	int profitPerHour = (int)(profit / ((System.currentTimeMillis() - startTime) / 3600000.0D));
        
    	
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
        
        int offsetX = 140;
        int offsetY = 403;
        
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
        g.setFont(runescape_chat_font);
        g.drawImage(paintBG, null, 1, 338);        
        g.setPaint(Color.blue);
        g.drawString("Plague Doctor's Magic Guild Shopper", 212 ,370);
        g.setPaint(Color.black);
        g.drawString("Time Running: " + ft(runTime), offsetX, offsetY);        
        g.drawString("Profit: " + profit + " Profit/h: " + profitPerHour, offsetX, offsetY + 15);
        g.drawString("Status: " + status + ".", offsetX, offsetY + 30);
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
    
    public static void errorBox(String infoMessage, String titleBar)
    {
        JOptionPane.showMessageDialog(null, infoMessage, "Error: " + titleBar, JOptionPane.ERROR_MESSAGE);
    }
 
 
}





