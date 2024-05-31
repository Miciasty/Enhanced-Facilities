package nsk.enhanced.Regions.restrictions;

import nsk.enhanced.Regions.Restriction;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "war_restriction")
public class WarRestriction  extends Restriction {

    @Column(nullable = false)
    private int enemyFactionID;

    public WarRestriction() {}

    public WarRestriction(RestrictionType type, boolean value, int factionID) {
        super(type, value);
        setEnemyFactionID(factionID);
    }

    public int getEnemyFactionID() {
        return enemyFactionID;
    }

    public void setEnemyFactionID(int id) {
        this.enemyFactionID = id;
    }
}
