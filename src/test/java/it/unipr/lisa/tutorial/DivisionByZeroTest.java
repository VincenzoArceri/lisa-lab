package it.unipr.lisa.tutorial;

import org.junit.Test;

import it.unive.lisa.DefaultConfiguration;
import it.unive.lisa.LiSA;
import it.unive.lisa.conf.LiSAConfiguration;
import it.unive.lisa.conf.LiSAConfiguration.GraphType;
import it.unive.lisa.imp.IMPFrontend;
import it.unive.lisa.imp.ParsingException;
import it.unive.lisa.program.Program;

public class DivisionByZeroTest {

	@Test
	public void divisionByZeroTest() throws ParsingException {
		Program program = IMPFrontend.processFile("inputs/div.imp");
		LiSAConfiguration conf = new DefaultConfiguration();
		conf.workdir = "output/div";
		conf.serializeInputs = true;
		conf.analysisGraphs = GraphType.HTML;
		conf.jsonOutput = true;
		conf.syntacticChecks.add(new DivisionByZeroChecker());
		
		
		//		conf.abstractState = DefaultConfiguration.simpleState(
		//				DefaultConfiguration.defaultHeapDomain(), 
		//				new DefiniteDataflowDomain<>(new MyAvailableExpressions()), 
		//				DefaultConfiguration.defaultTypeDomain());

		LiSA lisa = new LiSA(conf);
		lisa.run(program);

	}
}
