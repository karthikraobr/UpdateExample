package de.visuflow.ex2.updated;

import soot.Local;
import soot.SootClass;
import soot.SootField;
import soot.Unit;

public class FlowAbstractionUpdated {

	private final Unit source;
	private final Local local;
	private final SootField field;

	public FlowAbstractionUpdated(Unit source, Local local) {
		this(source, local, null);
	}

	public FlowAbstractionUpdated(Unit source, SootField field) {
		this(source, null, field);
	}
	
	public FlowAbstractionUpdated(Unit source, SootClass clazz) {
	    this(source, null, null);
	}

	public FlowAbstractionUpdated(Unit source, Local local, SootField field) {
		this.source = source;
		this.local = local;
		this.field = field;
	}

	public Unit getSource() {
		return this.source;
	}

	public Local getLocal() {
		return this.local;
	}

	public SootField getField() {
		return this.field;
	}
	
	public SootClass getBaseClass() {
	    if(this.field != null) {
	        return this.field.getDeclaringClass();
	    } else {
	        throw new RuntimeException("Field is not set");
	    }
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((local == null) ? 0 : local.hashCode());
		result = prime * result + ((field == null) ? 0 : field.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || !(obj instanceof FlowAbstractionUpdated))
			return false;
		FlowAbstractionUpdated other = (FlowAbstractionUpdated) obj;
		if (local == null) {
			if (other.local != null)
				return false;
		} else if (!local.equals(other.local))
			return false;
		if (field == null) {
			if (other.field != null)
				return false;
		} else if (!field.equals(other.field))
			return false;
		return true;
	}

	@Override
	public String toString() {
		if (local != null)
			return "LOCAL " + local;
		if (field != null)
			return "FIELD " + field;
		return "";
	}

	public FlowAbstractionUpdated deriveWithNewSource(Unit newSource) {
		return new FlowAbstractionUpdated(newSource, local, field);
	}

}
