package hstools.domain.components;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import hstools.domain.entities.Deck;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

/**
 * Neural Network deck archtype detector
 * 
 * @author EGrohs
 *
 */
@Component
public class ArtificialNeuralNetwork {
	@Autowired
	private DeckComponent deckComp;
	private ClassLoader clsLoader = ArtificialNeuralNetwork.class.getClassLoader();
	// Instance of NN
	private MultilayerPerceptron mlp = new MultilayerPerceptron();
//TODO usar enum?
	ArrayList<String> classVal = new ArrayList<String>();

	@PostConstruct
	public void init() {
		trainFromFile("deckArchtypeTrain.arff");
	}

	private void trainFromFile(String filepath) {
		try {
			// Reading training arff or csv file
			FileReader trainreader = new FileReader(new File(clsLoader.getResource(filepath).getFile()));
			Instances train = new Instances(trainreader);
			train.setClassIndex(train.numAttributes() - 1);
			// Setting Parameters
			mlp.setLearningRate(0.1);
			mlp.setMomentum(0.2);
			mlp.setTrainingTime(20000);
			mlp.setHiddenLayers("20");
			mlp.buildClassifier(train);

			// For evaluation of training data,
			Evaluation eval = new Evaluation(train);
			eval.evaluateModel(mlp, train);
			System.out.println("ERROR: " + eval.errorRate()); // Printing Training Mean root squared Error
			System.out.println(eval.toSummaryString()); // Summary of Training
			// To apply K-Fold validation
			int kfolds = 3;
			eval.crossValidateModel(mlp, train, kfolds, new Random(1));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private Instances classify(Instances predicteddata) {
		predicteddata.setClassIndex(predicteddata.numAttributes() - 1);
		// Instances predicteddata = new Instances(datapredict);
		// Predict Part
		for (int i = 0; i < predicteddata.numInstances(); i++) {
			try {
				double clsLabel = mlp.classifyInstance(predicteddata.instance(i));
				predicteddata.instance(i).setClassValue(clsLabel);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return predicteddata;
	}

	private void writeOutput(Instances predicteddata) {
		// Storing again in arff
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter("saida.txt"));
			writer.write(predicteddata.toString());
			writer.newLine();
			writer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (writer != null)
				try {
					writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}

	public void classifyDeck(Deck deck) {
		deckComp.calcStats(deck);
		Instances archInst = defineArchtypesInstancesAttributes();
		double[] instanceValue1 = new double[archInst.numAttributes()];
		// qnt cards, type == "minion" && cost < 3
		instanceValue1[0] = deck.getLow_cost_minions();
		// (ones + twos * 2 + threes * 3 + fours * 4 + fives * 5 + sixes * 7) / 30.0;
		instanceValue1[1] = deck.getAvg_mana();
		// qnt cards com tags "DRAW" or "GENERATE"
		instanceValue1[2] = deck.getCard_adv();
		// qnt cards com tags "TAUNT", "LIFESTEAL", "ARMOR" or "HEALTH_RESTORE"
		instanceValue1[3] = deck.getSurv();
		// instanceValue1[4] = 0; // archtype a ser descoberto

		// 9,3.066666666666667,1,2,AGGRO
		// 6,3.933333,5,8,COMBO
		archInst.add(new DenseInstance(1.0, instanceValue1));

		classify(archInst);
		deck.setArchtype(classVal.get((int) archInst.get(0).value(4)));
		System.out.println(archInst);
	}

	public void generateTrainFile() {
		System.out.println("@RELATION archtypes");
		System.out.println("@ATTRIBUTE low_cost_minions NUMERIC");
		System.out.println("@ATTRIBUTE avg_mana NUMERIC");
		System.out.println("@ATTRIBUTE card_adv NUMERIC");
		System.out.println("@ATTRIBUTE surv NUMERIC");
		System.out.println("@ATTRIBUTE archtype {AGGRO,MIDRANGE,CONTROL,COMBO,FACE,TRIBAL,TEMPO,MILL}");
		System.out.println("@DATA");
		for (Deck deck : deckComp.getDecks()) {
			deckComp.calcStats(deck);
			System.out.print(deck.getLow_cost_minions() + ",");
			System.out.print(deck.getAvg_mana() + ",");
			System.out.print(deck.getCard_adv() + ",");
			System.out.print(deck.getSurv() + ",");
			System.out.println(deck.getArchtype());
		}
	}

	private Instances defineArchtypesInstancesAttributes() {
		ArrayList<Attribute> atts = new ArrayList<Attribute>(5);
		classVal.add("AGGRO");
		classVal.add("MIDRANGE");
		classVal.add("CONTROL");
		classVal.add("COMBO");
		classVal.add("FACE");
		classVal.add("TRIBAL");
		classVal.add("TEMPO");
		classVal.add("MILL");

		atts.add(new Attribute("low_cost_minions"));
		atts.add(new Attribute("avg_mana"));
		atts.add(new Attribute("card_adv"));
		atts.add(new Attribute("surv"));
		atts.add(new Attribute("archtype", classVal));

		return new Instances("archtypes", atts, 0);
	}
}