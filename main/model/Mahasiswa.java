package model;

/**
 * Class Mahasiswa sebagai model data utama.
 * Implementasi Comparable diperlukan untuk penyimpanan dalam Binary Search Tree (BST)
 * berdasarkan NIM sebagai kunci pengurutan.
 */
public class Mahasiswa implements Comparable<Mahasiswa> {
    private String nim;
    private String nama;
    private String jurusan;

    public Mahasiswa(String nim, String nama, String jurusan) {
        this.nim = nim;
        this.nama = nama;
        this.jurusan = jurusan;
    }

    // Getter dan Setter
    public String getNim() { return nim; }
    public void setNim(String nim) { this.nim = nim; }

    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }

    public String getJurusan() { return jurusan; }
    public void setJurusan(String jurusan) { this.jurusan = jurusan; }

    /**
     * Metode untuk membandingkan objek Mahasiswa berdasarkan NIM.
     * Digunakan oleh BST untuk menentukan posisi node.
     */
    @Override
    public int compareTo(Mahasiswa other) {
        return this.nim.compareTo(other.nim);
    }

    @Override
    public String toString() {
        return "NIM: " + nim + " | Nama: " + nama + " | Jurusan: " + jurusan;
    }

    /**
     * Metode pembantu untuk mengubah data Mahasiswa menjadi format baris CSV.
     */
    public String toCSVString() {
        return nim + "," + nama + "," + jurusan;
    }

    /**
     * Metode pembantu statis untuk membuat objek Mahasiswa dari baris CSV.
     */
    public static Mahasiswa fromCSVString(String csvLine) {
        String[] data = csvLine.split(",");
        if (data.length == 3) {
            return new Mahasiswa(data[0], data[1], data[2]);
        }
        return null;
    }
}