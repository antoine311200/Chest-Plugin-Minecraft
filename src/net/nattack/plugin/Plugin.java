package net.nattack.plug;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin {
	
	public int time = 0;
	
	private PlugListener listener;

	@Override
	public void onDisable() {
		
		System.out.println( "[Chest Plugin] Unloaded");
		getLogger().info("[Chest Plugin] 1.0.0 by Nattack has been invoked!");
	}
	
	@Override
	public void onEnable()  {
		
		System.out.println( "[Chest Plugin] Loaded");
		getLogger().info("[Chest Plugin] 1.0.0 by Nattack has been invoked!");
		
		listener = new PlugListener(this);
		
		PluginManager pm = getServer().getPluginManager();
		
		pm.registerEvents(listener, this);
	}
}
