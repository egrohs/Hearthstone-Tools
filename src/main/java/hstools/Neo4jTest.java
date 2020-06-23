package hstools;

import hstools.domain.entities.Node;

public class Neo4jTest {// implements AutoCloseable {
	static EntityService es = new EntityService();

	public static void main(String... args) throws Exception {
		createGraph();
	}

	private static void createGraph() {
		Iterable<Node> ns = es.findAll();
		if (ns == null) {
			System.out.println("null");
		}
//		Node c1 = new Node("NOME");
//		c1 = es.createOrUpdate(c1);
//		c1 = es.find(0L);
//		System.out.println(c1.getName());
	}
}