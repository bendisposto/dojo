public class Happy {

    public static int happyStep(int n) {
        return -1;
    }

    public static boolean isHappy(int n) {
        return false;
    }

    /**
     * Zerlegt n in Ziffern. Die einzelnen Ziffern werden in einem Array
     * gespeichert. Die Ziffern sind nicht in korrekter Reihenfolge, das spielt
     * hier aber keine Rolle, da sowieso summiert wird.
     *
     * @param Zahl, die zerlegt werden soll
     * @return Array der Ziffern der Eingabezahl
     */
    public static int[] digits(int n) {
        int size = String.valueOf(n).length(); // Arraylaenge bestimmen
        int[] result = new int[size];
        for (int j = 0; j < result.length; j = j + 1, n = n / 10) {
            result[j] = n % 10;
        }
        return result;
    }

}