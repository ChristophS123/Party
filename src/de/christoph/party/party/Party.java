package de.christoph.party.party;

import de.christoph.party.PartySystem;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Party {

    private ProxiedPlayer partyOwner;
    private ArrayList<ProxiedPlayer> partyMembers;
    private ArrayList<ProxiedPlayer> partyModerators;

    private ScheduledTask taskID;

    public Party(ProxiedPlayer partyOwner) {
        this.partyOwner = partyOwner;
        partyMembers = new ArrayList<ProxiedPlayer>();
        partyModerators = new ArrayList<>();
    }

    public void invitePlayers(ProxiedPlayer sender, ProxiedPlayer target) {
        sender.sendMessage(
                PartySystem.PREFIX + "§7Du hast den Spieler §6" + target.getDisplayName() +
                        " §7in deine Party eingeladen.");
        target.sendMessage(PartySystem.PREFIX + "§7Du wurdest vom Spieler §6" + sender.getDisplayName() +
                " §7in eine Party eingeladen. Benutze innerhalb von §610 Sekunden §7den Befehl" +
                " §6/party accept §7um diese anzunehmen.");
        PartySystem.getPartyListeners().getPlayerInvitations().put(target, this);
        taskID = BungeeCord.getInstance().getScheduler().schedule(PartySystem.getPlugin(), new Runnable() {
            int seconds = 10;
            @Override
            public void run() {
                if(seconds == 0) {
                    target.sendMessage(PartySystem.PREFIX + "§7Die Partyanfrage ist §cabgelaufen.");
                    BungeeCord.getInstance().getScheduler().cancel(taskID);
                    PartySystem.getPartyListeners().getPlayerInvitations().remove(target);
                }
                seconds--;
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    public void removeParty() {
        for(ProxiedPlayer all : partyMembers) {
            all.sendMessage(PartySystem.PREFIX + "§7Die Party, in der du warst, wurde §caufgelöst§7.");
            PartySystem.getPartyListeners().getPlayerParty().remove(all);
        }
        partyMembers.clear();
        partyModerators.clear();
        setPartyOwner(null);
    }

    public void addPlayerToParty(ProxiedPlayer targetPlayer) {
        partyMembers.add(targetPlayer);
        for(ProxiedPlayer all : getPartyMembers())
            all.sendMessage(PartySystem.PREFIX + "§7Der Spieler §a" + targetPlayer.getDisplayName() + "§7 hat deine Party betreten.");
        PartySystem.getPartyListeners().getPlayerParty().put(targetPlayer, this);
    }

    public void removePlayer(ProxiedPlayer target) {
        partyMembers.remove(target);
        if(partyModerators.contains(target))
            partyModerators.remove(target);
        for(ProxiedPlayer all : getPartyMembers())
            all.sendMessage(PartySystem.PREFIX + "§7Der Spieler §c" + target.getDisplayName() + "§7 hat deine Party verlassen.");
        PartySystem.getPartyListeners().getPlayerParty().remove(target);
        if(getPartyMembers().size() >= 1 && partyOwner == target) {
            setPartyOwner(getPartyMembers().get(0));
            partyOwner.sendMessage(PartySystem.PREFIX + "§7Du bist nun der Party Owner");
        } else if(getPartyMembers().size() < 1)
            removeParty();
    }


    public void promotePlayer(ProxiedPlayer target) {
        partyModerators.add(target);
    }


    public void setPartyOwner(ProxiedPlayer partyOwner) {
        this.partyOwner = partyOwner;
    }

    public ProxiedPlayer getPartyOwner() {
        return partyOwner;
    }

    public ArrayList<ProxiedPlayer> getPartyMembers() {
        return partyMembers;
    }

    public ScheduledTask getTaskID() {
        return taskID;
    }

    public ArrayList<ProxiedPlayer> getPartyModerators() {
        return partyModerators;
    }
}
