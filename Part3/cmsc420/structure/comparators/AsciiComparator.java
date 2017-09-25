package cmsc420.structure.comparators;


import java.util.Comparator;

public class AsciiComparator implements Comparator<String> {
	private static final AsciiComparator INSTANCE = new AsciiComparator();
	
	@Override
	public int compare(String str1, String str2) {
		return str2.compareTo(str1);
	}
	
	public static AsciiComparator getInstance() {
		return INSTANCE;
	}
}
