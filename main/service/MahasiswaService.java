package service;

import hash.HashTable;
import model.Mahasiswa;
import tree.BST;
import util.CSVUtil;
import util.InputValidator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Service untuk mengelola data Mahasiswa.
 *
 * Data disimpan di dua struktur sekaligus dan selalu disinkronkan:
 * - HashTable (key = NIM): pencarian, tambah, hapus rata-rata O(1).
 * - BST (key = NIM)      : menyajikan daftar terurut lewat traversal inorder, O(n).
 *
 * Objek Mahasiswa yang sama dipakai bersama oleh kedua struktur, sehingga
 * update nama/jurusan otomatis terlihat di keduanya (NIM sebagai key tidak
 * berubah).
 */
public class MahasiswaService {

    private final HashTable<String, Mahasiswa> hashTable = new HashTable<>();
    private final BST<String, Mahasiswa> bst = new BST<>();

    /** CREATE: menambah mahasiswa baru ke kedua struktur data. */
    public boolean tambahMahasiswa(String nim, String nama, String jurusan) {
        if (!InputValidator.isValidNim(nim) || !InputValidator.isValidNama(nama)) {
            System.err.println("Gagal: Validasi input NIM atau Nama tidak valid.");
            return false;
        }
        if (hashTable.containsKey(nim)) {
            System.err.println("Gagal: Mahasiswa dengan NIM " + nim + " sudah ada.");
            return false;
        }

        Mahasiswa m = new Mahasiswa(nim, nama, jurusan);
        hashTable.put(nim, m);   // O(1) rata-rata
        bst.insert(nim, m);      // O(log n) rata-rata

        System.out.println("Berhasil menambahkan mahasiswa: " + m.getNama());
        return true;
    }

    /** READ: mencari mahasiswa berdasarkan NIM lewat Hash Table. */
    public Mahasiswa cariBerdasarkanNim(String nim) {
        if (nim == null || nim.isEmpty()) {
            return null;
        }
        return hashTable.get(nim);
    }

    /** READ (terurut): seluruh mahasiswa terurut menaik berdasarkan NIM lewat inorder BST. */
    public List<Mahasiswa> dapatkanSemuaTerurut() {
        return bst.inOrder();
    }

    /** UPDATE: mengubah nama dan jurusan. NIM tidak bisa diubah. */
    public boolean updateMahasiswa(String nim, String namaBaru, String jurusanBaru) {
        Mahasiswa m = cariBerdasarkanNim(nim);
        if (m == null) {
            System.err.println("Gagal: Mahasiswa dengan NIM " + nim + " tidak ditemukan.");
            return false;
        }
        if (!InputValidator.isValidNama(namaBaru)) {
            System.err.println("Gagal: Nama baru tidak valid.");
            return false;
        }

        // Objek yang sama dipakai Hash Table dan BST, jadi cukup diubah sekali.
        m.setNama(namaBaru);
        m.setJurusan(jurusanBaru);

        System.out.println("Data mahasiswa berhasil diperbarui.");
        return true;
    }

    /** DELETE: menghapus mahasiswa dari kedua struktur data. */
    public boolean hapusMahasiswa(String nim) {
        if (nim == null || nim.isEmpty()) {
            System.err.println("Gagal: NIM tidak boleh kosong.");
            return false;
        }
        Mahasiswa m = hashTable.remove(nim);
        if (m == null) {
            System.err.println("Gagal: Mahasiswa dengan NIM " + nim + " tidak ditemukan.");
            return false;
        }
        bst.delete(nim);

        System.out.println("Mahasiswa " + m.getNama() + " berhasil dihapus.");
        return true;
    }

    /** Jumlah mahasiswa yang tersimpan. */
    public int jumlahData() {
        return hashTable.size();
    }

    /** Tinggi BST saat ini (untuk menunjukkan kondisi keseimbangan tree). */
    public int tinggiBst() {
        return bst.height();
    }

    /**
     * Memasukkan banyak mahasiswa sekaligus tanpa mencetak per data.
     * Urutan DIACAK dulu sebelum masuk ke BST, karena BST tidak menyeimbangkan
     * diri: data terurut (misalnya dari CSV hasil simpan) akan membuat tree
     * miring menjadi seperti linked list (O(n)) dan berisiko StackOverflowError
     * pada rekursi yang dalam. NIM yang sudah ada dilewati.
     *
     * @return jumlah data yang benar-benar ditambahkan
     */
    public int tambahBanyak(List<Mahasiswa> daftar) {
        List<Mahasiswa> acak = new ArrayList<>(daftar);
        Collections.shuffle(acak);
        int ditambah = 0;
        for (Mahasiswa m : acak) {
            if (!hashTable.containsKey(m.getNim())) {
                hashTable.put(m.getNim(), m);
                bst.insert(m.getNim(), m);
                ditambah++;
            }
        }
        return ditambah;
    }

    // ================= SINKRONISASI DENGAN CSV =================

    /** Memuat data dari CSV ke Hash Table dan BST (dengan urutan diacak). */
    public void muatDariCSV(String filePath) {
        List<Mahasiswa> list = CSVUtil.bacaDariCSV(filePath);
        int ditambah = tambahBanyak(list);
        System.out.println("Data dari CSV dimuat: " + ditambah + " data baru ditambahkan.");
    }

    /** Menyimpan data ke CSV; memakai inorder BST agar file terurut menurut NIM. */
    public void simpanKeCSV(String filePath) {
        CSVUtil.simpanKeCSV(filePath, dapatkanSemuaTerurut());
    }
}