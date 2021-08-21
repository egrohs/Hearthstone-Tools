package hstools;

@org.springframework.context.annotation.Configuration
//@EnableNeo4jRepositories(basePackageClasses = UserRepository.class)
//@ComponentScan(basePackageClasses = UserService.class)
public class EmbeddedConfig {
//	@Autowired
//	private Environment env;
//
//	@Bean
//	public Configuration configuration() {
//		String s = env.getProperty("neo4j-db");
//		System.out.println(s);
//		return new Configuration.Builder().uri(s).build();
//	}
//
//	@Bean
//	public SessionFactory sessionFactory() {
//		// with domain entity base package(s)
//		return new SessionFactory(configuration(), "hstools.domain.entities");
//	}
//
//	@Bean
//	public Neo4jTransactionManager transactionManager() {
//		return new Neo4jTransactionManager(sessionFactory());
//	}
}