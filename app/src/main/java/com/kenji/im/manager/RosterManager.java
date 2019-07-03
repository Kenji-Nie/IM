package com.kenji.im.manager;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.roster.Roster;
import org.jxmpp.jid.BareJid;

public class RosterManager {
    private static RosterManager instance;
    private Roster roster;

    private RosterManager() {

    }

    public static RosterManager getInstance(AbstractXMPPConnection connection) {
        instance = new RosterManager();
        instance.roster = Roster.getInstanceFor(connection);
        return instance;
    }

    public boolean addEntry(BareJid user, String name, String group) {
        try {
            roster.createEntry(user, name, new String[]{group});
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


}
