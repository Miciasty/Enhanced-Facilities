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


    public Restriction() { /* Pusty konstruktor wymagany przez JPA */ };

    public Restriction(RestrictionType type) {
        this.setType(type);
    }

    // --- --- --- --- // Setter's / Getter's // --- --- --- --- //

    public int getId() {
        return id;
    }
    public RestrictionType getType() {
        return this.type;
    }

    private void setType(RestrictionType name) {
        this.type = name;
    }

    // --- --- --- --- // Methods // --- --- --- --- //

    public String toString() {
        return this.type.toString();
    }

    // --- --- --- --- // Allowed Restrictions // --- --- --- --- //

    public enum RestrictionType {
        MOB_SPAWNING,
        BLOCK_BREAK,
        BLOCK_PLACE,

        FRIENDLY_FIRE,

        NULLIFY_DAMAGE,
        NULLIFY_EXPLOSION,

        DENY_ENTRY
    }

}
