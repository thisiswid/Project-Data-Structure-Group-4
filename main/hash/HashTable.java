package hash;

import java.util.HashMap;
import java.util.Map;

/**
 * HashTable<K, V> adalah implementasi struktur data Hash Table untuk
 * Manajemen Data Mahasiswa.
 *
 * CATATAN REVISI: sesuai aturan tugas yang telah direvisi oleh dosen, Hash
 * Table ini memakai pustaka java.util.HashMap sebagai mesin penyimpanan
 * utama (bukan lagi array bucket dan Linked List buatan sendiri seperti versi
 * sebelumnya). Kelas ini berfungsi sebagai pembungkus (wrapper) tipis di atas
 * HashMap agar seluruh kode lain (Main, MahasiswaService, dan lain-lain)
 * tetap memakai tipe HashTable<K, V> yang konsisten dengan nama paket
 * "hash", tanpa perlu bergantung langsung pada java.util.HashMap di
 * tempat lain.
 *
 * Kelas ini generic sehingga bisa dipakai untuk pasangan key-value apa saja,
 * misalnya HashTable<String, Mahasiswa> dengan NIM sebagai key.
 *
 * Kompleksitas waktu (mengikuti karakteristik java.util.HashMap):
 * - put(key, value)   : O(1) rata-rata, O(n) terburuk pada kasus hash yang
 *                        sangat buruk.
 * - get(key)          : O(1) rata-rata, O(n) terburuk.
 * - remove(key)        : O(1) rata-rata, O(n) terburuk.
 *
 * Catatan: karena penyimpanan internal (array bucket, resize, penghitungan
 * collision) sepenuhnya ditangani secara privat oleh java.util.HashMap dan
 * tidak diekspos lewat API publik-nya, method seperti getCapacity(),
 * getLoadFactor(), dan getCollisionCount() dari versi buatan sendiri
 * sebelumnya dihapus karena sudah tidak relevan.
 *
 * @param <K> tipe data key
 * @param <V> tipe data value
 */
public class HashTable<K, V> {

    /** Map internal yang menjadi mesin penyimpanan utama Hash Table ini. */
    private final Map<K, V> map;

    /**
     * Membuat HashTable baru yang kosong, didukung oleh java.util.HashMap
     * dengan konfigurasi default (kapasitas awal 16, load factor 0.75).
     */
    public HashTable() {
        this.map = new HashMap<>();
    }

    /**
     * Memvalidasi bahwa key tidak null. Key null tidak diperbolehkan agar
     * perilaku validasi tetap konsisten dengan versi sebelumnya dan dengan
     * kebutuhan NIM mahasiswa yang tidak boleh kosong.
     *
     * @param key key yang akan divalidasi
     */
    private void validateKey(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key tidak boleh null");
        }
    }

    /**
     * Menyisipkan atau memperbarui pasangan key-value ke dalam Hash Table.
     * Jika key sudah ada sebelumnya, value lama akan ditimpa dengan value
     * baru (perilaku standar HashMap.put).
     *
     * @param key   key yang akan disimpan, tidak boleh null
     * @param value value yang akan disimpan, tidak boleh null
     */
    public void put(K key, V value) {
        validateKey(key);
        if (value == null) {
            throw new IllegalArgumentException("Value tidak boleh null");
        }
        map.put(key, value);
    }

    /**
     * Mengambil value berdasarkan key.
     *
     * @param key key yang dicari, tidak boleh null
     * @return value yang terkait dengan key, atau null jika key tidak ditemukan
     */
    public V get(K key) {
        validateKey(key);
        return map.get(key);
    }

    /**
     * Menghapus pasangan key-value berdasarkan key.
     *
     * @param key key yang akan dihapus, tidak boleh null
     * @return value yang terhapus, atau null jika key tidak ditemukan
     */
    public V remove(K key) {
        validateKey(key);
        return map.remove(key);
    }

    /**
     * Memeriksa apakah suatu key ada di dalam Hash Table.
     *
     * @param key key yang dicari, tidak boleh null
     * @return true jika key ditemukan, false jika tidak
     */
    public boolean containsKey(K key) {
        validateKey(key);
        return map.containsKey(key);
    }

    /**
     * Mengembalikan jumlah pasangan key-value yang tersimpan saat ini.
     *
     * @return jumlah elemen
     */
    public int size() {
        return map.size();
    }

    /**
     * Memeriksa apakah Hash Table kosong.
     *
     * @return true jika tidak ada elemen tersimpan
     */
    public boolean isEmpty() {
        return map.isEmpty();
    }

    /**
     * Mengosongkan seluruh isi Hash Table.
     */
    public void clear() {
        map.clear();
    }
}
