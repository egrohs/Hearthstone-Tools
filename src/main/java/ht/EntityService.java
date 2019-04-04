package ht;

import ht.model.Nodo;

public class EntityService extends GenericService<Nodo> {
	@Override
	Class<Nodo> getEntityType() {
		return Nodo.class;
	}
}
