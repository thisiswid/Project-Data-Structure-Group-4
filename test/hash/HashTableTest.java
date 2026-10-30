package hash;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test untuk kelas HashTable<K, V>.
 *
 * CATATAN REVISI: HashTable sekarang dibungkus di atas java.util.HashMap,
 * sehingga skenario yang sebelumnya menguji detail implementasi manual
 * (collision counter, load factor, resize/rehash) sudah tidak relevan dan
 * dihapus. Pengujian difokuskan pada kebenaran operasi CRUD dan validasi
 * input, yang merupakan kontrak publik dari kelas ini.
 */
class HashTableTest {

    private HashTable<String, String> table;

    @BeforeEach
    void setUp() {
        table = new HashTable<>();
    }

    @Test
    @DisplayName("Menambahkan satu data dapat diambil kembali")
    void testPutSingleData() {
        table.put("A001", "Budi");
        assertEquals(1, table.size());
        assertEquals("Budi", table.get("A001"));
    }

    @Test
    @DisplayName("Menambahkan banyak data tersimpan dengan benar")
    void testPutManyData() {
        for (int i = 0; i < 50; i++) {
            table.put("NIM" + i, "Mahasiswa" + i);
        }
        assertEquals(50, table.size());
        for (int i = 0; i < 50; i++) {
            assertEquals("Mahasiswa" + i, table.get("NIM" + i));
        }
    }

    @Test
    @DisplayName("Mengambil data berdasarkan key yang benar")
    void testGetByKey() {
        table.put("A001", "Budi");
        table.put("A002", "Siti");
        assertEquals("Budi", table.get("A001"));
        assertEquals("Siti", table.get("A002"));
    }

    @Test
    @DisplayName("Menghapus data mengembalikan value dan mengurangi size")
    void testRemove() {
        table.put("A001", "Budi");
        table.put("A002", "Siti");

        String removed = table.remove("A001");

        assertEquals("Budi", removed);
        assertEquals(1, table.size());
        assertNull(table.get("A001"));
        assertEquals("Siti", table.get("A002"));
    }

    @Test
    @DisplayName("Mengambil key yang tidak ditemukan mengembalikan null")
    void testGetKeyNotFound() {
        table.put("A001", "Budi");
        assertNull(table.get("TIDAK_ADA"));
    }

    @Test
    @DisplayName("Remove key yang tidak ditemukan mengembalikan null dan tidak mengubah size")
    void testRemoveKeyNotFound() {
        table.put("A001", "Budi");
        assertNull(table.remove("TIDAK_ADA"));
        assertEquals(1, table.size());
    }

    @Test
    @DisplayName("Memperbarui value dengan key yang sama tidak menambah size")
    void testUpdateExistingKey() {
        table.put("A001", "Budi");
        table.put("A001", "Budi Santoso");

        assertEquals(1, table.size());
        assertEquals("Budi Santoso", table.get("A001"));
    }

    /**
     * Key dengan hashCode() yang sengaja dibuat sama. Dengan HashMap sebagai
     * mesin internal, penanganan collision sepenuhnya didelegasikan ke
     * implementasi HashMap bawaan Java, tetapi perilaku dari sisi pengguna
     * (tidak ada data yang hilang atau saling menimpa) tetap harus benar.
     */
    private static class FixedHashKey {
        private final String id;

        FixedHashKey(String id) {
            this.id = id;
        }

        @Override
        public int hashCode() {
            return 42; // sengaja selalu sama agar jatuh ke bucket internal yang sama
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof FixedHashKey)) return false;
            return this.id.equals(((FixedHashKey) obj).id);
        }
    }

    @Test
    @DisplayName("Key dengan hashCode sama tetap tersimpan dan terbaca dengan benar tanpa saling menimpa")
    void testKeysWithSameHashCodeDoNotOverwriteEachOther() {
        HashTable<FixedHashKey, String> customTable = new HashTable<>();

        FixedHashKey key1 = new FixedHashKey("K1");
        FixedHashKey key2 = new FixedHashKey("K2");
        FixedHashKey key3 = new FixedHashKey("K3");

        customTable.put(key1, "Data1");
        customTable.put(key2, "Data2");
        customTable.put(key3, "Data3");

        assertEquals(3, customTable.size());
        assertEquals("Data1", customTable.get(key1));
        assertEquals("Data2", customTable.get(key2));
        assertEquals("Data3", customTable.get(key3));
    }

    @Test
    @DisplayName("Seluruh data tetap tersedia meskipun jumlah data besar (memicu resize internal HashMap)")
    void testDataIntactWithLargeDataset() {
        for (int i = 0; i < 1000; i++) {
            table.put("Key" + i, "Value" + i);
        }

        assertEquals(1000, table.size());
        for (int i = 0; i < 1000; i++) {
            assertEquals("Value" + i, table.get("Key" + i));
        }
    }

    @Test
    @DisplayName("put dengan key null melempar IllegalArgumentException")
    void testPutNullKeyThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> table.put(null, "value"));
    }

    @Test
    @DisplayName("get dengan key null melempar IllegalArgumentException")
    void testGetNullKeyThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> table.get(null));
    }

    @Test
    @DisplayName("remove dengan key null melempar IllegalArgumentException")
    void testRemoveNullKeyThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> table.remove(null));
    }

    @Test
    @DisplayName("put dengan value null melempar IllegalArgumentException")
    void testPutNullValueThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> table.put("A001", null));
    }

    @Test
    @DisplayName("containsKey mengembalikan status keberadaan key dengan benar")
    void testContainsKey() {
        table.put("A001", "Budi");
        assertTrue(table.containsKey("A001"));
        assertFalse(table.containsKey("TIDAK_ADA"));
    }

    @Test
    @DisplayName("isEmpty dan clear bekerja dengan benar")
    void testIsEmptyAndClear() {
        assertTrue(table.isEmpty());

        table.put("A001", "Budi");
        assertFalse(table.isEmpty());

        table.clear();
        assertTrue(table.isEmpty());
        assertEquals(0, table.size());
        assertNull(table.get("A001"));
    }
}
