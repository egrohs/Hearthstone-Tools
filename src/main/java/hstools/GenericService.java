package hstools;

import org.neo4j.ogm.session.Session;

import hstools.model.Node;

interface Service<T> {
	public Iterable<T> findAll();

	public T find(Long id);

	public void delete(Long id);

	public T createOrUpdate(T object);
}

abstract class GenericService<T extends Node> implements Service<T> {
	private static final int DEPTH_LIST = 0;
	private static final int DEPTH_ENTITY = 1;
	protected Session session = Neo4jSessionFactory.getInstance().getNeo4jSession();

	@Override
	public Iterable<T> findAll() {
		return session.loadAll(getEntityType(), DEPTH_LIST);
	}

	@Override
	public T find(Long id) {
		return session.load(getEntityType(), id, DEPTH_ENTITY);
	}

	@Override
	public void delete(Long id) {
		session.delete(session.load(getEntityType(), id));
	}

	@Override
	public T createOrUpdate(T entity) {
		session.save(entity, 2);
		return find(entity.getId());
	}

	abstract Class<T> getEntityType();
}
