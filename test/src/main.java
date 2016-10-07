import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.Entity;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
 
 
import java.awt.*;
 
 
@ScriptManifest(name = "test", author = "YOURNAME", version = 1.0, info = "INFO(NOTNEEDED)", logo = "IMGURLINKTOLOGO(NOTNEEDED)")
public class main extends Script {
	
	private Position spinwheel = new Position(3208, 3213, 1);
	RS2Widget bowStringWidget = getWidgets().get(459, 89);
	Entity wheel = objects.closest("Spinning wheel"); 
 
 
 
 
    @Override
    public void onStart() {
        // This gets executed when the script first starts.
 
 
    }
   
    @Override
    public void onExit() {
        // This will get executed when the user hits the stop script button.
 
 
    }
 
 
    @Override
    public int onLoop() {
    	
    	if(myPlayer().isMoving() && myPlayer().isAnimating())
    		return 700;
        // Called every loop, the time between this call of loop and the next one depends on what you return
    	getWalking().webWalk(spinwheel);
    	
 
        return 700; //The amount of time in milliseconds before the loop is called again
    }
 
 
    @Override
    public void onPaint(Graphics2D g) {
        //This is where you will put your code for paint(s)
 
 
 
 
    }
 
 
}