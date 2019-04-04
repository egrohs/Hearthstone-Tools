package ht;

import ht.model.Entity;

public class EntityService extends GenericService<Entity> {
	@Override
	Class<Entity> getEntityType() {
		return Entity.class;
	}
}
