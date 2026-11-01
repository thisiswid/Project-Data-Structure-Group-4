package service;

import model.Mahasiswa;
import util.InputValidator;
import util.CSVUtil;

// TODO: Impor kelas kustom Anda di sini
// import hash.HashTable;
// import bst.BST; 
import java.util.List;
import java.util.ArrayList;

/**
 * Service untuk mengelola data Mahasiswa.
 * Menyediakan operasi CRUD dan menyinkronkan data antara Hash Table dan BST.
 */
public class MahasiswaService {

    // TODO: Ganti dengan implementasi kustom Anda
    // private HashTable<String, Mahasiswa> hashTable; // Key: NIM, Value: Objek Mahasiswa
    // private BST<Mahasiswa> bst; // Menyimpan objek Mahasiswa secara terurut berdasarkan NIM

    // --- MOCK IMPLEMENTATION DENGAN JAVA COLLECTIONS (UNTUK DEMO) ---
    private java.util.HashMap<String, Mahasiswa> hashTable = new java.util.HashMap<>();
    private java.util.TreeSet<Mahasiswa> bst = new java.util.TreeSet<>();
    // ----------------------------------------------------------------

    public MahasiswaService() {
        // TODO: Inisialisasi struktur data kustom Anda
        // this.hashTable = new HashTable<>();
        // this.bst = new BST<>();
    }

    /**
     * Operasi CREATE: Menambah mahasiswa baru. Menjamin sinkronisasi di kedua struktur data.
     */
    public boolean tambahMahasiswa(String nim, String nama, String jurusan) {
        if (!InputValidator.isValidNim(nim) || !InputValidator.isValidNama(nama)) {
            System.err.println("Gagal: Validasi input NIM atau Nama tidak valid.");
            return false;
        }

        // Cek apakah data sudah ada (menggunakan Hash Table agar cepat)
        // TODO: Sesuaikan dengan metode HashTable Anda, misal hashTable.get(nim)
        if (hashTable.containsKey(nim)) {
            System.err.println("Gagal: Mahasiswa dengan NIM " + nim + " sudah ada.");
            return false;
        }

        Mahasiswa m = new Mahasiswa(nim, nama, jurusan);
        
        // Simpan ke kedua struktur data untuk sinkronisasi
        // TODO: Ganti put/add dengan metode kustom Anda
        hashTable.put(nim, m); // O(1) avg
        bst.add(m);           // O(log n)

        System.out.println("Berhasil menambahkan mahasiswa: " + m.getNama());
        return true;
    }

    /**
     * Operasi READ: Mencari mahasiswa berdasarkan NIM. Menggunakan Hash Table untuk kecepatan.
     */
    public Mahasiswa cariBerdasarkanNim(String nim) {
        // TODO: Sesuaikan dengan metode HashTable Anda, misal return hashTable.get(nim);
        return hashTable.get(nim);
    }

    /**
     * Operasi READ (Terurut): Menampilkan semua mahasiswa terurut berdasarkan NIM. Menggunakan BST.
     */
    public List<Mahasiswa> dapatkanSemuaTerurut() {
        List<Mahasiswa> list = new ArrayList<>();
        // TODO: Implementasikan penjelajahan in-order pada BST Anda untuk mendapatkan data terurut
        // Misal: bst.inOrderTraversal(list::add);
        list.addAll(bst); // Menggunakan TreeSet bawaan untuk contoh
        return list;
    }

    /**
     * Operasi UPDATE: Mengubah data mahasiswa. Menjamin sinkronisasi di kedua struktur data.
     */
    public boolean updateMahasiswa(String nim, String namaBaru, String jurusanBaru) {
        // TODO: hashTable.get(nim)
        Mahasiswa m = hashTable.get(nim);
        if (m == null) {
            System.err.println("Gagal: Mahasiswa dengan NIM " + nim + " tidak ditemukan.");
            return false;
        }

        if (!InputValidator.isValidNama(namaBaru)) {
            System.err.println("Gagal: Nama baru tidak valid.");
            return false;
        }

        // 1. Update data di Hash Table (bisa langsung karena referensi objek sama)
        m.setNama(namaBaru);
        m.setJurusan(jurusanBaru);

        // 2. Update di BST.
        // Catatan: Karena kita menggunakan NIM sebagai key pembanding di Comparable,
        // dan NIM tidak berubah, kita tidak perlu menghapus dan memasukkan ulang ke BST.
        // Jika NIM *bisa* berubah, kita harus menghapus m lama dari BST dan memasukkan m baru.

        System.out.println("Data mahasiswa berhasil diperbarui.");
        return true;
    }

    /**
     * Operasi DELETE: Menghapus mahasiswa berdasarkan NIM. Menjamin sinkronisasi di kedua struktur data.
     */
    public boolean hapusMahasiswa(String nim) {
        // TODO: hashTable.remove(nim)
        Mahasiswa m = hashTable.remove(nim);
        if (m == null) {
            System.err.println("Gagal: Mahasiswa dengan NIM " + nim + " tidak ditemukan.");
            return false;
        }

        // Hapus dari BST agar sinkron
        // TODO: bst.remove(m)
        bst.remove(m);

        System.out.println("Mahasiswa " + m.getNama() + " berhasil dihapus.");
        return true;
    }

    // ================= SINKRONISASI DENGAN CSV =================

    /**
     * Memuat data dari CSV ke dalam struktur data internal (Hash Table & BST).
     */
    public void muatDariCSV(String filePath) {
        List<Mahasiswa> list = CSVUtil.bacaDariCSV(filePath);
        for (Mahasiswa m : list) {
            // Kita gunakan logic penambahan langsung agar tidak double-validasi dari generator
            // TODO: hashTable.put & bst.add
            if (!hashTable.containsKey(m.getNim())) {
                hashTable.put(m.getNim(), m);
                bst.add(m);
            }
        }
        System.out.println("Data dari CSV berhasil dimuat ke struktur data internal.");
    }

    /**
     * Menyimpan data dari struktur data internal ke file CSV.
     */
    public void simpanKeCSV(String filePath) {
        // Kita gunakan data dari BST agar file CSV outputnya terurut berdasarkan NIM.
        List<Mahasiswa> daftar = dapatkanSemuaTerurut();
        CSVUtil.simpanKeCSV(filePath, daftar);
    }
}