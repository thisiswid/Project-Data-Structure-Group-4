package graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * Graph<V> adalah implementasi struktur data Graph tidak berarah (undirected
 * graph) menggunakan adjacency list.
 *
 * CATATAN: sesuai aturan tugas yang telah direvisi oleh dosen, adjacency
 * list dibangun menggunakan pustaka java.util, yaitu HashMap<V, List<V>>
 * sebagai peta dari setiap vertex ke daftar tetangganya, dan ArrayList
 * sebagai implementasi List untuk daftar tetangga tersebut.
 *
 * Setiap sisi (edge) bersifat dua arah: menambahkan edge antara A dan B akan
 * membuat B tercatat sebagai tetangga A, dan A tercatat sebagai tetangga B.
 *
 * Kelas ini generic sehingga vertex bisa bertipe apa saja, misalnya String
 * (nama/NIM mahasiswa) atau tipe data lain yang mengimplementasikan
 * equals()/hashCode() dengan benar.
 *
 * Kompleksitas waktu:
 * - addVertex(v)        : O(1) rata-rata.
 * - addEdge(a, b)       : O(1) rata-rata (tidak menghitung biaya pencarian
 *                          duplikat pada ArrayList, yang O(derajat vertex)).
 * - getNeighbors(v)     : O(1) untuk mengambil referensi list.
 * - bfs(start)/dfs(start): O(V + E), V = jumlah vertex, E = jumlah edge.
 *
 * @param <V> tipe data vertex
 */
public class Graph<V> {

    /** Adjacency list: setiap vertex dipetakan ke daftar tetangganya. */
    private final Map<V, List<V>> adjacencyList;

    /**
     * Membuat Graph baru yang kosong (tanpa vertex maupun edge).
     */
    public Graph() {
        this.adjacencyList = new HashMap<>();
    }

    /**
     * Memvalidasi bahwa vertex tidak null.
     *
     * @param vertex vertex yang akan divalidasi
     */
    private void validateVertex(V vertex) {
        if (vertex == null) {
            throw new IllegalArgumentException("Vertex tidak boleh null");
        }
    }

    /**
     * Menambahkan vertex baru ke dalam graph. Jika vertex sudah ada
     * sebelumnya, method ini tidak melakukan apa pun (tidak menimpa daftar
     * tetangga yang sudah ada).
     *
     * @param vertex vertex yang akan ditambahkan, tidak boleh null
     */
    public void addVertex(V vertex) {
        validateVertex(vertex);
        adjacencyList.putIfAbsent(vertex, new ArrayList<>());
    }

    /**
     * Menambahkan sisi (edge) tidak berarah antara dua vertex. Jika salah
     * satu atau kedua vertex belum terdaftar, keduanya akan otomatis
     * ditambahkan sebagai vertex baru terlebih dahulu.
     *
     * Edge ganda (menambahkan edge yang sama dua kali) tidak akan membuat
     * duplikat di daftar tetangga.
     *
     * @param vertexA vertex pertama, tidak boleh null
     * @param vertexB vertex kedua, tidak boleh null
     */
    public void addEdge(V vertexA, V vertexB) {
        validateVertex(vertexA);
        validateVertex(vertexB);

        addVertex(vertexA);
        addVertex(vertexB);

        List<V> neighborsOfA = adjacencyList.get(vertexA);
        if (!neighborsOfA.contains(vertexB)) {
            neighborsOfA.add(vertexB);
        }

        // Karena graph tidak berarah, sisi juga dicatat dari arah sebaliknya.
        // Pengecualian: jika vertexA sama dengan vertexB (self-loop), jangan
        // menambahkan tetangga dua kali.
        if (!vertexA.equals(vertexB)) {
            List<V> neighborsOfB = adjacencyList.get(vertexB);
            if (!neighborsOfB.contains(vertexA)) {
                neighborsOfB.add(vertexA);
            }
        }
    }

    /**
     * Menghapus sisi (edge) antara dua vertex, jika ada. Vertex itu sendiri
     * tidak dihapus dari graph.
     *
     * @param vertexA vertex pertama, tidak boleh null
     * @param vertexB vertex kedua, tidak boleh null
     */
    public void removeEdge(V vertexA, V vertexB) {
        validateVertex(vertexA);
        validateVertex(vertexB);

        List<V> neighborsOfA = adjacencyList.get(vertexA);
        if (neighborsOfA != null) {
            neighborsOfA.remove(vertexB);
        }

        List<V> neighborsOfB = adjacencyList.get(vertexB);
        if (neighborsOfB != null) {
            neighborsOfB.remove(vertexA);
        }
    }

    /**
     * Menghapus vertex beserta seluruh sisi yang terhubung dengannya.
     *
     * @param vertex vertex yang akan dihapus, tidak boleh null
     * @return true jika vertex ditemukan dan berhasil dihapus, false jika tidak ada
     */
    public boolean removeVertex(V vertex) {
        validateVertex(vertex);

        List<V> neighbors = adjacencyList.remove(vertex);
        if (neighbors == null) {
            return false;
        }

        // Hapus referensi ke vertex ini dari daftar tetangga vertex lain.
        for (List<V> otherNeighbors : adjacencyList.values()) {
            otherNeighbors.remove(vertex);
        }
        return true;
    }

    /**
     * Mengembalikan daftar tetangga dari suatu vertex.
     *
     * @param vertex vertex yang dicari tetangganya, tidak boleh null
     * @return daftar tetangga, atau list kosong jika vertex tidak ditemukan
     */
    public List<V> getNeighbors(V vertex) {
        validateVertex(vertex);
        List<V> neighbors = adjacencyList.get(vertex);
        return neighbors == null ? new ArrayList<>() : neighbors;
    }

    /**
     * Memeriksa apakah suatu vertex ada di dalam graph.
     *
     * @param vertex vertex yang dicari, tidak boleh null
     * @return true jika vertex ditemukan, false jika tidak
     */
    public boolean containsVertex(V vertex) {
        validateVertex(vertex);
        return adjacencyList.containsKey(vertex);
    }

    /**
     * Memeriksa apakah ada sisi (edge) antara dua vertex.
     *
     * @param vertexA vertex pertama, tidak boleh null
     * @param vertexB vertex kedua, tidak boleh null
     * @return true jika kedua vertex terhubung langsung, false jika tidak
     */
    public boolean hasEdge(V vertexA, V vertexB) {
        validateVertex(vertexA);
        validateVertex(vertexB);
        List<V> neighbors = adjacencyList.get(vertexA);
        return neighbors != null && neighbors.contains(vertexB);
    }

    /**
     * Mengembalikan jumlah vertex yang ada di dalam graph.
     *
     * @return jumlah vertex
     */
    public int size() {
        return adjacencyList.size();
    }

    /**
     * Memeriksa apakah graph kosong (tidak memiliki vertex sama sekali).
     *
     * @return true jika graph kosong
     */
    public boolean isEmpty() {
        return adjacencyList.isEmpty();
    }

    /**
     * Melakukan traversal Breadth-First Search (BFS) mulai dari vertex awal
     * yang diberikan, menggunakan Queue (LinkedList) untuk antrean kunjungan
     * dan HashSet untuk menandai vertex yang sudah dikunjungi.
     *
     * @param start vertex awal, harus sudah terdaftar di dalam graph
     * @return daftar vertex sesuai urutan kunjungan BFS
     */
    public List<V> bfs(V start) {
        validateVertex(start);
        List<V> hasilUrutan = new ArrayList<>();
        if (!adjacencyList.containsKey(start)) {
            return hasilUrutan; // vertex awal tidak ditemukan, kembalikan list kosong
        }

        Set<V> sudahDikunjungi = new HashSet<>();
        Queue<V> antrean = new LinkedList<>();

        antrean.add(start);
        sudahDikunjungi.add(start);

        while (!antrean.isEmpty()) {
            V current = antrean.poll();
            hasilUrutan.add(current);

            for (V neighbor : adjacencyList.get(current)) {
                if (!sudahDikunjungi.contains(neighbor)) {
                    sudahDikunjungi.add(neighbor);
                    antrean.add(neighbor);
                }
            }
        }
        return hasilUrutan;
    }

    /**
     * Melakukan traversal Depth-First Search (DFS) mulai dari vertex awal
     * yang diberikan, secara rekursif.
     *
     * @param start vertex awal, harus sudah terdaftar di dalam graph
     * @return daftar vertex sesuai urutan kunjungan DFS
     */
    public List<V> dfs(V start) {
        validateVertex(start);
        List<V> hasilUrutan = new ArrayList<>();
        if (!adjacencyList.containsKey(start)) {
            return hasilUrutan; // vertex awal tidak ditemukan, kembalikan list kosong
        }

        Set<V> sudahDikunjungi = new HashSet<>();
        dfsRekursif(start, sudahDikunjungi, hasilUrutan);
        return hasilUrutan;
    }

    /**
     * Method bantu rekursif untuk DFS: mengunjungi vertex saat ini, lalu
     * mengunjungi setiap tetangga yang belum dikunjungi secara rekursif.
     */
    private void dfsRekursif(V current, Set<V> sudahDikunjungi, List<V> hasilUrutan) {
        sudahDikunjungi.add(current);
        hasilUrutan.add(current);

        for (V neighbor : adjacencyList.get(current)) {
            if (!sudahDikunjungi.contains(neighbor)) {
                dfsRekursif(neighbor, sudahDikunjungi, hasilUrutan);
            }
        }
    }
}
