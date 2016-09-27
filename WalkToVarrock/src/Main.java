import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
 
 
import java.awt.*;
 
 
@ScriptManifest(name = "SCRIPTNAME", author = "YOURNAME", version = 1.0, info = "INFO(NOTNEEDED)", logo = "IMGURLINKTOLOGO(NOTNEEDED)")
public class Main extends Script {
	
	private	Area varrockArea = new Area(3125, 3520, 3311, 3331);
 
 
 
 
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
    	
    	if(!varrockArea.contains(myPlayer()))
    	{
    		Position varrockFountain = new Position(3214, 3424, 0);
    		getWalking().webWalk(varrockFountain);
    	}
 
        return 700; //The amount of time in milliseconds before the loop is called again
    }
 
 
    @Override
    public void onPaint(Graphics2D g) {
        //This is where you will put your code for paint(s)
 
 
 
 
    }
 
 
}