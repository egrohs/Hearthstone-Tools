package ht;

import org.neo4j.graphdb.GraphDatabaseService;

import ht.model.Nodo;

public class Neo4jTest {// implements AutoCloseable {
	static EntityService es = new EntityService();

	public static void main(String... args) throws Exception {
		createGraph();
	}

	private static void createGraph() {
		Iterable<Nodo> ns = es.findAll();
		if (ns == null) {
			System.out.println("null");
		}
		Nodo c1 = new Nodo("NOME");
		c1 = es.createOrUpdate(c1);
		c1 = es.find(0L);
		System.out.println(c1.getName());
	}
}