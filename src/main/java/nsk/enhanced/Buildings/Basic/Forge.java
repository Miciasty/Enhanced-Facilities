package nsk.enhanced.Buildings.Basic;

import nsk.enhanced.Buildings.Building;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("forge")
public class Forge extends Building {

    public Forge() {}

}
