package hstools.domain.components;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import hstools.Constants.Archtype;
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
	List<String> classVal = new ArrayList<String>();
	private boolean trained = false;

//	@PostConstruct
//	public void init() {
//		trainFromFile("deckArchtypeTrain.arff");
//	}

	private void trainFromFile(String filepath) {
		if (!trained) {
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
				trained = true;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
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
			} catch (IllegalStateException e) {
				// No input instance format defined
				System.err.println("Have you trained the ANN? FLAG trained = false");
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// Src and Dest differ in # of attributes
				System.err.println("Train file has differente atribs. Have you refreshed th project?");
				e.printStackTrace();
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
		trainFromFile("deckArchtypeTrain.arff");
		deckComp.calcStats(deck);
		Instances archInst = defineArchtypesInstancesAttributes();
		double[] instanceValue1 = new double[archInst.numAttributes()];
		// qnt cards, type == "minion" && cost < 3
		instanceValue1[0] = deck.getStats().getLow_cost_minions();
		instanceValue1[1] = deck.getStats().getHigh_cost();
		instanceValue1[2] = deck.getStats().getSoft_removals() + deck.getStats().getHard_removals();
		instanceValue1[3] = deck.getStats().getBoard_control();
		// qnt cards com tags "DRAW" or "GENERATE"
		instanceValue1[4] = deck.getStats().getCard_adv();
		// qnt cards com tags "TAUNT", "LIFESTEAL", "ARMOR" or "HEALTH_RESTORE"
		instanceValue1[5] = deck.getStats().getSurvs();
		instanceValue1[6] = 0; // archtype a ser descoberto

		archInst.add(new DenseInstance(1.0, instanceValue1));

		classify(archInst);
		deck.getStats().setArchtype(Archtype.values()[(int) archInst.get(0).value(6)]);
		System.out.println(archInst);
	}

	public void generateTrainFile() {
		System.out.println("@RELATION archtypes");
		System.out.println("@ATTRIBUTE low_cost_minions NUMERIC");
		System.out.println("@ATTRIBUTE high_cost NUMERIC");
		System.out.println("@ATTRIBUTE removals NUMERIC");
		System.out.println("@ATTRIBUTE board_control NUMERIC");
		System.out.println("@ATTRIBUTE card_adv NUMERIC");
		System.out.println("@ATTRIBUTE surv NUMERIC");
		System.out.println("@ATTRIBUTE archtype {AGGRO,MIDRANGE,CONTROL,COMBO,FACE,TRIBAL,TEMPO,MILL}");
		System.out.println("@DATA");
		for (Deck deck : deckComp.getDecks()) {
			deckComp.calcStats(deck);
			System.out.print(deck.getStats().getLow_cost_minions() + ",");
			// System.out.print(deck.getStats().getAvg_mana() + ",");
			System.out.print(deck.getStats().getHigh_cost() + ",");
			System.out.print(deck.getStats().getSoft_removals() + deck.getStats().getHard_removals() + ",");
			System.out.print(deck.getStats().getBoard_control() + ",");

			System.out.print(deck.getStats().getCard_adv() + ",");
			System.out.print(deck.getStats().getSurvs() + ",");
			System.out.println(deck.getStats().getArchtype());
		}
	}

	private Instances defineArchtypesInstancesAttributes() {
		ArrayList<Attribute> atts = new ArrayList<Attribute>();
		classVal = Stream.of(Archtype.values()).map(Archtype::name).collect(Collectors.toList());
		atts.add(new Attribute("low_cost_minions"));
		atts.add(new Attribute("high_cost"));
		atts.add(new Attribute("removals"));
		atts.add(new Attribute("board_control"));
		atts.add(new Attribute("card_adv"));
		atts.add(new Attribute("surv"));
		atts.add(new Attribute("archtype", classVal));

		return new Instances("archtypes", atts, 0);
	}
}