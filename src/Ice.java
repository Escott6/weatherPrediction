
import java.util.*;
import java.io.*;

public class Ice {
	private static String FrozenDays = "1855,1856,1857,1858,1859,1860,1861,1862,1863,1864,1865,1866,1867,1868,1869,1870,1871,1872,1873,1874,1875,1876,1877,1878,1879,1880,1881,1882,1883,1884,1885,1886,1887,1888,1889,1890,1891,1892,1893,1894,1895,1896,1897,1898,1899,"
			+ "1900,1901,1902,1903,1904,1905,1906,1907,1908,1909,1910,1911,1912,1913,1914,1915,1916,1917,1918,1919,1920,1921,1922,1923,1924,1925,1926,1927,1928,1929,1930,1931,1932,1933,1934,1935,1936,1937,1938,1939,1940,1941,1942,1943,1944,1945,1946,1947,1948,1949,1950,"
			+ "1951,1952,1953,1954,1955,1956,1957,1958,1959,1960,1961,1962,1963,1964,1965,1966,1967,1968,1969,1970,1971,1972,1973,1974,1975,1976,1977,1978,1979,1980,1981,1982,1983,1984,1985,1986,1987,1988,1989,1990,1991,1992,1993,1994,1995,1996,1997,1998,1999,2000,2001,"
			+ "2002,2003,2004,2005,2006,2007,2008,2009,2010,2011,2012,2013,2014,2015,2016";
	private static String WinterYears = "118,151,121,96,110,117,132,104,125,118,125,123,110,127,131,99,126,144,136,126,91,130,62,112,99,161,78,124,119,124,128,131,113,88,75,111,97,112,101,101,91,110,100,130,111,107,105,89,126,108,97,94,83,106,98,101,108,99,88,115,102,116,"
			+ "115,82,110,81,96,125,104,105,124,103,106,96,107,98,65,115,91,94,101,121,105,97,105,96,82,116,114,92,98,101,104,96,109,122,114,81,85,92,114,111,95,126,105,108,117,112,113,120,65,98,91,108,113,110,105,97,105,107,88,115,123,118,99,93,96,54,111,85,107,89,87,97,93,"
			+ "88,99,108,94,74,119,102,47,82,53,115,21,89,80,101,95,66,106,97,87,109,57,87,117,91,62,65";
	
	private static List<String> FDays = Arrays.asList(FrozenDays.split(","));
	private static List<String> WYears = Arrays.asList(WinterYears.split(","));

	public static void main(String args[]) {
		if (args.length == 0) {
			System.out.println("Invalid Number of Input Arguments");
			return;
		}
		int flag = Integer.valueOf(args[0]);
		HashMap map = readFile1();
		if (flag == 100) {
			for (Object key : map.keySet()) {
				System.out.println((Integer) key + " " + map.get(key));
			}
		}
		if (flag == 200) {
			System.out.println(map.size());
			double sum = 0;
			for (Object numDays : map.values()) {
				sum += (Integer) numDays;
			}
			double sampleMean = (sum / map.size());
			System.out.println(String.format("%.2f", sampleMean));
			double standardDev = 0;
			for (Object key : map.keySet()) {
				standardDev += Math.pow((((Integer) map.get(key)) - sampleMean), 2) / (map.size() - 1);
			}
			standardDev = Math.sqrt(standardDev);
			System.out.println(String.format("%.2f", standardDev));
		}
		if (flag == 300) {
			if (args.length < 3) {
				System.out.println("Invalid Number of Input Arguments");
				return;
			}
			double MSE = MSE(args, map);
			System.out.println(String.format("%.2f", MSE));
		}
		if (flag == 400) {
			if (args.length < 3) {
				System.out.println("Invalid Number of Input Arguments");
				return;
			}
			double Px = GD(args, map, true);
			double Py = GD(args, map, false);
			System.out.println(String.format("%.2f", Px));
			System.out.println(String.format("%.2f", Py));
		}
		if (flag == 500) {
			if (args.length < 3) {
				System.out.println("Invalid Number of Input Arguments");
				return;
			}
			String[] betaArgs = new String[3];
			betaArgs[1] = "0";
			betaArgs[2] = "0";
			int i = 1;
			while (i != Integer.valueOf(args[2]) + 1) {
				double betaZero = Double.valueOf(betaArgs[1]) - (Double.valueOf(args[1])) * GD(betaArgs, map, true);
				double betaOne = Double.valueOf(betaArgs[2]) - (Double.valueOf(args[1])) * GD(betaArgs, map, false);
				betaArgs[1] = Double.toString(betaZero);
				betaArgs[2] = Double.toString(betaOne);
				System.out.println(i + " " + String.format(String.valueOf("%.2f"), betaZero) + " "
						+ String.format(String.valueOf("%.2f"), betaOne) + " "
						+ String.format("%.2f", MSE(betaArgs, map)));
				i++;
			}
		}
		if (flag == 600) {
			double[] betaValues = closedForm(map);
			System.out.println(String.format("%.2f", betaValues[0]));
			System.out.println(String.format("%.2f", betaValues[1]));
			String[] newArgs = new String[3];
			newArgs[1] = Double.toString(betaValues[0]);
			newArgs[2] = Double.toString(betaValues[1]);
			System.out.println(String.format("%.2f", MSE(newArgs, map)));
		}
		if (flag == 700) {
			int year = Integer.valueOf(args[1]);
			double[] betaValues = closedForm(map);
			double prediction = (betaValues[0] + (betaValues[1] * year));
			System.out.println(String.format("%.2f", prediction));
		}
		if (flag == 800) {
			HashMap<Double, Integer> newMap = newMap(map);
			// compute values
			String[] betaArgs = new String[3];
			betaArgs[1] = "0";
			betaArgs[2] = "0";
			int i = 1;
			while (i != Integer.valueOf(args[2]) + 1) {
				double betaZero = Double.valueOf(betaArgs[1]) - (Double.valueOf(args[1])) * GD2(betaArgs, newMap, true);
				double betaOne = Double.valueOf(betaArgs[2]) - (Double.valueOf(args[1])) * GD2(betaArgs, newMap, false);
				betaArgs[1] = Double.toString(betaZero);
				betaArgs[2] = Double.toString(betaOne);
				System.out.println(i + " " + String.format(String.valueOf("%.2f"), betaZero) + " "
						+ String.format(String.valueOf("%.2f"), betaOne) + " "
						+ String.format("%.2f", MSE2(betaArgs, newMap)));
				i++;
			}
		}
		if (flag == 900) {
			HashMap<Double, Integer> newMap = newMap(map);
			// compute values
			String[] betaArgs = new String[3];
			betaArgs[1] = "0";
			betaArgs[2] = "0";
			int i = 1;
			while (i != Integer.valueOf(args[2]) + 1) {
				double betaZero = Double.valueOf(betaArgs[1]) - (Double.valueOf(args[1])) * GD3(betaArgs, newMap, true);
				double betaOne = Double.valueOf(betaArgs[2]) - (Double.valueOf(args[1])) * GD3(betaArgs, newMap, false);
				betaArgs[1] = Double.toString(betaZero);
				betaArgs[2] = Double.toString(betaOne);
				System.out.println(i + " " + String.format(String.valueOf("%.2f"), betaZero) + " "
						+ String.format(String.valueOf("%.2f"), betaOne) + " "
						+ String.format("%.2f", MSE2(betaArgs, newMap)));
				i++;
			}
		}
	}

	private static HashMap<Double, Integer> newMap(HashMap map) {
		// normalize x data points
		double xMean = 0;
		double standardDev = 0;
		for (Object year : map.keySet()) {
			xMean += (Integer) year;
		}
		xMean = xMean / map.size();

		for (Object key : map.keySet()) {
			standardDev += Math.pow((((Integer) (key)) - xMean), 2) / (map.size() - 1);
		}
		standardDev = Math.sqrt(standardDev);
		HashMap<Double, Integer> newMap = new HashMap<>();
		for (Object key : map.keySet()) {
			double newX = ((Integer) (key) - xMean) / standardDev;
			newMap.put(newX, (Integer) map.get(key));
		}
		return newMap;
	}

	private static double[] closedForm(HashMap map) {
		double[] betaValues = new double[2];
		double betaHat0 = 0;
		double betaHat1 = 0;
		double xMean = 0;
		double yMean = 0;
		// get the Mean for x and y
		for (Object year : map.keySet()) {
			yMean += (Integer) map.get(year);
			xMean += (Integer) year;
		}
		xMean = xMean / map.size();
		yMean = yMean / map.size();
		// Find Beta Hat 1
		double bTop = 0;
		double bBottom = 0;
		for (Object year : map.keySet()) {
			bTop += ((Integer) year - xMean) * ((Integer) map.get(year) - yMean);
			bBottom += Math.pow(((Integer) year - xMean), 2);
		}
		betaHat1 = bTop / bBottom;
		// Beta Hat 0
		betaHat0 = yMean - (betaHat1 * xMean);
		betaValues[0] = betaHat0;
		betaValues[1] = betaHat1;
		return betaValues;
	}

	private static double GD(String[] args, HashMap map, boolean x) {
		double partialD = 0;
		double arg1 = Double.valueOf(args[1]);
		double arg2 = Double.valueOf(args[2]);
		for (Object key : map.keySet()) {
			if (x) {
				partialD += (arg1 + arg2 * (Integer) key - (Integer) map.get(key));
			} else {
				partialD += ((arg1 + arg2 * (Integer) key - (Integer) map.get(key)) * (Integer) key);
			}
		}
		partialD = (partialD * 2) / map.size();
		return partialD;
	}

	private static double GD2(String[] args, HashMap map, boolean x) {
		double partialD = 0;
		double arg1 = Double.valueOf(args[1]);
		double arg2 = Double.valueOf(args[2]);
		for (Object key : map.keySet()) {
			if (x) {
				partialD += (arg1 + arg2 * (Double) key - (Integer) map.get(key));
			} else {
				partialD += ((arg1 + arg2 * (Double) key - (Integer) map.get(key)) * (Double) key);
			}
		}
		partialD = (partialD * 2) / map.size();
		return partialD;
	}
	
	private static double GD3(String[] args, HashMap map, boolean x) {
		double partialD = 0;
		double arg1 = Double.valueOf(args[1]);
		double arg2 = Double.valueOf(args[2]);
		Random rand = new Random();
		Object[] Keys = map.keySet().toArray();
		Object key = Keys[rand.nextInt(Keys.length)];
			if (x) {
				partialD += 2*(arg1 + arg2 * (Double) key - (Integer) map.get(key));
			} else {
				partialD += 2*((arg1 + arg2 * (Double) key - (Integer) map.get(key)) * (Double) key);
			}
		return partialD;
	}

	private static double MSE(String args[], HashMap<Integer, Integer> map) {
		double arg1 = Double.valueOf(args[1]);
		double arg2 = Double.valueOf(args[2]);
		double MSE = 0;
		for (Object key : map.keySet()) {
			MSE += Math.pow((arg1 + arg2 * (Integer) key - (Integer) map.get(key)), 2);
		}
		MSE = MSE / map.size();
		return MSE;
	}

	private static double MSE2(String[] args, HashMap<Double, Integer> map) {
		double arg1 = Double.valueOf(args[1]);
		double arg2 = Double.valueOf(args[2]);
		double MSE = 0;
		for (Object key : map.keySet()) {
			MSE += Math.pow((arg1 + arg2 * (Double) key - (Integer) map.get(key)), 2);
		}
		MSE = MSE / map.size();
		return MSE;
	}

	private static HashMap<Integer, Integer> readFile1() {
		HashMap<Integer, Integer> newMap = new HashMap<>();
		for (int i =0; i< FDays.size(); i++) {
			int j = Integer.valueOf(FDays.get(i));
			int k = Integer.valueOf(WYears.get(i));
				newMap.put(j, k);
		}
		return newMap;
	}
}
