package hstools;

import hstools.model.Node;

public class EntityService extends GenericService<Node> {
	@Override
	Class<Node> getEntityType() {
		return Node.class;
	}
}
