package org.openbox.sf5.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ValueOfTheCarrierFrequency")
public class ValueOfTheCarrierFrequency extends AbstractDbEntity implements Serializable {

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((polarization == null) ? 0 : polarization.hashCode());
		result = prime * result + ((typeOfCarrierFrequency == null) ? 0 : typeOfCarrierFrequency.hashCode());
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
		ValueOfTheCarrierFrequency other = (ValueOfTheCarrierFrequency) obj;
		if (polarization != other.polarization) {
			return false;
		}
		if (typeOfCarrierFrequency != other.typeOfCarrierFrequency) {
			return false;
		}
		return true;
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

	public ValueOfTheCarrierFrequency(CarrierFrequency TypeOfCarrierFrequency, KindsOfPolarization Polarization,
			long LowerThreshold, long UpperThreshold) {

		this.typeOfCarrierFrequency = TypeOfCarrierFrequency;
		this.polarization = Polarization;
		this.lowerThreshold = LowerThreshold;
		this.upperThreshold = UpperThreshold;

	}

	public ValueOfTheCarrierFrequency() {
	}
}