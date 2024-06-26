package nsk.enhanced;

import nsk.enhanced.Buildings.Basic.Sawmill;
import nsk.enhanced.Buildings.Building;
import nsk.enhanced.Civilization.Faction;
import nsk.enhanced.Regions.Region;
import nsk.enhanced.Regions.Restriction;
import nsk.enhanced.Regions.Territory;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.Test;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DatabaseTest {

    private List<Faction> factions = new ArrayList<>();
    private List<Building> buildings = new ArrayList<>();
    private List<Region> regions = new ArrayList<>();
    private List<Territory> territories = new ArrayList<>();
    private List<Restriction> restrictions = new ArrayList<>();

    private SessionFactory sessionFactory;

    @Test
    public void TestDatabase() {

        sessionFactory = new Configuration().configure().buildSessionFactory();

        /*StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure()
                .build();

        Metadata metadata = new MetadataSources(registry).buildMetadata();*/

        //SchemaExport schemaExport = new SchemaExport();
        //schemaExport.drop(EnumSet.of(TargetType.DATABASE), metadata);

        //SessionFactory sessionFactory = metadata.buildSessionFactory();
        //Session session = sessionFactory.openSession();

        //session.beginTransaction();

        loadFactionsFromDatabase();

        System.out.println("Factions: " + factions.size());
        for (Faction f : factions) {
            System.out.println(f.getName());
        }

        /*
        System.out.println("Buildings: " + buildings.size());
        for (Building b : buildings) {
            System.out.println(b.getType());
        }

        System.out.println("Territories: " + territories.size());
        for (Territory t : territories) {
            System.out.println(t.getId());
        }

        System.out.println("Regions: " + regions.size());
        for (Region r : regions) {
            System.out.println(r.getId());
        }

        System.out.println("Restrictions: " + restrictions.size());
        for (Restriction r : restrictions) {
            System.out.println(r.getId());
        }
        */


        Faction faction = factions.get(1);
        List<Building> buildingst = faction.getBuildings();

        Building building = buildingst.get(1);
       try {
           buildingst.remove(building);
           this.saveEntityAsync(faction)
                   .thenRun(() -> {
                       System.out.println("Building was successfully removed");
                   }).exceptionally(e -> {
                   buildingst.add(building);
                   throw new IllegalStateException("Query failed", e);
               }).get();
       } catch (Exception e) {
           e.printStackTrace();
       }


        //session.getTransaction().commit();

        //session.close();

        //sessionFactory.close();

        //System.out.println(building.getType());
    }

    public <T> CompletableFuture<Void> saveEntityAsync(T entity) {

        return CompletableFuture.runAsync(() -> {

            System.out.println("Trying to save " + entity.getClass().getSimpleName());

            try (Session session = sessionFactory.openSession()) {
                session.beginTransaction();

                System.out.println("Saving " + entity.getClass().getSimpleName());

                session.saveOrUpdate(entity);
                session.getTransaction().commit();
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }

    public <T> CompletableFuture<Void> deleteEntityAsync(T entity) {

        return CompletableFuture.runAsync(() -> {

            System.out.println("Trying to delete " + entity.getClass().getSimpleName());

            try (Session session = sessionFactory.openSession()) {
                session.beginTransaction();

                System.out.println("Deleting " + entity.getClass().getSimpleName());

                session.delete(entity);
                session.getTransaction().commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void loadFactionsFromDatabase() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Faction> query = builder.createQuery(Faction.class);
            query.from(Faction.class);

            List<Faction> result = session.createQuery(query).getResultList();
            for (Faction f : result) {
                factions.add(f);

                Hibernate.initialize(f.getPlayers());
                Hibernate.initialize(f.getBuildings());
                Hibernate.initialize(f.getTerritory());
                Hibernate.initialize(f.getRestrictions());

                List<Building> buildings = f.getBuildings();

                for (Building b : buildings) {
                    Hibernate.initialize(b.getRegions());
                    Hibernate.initialize(b.getRestrictions());
                }

            }
            session.getTransaction().commit();

            // this.loadTerritoriesFromDatabase();
            // this.loadBuildingsFromDatabase();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadBuildingsFromDatabase() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Building> query = builder.createQuery(Building.class);
            query.from(Building.class);

            List<Building> result = session.createQuery(query).getResultList();
            for (Building b : result) {
                buildings.add(b);

                Hibernate.initialize(b.getRegions());
                Hibernate.initialize(b.getRestrictions());
            }
            session.getTransaction().commit();

            this.loadRegionsFromDatabase();
            this.loadRestrictionsFromDatabase();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadTerritoriesFromDatabase() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Territory> query = builder.createQuery(Territory.class);
            query.from(Territory.class);

            List<Territory> result = session.createQuery(query).getResultList();
            territories.addAll(result);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadRegionsFromDatabase() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Region> query = builder.createQuery(Region.class);
            query.from(Region.class);

            List<Region> result = session.createQuery(query).getResultList();
            regions.addAll(result);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadRestrictionsFromDatabase() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Restriction> query = builder.createQuery(Restriction.class);
            query.from(Restriction.class);

            List<Restriction> result = session.createQuery(query).getResultList();
            restrictions.addAll(result);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


