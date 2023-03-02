package projectv1_2;

import java.util.*;

public class PMAlgorithms {

	/*
	 * input: length of string generates a random alphanumeric string with legnth
	 * len
	 */
	public static String generateString(int len) {

		// buffer to return the string
		StringBuffer buff = new StringBuffer(len);

		// A Random object to use nextInt() method
		Random random = new Random();

		// all the possible characters (alphanumeric)
		String CHARACTER_SET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz";

		for (int i = 0; i < len; i++) {

			// nextInt() to generate a number between 0 and length inclusive
			int offset = random.nextInt(CHARACTER_SET.length());

			// add the char. to the string
			buff.append(CHARACTER_SET.substring(offset, offset + 1));
		}
		
		return buff.toString();

	}

	/*
	 * input: the text to search within and the pattern to search for
	 * 
	 * returns a list of all locations of the first letter of the pattern in the
	 * text
	 */
	public static List<Integer> bruteForce(char[] text, char[] pattern) {

		long t1 = System.nanoTime(); // start time

		// a list of all locations of the first letter of the pattern in the text
		List<Integer> locations = new LinkedList<>();

		int n = text.length;
		int m = pattern.length;

		// to traverse through text
		// n-m, pattern cannot be found after this index
		for (int i = 0; i <= n - m; i++) {

			int k = 0; // used to loop through the pattern

			// increment k if corresponding char. in pattern matches
			while (k < m && text[i + k] == pattern[k])
				k++;
			if (k == m) // end of pattern reached (pattern is found/matched)
				locations.add(i); // add the starting index of the found pattern
		}

		long t2 = System.nanoTime(); // end time

		// difference of start time and end time gives the elapsed time
		// then add the elapsed time to counterTimeBrutrForce (which is in GUI_App
		// class)
		// this counter is used to store the time the method took
		GUI_App.counterTimeBrutrForce += t2 - t1;
		return locations;

	}

	/*
	 * input: the text to search within and the pattern to search for
	 * 
	 * returns a list of all locations of the first letter of the pattern in the
	 * text
	 */
	public static List<Integer> boyerMoore(char[] text, char[] pattern) {

		long t1 = System.nanoTime(); // start time

		// a list of all locations of the first letter of the pattern in the text
		List<Integer> locations = new LinkedList<>();

		int n = text.length; // length of text
		int m = pattern.length; // length of pattern

		// it stores the index of last occurrence of each character
		// in the pattern, and if the char. is not in the pattern; its index is -1 as
		// default (see next lines)
		Map<Character, Integer> last = new HashMap<>();

		for (int i = 0; i < n; i++)
			last.put(text[i], -1); // set -1 as default for all text characters

		for (int k = 0; k < m; k++) {
			// for each char. in the pattern, first occurrence from the right is stored
			last.put(pattern[k], k);
		}

		/*
		 * Looking-Glass Heuristic: When testing a possible placement of the pattern
		 * against the text, perform the comparisons against the pattern from
		 * right-to-left.
		 */
		int i = m - 1; // an index into the text
		int k = m - 1; // an index into the pattern
		while (i < n) {

			if (text[i] == pattern[k]) { // a matching character
				if (k == 0) { // pattern found
					locations.add(i); // insert the first index (in the text) of the found pattern
					k = m - 1; // reset k back to the end of the pattern
					i += m; // move i to the first index after the pattern in the text
					continue;
				}

				// continue checking the text for the pattern
				i--;
				k--;

			} else { // Character-Jump Heuristic (description in report).

				/*
				 * if there's a mismatch of character text[i]=c with the corresponding
				 * pattern[k], we do the following: If c is not contained anywhere in the
				 * pattern, then shift the pattern completely past text[i] = c. Otherwise, shift
				 * the pattern until an occurrence of character c gets aligned with text[i].
				 */
				i += m - Math.min(k, 1 + last.get(text[i]));

				k = m - 1; // reset k back to the end of the pattern
			}
		}
		long t2 = System.nanoTime(); // end time

		// difference of start time and end time gives the elapsed time,
		// then add the elapsed time to counterTimeBrutrForce (which is in GUI_App
		// class)
		// this counter is used to store the time the method took
		GUI_App.counterTimeBoyerMoore += t2 - t1;

		return locations;
	}

	/*
	 * input: the text to search within and the pattern to search for
	 * 
	 * returns a list of all locations of the first letter of the pattern in the
	 * text
	 */
	public static List<Integer> KMP(char[] text, char[] pattern) {

		long t1 = System.nanoTime(); // start time

		// a list of all locations of the first letter of the pattern in the text
		List<Integer> locations = new LinkedList<>();

		int n = text.length; // text length
		int m = pattern.length; // pattern length

		// indicates the proper shift of the pattern upon a failed comparison
		int[] fail = computeFailKMP(pattern);

		int j = 0; // to index into text
		int k = 0; // to index into pattern

		while (j < n) {
			if (text[j] == pattern[k]) { // if corresponding chars match
				if (k == m - 1) { // if pattern exhausted
					locations.add(j - m + 1); // insert the first index (in the text) of the found pattern
					j++; // move to the next index
					k = 0;
					continue;
				}
				j++; // otherwise, try to extend match
				k++;
			} else if (k > 0)
				k = fail[k - 1]; // reuse suffix of P[0..k-1]
			else
				j++;
		}

		long t2 = System.nanoTime(); // end time

		// difference of start time and end time gives the elapsed time,
		// then add the elapsed time to counterTimeBrutrForce (which is in GUI_App
		// class)
		// this counter is used to store the time the method took
		GUI_App.counterTimeKMP += t2 - t1;

		return locations;
	}

	/*
	 * returns the length of the longest prefix of the pattern that is a suffix of
	 * the substring pattern[1..k] (note that we did not include pattern[0] here,
	 * since we will shift at least one unit)
	 * 
	 */
	private static int[] computeFailKMP(char[] pattern) {

		int m = pattern.length; // length of pattern
		int[] fail = new int[m]; // by default, all overlaps are zero

		int j = 1;
		int k = 0;

		while (j < m) { // compute fail[j] during this pass, if nonzero
			if (pattern[j] == pattern[k]) { // k + 1 characters match thus far
				fail[j] = k + 1;
				j++;
				k++;
			} else if (k > 0) // k follows a matching prefix
				k = fail[k - 1];
			else // no match found starting at j
				j++;
		}
		return fail;
	}
}
