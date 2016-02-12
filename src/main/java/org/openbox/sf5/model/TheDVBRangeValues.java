package org.openbox.sf5.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TheDVBRangeValues")
public class TheDVBRangeValues extends AbstractDbEntity implements Serializable {

	private static final long serialVersionUID = 1635144675404567877L;

	@Id
	RangesOfDVB rangeOfDVB;

	public RangesOfDVB getRangeOfDVB() {
		return this.rangeOfDVB;
	}

	public void setRangeOfDVB(RangesOfDVB RangeOfDVB) {
		this.rangeOfDVB = RangeOfDVB;
	}

	@Column(name = "lowerThreshold", unique = false, nullable = true, precision = 5)
	private long lowerThreshold;

	public long getLowerThreshold() {
		return this.lowerThreshold;
	}

	public void setLowerThreshold(long LowerThreshold) {
		this.lowerThreshold = LowerThreshold;
	}

	@Column(name = "upperThreshold", unique = false, nullable = true, precision = 5)
	private long upperThreshold;

	public long getUpperThreshold() {
		return this.upperThreshold;
	}

	public void setUpperThreshold(long UpperThreshold) {
		this.upperThreshold = UpperThreshold;
	}

	public TheDVBRangeValues(RangesOfDVB RangeOfDVB, long LowerThreshold, long UpperThreshold) {

		this.rangeOfDVB = RangeOfDVB;
		this.lowerThreshold = LowerThreshold;
		this.upperThreshold = UpperThreshold;

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (lowerThreshold ^ (lowerThreshold >>> 32));
		result = prime * result + ((rangeOfDVB == null) ? 0 : rangeOfDVB.hashCode());
		result = prime * result + (int) (upperThreshold ^ (upperThreshold >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		TheDVBRangeValues other = (TheDVBRangeValues) obj;
		if (lowerThreshold != other.lowerThreshold) {
			return false;
		}
		if (rangeOfDVB != other.rangeOfDVB) {
			return false;
		}
		if (upperThreshold != other.upperThreshold) {
			return false;
		}
		return true;
	}

	public TheDVBRangeValues() {
	}
}