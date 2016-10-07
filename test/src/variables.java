

import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.Entity;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;

public class variables {
	
	private Position spinwheel = new Position(3208, 3213, 1);
	RS2Widget bowStringWidget = getWidgets().get(459, 89);
	Entity wheel = objects.closest("Spinning wheel"); 

}

