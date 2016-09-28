package com.plaguedoctor.oldschoolscripts.cannonballsmelter;

import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.script.MethodProvider;

public enum Furnace {

	ALKHARID(new Area(3265, 3173, 3272, 3161),  new Area(3279, 3188, 3272, 3184), false, "Al-Kharid"),
	FALADOR(new Area(
			new int[][]{
				{2943, 3373},
				{2943, 3368},
				{2945, 3368},
				{2945, 3366},
				{2950, 3366},
				{2950, 3369},
				{2950, 3370},
				{2948, 3370},
				{2948, 3374},
				{2943, 3374}
			}
		), new Area(2970, 3368, 2975, 3372), false, "Falador"),
	EDGEVILLE(new Area(3098, 3488, 3092, 3495), new Area(3110, 3501, 3105, 3496), true, "Edgeville");
	
	private Area bankArea;
	private Area furnaceArea;
	private boolean membersOnly;
	private String furnaceName;
	
	Furnace(Area bankArea, Area furnaceArea, boolean membersOnly, String furnaceName)
	{
		this.bankArea = bankArea;
		this.furnaceArea = furnaceArea;
		this.membersOnly = membersOnly;
		this.furnaceName = furnaceName;
	}
	
	/**
	 * Returns true if the current player can use this furnace.
	 * 
	 * @param mp Intance of MethodProvider
	 * @param isMember The current bots methodprovider
	 * @return Returns whether player can currently use the furnace.
	 */
	public boolean canUse(MethodProvider mp, boolean isMember)
	{
		if(membersOnly && mp.getWorlds().isMembersWorld() && isMember)
			return true;
		else if(!membersOnly)
			return true;
		return false;
	}
	
	/**
	 * Returns true if the current player can use this furnace.
	 * 
	 * @return bankArea Returns an instance of an Area which is the bank.
	 */
	public Area getBankArea()
	{
		return bankArea;
	}
	
	/**
	 * Returns true if the current player can use this furnace.
	 * 
	 * @return furnaceArea Returns an instance of an Area which is the furnace room.
	 */
	public Area getFurnaceArea()
	{
		return furnaceArea;
	}
	
	/**
	 * Returns the name of the area that contains the currently selected furnace.
	 * 
	 * @return furnaceArea Returns an instance of an Area which is the furnace room.
	 */
	public String getName()
	{
		return furnaceName;
	}
	
}
