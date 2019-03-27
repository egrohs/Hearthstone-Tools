package hcs;

public class App {

    public static void main(String[] args) {
	CardBuilder cb = new CardBuilder();
	cb.leCards();
	//new SinergyFromText();
	new TagBuilder();
	//cb.generateCardSynergies(cb.getCard("Leeroy Jenkins"));
    }
}
