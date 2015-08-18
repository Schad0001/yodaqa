package cz.brmlab.yodaqa;

import cz.brmlab.yodaqa.pipeline.solrfull.BingFullPrimarySearch;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;

import cz.brmlab.yodaqa.flow.MultiCASPipeline;
import cz.brmlab.yodaqa.flow.asb.ParallelEngineFactory;
import cz.brmlab.yodaqa.io.collection.TSVQuestionReader;
import cz.brmlab.yodaqa.io.collection.GoldStandardAnswerPrinter;
import org.apache.uima.collection.CollectionReaderDescription;
import cz.brmlab.yodaqa.pipeline.YodaQA;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.factory.CollectionReaderFactory.createReaderDescription;


/* FIXME: Massive code duplication of YodaQA_Interactive and YodaQA_GS.
 * Let's abstract out the processing pipeline later. */

public class YodaQA_GS {
	public static void main(String[] args) throws Exception {
		BingFullPrimarySearch.DISABLED = true;
		if (args.length > 0) {
			for (int i = 0; i < args.length; i++) {
				if (args[i].equals("use-bing")) BingFullPrimarySearch.DISABLED = false;
			}
		}
		if (args.length != 2 && args.length != 3) {
			System.err.println("Usage: YodaQA_GS INPUT.TSV OUTPUT.TSV");
			System.err.println("Measures YodaQA performance on some Gold Standard questions.");
			System.exit(1);
		}

		CollectionReaderDescription reader = createReaderDescription(
				TSVQuestionReader.class,
				TSVQuestionReader.PARAM_TSVFILE, args[0],
				TSVQuestionReader.PARAM_LANGUAGE, "en");

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
