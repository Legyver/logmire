package com.legyver.logmire.ui.util;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Create abbreviations for show/hide package labels
 * The idea was to split out individual icons for each filter.  This may no longer be needed as having a unified icon seems cleaner.
 */
public enum PackageAbbreviator {
	INSTANCE;
	private Map<String, String> existingAbbreviationMap = new HashMap<>();
	private Set<String> existingAbbreviations = new HashSet<>();

	/**
	 * Create a unique abbreviation for a package.
	 * @param packageName: The name of the package
	 * Note: order matters.  First package to abbreviation wins.
	 * @return: the abbreviation
	 */
	public String createUniqueAbbreviation(String packageName) {
		String abbreviation = getAbbreviation(packageName);
		if (abbreviation == null) {
			if (!packageName.contains(".")) {
				abbreviation = abbreviateSingleSegment(packageName, packageName);
			} else {
				String[] parts = packageName.split("\\.");
				if (parts.length == 1) {//sun, java, javax, javafx, etc
					String part = parts[0];
					abbreviation = abbreviateSingleSegment(packageName, part);
				} else {
					abbreviation = Stream.of(parts)
							.map(s->s.substring(0, 1))//first letter
							.collect(Collectors.joining("."));
					putAbbreviation(packageName, abbreviation);
				}
			}
		}
		return abbreviation;
	}

	private String abbreviateSingleSegment(String packageName, String part) {
		String candidate = part.substring(0, 1);
		if (!existingAbbreviations.contains(candidate)) {
			putAbbreviation(packageName, candidate);
		} else {
			String suffix = "";
			String prefix = candidate;//at this point, the first letter of the package
			for (int i = part.length() - 1; i > -1; i--) {
				suffix = part.charAt(i) + suffix;//x, fx, etc
				candidate = prefix + suffix;//jx, jfx, etc
				if (!existingAbbreviations.contains(candidate)) {
					putAbbreviation(packageName, candidate);
					break;
				}
			}
		}
		return candidate;
	}

	private void putAbbreviation(String packageName, String candidate) {
		existingAbbreviations.add(candidate);
		existingAbbreviationMap.put(packageName.endsWith(".") ? packageName : packageName + ".", candidate);
	}

	private String getAbbreviation(String packageName) {
		return existingAbbreviationMap.get(packageName.endsWith(".") ? packageName : packageName + ".");
	}

}
