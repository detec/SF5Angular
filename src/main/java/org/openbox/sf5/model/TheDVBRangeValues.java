package org.openbox.sf5.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * DVB ranges
 *
 * @author Andrii Duplyk
 *
 */
@Entity
@Table(name = "TheDVBRangeValues")
public class TheDVBRangeValues extends AbstractDbEntity implements Serializable {

	private static final long serialVersionUID = 1635144675404567877L;

	@Id
    @Enumerated(EnumType.ORDINAL)
	private RangesOfDVB rangeOfDVB;

	@Column(name = "lowerThreshold", unique = false, nullable = true, precision = 5)
	private long lowerThreshold;

	@Column(name = "upperThreshold", unique = false, nullable = true, precision = 5)
	private long upperThreshold;

	/**
	 *
	 * @param RangeOfDVB
	 * @param LowerThreshold
	 * @param UpperThreshold
	 */
	public TheDVBRangeValues(RangesOfDVB rangeOfDVB, long lowerThreshold, long upperThreshold) {

		this.rangeOfDVB = rangeOfDVB;
		this.lowerThreshold = lowerThreshold;
		this.upperThreshold = upperThreshold;

	}

	/**
	 *
	 */
	public TheDVBRangeValues() {
	}

    public RangesOfDVB getRangeOfDVB() {
		return this.rangeOfDVB;
	}

	public void setRangeOfDVB(RangesOfDVB rangeOfDVB) {
		this.rangeOfDVB = rangeOfDVB;
	}

	public long getLowerThreshold() {
		return this.lowerThreshold;
	}

	public void setLowerThreshold(long lowerThreshold) {
		this.lowerThreshold = lowerThreshold;
	}

	public long getUpperThreshold() {
		return this.upperThreshold;
	}

	public void setUpperThreshold(long upperThreshold) {
		this.upperThreshold = upperThreshold;
	}

}