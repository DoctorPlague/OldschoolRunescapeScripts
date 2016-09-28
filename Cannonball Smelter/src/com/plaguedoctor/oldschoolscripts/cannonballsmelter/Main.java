package com.plaguedoctor.oldschoolscripts.cannonballsmelter;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
 
import java.awt.*;
 
@ScriptManifest(author = "Plague Doctor", info = "Smelts cannonballs and banks them", name = "Cannonball Smelter", version = 0, logo = "")
public class Main extends Script {
 
    @Override
    public void onStart() {
        log("Let's get started!");
    }
 
    @Override
    public int onLoop() throws InterruptedException {
        return random(200, 300);
    }
 
    @Override
    public void onExit() {
        log("Thanks for running my Tea Thiever!");
    }
 
    @Override
    public void onPaint(Graphics2D g) {
 
    }
 
}