package com.ilp.ilpschedule.util;


/**
 * The <code>Vector</code> class provides some useful static functions to
 * compute vectors.
 *
 * @author Michael Lambertz
 */

public class Vector {

    public static double[] normalize(double[] input) {
        double sumSquares = 0.0;
        // First calculate the length
        for (int i = 0; i < input.length; i++) {
            sumSquares += Math.pow(input[i], 2);
        }
        // The actual length of the vector
        double len = Math.sqrt(sumSquares);
        return Vector.scale(1 / len, input);
    }


    /**
     * Compares the content of two string objects.
     *
     * @param vec1 the first vector
     * @param vec2 the second vector
     * @return true, if the vectors are equal
     */

    public static boolean equals(double[] vec1, double[] vec2) {
        if (vec1.length != vec2.length) {
            return (false);
        }
        for (int i = 0; i < vec1.length; ++i) {
            if (vec1[i] != vec2[i]) {
                return (false);
            }
        }
        return (true);
    }

    /**
     * Fills a string with blanks until it reaches a desired length.
     *
     * @param in  string to fill
     * @param len desired length
     * @return the input string eventually suffixed with blanks
     */
    private static String fillString(String in, int len) {
        String out = new String(in);
        while (out.length() < len) {
            out = " " + out;
        }
        return (out);
    }

    /**
     * Converts a vector object into a <code>String</code> object
     * representing its content.
     *
     * @param vector the vector to be converted to a string
     * @return the string representing the content of the vector
     */
    public static String toString(double[] vector) {
        String result = "";
        for (int i = 0; i < vector.length; ++i) {
            result += fillString(Double.toString(vector[i]), 24) + "\n";
        }
        return (result);
    }

    /**
     * Scales a vector and returns the result in a new
     * vector object.
     *
     * @param vector the vector to scale
     * @param fac    the factor to scale with
     * @return the scaled vector
     */
    public static double[] scale(double fac, double[] vector) {
        int n = vector.length;
        double[] res = new double[n];
        for (int i = 0; i < n; ++i) {
            res[i] = fac * vector[i];
        }
        return (res);
    }

    /**
     * Adds two vectors and returns the result in a new vector object.
     *
     * @param vec1 the first vector
     * @param vec2 the second vector
     * @return the resulting vector
     */
    public static double[] add(double[] vec1, double[] vec2) {
        int m = vec1.length;
        double[] res = new double[m];
        for (int i = 0; i < m; ++i) {
            res[i] = vec1[i] + vec2[i];
        }
        return (res);
    }


    public static double[] center(double[] vec) {
        int n = vec.length;
        double mValue = 0.0;
        for (int i = 0; i < n; i++) {
            mValue += vec[i];
        }
        mValue /= n;

        double[] cVec = new double[n];
        for (int i = 0; i < n; i++) {
            cVec[i] = vec[i] - mValue;
        }
        return cVec;
    }
}
