package nsk.enhanced;

import nsk.enhanced.Buildings.Basic.Forge;
import nsk.enhanced.Buildings.Basic.Sawmill;
import nsk.enhanced.Buildings.Basic.TownHall;
import nsk.enhanced.Buildings.Basic.Windmill;
import nsk.enhanced.Buildings.Building;
import nsk.enhanced.Civilization.Faction;
import nsk.enhanced.Methods.PluginInstance;
import nsk.enhanced.Regions.Region;
import nsk.enhanced.Regions.Restriction;
import nsk.enhanced.Regions.Territory;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;
import org.junit.Test;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.ArrayList;
import java.util.EnumSet;
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

        Faction faction = factions.get(2);

        String p = faction.getName();
        faction.setName("GUILD");

        try {


            CompletableFuture.allOf(
                    this.saveEntityAsync(faction)
            ).exceptionally(e -> {
                faction.setName(p);
                throw new IllegalStateException("Query failed! ", e);
            });
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

            System.out.println("Saving " + entity.getClass().getSimpleName());

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


    public void loadFactionsFromDatabase() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Faction> query = builder.createQuery(Faction.class);
            query.from(Faction.class);

            List<Faction> result = session.createQuery(query).getResultList();
            factions.addAll(result);
            session.getTransaction().commit();

            this.loadTerritoriesFromDatabase();
            this.loadBuildingsFromDatabase();

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
            buildings.addAll(result);
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


