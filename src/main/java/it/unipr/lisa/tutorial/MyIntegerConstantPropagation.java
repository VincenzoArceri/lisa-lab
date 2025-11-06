package it.unipr.lisa.tutorial;

import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.nonrelational.value.BaseNonRelationalValueDomain;
import it.unive.lisa.util.representation.StructuredRepresentation;

public class MyIntegerConstantPropagation implements BaseNonRelationalValueDomain<MyIntegerConstantPropagation> {

	@Override
	public MyIntegerConstantPropagation lubAux(MyIntegerConstantPropagation other) throws SemanticException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean lessOrEqualAux(MyIntegerConstantPropagation other) throws SemanticException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public MyIntegerConstantPropagation top() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MyIntegerConstantPropagation bottom() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StructuredRepresentation representation() {
		// TODO Auto-generated method stub
		return null;
	}
	

}
