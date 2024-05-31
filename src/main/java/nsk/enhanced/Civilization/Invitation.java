package nsk.enhanced.Civilization;

import org.bukkit.entity.Player;

public class Invitation {

    private Player invited;
    private Faction faction;

    private boolean isAccepted = false;

    public Invitation(Player invited, Faction faction) {
        this.setInvited(invited);
        this.setFaction(faction);
    }

    public Player getInvited() { return invited; }
    public Faction getFaction() { return faction; }

    public void setInvited(Player invited) { this.invited = invited; }
    public void setFaction(Faction faction) { this.faction = faction; }

    public void accept(boolean accepted) { this.isAccepted = accepted; }
    public void decline(boolean accepted) { this.isAccepted = accepted; }

    public boolean status() { return isAccepted; }

    public boolean isInvited(Player player) {
        return invited.equals(player);
    }
}
