package de.christoph.party.commands;

import de.christoph.party.PartySystem;
import de.christoph.party.party.Party;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class PartyCommand extends Command {

    public PartyCommand() {
        super("party");
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        ProxiedPlayer player = (ProxiedPlayer) commandSender;
        if(strings.length == 0) {
            sendMenu(player);
        } else if(strings.length == 2) {
            if(strings[0].equalsIgnoreCase("invite")) {
                invitePlayer(player, strings);
            } else if(strings[0].equalsIgnoreCase("kick")) {
                kickPlayer(player, strings);
            } else if(strings[0].equalsIgnoreCase("promote")) {
               promotePlayer(player, strings);
            } else
                sendMenu(player);
        } else if(strings.length == 1) {
            if(strings[0].equalsIgnoreCase("accept")) {
                acceptInvitation(player, strings);
            } else if(strings[0].equalsIgnoreCase("leave")) {
                leaveParty(player, strings);
            } else if(strings[0].equalsIgnoreCase("delete")) {
                deletePlayer(player, strings);
            } else if(strings[0].equalsIgnoreCase("list")) {
                listPlayersOfParty(player, strings);
            } else
                sendMenu(player);
        } else
            sendMenu(player);
    }

    private void listPlayersOfParty(ProxiedPlayer player, String[] strings) {
        if(PartySystem.getPartyListeners().getPlayerParty().containsKey(player)) {
            player.sendMessage(PartySystem.PREFIX + "§7---- §a§lParty Mitglieder §7----");
            for(ProxiedPlayer all : PartySystem.getPartyListeners().getPlayerParty().get(player).getPartyMembers()) {
                if(PartySystem.getPartyListeners().isOwner(all))
                    player.sendMessage(PartySystem.PREFIX + "§7" + all.getDisplayName() + " §7[§4Owner§7]");
                else if(PartySystem.getPartyListeners().isModerator(all))
                    player.sendMessage(PartySystem.PREFIX + "§7" + all.getDisplayName() + " §7[§6Moderator§7]");
                else
                    player.sendMessage(PartySystem.PREFIX + "§7" + all.getDisplayName());
            }
            player.sendMessage(PartySystem.PREFIX + "§7---- §a§lParty Mitglieder §7----");
        } else
            player.sendMessage(PartySystem.PREFIX + "§7Du bist in keiner Party.");
    }

    private void deletePlayer(ProxiedPlayer player, String[] strings) {
        if(PartySystem.getPartyListeners().isOwner(player)) {
            PartySystem.getPartyListeners().getPlayerParty().get(player).removeParty();
        } else
            player.sendMessage(PartySystem.PREFIX + "§7Du bist kein Owner einer Party.");
    }

    private void leaveParty(ProxiedPlayer player, String[] strings) {
        if(PartySystem.getPartyListeners().getPlayerParty().containsKey(player)) {
            Party party = PartySystem.getPartyListeners().getPlayerParty().get(player);
            party.removePlayer(player);
            player.sendMessage(PartySystem.PREFIX + "§7Du hast deine Party verlassen.");
        } else
            player.sendMessage(PartySystem.PREFIX + "§7Du befindest dich nicht in einer Party.");
    }

    private void acceptInvitation(ProxiedPlayer player, String[] strings) {
        if(PartySystem.getPartyListeners().getPlayerInvitations().containsKey(player)) {
            PartySystem.getPartyListeners().getPlayerInvitations().get(player).addPlayerToParty(player);
            PartySystem.getPartyListeners().getPlayerInvitations().remove(player);
            BungeeCord.getInstance().getScheduler().cancel(PartySystem.getPartyListeners().getPlayerParty().get(player).getTaskID());
        } else
            player.sendMessage(PartySystem.PREFIX + "§7Du hast keine offenen Anfragen.");
    }

    private void promotePlayer(ProxiedPlayer player, String[] strings) {
        if (PartySystem.getPartyListeners().isOwner(player)) {
            ProxiedPlayer target = BungeeCord.getInstance().getPlayer(strings[1]);
            if(target != null) {
                if(PartySystem.getPartyListeners().getPlayerParty().containsKey(target) && PartySystem.getPartyListeners().getPlayerParty().get(target) == PartySystem.getPartyListeners().getPlayerParty().get(player)) {
                    PartySystem.getPartyListeners().getPlayerParty().get(target).promotePlayer(target);
                    target.sendMessage(PartySystem.PREFIX + "§7Du bist zum Party Moderator aufgestiegen.");
                    player.sendMessage(PartySystem.PREFIX + "§7Der Spieler ist zum Party Moderator aufgestiegen.");
                } else
                    player.sendMessage(PartySystem.PREFIX + "§7Der Spieler ist nicht in deiner Party.");
            } else
                player.sendMessage(PartySystem.PREFIX + "§7Dieser Spieler ist nicht auf dem Server.");
        } else
            player.sendMessage(PartySystem.PREFIX + "§7Nur der Besitzer einer Party kann Spieler promoten.");
    }

    private void sendMenu(ProxiedPlayer player) {
        player.sendMessage(" ");
        player.sendMessage("§6/party invite <name> §7--- Lade einen Spieler ein");
        player.sendMessage("§6/party accept §7--- Nehme eine Partyanfrage an");
        player.sendMessage("§6/party list §7--- Zeige alle Spieler deiner Party.");
        player.sendMessage("§6/party kick <name> §7--- Kicke einen Spieler aus deiner Party");
        player.sendMessage("§6/party promote <name> §7--- Mache einen Spieler zum Party Moderator");
        player.sendMessage("§6/party leave §7--- Verlasse deine Party");
        player.sendMessage("§6/party delete §7--- Lösche deine Party");
        player.sendMessage(" ");
    }

    private void invitePlayer(ProxiedPlayer player, String[] strings) {
        if(PartySystem.getPartyListeners().isOwner(player)) {
            if(!strings[1].equals(player.getName())) {
                ProxiedPlayer target = BungeeCord.getInstance().getPlayer(strings[1]);
                if(target != null) {
                    if(PartySystem.getPartyListeners().getPlayerParty().containsKey(target) || PartySystem.getPartyListeners().getPlayerInvitations().containsKey(target))
                        player.sendMessage(PartySystem.PREFIX + "§7Dieser Spieler kann gerade keine Anfragen bekommen.");
                    else {
                        Party party = PartySystem.getPartyListeners().getPlayerParty().get(player);
                        party.invitePlayers(player, target);
                    }
                } else
                    player.sendMessage(PartySystem.PREFIX + "§7Dieser Spieler ist nicht auf dem Server.");
            } else
                player.sendMessage(PartySystem.PREFIX + "§7Du kannst dich nicht selber in eine Party einladen.");
        } else if(PartySystem.getPartyListeners().getPlayerParty().containsKey(player)) {
            player.sendMessage(PartySystem.PREFIX + "§7Nur der Party Owner darf Einladungen versenden.");
        } else if(!PartySystem.getPartyListeners().getPlayerParty().containsKey(player)) {
            Party party = new Party(player);
            party.addPlayerToParty(player);
            party.setPartyOwner(player);
            ProxiedPlayer target = BungeeCord.getInstance().getPlayer(strings[1]);
            if(target != null) {
                if(PartySystem.getPartyListeners().getPlayerParty().containsKey(target) || PartySystem.getPartyListeners().getPlayerInvitations().containsKey(target))
                    player.sendMessage(PartySystem.PREFIX + "§7Dieser Spieler kann gerade keine Anfragen bekommen.");
                else {
                    party.invitePlayers(player, target);
                }
            } else
                player.sendMessage(PartySystem.PREFIX + "§7Dieser Spieler ist nicht auf dem Server.");
        }
    }

    private void kickPlayer(ProxiedPlayer player, String[] strings) {
        if(PartySystem.getPartyListeners().isOwner(player)) {
            ProxiedPlayer target = BungeeCord.getInstance().getPlayer(strings[1]);
            if(target != null) {
                if(PartySystem.getPartyListeners().getPlayerParty().containsKey(target) && PartySystem.getPartyListeners().getPlayerParty().get(target) == PartySystem.getPartyListeners().getPlayerParty().get(player)) {
                    PartySystem.getPartyListeners().getPlayerParty().get(target).removePlayer(target);
                    target.sendMessage(PartySystem.PREFIX + "§7Du wurdest aus deiner Party rausgeworfen.");
                } else
                    player.sendMessage(PartySystem.PREFIX + "§7Dieser Spieler ist nicht in deiner Party.");
            } else
                player.sendMessage(PartySystem.PREFIX + "§7Dieser Spieler ist nicht auf dem Server.");
        } else
            player.sendMessage(PartySystem.PREFIX + "§7Nur der Besitzer einer Party kann Spieler kicken.");
    }

}
