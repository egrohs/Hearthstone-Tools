package hstools;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.data.neo4j.transaction.Neo4jTransactionManager;

@org.springframework.context.annotation.Configuration
//@EnableNeo4jRepositories(basePackageClasses = UserRepository.class)
//@ComponentScan(basePackageClasses = UserService.class)
public class EmbeddedConfig {
	@Autowired
	private Environment env;

	@Bean
	public Configuration configuration() {
		String s = env.getProperty("neo4j-db");
		System.out.println(s);
		return new Configuration.Builder().uri(s).build();
	}

	@Bean
	public SessionFactory sessionFactory() {
		// with domain entity base package(s)
		return new SessionFactory(configuration(), "hstools.domain.entities");
	}

	@Bean
	public Neo4jTransactionManager transactionManager() {
		return new Neo4jTransactionManager(sessionFactory());
	}
//    @Bean(destroyMethod = "shutdown")
//    public GraphDatabaseService graphDatabaseService() {
//        GraphDatabaseService graphDatabaseService = new GraphDatabaseFactory()
//            .newEmbeddedDatabaseBuilder(new File("target/graph.db"))
//            .setConfig(GraphDatabaseSettings.forbid_shortestpath_common_nodes, "false")
//            .newGraphDatabase();
//
//        return graphDatabaseService;
//    }

//    @Bean
//    public SessionFactory getSessionFactory() {
//        org.neo4j.ogm.config.Configuration configuration = new org.neo4j.ogm.config.Configuration();
//        EmbeddedDriver driver = new EmbeddedDriver(graphDatabaseService());
//        Components.setDriver(driver);
//        return new SessionFactory(configuration, User.class.getPackage().getName());
//    }

//    @Bean
//    public Neo4jTransactionManager transactionManager() throws Exception {
//        return new Neo4jTransactionManager(getSessionFactory());
//    }
}