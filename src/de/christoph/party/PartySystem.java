package de.christoph.party;

import de.christoph.party.commands.PartyCommand;
import de.christoph.party.party.PartyListeners;
import net.md_5.bungee.api.plugin.Plugin;

public class PartySystem extends Plugin {

    public static final String PREFIX = "§6Party §8» §r";
    private static PartySystem plugin;
    private static PartyListeners partyListeners;

    @Override
    public void onEnable() {
        plugin = this;
        partyListeners = new PartyListeners();
        getProxy().getPluginManager().registerListener(this, partyListeners);
        getProxy().getPluginManager().registerCommand(this, new PartyCommand());
    }

    public static PartySystem getPlugin() {
        return plugin;
    }

    public static PartyListeners getPartyListeners() {
        return partyListeners;
    }
}
