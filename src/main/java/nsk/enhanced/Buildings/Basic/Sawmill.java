package nsk.enhanced.Buildings.Basic;

import nsk.enhanced.Buildings.Building;
import nsk.enhanced.Regions.Region;

import java.util.ArrayList;

public class Sawmill extends Building {


    public Sawmill(String type, int id, String level, int durability, ArrayList<Region> regions) {
        super(type, id, level, durability, regions);
    }

}
