package com.legyver.logmire.task.indexlog;

import java.util.ArrayList;
import java.util.List;

public class IndexPattern {
	public static final List<String> THREE = new ArrayList<>();
	public static final List<String> FOUR = new ArrayList<>();
	public static final List<String> FIVE = new ArrayList<>();

	static {
		augment(THREE, new StringBuilder(), 3);
		augment(THREE, FOUR,1);//cheat and use 3 as the basis four 4, etc
		augment(FOUR, FIVE, 1);
	}

	public static void augment(List<String> previousSearchPatterns, List<String> searchPatterns, int remainingIterations) {
		for (String s : previousSearchPatterns) {
			augment(searchPatterns, new StringBuilder(s), remainingIterations - 1);
		}
	}

	private static void augment(List<String> searchPatterns, StringBuilder sb , int remainingIterations) {
		if (remainingIterations == 0) {
			searchPatterns.add(sb.toString());
			return;
		}
		for (char cursor = 'a'; cursor <= 'z'; cursor++) {
			StringBuilder sb2 = new StringBuilder(sb).append(cursor);
			augment(searchPatterns, sb2, remainingIterations - 1);
		}
	}

}
