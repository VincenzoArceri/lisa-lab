package it.unipr.lisa.tutorial;

import org.junit.Test;

import it.unive.lisa.DefaultConfiguration;
import it.unive.lisa.LiSA;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.conf.LiSAConfiguration;
import it.unive.lisa.conf.LiSAConfiguration.GraphType;
import it.unive.lisa.imp.IMPFrontend;
import it.unive.lisa.imp.ParsingException;
import it.unive.lisa.program.Program;

public class MySignTest {

	@Test
	public void testAvailableTest() throws ParsingException {
		Program program = IMPFrontend.processFile("inputs/signs.imp");
		
		LiSAConfiguration conf = new DefaultConfiguration();
		conf.workdir = "output/sign";
		conf.analysisGraphs = GraphType.HTML;
		conf.jsonOutput = true;
		
		conf.abstractState = DefaultConfiguration.simpleState(
				DefaultConfiguration.defaultHeapDomain(), 
				new ValueEnvironment<MySign>(new MySign()), 
				DefaultConfiguration.defaultTypeDomain());
		
		LiSA lisa = new LiSA(conf);
		lisa.run(program);
	}
}
