package nsk.enhanced.Regions;

import nsk.enhanced.Methods.PluginInstance;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.Listener;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "territories")
public class Territory implements Listener {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private double xA, yA, zA;

    @Column(nullable = false)
    private double xB, yB, zB;

    @Column(nullable = false)
    private String worldName;


    public Territory() { /* Pusty konstruktor wymagany przez JPA */ }

    public Territory(Location pointA, Location pointB) {

        try {
            if (!pointA.getWorld().equals(pointB.getWorld())) {
                throw new IllegalArgumentException("pointA and pointB must be in the same world");
            } else {

                setWorld(pointA.getWorld());
                setPointA(pointA);
                setPointB(pointB);

            }
        } catch (Exception e) {
//          PluginInstance.getInstance().playerWarning(e, p);
            PluginInstance.getInstance().consoleError(e);
        }
    }

    // --- --- --- --- // Setter's / Getter's // --- --- --- --- //

    public int getId() {
        return id;
    }

    public World getWorld() {
        return getPointA().getWorld();
    }

    public Location getPointA() {
        return new Location(Bukkit.getWorld(worldName), xA, yA, zA);
    }
    public Location getPointB() {
        return new Location(Bukkit.getWorld(worldName), xB, yB, zB);
    }

    protected void setWorld(World world) {
        worldName = world.getName();
    }

    protected void setPointA(Location pointA) {
        this.xA = pointA.getX();
        this.yA = pointA.getY();
        this.zA = pointA.getZ();
    }
    protected void setPointB(Location pointB) {
        this.xB = pointB.getX();
        this.yB = pointB.getY();
        this.zB = pointB.getZ();
    }

    public boolean contains(Location location) {
        if (!location.getWorld().getName().equals(worldName)) {
            return false;
        }

        double minX = Math.min(xA, xB);
        double maxX = Math.max(xA, xB);
        double minY = Math.min(yA, yB);
        double maxY = Math.max(yA, yB);
        double minZ = Math.min(zA, zB);
        double maxZ = Math.max(zA, zB);

        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();

        return  x >= minX && x <= maxX &&
                y >= minY && y <= maxY &&
                z >= minZ && z <= maxZ;
    }

    // --- --- --- --- // Methods // --- --- --- --- //

    public boolean overlaps(Territory other) {
        if (!this.worldName.equals(other.worldName)) {
            return false;
        }

        return (
                this.contains(other.getPointA()) || this.contains(other.getPointB()) ||
                other.contains(this.getPointA()) || other.contains(this.getPointB()));
    }

    @Override
    public String toString() {
        return "Region{" + "pointA=" + getPointA() + ", pointB=" + getPointB() + '}';
    }
}
