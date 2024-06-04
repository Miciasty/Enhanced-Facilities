package nsk.enhanced.Buildings.Basic;

import nsk.enhanced.Buildings.Building;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("windmill")
public class Windmill extends Building {

    public Windmill() {}

}
