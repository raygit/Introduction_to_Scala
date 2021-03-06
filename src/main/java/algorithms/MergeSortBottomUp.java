public class MergeSortBottomUp {

    private static boolean less(Comparable a, Comparable b) {
        return a.compareTo(b) < 0;
    }

    // this version uses a temporary array whose size is proportional to its length, N.
    // and it only does 1 thing, to merge two (sorted) arrays into one array.
    public static void merge(Comparable[] a, int low, int mid, int high, Comparable[] aux) {
        int i = low;
        int j = mid + 1;
        for( int k = low; k <= high; ++k )  // copy a[low..high] to aux[low..high]
            aux[k] = a[k];

        for(int k = low; k <= high; ++k)
            if      (i > mid)             a[k] = aux[j++];
            else if (j > high)            a[k] = aux[i++];
            else if (less(aux[j],aux[i])) a[k] = aux[j++];
            else                          a[k] = aux[i++];

    }

    public static void sort(Comparable[]a) {
        int N = a.length;
        Comparable[] aux = new Comparable[a.length];
        for(int sz = 1; sz < N; sz = sz + sz)
            for(int low = 0; low < N - sz; low += sz + sz)
                merge(a, low, low+sz-1, Math.min(low+sz+sz-1, N-1), aux);
    }

    public static void main(String[] args) {
        Comparable[] data = new Comparable[100000]; // took < 5 seconds on my OSX Intel Core i7 @ 2.7Ghz
        int count = 100000;
        for(int i =0; i < data.length; ++i,--count) 
            data[i] = count;
        sort(data);
        for(int i =0; i < data.length; ++i) 
            System.out.println(data[i]+",");
    }

}
