package hcs.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import hcs.Universo;
import hcs.model.Carta.CLASS;

public class Deck extends Entidade {
	Carta.CLASS classe = CLASS.NEUTRAL;
	Map<Carta, Integer> cartas = new HashMap<Carta, Integer>();

	public Deck(String nome, Map<Carta, Integer> cartas) {
		this.name = nome;
		this.cartas = cartas;
		this.classe = Universo.whichClass(new ArrayList<Carta>(cartas.keySet()));
	}

	public Integer getQnt(String nome) {
		// System.out.println("getQnt "+nome);
		Carta c = Universo.getCard(nome);
		if (cartas.containsKey(c)) {
			return cartas.get(c);
		}
		return null;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(name + "\r\n");
		sb.append(classe + "\r\n");
		for (Carta c : cartas.keySet()) {
			sb.append(c.name + "\t" + cartas.get(c) + "\r\n");
		}
		return sb.toString();
	}
}
