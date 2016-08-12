package org.openbox.sf5.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ValueOfTheCarrierFrequency")
public class ValueOfTheCarrierFrequency extends AbstractDbEntity implements Serializable {

	@Override
	public int hashCode() {
		return Objects.hash(this.polarization, this.typeOfCarrierFrequency);
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
		ValueOfTheCarrierFrequency other = (ValueOfTheCarrierFrequency) obj;

		return Objects.equals(typeOfCarrierFrequency, other.typeOfCarrierFrequency)
				&& Objects.equals(polarization, other.polarization);

	}

	private static final long serialVersionUID = -6095308495476745108L;

	@Id
	private CarrierFrequency typeOfCarrierFrequency;

	public CarrierFrequency getTypeOfCarrierFrequency() {
		return typeOfCarrierFrequency;
	}

	public void setTypeOfCarrierFrequency(CarrierFrequency TypeOfCarrierFrequency) {
		this.typeOfCarrierFrequency = TypeOfCarrierFrequency;
	}

	@Id
	private KindsOfPolarization polarization;

	@Column(name = "lowerThreshold", unique = false, nullable = false, precision = 5)
	private long lowerThreshold;

	public long getLowerThreshold() {
		return lowerThreshold;
	}

	public void setLowerThreshold(long LowerThreshold) {
		this.lowerThreshold = LowerThreshold;
	}

	@Column(name = "upperThreshold", unique = false, nullable = false, precision = 5)
	private long upperThreshold;

	public long getUpperThreshold() {
		return upperThreshold;
	}

	public void setUpperThreshold(long UpperThreshold) {
		this.upperThreshold = UpperThreshold;
	}

	public ValueOfTheCarrierFrequency(CarrierFrequency typeOfCarrierFrequency, KindsOfPolarization polarization,
			long lowerThreshold, long upperThreshold) {

		this.typeOfCarrierFrequency = typeOfCarrierFrequency;
		this.polarization = polarization;
		this.lowerThreshold = lowerThreshold;
		this.upperThreshold = upperThreshold;

	}

	public ValueOfTheCarrierFrequency() {
	}
}