package me.paul.lads.util;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;



public class Duration implements Serializable {
	
	private static final long serialVersionUID = 1323788364182546968L;
	
	public static final Duration ZERO = new Duration(0, Unit.MILLISECOND);

	public static enum Unit {
		MILLISECOND(1, "ms"), 
		TICK(50, "t"), 
		SECOND(1000, "s"), 
		MINUTE(60000, "m"), 
		HOUR(3600000, "h"), 
		DAY(86400000, "d"), 
		WEEK(604800000, "w"),
		YEAR(31536000000L, "y");
		
		final long milliseconds;
		final String symbol;
		final Pattern pattern;

		Unit(long milliseconds, String symbol) {
			this.milliseconds = milliseconds;
			this.symbol = symbol;
			this.pattern = Pattern.compile("(\\d+)" + symbol + "(?:\\s|$|\\d)");
		}

		public long getMilliseconds() {
			return milliseconds;
		}

		public static Unit getUnit(String id) {
			return Arrays.stream(values()).filter(u -> u != MILLISECOND && u.name().substring(0, 1).equalsIgnoreCase(id)).findFirst().orElseThrow(() -> {
				return new NumberFormatException();
			});
		}
	}

	private final long milliseconds;
	
	public Duration(long value, Unit unit) {
		milliseconds = (value * unit.getMilliseconds());
	}

	public Duration(double value, Unit unit) {
		milliseconds = (long)(value * unit.getMilliseconds());
	}

	public Duration(Date startTime, Date endTime) {
		milliseconds = endTime.getTime() - startTime.getTime();
	}

	public Date getOffsetDate(Date startDate) {
		return startDate == null ? null : new Date(startDate.getTime() + toMilliseconds());
	}

	public long toMilliseconds() {
		return milliseconds;
	}
	
	public long ms() {
		return toMilliseconds();
	}

	public int toTicks() {
		return (int) getValue(Unit.TICK);
	}
	
	public int ticks() {
		return toTicks();
	}

	public long toSeconds() {
		return getValue(Unit.SECOND);
	}
	
	public long seconds() {
		return toSeconds();
	}

	public long toMinutes() {
		return getValue(Unit.MINUTE);
	}
	
	public long mins() {
		return toMinutes();
	}

	public long toHours() {
		return getValue(Unit.HOUR);
	}
	
	public long hrs() {
		return toHours();
	}

	public long toDays() {
		return getValue(Unit.DAY);
	}
	
	public long days() {
		return toDays();
	}

	public long toWeeks() {
		return getValue(Unit.WEEK);
	}
	
	public long weeks() {
		return toWeeks();
	}
	
	public long years() {
		return getValue(Unit.YEAR);
	}

	public long getValue(Unit unit) {
		return toMilliseconds() / unit.getMilliseconds();
	}
	
	/**
	 * Gets the value of this Duration, casting the longs to doubles to get a decimal.
	 * @param unit
	 * @return
	 */
	public double getDecimalValue(Unit unit) {
		return toMilliseconds() / (double)unit.getMilliseconds();
	}
	
	/**
	 * @deprecated Use {@link #valueOf(String)}
	 */
	public static long totalStringToMinutes(String string) {
		long total = 0L;
		boolean contains = false;
		char[] chars = new char[] {
									'w', 'd', 'h', 'm', 's'
		};
		for (char character : chars) {
			int charIndex = string.indexOf(character);
			if (charIndex < 0)
				continue;
			total += stringToMinutes(string.substring(0, charIndex + 1));
			string = string.replaceAll(string.substring(0, charIndex + 1), "");
			contains = true;
		}
		if (!contains)
			try {
				total += Long.parseLong(string);
			} catch (NumberFormatException e) {
				e.printStackTrace();
				throw e;
			}
		return total;
	}

	public static long stringToMinutes(String string) {
		return new Duration(Long.parseLong(string.substring(0, string.length() - 1)), Unit.getUnit(string.substring(string.length() - 1, string.length()))).getValue(Unit.MINUTE);
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof Duration && toMilliseconds() == ((Duration) o).toMilliseconds();
	}

	@Override
	public int hashCode() {
		return new Long(toMilliseconds()).hashCode();
	}

	@Override
	public String toString() {
		return formatText();
	}

	public String format() {
		// Fix formatting for negative durations
		boolean negative = milliseconds < 0;
		Duration d = (negative) ? Duration.milliseconds(-milliseconds) : this;
		
		long hours = d.toHours();
		long minutes = d.toMinutes() % 60;
		long seconds = d.toSeconds() % 60;
		
		String str = (negative) ? "-" : "";
		if (hours > 0)
			return String.format("%s%s:%s%s:%s%s", str, hours, minutes / 10, minutes % 10, seconds / 10, seconds % 10);
		return String.format("%s%s:%s%s", str, minutes, seconds / 10, seconds % 10);
	}
	
	public String formatText() {
		return formatText(Unit.SECOND);
	}
	
	public String formatText(Unit smallest) {
		long ms = milliseconds;
		List<String> strings = new LinkedList<>();
		
		for (int i = Unit.values().length - 1; i != -1 && ms > 0; i--) {
			if (smallest != null && i < smallest.ordinal())
				continue;
			
			Unit u = Unit.values()[i];
			long v = ms(ms).getValue(u);
			
			if (v != 0) {
				strings.add(v + u.symbol);
				ms -= new Duration(v, u).ms();
			}
		}
		
		return StringUtils.join(strings, " ");
	}
	
	public boolean isInfinite() {
		return toMilliseconds() >= Long.MAX_VALUE;
	}
	
	/**
	 * Add this duration to the parameter into a new Duration.
	 * @param duration
	 * @return A new duration with the sum of this and <b>duration</b>
	 */
	public Duration add(Duration duration) {
		return Duration.milliseconds(this.toMilliseconds() + duration.toMilliseconds());
	}

	/**
	 * Subtracts the parameter from this into a new Duration.
	 * @param duration
	 * @return A new duration with the difference of {@code this} and {@code duration}.
	 */
	public Duration subtract(Duration duration) {
		return Duration.milliseconds(this.toMilliseconds() - duration.toMilliseconds());
	}
	
	/**
	 * Multiplies this and the parameter into a new Duration.
	 * @param duration
	 * @return A new duration with the product of {@code this} and {@code duration}.
	 */
	public Duration multiply(Duration duration) {
		return Duration.milliseconds(this.toMilliseconds() * duration.toMilliseconds());
	}
	
	/**
	 * Divides this by the parameter into a new Duration.
	 * @param duration
	 * @return A new duration with the dividend of {@code this} and {@code duration}.
	 */
	public Duration divide(Duration duration) {
		return Duration.milliseconds(this.toMilliseconds() / duration.toMilliseconds());
	}

	public static Duration milliseconds(double milliseconds) {
		return new Duration(milliseconds, Unit.MILLISECOND);
	}
	
	public static Duration milliseconds(long milliseconds) {
		return new Duration(milliseconds, Unit.MILLISECOND);
	}
	
	public static Duration ms(double milliseconds) {
		return milliseconds(milliseconds);
	}

	public static Duration ms(long milliseconds) {
		return milliseconds(milliseconds);
	}
	
	public static Duration ticks(double ticks) {
		return new Duration(ticks, Unit.TICK);
	}
	
	public static Duration ticks(long ticks) {
		return new Duration(ticks, Unit.TICK);
	}
	
	public static Duration t(double ticks) {
		return ticks(ticks);
	}
	
	public static Duration t(long ticks) {
		return ticks(ticks);
	}

	public static Duration seconds(double seconds) {
		return new Duration(seconds, Unit.SECOND);
	}

	public static Duration seconds(long seconds) {
		return new Duration(seconds, Unit.SECOND);
	}
	
	public static Duration secs(double seconds) {
		return seconds(seconds);
	}
	
	public static Duration secs(long seconds) {
		return seconds(seconds);
	}

	public static Duration minutes(double minutes) {
		return new Duration(minutes, Unit.MINUTE);
	}
	
	public static Duration minutes(long minutes) {
		return new Duration(minutes, Unit.MINUTE);
	}
	
	public static Duration mins(double minutes) {
		return minutes(minutes);
	}
	
	public static Duration mins(long minutes) {
		return minutes(minutes);
	}

	public static Duration hours(double hours) {
		return new Duration(hours, Unit.HOUR);
	}
	
	public static Duration hours(long hours) {
		return new Duration(hours, Unit.HOUR);
	}
	
	public static Duration hrs(double hours) {
		return hours(hours);
	}
	
	public static Duration hrs(long hours) {
		return hours(hours);
	}

	public static Duration days(double days) {
		return new Duration(days, Unit.DAY);
	}
	
	public static Duration days(long days) {
		return new Duration(days, Unit.DAY);
	}
	
	public static Duration weeks(double weeks) {
		return new Duration(weeks, Unit.WEEK);
	}
	
	public static Duration weeks(long weeks) {
		return new Duration(weeks, Unit.WEEK);
	}
	
	public static Duration years(long years) {
		return new Duration(years, Unit.YEAR);
	}

	public static Duration millisecondsFromNow(long timestamp) {
		return milliseconds(timestamp - System.currentTimeMillis());
	}
	
	public static Duration infinite() {
		return milliseconds(Long.MAX_VALUE);
	}
	
	/**
	 * @param time The time to measure between.
	 * @return A Duration that is equal to the length of time between right now
	 *         and the given Timestamp.
	 */
	public static Duration since(Timestamp time) {
		if (time.getTime() > System.currentTimeMillis())
			return ms(time.getTime() - System.currentTimeMillis());
		
		return ms(System.currentTimeMillis() - time.getTime());
	}
	
	/**
	 * @param time The time to measure between.
	 * @return A Duration that is equal to the length of time between right now
	 *         and the given Timestamp.
	 */
	public static Duration since(long ms) {
		return ms(System.currentTimeMillis() - ms);
	}
	
	public Long getValue() {
		return milliseconds;
	}
	
	/**
	 * Parses the value of the given String. For example, {@code "10m 15ms"} or
	 * {@code "10m15ms"} becomes a duration of 10 minutes and 15 ms.
	 * 
	 * @param str The string to evaluate.
	 * @return A Duration that parses the String.
	 */
	public static Duration valueOf(String str) {
		Duration dur = Duration.ms(0);
		
		for (Unit u : Unit.values()) {
			Matcher m = u.pattern.matcher(str);
			
			while (m.find())
				dur = dur.add(new Duration(Integer.valueOf(m.group().replaceAll(m.pattern().pattern(), "$1").trim()), u));
		}
		
		return dur;
	}
	
	public static void main(String[] args) {
		Validate.isTrue(Duration.milliseconds(398474383).formatText().equals("4d 14h 41m 14s"));
		Validate.isTrue(valueOf("6d5m10s3ms").milliseconds == days(6).add(mins(5)).add(secs(10)).add(ms(3)).milliseconds);
	}
	
}
