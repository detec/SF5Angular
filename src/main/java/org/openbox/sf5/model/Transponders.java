package org.openbox.sf5.model;

import java.io.Serializable;
import java.lang.reflect.Field;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "Transponders")
@XmlRootElement
public class Transponders extends AbstractDbEntity implements Serializable {

	private static final long serialVersionUID = -3945460836260580586L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	public long getId() {

		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Column(name = "frequency", unique = false, nullable = false, precision = 5)
	@Min(value = 2000)
	private long frequency;

	public long getFrequency() {
		return frequency;
	}

	public void setFrequency(long Frequency) {
		this.frequency = Frequency;
	}

	@Override
	public String toString() {
		return String.valueOf(frequency);
	}

	@Enumerated(EnumType.ORDINAL)
	@Column(nullable = false)
	@NotNull
	private Polarization polarization;

	public Polarization getPolarization() {
		return polarization;
	}

	public void setPolarization(Polarization Polarization) {
		this.polarization = Polarization;
	}

	@Enumerated(EnumType.ORDINAL)
	@Column(nullable = true)
	@JsonProperty("FEC")
	private TypesOfFEC FEC;

	public TypesOfFEC getFEC() {
		return FEC;
	}

	public void setFEC(TypesOfFEC FEC) {
		this.FEC = FEC;
	}

	@Enumerated(EnumType.ORDINAL)
	@Column(nullable = false)
	@NotNull
	private CarrierFrequency carrier;

	public CarrierFrequency getCarrier() {
		return carrier;
	}

	public void setCarrier(CarrierFrequency Carrier) {
		this.carrier = Carrier;
	}

	@Column(name = "speed", unique = false, nullable = false, precision = 5)
	@Min(value = 1000)
	private long speed;

	public long getSpeed() {
		return speed;
	}

	public void setSpeed(long Speed) {
		this.speed = Speed;
	}

	@Enumerated(EnumType.ORDINAL)
	@Column(nullable = false)
	@NotNull
	private DVBStandards versionOfTheDVB;

	public DVBStandards getVersionOfTheDVB() {
		return versionOfTheDVB;
	}

	public void setVersionOfTheDVB(DVBStandards VersionOfTheDVB) {
		this.versionOfTheDVB = VersionOfTheDVB;
	}

	@Enumerated(EnumType.ORDINAL)
	@Column(nullable = false)
	@NotNull
	private RangesOfDVB rangeOfDVB;

	public RangesOfDVB getRangeOfDVB() {
		return rangeOfDVB;
	}

	public void setRangeOfDVB(RangesOfDVB RangeOfDVB) {
		this.rangeOfDVB = RangeOfDVB;
	}

	@ManyToOne
	@JoinColumn(name = "satellite", unique = false, nullable = false, foreignKey = @ForeignKey(name = "FK_SatelliteTransponders"))
	@NotNull
	private Satellites satellite;

	public Satellites getSatellite() {
		return satellite;
	}

	public void setSatellite(Satellites Satellite) {
		this.satellite = Satellite;
	}

	public Transponders(long Frequency, Polarization Polarization, TypesOfFEC FEC, CarrierFrequency Carrier, long Speed,
			DVBStandards VersionOfTheDVB, RangesOfDVB RangeOfDVB, Satellites Satellite) {

		this.frequency = Frequency;
		this.polarization = Polarization;
		this.FEC = FEC;
		this.carrier = Carrier;
		this.speed = Speed;
		this.versionOfTheDVB = VersionOfTheDVB;
		this.rangeOfDVB = RangeOfDVB;
		this.satellite = Satellite;

	}

	public Transponders() {
	}

	protected void setObjectFieldsFrom(Transponders origObj) throws IllegalAccessException {
		Field fields[];
		Class curClass = origObj.getClass();

		if (!curClass.isAssignableFrom(this.getClass())) {
			throw new IllegalArgumentException("New object must be the same class or a subclass of original");
		}

		// Spin through all fields of the class & all its superclasses
		do {
			fields = curClass.getDeclaredFields();

			for (int i = 0; i < fields.length; i++) {
				if (fields[i].getName().equals("serialVersionUID")) {
					continue;
				}
				fields[i].set(this, fields[i].get(origObj));
			}
			curClass = curClass.getSuperclass();
		} while (curClass != null);
	}

	public Transponders(Transponders origObj) {
		try {
			setObjectFieldsFrom(origObj);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}