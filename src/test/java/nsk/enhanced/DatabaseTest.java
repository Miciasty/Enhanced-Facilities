package nsk.enhanced;

import nsk.enhanced.Civilization.Faction;
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

import java.util.EnumSet;
import java.util.List;

public class DatabaseTest {
    public static void main(String[] args) {

        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure()
                .build();

        Metadata metadata = new MetadataSources(registry).buildMetadata();

        SchemaExport schemaExport = new SchemaExport();
        schemaExport.drop(EnumSet.of(TargetType.DATABASE), metadata);


        SessionFactory sessionFactory = metadata.buildSessionFactory();
        Session session = sessionFactory.openSession();

        session.beginTransaction();

        Faction faction = new Faction("Test1");

        session.save(faction);

        session.getTransaction().commit();
        session.close();

        sessionFactory.close();
    }
}


