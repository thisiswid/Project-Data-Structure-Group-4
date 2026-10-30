import graph.Graph;
import hash.HashTable;

import java.util.List;
import java.util.Scanner;

/**
 * Main adalah titik masuk (entry point) aplikasi Manajemen Data Mahasiswa.
 *
 * CATATAN STATUS: Secara formal, penyusunan menu aplikasi dan penggabungan
 * seluruh package adalah tanggung jawab Anggota 1 (Ketua dan Integrator).
 * Package model dan service masih berupa placeholder dan belum
 * diimplementasikan oleh anggota yang bertanggung jawab, sehingga belum
 * bisa diintegrasikan ke sini.
 *
 * Supaya proyek tetap punya aplikasi yang bisa benar-benar dijalankan dan
 * diuji sejauh progres saat ini, Main.java ini dibuat sebagai menu konsol
 * sederhana yang mendemonstrasikan dua struktur yang sudah selesai dan
 * fungsional: HashTable<K, V> (package hash, berbasis java.util.HashMap)
 * dan Graph<V> (package graph, adjacency list berbasis java.util.HashMap
 * dan java.util.ArrayList). Begitu Mahasiswa dan MahasiswaService selesai
 * diimplementasikan oleh anggota yang bertanggung jawab, menu ini dapat
 * diperluas atau digantikan oleh Anggota 1 untuk mengelola data Mahasiswa
 * yang sesungguhnya (NIM sebagai key, objek Mahasiswa sebagai value).
 */
public class Main {

    private static final HashTable<String, String> dataTable = new HashTable<>();
    private static final Graph<String> dataGraph = new Graph<>();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=====================================================");
        System.out.println(" DEMO HASH TABLE & GRAPH - Manajemen Data Mahasiswa");
        System.out.println(" (Menu Mahasiswa/Service menunggu implementasi");
        System.out.println("  Anggota 1 dan 4)");
        System.out.println("=====================================================");

        boolean running = true;
        while (running) {
            tampilkanMenu();
            String pilihan = scanner.nextLine().trim();

            switch (pilihan) {
                case "1":
                    tambahData();
                    break;
                case "2":
                    cariData();
                    break;
                case "3":
                    hapusData();
                    break;
                case "4":
                    tampilkanStatistikHashTable();
                    break;
                case "5":
                    tambahEdgeGraph();
                    break;
                case "6":
                    tampilkanTetanggaGraph();
                    break;
                case "7":
                    jalankanBfs();
                    break;
                case "8":
                    jalankanDfs();
                    break;
                case "0":
                    running = false;
                    System.out.println("Keluar dari aplikasi. Sampai jumpa!");
                    break;
                default:
                    System.out.println("Pilihan tidak dikenali, silakan coba lagi.\n");
            }
        }

        scanner.close();
    }

    /** Menampilkan daftar menu yang tersedia kepada pengguna. */
    private static void tampilkanMenu() {
        System.out.println("=== Menu Hash Table ===");
        System.out.println("1. Tambah data (key = NIM, value = nama)");
        System.out.println("2. Cari data berdasarkan key");
        System.out.println("3. Hapus data berdasarkan key");
        System.out.println("4. Tampilkan statistik Hash Table");
        System.out.println("=== Menu Graph ===");
        System.out.println("5. Tambah edge (relasi antar dua NIM/nama)");
        System.out.println("6. Tampilkan tetangga suatu vertex");
        System.out.println("7. Jalankan BFS dari suatu vertex");
        System.out.println("8. Jalankan DFS dari suatu vertex");
        System.out.println("0. Keluar");
        System.out.print("> ");
    }

    // ================= Menu Hash Table =================

    /** Menambahkan pasangan key-value baru ke dalam HashTable. */
    private static void tambahData() {
        System.out.print("Masukkan key (contoh: NIM): ");
        String key = scanner.nextLine().trim();
        System.out.print("Masukkan value (contoh: nama): ");
        String value = scanner.nextLine().trim();

        if (key.isEmpty() || value.isEmpty()) {
            System.out.println("Key dan value tidak boleh kosong.\n");
            return;
        }

        dataTable.put(key, value);
        System.out.println("Data berhasil disimpan: " + key + " -> " + value + "\n");
    }

    /** Mencari dan menampilkan value berdasarkan key yang dimasukkan pengguna. */
    private static void cariData() {
        System.out.print("Masukkan key yang dicari: ");
        String key = scanner.nextLine().trim();

        String value = dataTable.get(key);
        if (value == null) {
            System.out.println("Key \"" + key + "\" tidak ditemukan.\n");
        } else {
            System.out.println("Ditemukan: " + key + " -> " + value + "\n");
        }
    }

    /** Menghapus data berdasarkan key yang dimasukkan pengguna. */
    private static void hapusData() {
        System.out.print("Masukkan key yang akan dihapus: ");
        String key = scanner.nextLine().trim();

        String removed = dataTable.remove(key);
        if (removed == null) {
            System.out.println("Key \"" + key + "\" tidak ditemukan, tidak ada yang dihapus.\n");
        } else {
            System.out.println("Data \"" + key + "\" -> \"" + removed + "\" berhasil dihapus.\n");
        }
    }

    /** Menampilkan statistik dasar HashTable: ukuran dan status kosong/tidak. */
    private static void tampilkanStatistikHashTable() {
        System.out.println("--- Statistik Hash Table ---");
        System.out.println("Jumlah data (size) : " + dataTable.size());
        System.out.println("Kosong?            : " + dataTable.isEmpty());
        System.out.println("-----------------------------\n");
    }

    // ================= Menu Graph =================

    /** Menambahkan edge (relasi) antara dua vertex pada Graph. */
    private static void tambahEdgeGraph() {
        System.out.print("Masukkan vertex pertama: ");
        String vertexA = scanner.nextLine().trim();
        System.out.print("Masukkan vertex kedua: ");
        String vertexB = scanner.nextLine().trim();

        if (vertexA.isEmpty() || vertexB.isEmpty()) {
            System.out.println("Vertex tidak boleh kosong.\n");
            return;
        }

        dataGraph.addEdge(vertexA, vertexB);
        System.out.println("Edge berhasil ditambahkan: " + vertexA + " <-> " + vertexB + "\n");
    }

    /** Menampilkan daftar tetangga dari suatu vertex. */
    private static void tampilkanTetanggaGraph() {
        System.out.print("Masukkan vertex: ");
        String vertex = scanner.nextLine().trim();

        if (!dataGraph.containsVertex(vertex)) {
            System.out.println("Vertex \"" + vertex + "\" tidak ditemukan.\n");
            return;
        }

        List<String> neighbors = dataGraph.getNeighbors(vertex);
        System.out.println("Tetangga dari \"" + vertex + "\": " + neighbors + "\n");
    }

    /** Menjalankan traversal BFS dari vertex awal yang dimasukkan pengguna. */
    private static void jalankanBfs() {
        System.out.print("Masukkan vertex awal: ");
        String start = scanner.nextLine().trim();

        List<String> hasil = dataGraph.bfs(start);
        if (hasil.isEmpty()) {
            System.out.println("Vertex \"" + start + "\" tidak ditemukan di graph.\n");
        } else {
            System.out.println("Urutan BFS: " + hasil + "\n");
        }
    }

    /** Menjalankan traversal DFS dari vertex awal yang dimasukkan pengguna. */
    private static void jalankanDfs() {
        System.out.print("Masukkan vertex awal: ");
        String start = scanner.nextLine().trim();

        List<String> hasil = dataGraph.dfs(start);
        if (hasil.isEmpty()) {
            System.out.println("Vertex \"" + start + "\" tidak ditemukan di graph.\n");
        } else {
            System.out.println("Urutan DFS: " + hasil + "\n");
        }
    }
}
