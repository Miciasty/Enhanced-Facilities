package nsk.enhanced.Buildings.Basic;

import nsk.enhanced.Buildings.Building;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("magiccore")
public class MagicCore extends Building {
    public MagicCore() {}
}
