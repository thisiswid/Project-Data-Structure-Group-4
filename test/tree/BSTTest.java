package tree;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test untuk kelas BST<K, V> (Binary Search Tree buatan sendiri).
 */
class BSTTest {

    private BST<Integer, String> bst;

    /** Tree contoh: 50 sebagai root; 30, 70; 20, 40, 60, 80. */
    @BeforeEach
    void setUp() {
        bst = new BST<>();
        for (int k : new int[]{50, 30, 70, 20, 40, 60, 80}) {
            bst.insert(k, "v" + k);
        }
    }

    @Test
    @DisplayName("Tree baru kosong")
    void testEmptyTree() {
        BST<Integer, String> empty = new BST<>();
        assertTrue(empty.isEmpty());
        assertEquals(0, empty.size());
        assertEquals(0, empty.height());
        assertNull(empty.minKey());
        assertNull(empty.maxKey());
        assertTrue(empty.inOrder().isEmpty());
    }

    @Test
    @DisplayName("Insert menambah jumlah elemen")
    void testInsert() {
        assertEquals(7, bst.size());
        assertFalse(bst.isEmpty());
    }

    @Test
    @DisplayName("Insert key yang sudah ada mengganti value tanpa menambah elemen")
    void testInsertDuplicateKeyReplacesValue() {
        bst.insert(40, "baru");
        assertEquals(7, bst.size());
        assertEquals("baru", bst.search(40));
    }

    @Test
    @DisplayName("Search menemukan key yang ada dan mengembalikan null jika tidak ada")
    void testSearch() {
        assertEquals("v60", bst.search(60));
        assertNull(bst.search(99));
        assertTrue(bst.contains(20));
        assertFalse(bst.contains(25));
    }

    @Test
    @DisplayName("Traversal inorder menghasilkan data terurut menaik")
    void testInOrder() {
        assertEquals(Arrays.asList(20, 30, 40, 50, 60, 70, 80), bst.inOrderKeys());
        assertEquals(Arrays.asList("v20", "v30", "v40", "v50", "v60", "v70", "v80"), bst.inOrder());
    }

    @Test
    @DisplayName("Traversal preorder: node, kiri, kanan")
    void testPreOrder() {
        List<String> expected = Arrays.asList("v50", "v30", "v20", "v40", "v70", "v60", "v80");
        assertEquals(expected, bst.preOrder());
    }

    @Test
    @DisplayName("Traversal postorder: kiri, kanan, node")
    void testPostOrder() {
        List<String> expected = Arrays.asList("v20", "v40", "v30", "v60", "v80", "v70", "v50");
        assertEquals(expected, bst.postOrder());
    }

    @Test
    @DisplayName("Delete node daun")
    void testDeleteLeaf() {
        assertEquals("v20", bst.delete(20));
        assertEquals(6, bst.size());
        assertFalse(bst.contains(20));
        assertEquals(Arrays.asList(30, 40, 50, 60, 70, 80), bst.inOrderKeys());
    }

    @Test
    @DisplayName("Delete node dengan satu anak")
    void testDeleteNodeWithOneChild() {
        bst.delete(20); // 30 kini hanya punya anak kanan (40)
        assertEquals("v30", bst.delete(30));
        assertEquals(Arrays.asList(40, 50, 60, 70, 80), bst.inOrderKeys());
    }

    @Test
    @DisplayName("Delete node dengan dua anak memakai successor")
    void testDeleteNodeWithTwoChildren() {
        assertEquals("v70", bst.delete(70));
        assertEquals(Arrays.asList(20, 30, 40, 50, 60, 80), bst.inOrderKeys());
        assertEquals(6, bst.size());
    }

    @Test
    @DisplayName("Delete root")
    void testDeleteRoot() {
        assertEquals("v50", bst.delete(50));
        assertEquals(Arrays.asList(20, 30, 40, 60, 70, 80), bst.inOrderKeys());
        assertFalse(bst.contains(50));
    }

    @Test
    @DisplayName("Delete key yang tidak ada mengembalikan null dan tidak mengubah tree")
    void testDeleteMissingKey() {
        assertNull(bst.delete(99));
        assertEquals(7, bst.size());
    }

    @Test
    @DisplayName("minKey, maxKey, dan height")
    void testMinMaxHeight() {
        assertEquals(20, bst.minKey());
        assertEquals(80, bst.maxKey());
        assertEquals(3, bst.height());
    }

    @Test
    @DisplayName("Data masuk terurut membuat tree miring (tinggi = jumlah data)")
    void testSkewedTreeHeight() {
        BST<Integer, String> skewed = new BST<>();
        for (int i = 1; i <= 10; i++) {
            skewed.insert(i, "v" + i);
        }
        assertEquals(10, skewed.height());
    }

    @Test
    @DisplayName("Key bertipe String terurut alfabetis")
    void testStringKeys() {
        BST<String, Integer> names = new BST<>();
        names.insert("Budi", 1);
        names.insert("Andi", 2);
        names.insert("Citra", 3);
        assertEquals(Arrays.asList("Andi", "Budi", "Citra"), names.inOrderKeys());
    }

    @Test
    @DisplayName("Key atau value null ditolak")
    void testNullRejected() {
        assertThrows(IllegalArgumentException.class, () -> bst.insert(null, "x"));
        assertThrows(IllegalArgumentException.class, () -> bst.insert(1, null));
        assertThrows(IllegalArgumentException.class, () -> bst.search(null));
        assertThrows(IllegalArgumentException.class, () -> bst.delete(null));
    }

    @Test
    @DisplayName("Clear mengosongkan tree")
    void testClear() {
        bst.clear();
        assertTrue(bst.isEmpty());
        assertEquals(0, bst.size());
        assertNull(bst.search(50));
    }
}
