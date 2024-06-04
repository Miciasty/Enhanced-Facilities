package nsk.enhanced.Buildings.Basic;

import nsk.enhanced.Buildings.Building;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("townhall")
public class TownHall extends Building {

    public TownHall() {}

}
