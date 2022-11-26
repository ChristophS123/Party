package de.christoph.party.party;

import de.christoph.party.PartySystem;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.event.EventHandler;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class PartyListeners implements Listener {

    private HashMap<ProxiedPlayer, Party> playerInvitations;
    private HashMap<ProxiedPlayer, Party> playerParty;
    private HashMap<String, Party> partyLeavers;

    private ScheduledTask taskID;

    public PartyListeners() {
        playerInvitations = new HashMap<>();
        playerParty = new HashMap<>();
        partyLeavers = new HashMap<String, Party>();
    }


    @EventHandler
    public void onServerSwitch(ServerSwitchEvent event) {
        ProxiedPlayer player = event.getPlayer();
        if(isOwner(player) || isModerator(player)) {
            for(ProxiedPlayer all :  playerParty.get(player).getPartyMembers()) {
                all.connect(player.getServer().getInfo());
            }
        }
    }


    @EventHandler
    public void onLeaveProxy(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        if(playerParty.containsKey(player)) {
            partyLeavers.put(player.getName(), playerParty.get(player));
            playerParty.get(player).removePlayer(player);
            taskID = BungeeCord.getInstance().getScheduler().schedule(PartySystem.getPlugin(), new Runnable() {
                int seconds = 5;
                @Override
                public void run() {
                    if(seconds == 0) {
                        partyLeavers.remove(player.getName());
                    }
                    seconds--;
                }
            }, 0, 1, TimeUnit.SECONDS);
        }
    }


    @EventHandler
    public void onJoinProxy(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();
        if(partyLeavers.containsKey(player.getName())) {
            Party party = partyLeavers.get(player.getName());
            party.addPlayerToParty(player);
            BungeeCord.getInstance().getScheduler().cancel(taskID);
            partyLeavers.remove(player.getName());
        }
    }



    public HashMap<ProxiedPlayer, Party> getPlayerInvitations() {
        return playerInvitations;
    }

    public HashMap<ProxiedPlayer, Party> getPlayerParty() {
        return playerParty;
    }

    public boolean isOwner(ProxiedPlayer proxiedPlayer) {
        return (playerParty.containsKey(proxiedPlayer) && proxiedPlayer == playerParty.get(proxiedPlayer).getPartyOwner());
    }


    public boolean isModerator(ProxiedPlayer proxiedPlayer) {
        return(playerParty.containsKey(proxiedPlayer) && playerParty.get(proxiedPlayer).getPartyModerators().contains(proxiedPlayer));
    }

}
