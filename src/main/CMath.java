package main;

public class CMath {
	
	public static boolean isBetween(int between, int low, int high) {
		return (low < between && between < high);
	}
	
	public static int power(int n, int f) {
        int i = f;
        while (i < n) {
            i *= f;
        }
        return i;
	}

	public static int power2(int n) {
		return 1 << (32 - Integer.numberOfLeadingZeros(n-1));
	}
	
}
