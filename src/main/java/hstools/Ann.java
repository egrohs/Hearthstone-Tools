package hstools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Random;

import weka.classifiers.Evaluation;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Instances;

public class Ann {
	static ClassLoader cl = Ann.class.getClassLoader();
	static Instances train = null;
	// Instance of NN
	static MultilayerPerceptron mlp = new MultilayerPerceptron();

	static FileReader trainreader = null;

	public static void simpleWekaTrain(String filepath) {
		try {
			// Reading training arff or csv file
			trainreader = new FileReader(new File(cl.getResource(filepath).getFile()));
			train = new Instances(trainreader);
			train.setClassIndex(train.numAttributes() - 1);
			// Setting Parameters
			mlp.setLearningRate(0.1);
			mlp.setMomentum(0.2);
			mlp.setTrainingTime(20000);
			mlp.setHiddenLayers("20");
			mlp.buildClassifier(train);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void main(String[] args) {
		simpleWekaTrain("deck_archtypes_train3.arff");
		// For evaluation of training data,

		try {
			Evaluation eval = new Evaluation(train);
			eval.evaluateModel(mlp, train);
			System.out.println(eval.errorRate()); // Printing Training Mean root squared Error
			System.out.println(eval.toSummaryString()); // Summary of Training
			// To apply K-Fold validation
			int kfolds = 3;
			eval.crossValidateModel(mlp, train, kfolds, new Random(1));
			// Evaluating/Predicting unlabelled data
			Instances datapredict = new Instances(
					new BufferedReader(new FileReader(new File(cl.getResource("deck_archtypes_test3.arff").getFile()))));
			datapredict.setClassIndex(datapredict.numAttributes() - 1);
			Instances predicteddata = new Instances(datapredict);
			// Predict Part
			for (int i = 0; i < datapredict.numInstances(); i++) {
				double clsLabel = mlp.classifyInstance(datapredict.instance(i));
				predicteddata.instance(i).setClassValue(clsLabel);
			}
			// Storing again in arff
			BufferedWriter writer = new BufferedWriter(new FileWriter("saida.txt"));
			writer.write(predicteddata.toString());
			writer.newLine();
			writer.flush();
			writer.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
