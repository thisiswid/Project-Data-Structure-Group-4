package benchmark;

import hash.HashTable;
import tree.BST;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Benchmark bertugas mengukur dan membandingkan efisiensi waktu operasi
 * cari, tambah, dan hapus antara empat struktur data:
 * <ol>
 *   <li>HashTable proyek (package hash, pembungkus java.util.HashMap),</li>
 *   <li>java.util.HashMap bawaan Java (dipakai langsung),</li>
 *   <li>BST proyek (package tree),</li>
 *   <li>Pencarian linear (daftar/array yang dipindai satu per satu).</li>
 * </ol>
 * pada tiga ukuran data: 20, 1.000, dan 10.000 data.
 *
 * <h3>Metode pengukuran</h3>
 * <ul>
 *   <li>Data dummy: NIM 10 digit acak unik (seed tetap, jadi hasil bisa
 *       diulang) dengan value berupa String nama.</li>
 *   <li><b>Tambah</b>: membangun struktur dari kosong dengan memasukkan n
 *       data dalam urutan acak.</li>
 *   <li><b>Cari</b>: mencari n key yang ada (urutan diacak) pada struktur
 *       yang sudah berisi n data.</li>
 *   <li><b>Hapus</b>: menghapus seluruh n key (urutan diacak) dari struktur
 *       yang sudah berisi n data.</li>
 *   <li>Waktu diukur dengan System.nanoTime() per satu batch n operasi,
 *       lalu dibagi n. Proses persiapan (membangun struktur untuk uji cari
 *       dan hapus) tidak ikut dihitung.</li>
 *   <li>Setiap skenario diulang beberapa kali setelah fase pemanasan (warm-up)
 *       JIT; nilai yang dilaporkan adalah <b>median</b> agar tidak terpengaruh
 *       lonjakan sesaat (misalnya akibat garbage collector).</li>
 *   <li>Satuan hasil: nanodetik (ns) per satu operasi.</li>
 * </ul>
 *
 * <h3>Catatan keadilan perbandingan</h3>
 * <ul>
 *   <li>Operasi "tambah" pada semua struktur berarti put/insert dengan
 *       semantik yang sama: jika key sudah ada, value ditimpa. Karena itu
 *       pencarian linear juga harus memindai daftar dulu untuk memeriksa
 *       duplikat (O(n)), bukan sekadar menambah di ujung.</li>
 *   <li>Keempat struktur dipanggil lewat interface yang sama
 *       ({@link Dict}). Biaya pemanggilan ini sama untuk semuanya, tetapi
 *       ikut menambah beberapa nanodetik pada angka yang sangat kecil
 *       (misalnya get pada HashMap).</li>
 *   <li>Hasil mikrobenchmark dipengaruhi JIT, GC, dan beban mesin, jadi
 *       bandingkan <i>pola/tren</i> antar struktur, bukan angka mutlaknya.</li>
 * </ul>
 *
 * <h3>Skenario tambahan: BST pada data terurut</h3>
 * BST tidak menyeimbangkan diri. Bila data dimasukkan dalam urutan menaik
 * (misalnya NIM berurutan dari file CSV), tree merosot menjadi seperti linked
 * list dengan tinggi n, sehingga operasi menjadi O(n). Skenario ini diukur
 * terpisah sebagai "BST (data terurut)".
 *
 * <h3>Cara menjalankan</h3>
 * <pre>
 *   mvn compile
 *   java -cp target/classes benchmark.Benchmark [file-keluaran.csv]
 * </pre>
 * Tanpa argumen, hasil ditulis ke {@code benchmark-results/hasil_benchmark.csv}.
 */
public class Benchmark {

    /** Ukuran data yang diuji sesuai ketentuan tugas. */
    private static final int[] SIZES = {20, 1_000, 10_000};

    /** Seed tetap agar data dan urutan acak selalu sama di tiap eksekusi. */
    private static final long SEED = 42L;

    /**
     * Jumlah putaran penuh. Setiap sel hasil adalah median dari median tiap
     * putaran, supaya efek JIT/GC sesaat pada satu putaran tidak mendominasi.
     */
    private static final int ROUNDS = 3;

    /** Nama struktur data (urutan sama dengan kolom pada tabel hasil). */
    private static final String HASHTABLE = "HashTable (proyek)";
    private static final String HASHMAP = "HashMap (java.util)";
    private static final String BST_NAME = "BST (data acak)";
    private static final String BST_SORTED = "BST (data terurut)";
    private static final String LINEAR = "Linear";

    private static final String[] STRUCTURES =
            {HASHTABLE, HASHMAP, BST_NAME, BST_SORTED, LINEAR};

    private static final String[] OPERATIONS = {"Tambah", "Cari", "Hapus"};

    /** Menampung hasil agar tidak "dibuang" oleh optimasi JIT (dead code). */
    private static volatile long sink;

    // ================= Abstraksi struktur data =================

    /** Antarmuka minimal yang disamakan untuk keempat struktur data. */
    interface Dict {
        void put(String key, String value);

        String get(String key);

        void remove(String key);
    }

    /** Adapter untuk HashTable proyek (package hash). */
    private static class HashTableDict implements Dict {
        private final HashTable<String, String> t = new HashTable<>();

        public void put(String k, String v) { t.put(k, v); }

        public String get(String k) { return t.get(k); }

        public void remove(String k) { t.remove(k); }
    }

    /** Adapter untuk java.util.HashMap yang dipakai langsung. */
    private static class HashMapDict implements Dict {
        private final Map<String, String> m = new HashMap<>();

        public void put(String k, String v) { m.put(k, v); }

        public String get(String k) { return m.get(k); }

        public void remove(String k) { m.remove(k); }
    }

    /** Adapter untuk BST proyek (package tree). */
    private static class BstDict implements Dict {
        private final BST<String, String> t = new BST<>();

        public void put(String k, String v) { t.insert(k, v); }

        public String get(String k) { return t.search(k); }

        public void remove(String k) { t.delete(k); }
    }

    /**
     * Pencarian linear: pasangan key-value disimpan dalam array dinamis dan
     * setiap operasi memindai dari indeks 0 sampai ketemu. Semua operasi O(n).
     */
    private static class LinearDict implements Dict {
        private String[] keys = new String[16];
        private String[] values = new String[16];
        private int size = 0;

        /** Mengembalikan indeks key, atau -1 bila tidak ada (pemindaian O(n)). */
        private int indexOf(String key) {
            for (int i = 0; i < size; i++) {
                if (keys[i].equals(key)) {
                    return i;
                }
            }
            return -1;
        }

        public void put(String k, String v) {
            int i = indexOf(k);          // cek duplikat dulu, sama seperti struktur lain
            if (i >= 0) {
                values[i] = v;
                return;
            }
            if (size == keys.length) {   // array penuh: gandakan kapasitas
                keys = Arrays.copyOf(keys, size * 2);
                values = Arrays.copyOf(values, size * 2);
            }
            keys[size] = k;
            values[size] = v;
            size++;
        }

        public String get(String k) {
            int i = indexOf(k);
            return i < 0 ? null : values[i];
        }

        public void remove(String k) {
            int i = indexOf(k);
            if (i < 0) {
                return;
            }
            // Geser elemen di kanan ke kiri agar tidak ada lubang: O(n)
            System.arraycopy(keys, i + 1, keys, i, size - i - 1);
            System.arraycopy(values, i + 1, values, i, size - i - 1);
            size--;
            keys[size] = null;
            values[size] = null;
        }
    }

    /** Membuat struktur baru yang masih kosong sesuai namanya. */
    private static Dict newDict(String structure) {
        switch (structure) {
            case HASHTABLE: return new HashTableDict();
            case HASHMAP:   return new HashMapDict();
            case LINEAR:    return new LinearDict();
            default:        return new BstDict(); // BST acak maupun terurut
        }
    }

    // ================= Data dummy =================

    /** Membuat n NIM unik (10 digit, berawalan "29") dengan seed tetap. */
    private static String[] generateKeys(int n, Random rnd) {
        Set<String> unique = new HashSet<>();
        List<String> list = new ArrayList<>(n);
        while (list.size() < n) {
            String nim = String.format("29%08d", rnd.nextInt(100_000_000));
            if (unique.add(nim)) {
                list.add(nim);
            }
        }
        return list.toArray(new String[0]);
    }

    /** Mengembalikan salinan array dengan urutan diacak. */
    private static String[] shuffled(String[] src, Random rnd) {
        List<String> l = new ArrayList<>(Arrays.asList(src));
        Collections.shuffle(l, rnd);
        return l.toArray(new String[0]);
    }

    /** Mengembalikan salinan array yang diurutkan menaik. */
    private static String[] sorted(String[] src) {
        String[] c = src.clone();
        Arrays.sort(c);
        return c;
    }

    // ================= Pengukuran =================

    /** Jumlah pengulangan (setelah warm-up) agar total operasi cukup banyak. */
    private static int repetitions(int n) {
        if (n <= 20) return 3_000;
        if (n <= 1_000) return 300;
        return 15;
    }

    /** Jumlah pengulangan warm-up yang dibuang. */
    private static int warmups(int n) {
        if (n <= 20) return 3_000;
        if (n <= 1_000) return 100;
        return 5;
    }

    /** Mengisi struktur dengan seluruh key (di luar pengukuran waktu). */
    private static Dict build(String structure, String[] insertOrder) {
        Dict d = newDict(structure);
        for (String k : insertOrder) {
            d.put(k, "Nama-" + k);
        }
        return d;
    }

    /**
     * Mengukur satu operasi pada satu struktur dan satu ukuran data.
     *
     * @return median waktu per operasi dalam nanodetik
     */
    private static double measure(String structure, String op, int n, String[] keys) {
        Random rnd = new Random(SEED + n);
        boolean sortedInsert = structure.equals(BST_SORTED);
        // Urutan pemasukan data: terurut menaik untuk skenario BST terurut,
        // selain itu acak. Urutan operasi cari/hapus selalu acak.
        String[] insertOrder = sortedInsert ? sorted(keys) : shuffled(keys, rnd);
        String[] opOrder = shuffled(keys, rnd);

        int warm = warmups(n);
        int reps = repetitions(n);
        double[] samples = new double[reps];

        for (int r = -warm; r < reps; r++) {
            long elapsed;
            long local = 0;
            if (op.equals("Tambah")) {
                Dict d = newDict(structure);
                long t0 = System.nanoTime();
                for (String k : insertOrder) {
                    d.put(k, "v");
                }
                elapsed = System.nanoTime() - t0;
                local = d.get(insertOrder[0]) == null ? 0 : 1;
            } else if (op.equals("Cari")) {
                Dict d = build(structure, insertOrder);
                long t0 = System.nanoTime();
                for (String k : opOrder) {
                    if (d.get(k) != null) {
                        local++;
                    }
                }
                elapsed = System.nanoTime() - t0;
            } else { // Hapus
                Dict d = build(structure, insertOrder);
                long t0 = System.nanoTime();
                for (String k : opOrder) {
                    d.remove(k);
                }
                elapsed = System.nanoTime() - t0;
                local = d.get(opOrder[0]) == null ? 1 : 0;
            }
            sink += local;
            if (r >= 0) {
                samples[r] = (double) elapsed / n;
            }
        }
        Arrays.sort(samples);
        return samples[reps / 2];
    }

    /** Memeriksa kebenaran dasar tiap struktur sebelum diukur. */
    private static void sanityCheck(String[] keys) {
        for (String s : STRUCTURES) {
            Dict d = build(s, keys);
            for (String k : keys) {
                if (d.get(k) == null) {
                    throw new IllegalStateException(s + " gagal menemukan key " + k);
                }
            }
            if (d.get("tidak-ada") != null) {
                throw new IllegalStateException(s + " menemukan key yang tidak ada");
            }
            for (String k : keys) {
                d.remove(k);
            }
            for (String k : keys) {
                if (d.get(k) != null) {
                    throw new IllegalStateException(s + " gagal menghapus key " + k);
                }
            }
        }
    }

    // ================= Program utama =================

    /** Menjalankan seluruh benchmark dan menampilkan serta menyimpan hasilnya. */
    private static void runAll(Path outFile) throws IOException {
        // Warm-up global: jalankan semua struktur & operasi sekali (hasil
        // dibuang) agar semua jalur kode sudah dikompilasi JIT sebelum diukur.
        System.out.println("Warm-up JIT ...");
        String[] warmKeys = generateKeys(1_000, new Random(SEED));
        for (String op : OPERATIONS) {
            for (String st : STRUCTURES) {
                measure(st, op, 1_000, warmKeys);
            }
        }

        // samples[putaran][ukuran][operasi][struktur] dalam ns/operasi
        double[][][][] samples =
                new double[ROUNDS][SIZES.length][OPERATIONS.length][STRUCTURES.length];

        for (int round = 0; round < ROUNDS; round++) {
            for (int si = 0; si < SIZES.length; si++) {
                int n = SIZES[si];
                String[] keys = generateKeys(n, new Random(SEED));
                if (round == 0) {
                    sanityCheck(keys);
                }
                System.out.println("Putaran " + (round + 1) + "/" + ROUNDS
                        + ", n = " + n + " ...");
                for (int oi = 0; oi < OPERATIONS.length; oi++) {
                    for (int ti = 0; ti < STRUCTURES.length; ti++) {
                        samples[round][si][oi][ti] =
                                measure(STRUCTURES[ti], OPERATIONS[oi], n, keys);
                    }
                }
            }
        }

        // results[ukuran][operasi][struktur] = median antar putaran
        double[][][] results = new double[SIZES.length][OPERATIONS.length][STRUCTURES.length];
        for (int si = 0; si < SIZES.length; si++) {
            for (int oi = 0; oi < OPERATIONS.length; oi++) {
                for (int ti = 0; ti < STRUCTURES.length; ti++) {
                    double[] v = new double[ROUNDS];
                    for (int r = 0; r < ROUNDS; r++) {
                        v[r] = samples[r][si][oi][ti];
                    }
                    Arrays.sort(v);
                    results[si][oi][ti] = v[ROUNDS / 2];
                }
            }
        }

        printTables(results);
        writeCsv(outFile, results);
        System.out.println("\nHasil disimpan ke: " + outFile.toAbsolutePath());
    }

    /** Menampilkan tabel hasil ke konsol, satu tabel per operasi. */
    private static void printTables(double[][][] results) {
        for (int oi = 0; oi < OPERATIONS.length; oi++) {
            System.out.println("\n=== " + OPERATIONS[oi].toUpperCase()
                    + " (ns per operasi, median) ===");
            StringBuilder header = new StringBuilder(String.format("%-8s", "n"));
            for (String s : STRUCTURES) {
                header.append(String.format("%22s", s));
            }
            System.out.println(header);
            for (int si = 0; si < SIZES.length; si++) {
                StringBuilder row = new StringBuilder(String.format("%-8d", SIZES[si]));
                for (int ti = 0; ti < STRUCTURES.length; ti++) {
                    row.append(String.format("%22.1f", results[si][oi][ti]));
                }
                System.out.println(row);
            }
        }
    }

    /** Menyimpan hasil ke CSV (format panjang: operasi,struktur,n,ns_per_op). */
    private static void writeCsv(Path outFile, double[][][] results) throws IOException {
        Path parent = outFile.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        try (PrintWriter w = new PrintWriter(Files.newBufferedWriter(outFile))) {
            w.println("operasi,struktur,n,ns_per_op");
            for (int oi = 0; oi < OPERATIONS.length; oi++) {
                for (int ti = 0; ti < STRUCTURES.length; ti++) {
                    for (int si = 0; si < SIZES.length; si++) {
                        w.printf(java.util.Locale.US, "%s,%s,%d,%.2f%n",
                                OPERATIONS[oi], STRUCTURES[ti], SIZES[si],
                                results[si][oi][ti]);
                    }
                }
            }
        }
    }

    /**
     * Titik masuk benchmark. Dijalankan di thread dengan stack besar karena
     * BST bersifat rekursif dan pada data terurut tingginya bisa mencapai n,
     * sehingga kedalaman rekursi berpotensi menyebabkan StackOverflowError.
     */
    public static void main(String[] args) throws Exception {
        Path out = Paths.get(args.length > 0 ? args[0] : "benchmark-results/hasil_benchmark.csv");
        Throwable[] error = new Throwable[1];
        Thread t = new Thread(null, () -> {
            try {
                runAll(out);
            } catch (Throwable e) {
                error[0] = e;
            }
        }, "benchmark", 256L * 1024 * 1024);
        t.start();
        t.join();
        if (error[0] != null) {
            throw new RuntimeException(error[0]);
        }
    }
}
