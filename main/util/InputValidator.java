package util;

/** Validasi input untuk data Mahasiswa. */
public class InputValidator {

    private InputValidator() {}

    /** NIM valid: tepat 10 digit angka. */
    public static boolean isValidNim(String nim) {
        return nim != null && nim.matches("\\d{10}");
    }

    /** Nama valid: tidak kosong, hanya huruf/spasi/titik/apostrof/tanda hubung, maksimal 100 karakter. */
    public static boolean isValidNama(String nama) {
        return nama != null && !nama.trim().isEmpty() && nama.length() <= 100
                && nama.matches("[\\p{L} .'-]+");
    }
}

