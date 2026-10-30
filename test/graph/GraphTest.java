package graph;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test untuk kelas Graph<V> (undirected graph berbasis adjacency list).
 */
class GraphTest {

    private Graph<String> graph;

    @BeforeEach
    void setUp() {
        graph = new Graph<>();
    }

    @Test
    @DisplayName("Menambahkan vertex baru tercatat di dalam graph")
    void testAddVertex() {
        graph.addVertex("A");
        assertTrue(graph.containsVertex("A"));
        assertEquals(1, graph.size());
    }

    @Test
    @DisplayName("Menambahkan vertex yang sama dua kali tidak membuat duplikat")
    void testAddDuplicateVertex() {
        graph.addVertex("A");
        graph.addVertex("A");
        assertEquals(1, graph.size());
    }

    @Test
    @DisplayName("Menambahkan edge membuat kedua vertex saling menjadi tetangga (tidak berarah)")
    void testAddEdgeIsUndirected() {
        graph.addEdge("A", "B");

        assertTrue(graph.hasEdge("A", "B"));
        assertTrue(graph.hasEdge("B", "A"));
        assertTrue(graph.getNeighbors("A").contains("B"));
        assertTrue(graph.getNeighbors("B").contains("A"));
    }

    @Test
    @DisplayName("Menambahkan edge otomatis menambahkan vertex yang belum ada")
    void testAddEdgeAutoAddsVertices() {
        graph.addEdge("A", "B");
        assertEquals(2, graph.size());
        assertTrue(graph.containsVertex("A"));
        assertTrue(graph.containsVertex("B"));
    }

    @Test
    @DisplayName("Menambahkan edge yang sama dua kali tidak membuat duplikat pada daftar tetangga")
    void testAddDuplicateEdge() {
        graph.addEdge("A", "B");
        graph.addEdge("A", "B");

        assertEquals(1, graph.getNeighbors("A").size());
        assertEquals(1, graph.getNeighbors("B").size());
    }

    @Test
    @DisplayName("Menghapus edge menghilangkan hubungan di kedua arah tanpa menghapus vertex")
    void testRemoveEdge() {
        graph.addEdge("A", "B");
        graph.removeEdge("A", "B");

        assertFalse(graph.hasEdge("A", "B"));
        assertFalse(graph.hasEdge("B", "A"));
        assertTrue(graph.containsVertex("A"));
        assertTrue(graph.containsVertex("B"));
    }

    @Test
    @DisplayName("Menghapus vertex juga menghapus seluruh edge yang terhubung dengannya")
    void testRemoveVertex() {
        graph.addEdge("A", "B");
        graph.addEdge("A", "C");

        boolean removed = graph.removeVertex("A");

        assertTrue(removed);
        assertFalse(graph.containsVertex("A"));
        assertFalse(graph.getNeighbors("B").contains("A"));
        assertFalse(graph.getNeighbors("C").contains("A"));
    }

    @Test
    @DisplayName("Menghapus vertex yang tidak ada mengembalikan false")
    void testRemoveNonExistentVertex() {
        assertFalse(graph.removeVertex("TIDAK_ADA"));
    }

    @Test
    @DisplayName("getNeighbors pada vertex yang tidak ada mengembalikan list kosong")
    void testGetNeighborsOfNonExistentVertex() {
        List<String> neighbors = graph.getNeighbors("TIDAK_ADA");
        assertNotNull(neighbors);
        assertTrue(neighbors.isEmpty());
    }

    @Test
    @DisplayName("BFS mengunjungi semua vertex yang terhubung sesuai urutan lebar")
    void testBfsTraversal() {
        // Graph: A - B - D
        //         \
        //          C
        graph.addEdge("A", "B");
        graph.addEdge("A", "C");
        graph.addEdge("B", "D");

        List<String> hasil = graph.bfs("A");

        assertEquals(4, hasil.size());
        assertEquals("A", hasil.get(0)); // vertex awal selalu pertama
        assertTrue(hasil.contains("B"));
        assertTrue(hasil.contains("C"));
        assertTrue(hasil.contains("D"));
        // D harus dikunjungi setelah B karena D adalah tetangga dari B, bukan A
        assertTrue(hasil.indexOf("D") > hasil.indexOf("B"));
    }

    @Test
    @DisplayName("DFS mengunjungi semua vertex yang terhubung")
    void testDfsTraversal() {
        graph.addEdge("A", "B");
        graph.addEdge("A", "C");
        graph.addEdge("B", "D");

        List<String> hasil = graph.dfs("A");

        assertEquals(4, hasil.size());
        assertEquals("A", hasil.get(0));
        assertTrue(hasil.contains("B"));
        assertTrue(hasil.contains("C"));
        assertTrue(hasil.contains("D"));
    }

    @Test
    @DisplayName("BFS dan DFS dari vertex yang tidak ada mengembalikan list kosong")
    void testTraversalFromNonExistentVertex() {
        graph.addVertex("A");
        assertTrue(graph.bfs("TIDAK_ADA").isEmpty());
        assertTrue(graph.dfs("TIDAK_ADA").isEmpty());
    }

    @Test
    @DisplayName("BFS/DFS tidak terjebak infinite loop pada graph yang memiliki siklus")
    void testTraversalHandlesCycleWithoutInfiniteLoop() {
        // Siklus: A - B - C - A
        graph.addEdge("A", "B");
        graph.addEdge("B", "C");
        graph.addEdge("C", "A");

        List<String> hasilBfs = graph.bfs("A");
        List<String> hasilDfs = graph.dfs("A");

        assertEquals(3, hasilBfs.size());
        assertEquals(3, hasilDfs.size());
    }

    @Test
    @DisplayName("Vertex null pada operasi utama melempar IllegalArgumentException")
    void testNullVertexThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> graph.addVertex(null));
        assertThrows(IllegalArgumentException.class, () -> graph.addEdge(null, "A"));
        assertThrows(IllegalArgumentException.class, () -> graph.addEdge("A", null));
        assertThrows(IllegalArgumentException.class, () -> graph.getNeighbors(null));
        assertThrows(IllegalArgumentException.class, () -> graph.containsVertex(null));
    }

    @Test
    @DisplayName("isEmpty mengembalikan status graph dengan benar")
    void testIsEmpty() {
        assertTrue(graph.isEmpty());
        graph.addVertex("A");
        assertFalse(graph.isEmpty());
    }
}
