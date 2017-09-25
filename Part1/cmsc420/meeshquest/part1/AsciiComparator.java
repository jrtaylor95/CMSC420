package cmsc420.meeshquest.part1;

import java.util.Comparator;

public class AsciiComparator implements Comparator<String> {
	@Override
	public int compare(String str1, String str2) {
		return str2.compareTo(str1);
	}
}
