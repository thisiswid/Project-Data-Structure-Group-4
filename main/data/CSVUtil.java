package util;

import model.Mahasiswa;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Utilitas untuk membaca dan menyimpan data Mahasiswa ke file CSV.
 */
public class CSVUtil {

    /**
     * Menyimpan daftar mahasiswa ke dalam file CSV.
     */
    public static void simpanKeCSV(String filePath, List<Mahasiswa> daftarMahasiswa) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            // Tulis Header (opsional)
            // writer.println("nim,nama,jurusan");
            
            for (Mahasiswa m : daftarMahasiswa) {
                writer.println(m.toCSVString());
            }
            System.out.println("Data berhasil disimpan ke CSV: " + filePath);
        } catch (IOException e) {
            System.err.println("Gagal menyimpan ke CSV: " + e.getMessage());
        }
    }

    /**
     * Membaca data mahasiswa dari file CSV.
     */
    public static List<Mahasiswa> bacaDariCSV(String filePath) {
        List<Mahasiswa> daftar = new ArrayList<>();
        File file = new File(filePath);
        
        if (!file.exists()) {
            return daftar;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Mahasiswa m = Mahasiswa.fromCSVString(line);
                if (m != null) {
                    daftar.add(m);
                }
            }
            System.out.println("Berhasil membaca " + daftar.size() + " data dari CSV.");
        } catch (IOException e) {
            System.err.println("Gagal membaca dari CSV: " + e.getMessage());
        }
        return daftar;
    }
}