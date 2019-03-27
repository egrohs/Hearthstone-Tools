package hcs;

public class App {

    public static void main(String[] args) {
	Universo.leCards();
	//new SinergyFromText();
	new Sinergias();
	Sinergias.generateCardSynergies(Universo.getCard("Leeroy Jenkins"));
    }
}
