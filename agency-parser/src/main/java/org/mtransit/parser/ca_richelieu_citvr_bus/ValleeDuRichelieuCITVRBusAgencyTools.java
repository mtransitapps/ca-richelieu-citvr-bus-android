package org.mtransit.parser.ca_richelieu_citvr_bus;

import static org.mtransit.commons.Constants.SPACE_;
import static org.mtransit.commons.RegexUtils.DIGITS;
import static org.mtransit.commons.StringUtils.EMPTY;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mtransit.commons.CharUtils;
import org.mtransit.commons.CleanUtils;
import org.mtransit.commons.RegexUtils;
import org.mtransit.commons.StringUtils;
import org.mtransit.parser.DefaultAgencyTools;
import org.mtransit.parser.MTLog;
import org.mtransit.parser.gtfs.data.GRoute;
import org.mtransit.parser.gtfs.data.GStop;
import org.mtransit.parser.mt.data.MAgency;

import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// https://exo.quebec/en/about/open-data
public class ValleeDuRichelieuCITVRBusAgencyTools extends DefaultAgencyTools {

	public static void main(@NotNull String[] args) {
		new ValleeDuRichelieuCITVRBusAgencyTools().start(args);
	}

	@Nullable
	@Override
	public List<Locale> getSupportedLanguages() {
		return LANG_FR;
	}

	@NotNull
	@Override
	public String getAgencyName() {
		return "exo VR";
	}

	@NotNull
	@Override
	public Integer getAgencyRouteType() {
		return MAgency.ROUTE_TYPE_BUS;
	}

	@NotNull
	@Override
	public String cleanRouteLongName(@NotNull String routeLongName) {
		routeLongName = CleanUtils.SAINT.matcher(routeLongName).replaceAll(CleanUtils.SAINT_REPLACEMENT);
		return CleanUtils.cleanLabel(routeLongName);
	}

	@Override
	public boolean defaultRouteIdEnabled() {
		return true;
	}

	@Override
	public boolean useRouteShortNameForRouteId() {
		return true;
	}

	@Override
	public boolean defaultRouteLongNameEnabled() {
		return true;
	}

	@Override
	public boolean defaultAgencyColorEnabled() {
		return true;
	}

	@Override
	public boolean forceStopTimeLastNoPickupType() {
		return true; // transit agency data incomplete
	}

	@Override
	public boolean directionFinderEnabled() {
		return true;
	}

	private static final Pattern SERVICE_LOCAL = Pattern.compile("(service local)", Pattern.CASE_INSENSITIVE);

	private static final Pattern MONT_SAINT_ = Pattern.compile("(mont-saint-|mont-st-)", Pattern.CASE_INSENSITIVE);
	private static final String MONT_SAINT_REPLACEMENT = "St-";

	private static final Pattern EXPRESS_ = CleanUtils.cleanWordsFR("express");

	private static final Pattern _DASH_ = Pattern.compile("( - )");
	private static final String _DASH_REPLACEMENT = "<>"; // form<>to

	@Override
	public @NotNull String cleanDirectionHeadsign(@Nullable GRoute gRoute, int directionId, boolean fromStopName, @NotNull String directionHeadSign) {
		directionHeadSign = CleanUtils.removeVia(directionHeadSign);
		return super.cleanDirectionHeadsign(gRoute, directionId, fromStopName, directionHeadSign);
	}

	@NotNull
	@Override
	public String cleanTripHeadsign(@NotNull String tripHeadsign) {
		tripHeadsign = _DASH_.matcher(tripHeadsign).replaceAll(_DASH_REPLACEMENT); // from - to => form<>to
		tripHeadsign = EXPRESS_.matcher(tripHeadsign).replaceAll(EMPTY);
		tripHeadsign = DEVANT_.matcher(tripHeadsign).replaceAll(EMPTY);
		tripHeadsign = MONT_SAINT_.matcher(tripHeadsign).replaceAll(MONT_SAINT_REPLACEMENT);
		tripHeadsign = SERVICE_LOCAL.matcher(tripHeadsign).replaceAll(StringUtils.EMPTY);
		tripHeadsign = CleanUtils.keepToFR(tripHeadsign);
		tripHeadsign = CleanUtils.cleanBounds(Locale.FRENCH, tripHeadsign);
		tripHeadsign = CleanUtils.cleanStreetTypesFRCA(tripHeadsign);
		return CleanUtils.cleanLabelFR(tripHeadsign);
	}

	private static final Pattern START_WITH_FACE_A = Pattern.compile("^(face à )", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
	private static final Pattern START_WITH_FACE_AU = Pattern.compile("^(face au )", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
	private static final Pattern START_WITH_FACE = Pattern.compile("^(face )", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

	private static final Pattern SPACE_FACE_A = Pattern.compile("( face à )", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
	private static final Pattern SPACE_WITH_FACE_AU = Pattern.compile("( face au )", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
	private static final Pattern SPACE_WITH_FACE = Pattern.compile("( face )", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

	private static final Pattern[] START_WITH_FACES = new Pattern[]{START_WITH_FACE_A, START_WITH_FACE_AU, START_WITH_FACE};

	private static final Pattern[] SPACE_FACES = new Pattern[]{SPACE_FACE_A, SPACE_WITH_FACE_AU, SPACE_WITH_FACE};

	private static final Pattern DEVANT_ = CleanUtils.cleanWordsFR("devant");

	@NotNull
	@Override
	public String cleanStopName(@NotNull String gStopName) {
		gStopName = _DASH_.matcher(gStopName).replaceAll(SPACE_);
		gStopName = DEVANT_.matcher(gStopName).replaceAll(EMPTY);
		gStopName = RegexUtils.replaceAllNN(gStopName, START_WITH_FACES, CleanUtils.SPACE);
		gStopName = RegexUtils.replaceAllNN(gStopName, SPACE_FACES, CleanUtils.SPACE);
		gStopName = CleanUtils.cleanBounds(Locale.FRENCH, gStopName);
		gStopName = CleanUtils.cleanStreetTypesFRCA(gStopName);
		return CleanUtils.cleanLabelFR(gStopName);
	}

	@NotNull
	@Override
	public String getStopCode(@NotNull GStop gStop) {
		if ("0".equals(gStop.getStopCode())) {
			return EMPTY;
		}
		return super.getStopCode(gStop);
	}

	private static final String A = "A";
	private static final String B = "B";
	private static final String C = "C";
	private static final String D = "D";
	private static final String E = "E";
	private static final String F = "F";
	private static final String G = "G";
	private static final String H = "H";

	private static final String LON = "LON";
	private static final String SHY = "SHY";
	private static final String SJU = "SJU";
	private static final String SBA = "SBA";
	private static final String OTP = "OTP";
	private static final String MSH = "MSH";
	private static final String MMS = "MMS";
	private static final String BEL = "BEL";

	@Override
	public int getStopId(@NotNull GStop gStop) {
		final String stopCode = getStopCode(gStop);
		if (!stopCode.isEmpty() && CharUtils.isDigitsOnly(stopCode)) {
			return Integer.parseInt(stopCode); // using stop code as stop ID
		}
		//noinspection DiscouragedApi
		final String stop_id = CleanUtils.cleanMergedID(gStop.getStopId()).toUpperCase(Locale.ENGLISH);
		final Matcher matcher = DIGITS.matcher(stop_id);
		if (matcher.find()) {
			final String digitsS = matcher.group();
			final int digits = Integer.parseInt(digitsS);
			int stopId;
			if (stop_id.startsWith(BEL)) {
				stopId = 100_000;
			} else if (stop_id.startsWith(MMS)) {
				stopId = 200_000;
			} else if (stop_id.startsWith(MSH)) {
				stopId = 300_000;
			} else if (stop_id.startsWith(OTP)) {
				stopId = 400_000;
			} else if (stop_id.startsWith(SBA)) {
				stopId = 500_000;
			} else if (stop_id.startsWith(SJU)) {
				stopId = 600_000;
			} else if (stop_id.startsWith(SHY)) {
				stopId = 700_000;
			} else if (stop_id.startsWith(LON)) {
				stopId = 800_000;
			} else {
				throw new MTLog.Fatal("Stop doesn't have an ID (start with)! " + gStop);
			}
			if (stop_id.endsWith(A)) {
				stopId += 1_000;
			} else if (stop_id.endsWith(B)) {
				stopId += 2_000;
			} else if (stop_id.endsWith(C)) {
				stopId += 3_000;
			} else if (stop_id.endsWith(D)) {
				stopId += 4_000;
			} else if (stop_id.endsWith(E)) {
				stopId += 5_000;
			} else if (stop_id.endsWith(F)) {
				stopId += 6_000;
			} else if (stop_id.endsWith(G)) {
				stopId += 7_000;
			} else if (stop_id.endsWith(H)) {
				stopId += 8_000;
			} else {
				throw new MTLog.Fatal("Stop doesn't have an ID (end with)! " + gStop);
			}
			return stopId + digits;
		}
		throw new MTLog.Fatal("Unexpected stop ID for %s!", gStop);
	}
}
