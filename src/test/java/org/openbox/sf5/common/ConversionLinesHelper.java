package org.openbox.sf5.common;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import org.openbox.sf5.model.Satellites;
import org.openbox.sf5.model.Settings;
import org.openbox.sf5.model.SettingsConversion;
import org.openbox.sf5.model.Transponders;

/**
 * Class to hold common methods to sort list of transponders and generate
 * tabular part of 32 lines for settings conversion list.
 *
 * @author Andrii Duplyk
 *
 */
public class ConversionLinesHelper {

	/**
	 * Fills raw, unsorted transponders to setting.
	 *
	 * @param newTransList
	 * @param setting
	 */
	public static void fillTranspondersToSetting(List<Transponders> newTransList, Settings setting) {

		// sorting transponders by satellite and speed.
		newTransList.sort(Comparator.comparing(chain(Transponders::getSatellite, Satellites::getId))
				.thenComparing(Transponders::getSpeed));

		newTransList.stream().limit(32).forEachOrdered(t -> fillTpLine(newTransList, setting, t));

	}

	static <T, U, R> Function<T, R> chain(Function<? super T, ? extends U> f1, Function<? super U, ? extends R> f2) {
		return t -> f2.apply(f1.apply(t));
	}

	private static void fillTpLine(List<Transponders> newTransList, Settings setting, Transponders t) {
		int currentIndex = newTransList.indexOf(t);
		int currentNumber = currentIndex + 1;
		int satIndex = (int) Math.ceil((double) currentNumber / 4);
		int tpIndex = (currentNumber % 4 == 0) ? 4 : currentNumber % 4; // %
																		// is
																		// remainder

		SettingsConversion sc = new SettingsConversion(setting, t, satIndex, tpIndex, Long.toString(t.getFrequency()),
				0);
		sc.setLineNumber(currentNumber);
		setting.getConversion().add(sc);
	}

}
