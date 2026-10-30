package tree;

import java.util.ArrayList;
import java.util.List;

/**
 * BST (Binary Search Tree) buatan sendiri untuk Manajemen Data Mahasiswa.
 *
 * Setiap node menyimpan sepasang key-value. Node di subtree kiri selalu
 * memiliki key lebih kecil dari node induknya, sedangkan node di subtree
 * kanan memiliki key lebih besar. Karena itu traversal inorder selalu
 * menghasilkan data terurut menaik berdasarkan key, hal yang tidak bisa
 * dilakukan oleh Hash Table. Di aplikasi ini BST dipakai sebagai indeks
 * sekunder untuk menampilkan daftar mahasiswa terurut (misalnya berdasarkan
 * nama atau IPK), sedangkan pencarian berdasarkan NIM ditangani Hash Table.
 *
 * Semua operasi utama (insert, search, delete) dan traversal ditulis
 * secara rekursif.
 *
 * Catatan penting: setiap key harus unik. Jika insert dipanggil dengan key
 * yang sudah ada, value lama akan diganti (bukan ditambah). Untuk indeks
 * berdasarkan nama atau IPK yang bisa kembar, gunakan key gabungan yang unik
 * (misalnya "nama|NIM") agar tidak ada data yang tertimpa.
 *
 * Kompleksitas waktu (h = tinggi tree):
 * - insert, search, delete : O(h). Tree seimbang h = O(log n), tree miring
 *                            (data dimasukkan sudah terurut) h = O(n).
 * - traversal              : O(n).
 * - min, max, height       : O(h), O(h), O(n).
 * Kompleksitas ruang: O(n) untuk node, ditambah O(h) tumpukan rekursi.
 *
 * Catatan: BST ini tidak menyeimbangkan diri sendiri (bukan AVL atau
 * Red-Black Tree), sehingga performanya bergantung pada urutan data masuk.
 *
 * @param <K> tipe data key, harus Comparable
 * @param <V> tipe data value
 */
public class BST<K extends Comparable<K>, V> {

    /** Node pada tree: menyimpan key, value, serta anak kiri dan kanan. */
    private static class Node<K, V> {
        K key;
        V value;
        Node<K, V> left;
        Node<K, V> right;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    /** Akar (root) tree; null jika tree kosong. */
    private Node<K, V> root;

    /** Jumlah node yang tersimpan saat ini. */
    private int size;

    /** Membuat BST kosong. */
    public BST() {
        this.root = null;
        this.size = 0;
    }

    /** Memastikan key tidak null agar perbandingan compareTo aman. */
    private void validateKey(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key tidak boleh null");
        }
    }

    // ================= Insert =================

    /**
     * Menyisipkan pasangan key-value. Jika key sudah ada, value lama diganti
     * dengan value baru dan jumlah elemen tidak bertambah.
     *
     * @param key   key yang disisipkan, tidak boleh null
     * @param value value yang disimpan, tidak boleh null
     */
    public void insert(K key, V value) {
        validateKey(key);
        if (value == null) {
            throw new IllegalArgumentException("Value tidak boleh null");
        }
        root = insert(root, key, value);
    }

    /** Insert rekursif: turun ke kiri atau kanan sampai menemukan tempat kosong. */
    private Node<K, V> insert(Node<K, V> node, K key, V value) {
        if (node == null) {
            size++;
            return new Node<>(key, value);
        }
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = insert(node.left, key, value);
        } else if (cmp > 0) {
            node.right = insert(node.right, key, value);
        } else {
            node.value = value; // key sama: perbarui value
        }
        return node;
    }

    // ================= Search =================

    /**
     * Mencari value berdasarkan key.
     *
     * @param key key yang dicari, tidak boleh null
     * @return value yang terkait, atau null jika key tidak ditemukan
     */
    public V search(K key) {
        validateKey(key);
        Node<K, V> node = search(root, key);
        return node == null ? null : node.value;
    }

    /** Search rekursif: bandingkan key, lalu lanjut ke kiri atau kanan. */
    private Node<K, V> search(Node<K, V> node, K key) {
        if (node == null) {
            return null;
        }
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            return search(node.left, key);
        } else if (cmp > 0) {
            return search(node.right, key);
        }
        return node;
    }

    /**
     * Memeriksa apakah key ada di dalam tree.
     *
     * @param key key yang dicari, tidak boleh null
     * @return true jika ditemukan
     */
    public boolean contains(K key) {
        validateKey(key);
        return search(root, key) != null;
    }

    // ================= Delete =================

    /**
     * Menghapus node berdasarkan key.
     *
     * @param key key yang dihapus, tidak boleh null
     * @return value yang terhapus, atau null jika key tidak ditemukan
     */
    public V delete(K key) {
        validateKey(key);
        Node<K, V> target = search(root, key);
        if (target == null) {
            return null;
        }
        V removed = target.value;
        root = delete(root, key);
        size--;
        return removed;
    }

    /**
     * Delete rekursif dengan tiga kasus:
     * 1) node daun: langsung dihapus,
     * 2) node punya satu anak: digantikan oleh anaknya,
     * 3) node punya dua anak: diganti dengan successor (node terkecil di
     *    subtree kanan), lalu successor dihapus dari subtree kanan.
     */
    private Node<K, V> delete(Node<K, V> node, K key) {
        if (node == null) {
            return null;
        }
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = delete(node.left, key);
        } else if (cmp > 0) {
            node.right = delete(node.right, key);
        } else {
            if (node.left == null) {
                return node.right;
            }
            if (node.right == null) {
                return node.left;
            }
            Node<K, V> successor = minNode(node.right);
            node.key = successor.key;
            node.value = successor.value;
            node.right = delete(node.right, successor.key);
        }
        return node;
    }

    // ================= Min, Max, Height =================

    /** Mencari node dengan key terkecil pada subtree (selalu ke kiri). */
    private Node<K, V> minNode(Node<K, V> node) {
        return node.left == null ? node : minNode(node.left);
    }

    /** Mencari node dengan key terbesar pada subtree (selalu ke kanan). */
    private Node<K, V> maxNode(Node<K, V> node) {
        return node.right == null ? node : maxNode(node.right);
    }

    /**
     * Mengembalikan key terkecil.
     *
     * @return key terkecil, atau null jika tree kosong
     */
    public K minKey() {
        return root == null ? null : minNode(root).key;
    }

    /**
     * Mengembalikan key terbesar.
     *
     * @return key terbesar, atau null jika tree kosong
     */
    public K maxKey() {
        return root == null ? null : maxNode(root).key;
    }

    /**
     * Mengembalikan tinggi tree (jumlah node pada jalur terpanjang dari root).
     * Tree kosong bertinggi 0, tree satu node bertinggi 1.
     *
     * @return tinggi tree
     */
    public int height() {
        return height(root);
    }

    private int height(Node<K, V> node) {
        if (node == null) {
            return 0;
        }
        return 1 + Math.max(height(node.left), height(node.right));
    }

    // ================= Traversal =================

    /**
     * Traversal inorder (kiri, node, kanan): hasil terurut menaik menurut key.
     *
     * @return daftar value terurut menurut key
     */
    public List<V> inOrder() {
        List<V> result = new ArrayList<>();
        inOrder(root, result);
        return result;
    }

    private void inOrder(Node<K, V> node, List<V> out) {
        if (node == null) {
            return;
        }
        inOrder(node.left, out);
        out.add(node.value);
        inOrder(node.right, out);
    }

    /**
     * Traversal inorder yang mengembalikan key-nya (terurut menaik).
     *
     * @return daftar key terurut menaik
     */
    public List<K> inOrderKeys() {
        List<K> result = new ArrayList<>();
        inOrderKeys(root, result);
        return result;
    }

    private void inOrderKeys(Node<K, V> node, List<K> out) {
        if (node == null) {
            return;
        }
        inOrderKeys(node.left, out);
        out.add(node.key);
        inOrderKeys(node.right, out);
    }

    /**
     * Traversal preorder (node, kiri, kanan). Berguna untuk menyalin
     * struktur tree.
     *
     * @return daftar value menurut urutan preorder
     */
    public List<V> preOrder() {
        List<V> result = new ArrayList<>();
        preOrder(root, result);
        return result;
    }

    private void preOrder(Node<K, V> node, List<V> out) {
        if (node == null) {
            return;
        }
        out.add(node.value);
        preOrder(node.left, out);
        preOrder(node.right, out);
    }

    /**
     * Traversal postorder (kiri, kanan, node). Berguna untuk menghapus
     * tree dari anak ke induk.
     *
     * @return daftar value menurut urutan postorder
     */
    public List<V> postOrder() {
        List<V> result = new ArrayList<>();
        postOrder(root, result);
        return result;
    }

    private void postOrder(Node<K, V> node, List<V> out) {
        if (node == null) {
            return;
        }
        postOrder(node.left, out);
        postOrder(node.right, out);
        out.add(node.value);
    }

    /**
     * Mencetak seluruh data terurut (inorder) ke konsol, satu baris per data
     * dengan format "key -> value".
     */
    public void printInOrder() {
        if (root == null) {
            System.out.println("(tree kosong)");
            return;
        }
        printInOrder(root);
    }

    private void printInOrder(Node<K, V> node) {
        if (node == null) {
            return;
        }
        printInOrder(node.left);
        System.out.println(node.key + " -> " + node.value);
        printInOrder(node.right);
    }

    // ================= Utilitas =================

    /**
     * Mengembalikan jumlah node yang tersimpan.
     *
     * @return jumlah elemen
     */
    public int size() {
        return size;
    }

    /**
     * Memeriksa apakah tree kosong.
     *
     * @return true jika tidak ada elemen
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /** Mengosongkan seluruh isi tree. */
    public void clear() {
        root = null;
        size = 0;
    }
}
