package nsk.enhanced.Civilization.status;

import nsk.enhanced.Civilization.Faction;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "wars")
public class atWar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "attacker_id", referencedColumnName = "id")
    private Faction attacker;
    //private List<Faction> attackerAllies;

    @ManyToOne
    @JoinColumn(name = "defender_id", referencedColumnName = "id")
    private Faction defender;
    //private List<Faction> defenderAllies;

    public atWar() { /* Pusty konstruktor wymagany przez JPA */ }

    public atWar(Faction attacker, Faction defender) {
        setAttacker(attacker);
        setDefender(defender);
    }

    // --- --- --- --- // Setter's / Getter's // --- --- --- --- //

    public int getId() {
        return id;
    }

    private void setAttacker(Faction attacker) {
        this.attacker = attacker;
    }
    private void setDefender(Faction defender) {
        this.defender = defender;
    }

    public Faction getAttacker() {
        return attacker;
    }
    public Faction getDefender() {
        return defender;
    }

}
