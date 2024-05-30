package nsk.enhanced.Buildings.Environment;

import nsk.enhanced.Buildings.Natural;
import org.bukkit.event.Listener;

public class Mine extends Natural implements Listener {

    private int gold = 6000;

    private void setHealth() {
        this.Health = -1;
    }
}
