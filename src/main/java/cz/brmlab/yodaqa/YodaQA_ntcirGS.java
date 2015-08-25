package cz.brmlab.yodaqa;

import cz.brmlab.yodaqa.flow.MultiCASPipeline;
import cz.brmlab.yodaqa.flow.asb.ParallelEngineFactory;
import cz.brmlab.yodaqa.io.collection.GoldStandardAnswerPrinter;
import cz.brmlab.yodaqa.io.collection.JSONQuestionReader;
import cz.brmlab.yodaqa.io.ntcir.NTCIRQuestionReader;
import cz.brmlab.yodaqa.pipeline.YodaQA;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.factory.CollectionReaderFactory.createReaderDescription;


/* FIXME: Massive code duplication of YodaQA_Interactive and YodaQA_GS.
 * Let's abstract out the processing pipeline later. */

public class YodaQA_ntcirGS {
	public static void main(String[] args) throws Exception {
		if (args.length != 2) {
//			System.err.println("Usage: YodaQA_GS INPUT.json OUTPUT.TSV");
			System.err.println("Measures YodaQA performance on ntcir-12 questions.");
			System.exit(1);
		}

		CollectionReaderDescription reader = createReaderDescription(
				NTCIRQuestionReader.class,
				NTCIRQuestionReader.PARAM_JSONFILE, args[0],
				NTCIRQuestionReader.PARAM_LANGUAGE, "en");

		AnalysisEngineDescription pipeline = YodaQA.createEngineDescription();

		AnalysisEngineDescription printer = createEngineDescription(
				GoldStandardAnswerPrinter.class,
				GoldStandardAnswerPrinter.PARAM_TSVFILE, args[1],
				ParallelEngineFactory.PARAM_NO_MULTIPROCESSING, 1);

		ParallelEngineFactory.registerFactory(); // comment out for a linear single-thread flow
		/* XXX: Later, we will want to create an actual flow
		 * to support scaleout. */
		MultiCASPipeline.runPipeline(reader,
				pipeline,
				printer);
	}
}
