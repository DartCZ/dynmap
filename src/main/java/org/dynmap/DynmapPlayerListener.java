package org.dynmap;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.util.config.ConfigurationNode;

public class DynmapPlayerListener extends PlayerListener {
    private MapManager mgr;
    private PlayerList playerList;
    private ConfigurationNode configuration;

    public DynmapPlayerListener(MapManager mgr, PlayerList playerList, ConfigurationNode configuration) {
        this.mgr = mgr;
        this.playerList = playerList;
        this.configuration = configuration;
    }

    @Override
    public void onPlayerCommand(PlayerChatEvent event) {
        String[] split = event.getMessage().split(" ");
        if (split[0].equalsIgnoreCase("/dynmap")) {
            if (split.length > 1) {
                if (configuration.getProperty("disabledcommands") instanceof Iterable<?>) {
                    for(String s : (Iterable<String>)configuration.getProperty("disabledcommands")) {
                        if (split[1].equals(s)) {
                            return;
                        }
                    }
                }
                
                if (split[1].equals("render")) {
                    Player player = event.getPlayer();
                    mgr.touch(player.getLocation());
                    event.setCancelled(true);
                } else if (split[1].equals("hide")) {
                	Player player = event.getPlayer();
                	if (split.length == 2) {
                        playerList.hide(player.getName());
                        player.sendMessage("You are now hidden on Dynmap.");
                    } else {
                    	for (int i = 2; i < split.length; i++) {
                            playerList.hide(split[i]);
                            player.sendMessage(split[i] + " is now hidden on Dynmap.");
                        }
                    }
                    event.setCancelled(true);
                } else if (split[1].equals("show")) {
                	Player player = event.getPlayer();
                	if (split.length == 2) {
                        playerList.show(player.getName());
                        player.sendMessage("You are now visible on Dynmap.");
                    } else {
                    	for (int i = 2; i < split.length; i++) {
                            playerList.show(split[i]);
                            player.sendMessage(split[i] + " is now visible on Dynmap.");
                        }
                    }
                    event.setCancelled(true);
                } else if (split[1].equals("fullrender")) {
                    Player player = event.getPlayer();
                    if (player.isOp() == true) {
                    	player.sendMessage("Starting fullrender on this world...");
                    	mgr.renderFullWorld(player.getLocation());
                    	event.setCancelled(true);
                    } else {
                    	player.sendMessage("Only OPs are allowed to use this command!");
                    }
                }
            }
        }
    }

    /**
     * Called when a player sends a chat message
     * 
     * @param event
     *            Relevant event details
     */
    public void onPlayerChat(PlayerChatEvent event) {
        mgr.pushUpdate(new Client.ChatMessage(event.getPlayer().getName(), event.getMessage()));
    }

    /**
     * Called when a player joins or quits the server
     */
    public void onPlayerJoin(PlayerEvent event) {
    	String joinMessage = configuration.getString("joinmessage", "%playername% joined");
  		joinMessage = joinMessage.replaceAll("%playername%", event.getPlayer().getName());
  		mgr.pushUpdate(new Client.ChatMessage("Server", joinMessage));
	}
	public void onPlayerQuit(PlayerEvent event) {
		String quitMessage = configuration.getString("quitmessage", "%playername% quit");
  		quitMessage = quitMessage.replaceAll("%playername%", event.getPlayer().getName());
  		mgr.pushUpdate(new Client.ChatMessage("Server", quitMessage));
	}

}