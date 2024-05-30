package nsk.enhanced.Regions;

import nsk.enhanced.Methods.PluginInstance;
import org.bukkit.Location;
import org.bukkit.event.Listener;

public class Region implements Listener {

    protected Location pointA, pointB;

    public Region(Location pointA, Location pointB) {

        try {
            if (!pointA.getWorld().equals(pointB.getWorld())) {
                throw new IllegalArgumentException("pointA and pointB must be in the same world");
            } else {

                setPointA(pointA);
                setPointB(pointB);

            }
        } catch (Exception e) {
//          PluginInstance.getInstance().playerWarning(e, p);
            PluginInstance.getInstance().consoleError(e);
        }
    }

    public Location getPointA() { return pointA; }
    public Location getPointB() { return pointB; }

    protected void setPointA(Location pointA) { this.pointA = pointA; }
    protected void setPointB(Location pointB) { this.pointB = pointB; }

    public boolean contains(Location location) {
        if (!location.getWorld().equals(pointA.getWorld()) || !location.getWorld().equals(pointB.getWorld())) {
            return false;
        }

        double minX = Math.min(pointA.getX(), pointB.getX());
        double maxX = Math.max(pointA.getX(), pointB.getX());
        double minY = Math.min(pointA.getY(), pointB.getY());
        double maxY = Math.max(pointA.getY(), pointB.getY());
        double minZ = Math.min(pointA.getZ(), pointB.getZ());
        double maxZ = Math.max(pointA.getZ(), pointB.getZ());

        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();

        return  x >= minX && x <= maxX &&
                y >= minY && y <= maxY &&
                z >= minZ && z <= maxZ;
    }

    @Override
    public String toString() {
        return "Region{" + "pointA=" + pointA + ", pointB=" + pointB + '}';
    }
}
