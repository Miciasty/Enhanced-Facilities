package nsk.enhanced.Buildings.Basic;

import nsk.enhanced.Buildings.Building;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("sanctuary")
public class Sanctuary extends Building {
    public Sanctuary() {}
}
