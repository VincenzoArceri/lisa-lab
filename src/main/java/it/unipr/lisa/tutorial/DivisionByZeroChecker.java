package it.unipr.lisa.tutorial;

import it.unive.lisa.checks.syntactic.CheckTool;
import it.unive.lisa.checks.syntactic.SyntacticCheck;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.literal.Int32Literal;
import it.unive.lisa.program.cfg.statement.numeric.Division;

public class DivisionByZeroChecker implements SyntacticCheck {

	@Override
	public boolean visit(CheckTool tool, CFG graph, Statement node) {
		if (node instanceof Division div) 
			if (div.getRight() instanceof Int32Literal i) {
				if (i.getValue() == 0)
					tool.warnOn(node, "[DEFINITE] Division by zero!");
			} else
				tool.warnOn(node, "[POSSIBLE] Division by zero!");


		return true;
	}
}
