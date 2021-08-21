package hstools.domain.dtos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import hstools.domain.entities.Card;
import hstools.domain.entities.SynergyEdge;
import lombok.Data;

@Data
public class GraphJson implements Serializable {
	private static final long serialVersionUID = 8572298599508101707L;

	public GraphJson(List<SynergyEdge<Card, Card>> syns) {
		Map<String, GNode> ns = new HashMap<String, GNode>();
		for (SynergyEdge<Card, Card> s : syns) {
			String so = s.getSource().getName();
			String tg = s.getTarget().getName();
			GNode n = ns.get(so);
			if (n == null) {
				n = new GNode(so);
				ns.put(so, n);
			} else {
				n.setGroup(n.getGroup() + 1);
			}
			nodes.add(n);
			n = ns.get(tg);
			if (n == null) {
				n = new GNode(tg);
				ns.put(tg, n);
			} else {
				n.setGroup(n.getGroup() + 1);
			}
			nodes.add(n);
			links.add(new GLink(s));
		}
	}

	private Set<GNode> nodes = new HashSet<>();
	private List<GLink> links = new ArrayList<>();
}

@Data
class GNode implements Serializable {
	private static final long serialVersionUID = 845349944267571841L;

	public GNode(String name) {
		this.id = name;
		this.group = 1;
	}

	String id;
	// "groupLabel": "Damage",
	int group;
}

@Data
class GLink implements Serializable {
	private static final long serialVersionUID = -8050748768561328048L;

	public GLink(SynergyEdge s) {
		this.source = s.getSource().getName();
		this.target = s.getTarget().getName();
		this.label = s.getLabel();
		this.value = 1;
	}

	String source;
	String target;
	String label;
	int value;
}