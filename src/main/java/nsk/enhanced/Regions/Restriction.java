package nsk.enhanced.Regions;

import javax.persistence.*;

@Entity
@Table(name = "restrictions")
@Inheritance(strategy = InheritanceType.JOINED)
public class Restriction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RestrictionType type;

    @Column(nullable = false)
    private boolean value;

    public Restriction() { /* Pusty konstruktor wymagany przez JPA */ };

    public Restriction(RestrictionType type, boolean value) {
        this.setName(type);
        this.setValue(value);
    }

    // --- --- --- --- // Setter's / Getter's // --- --- --- --- //

    public int getId() {
        return id;
    }
    public RestrictionType getName() {
        return this.type;
    }

    private void setId(int id) {
        this.id = id;
    }
    private void setName(RestrictionType name) {
        this.type = name;
    }
    private void setValue(boolean value) {
        this.value = value;
    }

    // --- --- --- --- // Methods // --- --- --- --- //

    public boolean isActive() {
        return this.value;
    }

    // --- --- --- --- // Allowed Restrictions // --- --- --- --- //

    public enum RestrictionType {
        WAR,
        MOB_SPAWNING,
        BLOCK_BREAK,
        BLOCK_PLACE,
        PVP,
    }

}
