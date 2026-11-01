// TUGAS PRAKTIKUM 3 - WEEK 10 - GROUP 4
// Algorithmic Analysis, Mathematical Induction, and Recursive Functions
// 1. AKHMAD FARUQ - NIM 2902820176
// 2. ALVITA KRIDAPRAKOSA - NIM 2602296406
// 3. CUCU CINTIA WIDIANI - NIM 2902806455
// 4. HASRIN CITRA UTAMI - NIM 2902816645
// 5. WIDIYA PRAMESTIKA - NIM 2902816304

import model.Mahasiswa;
import service.MahasiswaService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

/**
 * Main: titik masuk aplikasi Manajemen Data Mahasiswa.
 *
 * Menu konsol untuk operasi CRUD, tampilan terurut, sinkronisasi CSV, dan
 * pembangkit data dummy. Seluruh logika data ada di MahasiswaService, yang
 * memakai Hash Table (pencarian NIM) dan BST (daftar terurut).
 */
public class Main {

    private static final String FILE_CSV = "resources/data/mahasiswa.csv";
    private static final String[] JURUSAN = {
            "Computer Science", "Information Systems", "Cyber Security",
            "Mobile Application and Technology", "Game Application and Technology"
    };

    private static final MahasiswaService service = new MahasiswaService();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // Jalankan di thread dengan stack besar: BST bersifat rekursif sehingga
        // pada data yang sangat banyak kedalaman rekursinya bisa besar.
        Thread t = new Thread(null, Main::jalankan, "main-app", 64L * 1024 * 1024);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void jalankan() {
        System.out.println("=====================================================");
        System.out.println("   MANAJEMEN DATA MAHASISWA (Hash Table + BST)");
        System.out.println("=====================================================");

        boolean running = true;
        while (running) {
            tampilkanMenu();
            String pilihan = scanner.nextLine().trim();

            switch (pilihan) {
                case "1": tambah(); break;
                case "2": cari(); break;
                case "3": tampilkanTerurut(); break;
                case "4": ubah(); break;
                case "5": hapus(); break;
                case "6": service.muatDariCSV(FILE_CSV); System.out.println(); break;
                case "7": service.simpanKeCSV(FILE_CSV); System.out.println(); break;
                case "8": buatDummy(); break;
                case "9": statistik(); break;
                case "0":
                    running = false;
                    System.out.println("Keluar dari aplikasi. Sampai jumpa!");
                    break;
                default:
                    System.out.println("Pilihan tidak dikenali, silakan coba lagi.\n");
            }
        }

        scanner.close();
    }

    /** Menampilkan daftar menu yang tersedia kepada pengguna. */
    private static void tampilkanMenu() {
        System.out.println("--------------- MENU ---------------");
        System.out.println("1. Tambah mahasiswa");
        System.out.println("2. Cari mahasiswa berdasarkan NIM (Hash Table)");
        System.out.println("3. Tampilkan semua mahasiswa terurut NIM (BST inorder)");
        System.out.println("4. Ubah data mahasiswa");
        System.out.println("5. Hapus mahasiswa");
        System.out.println("6. Muat data dari CSV (" + FILE_CSV + ")");
        System.out.println("7. Simpan data ke CSV (" + FILE_CSV + ")");
        System.out.println("8. Buat data dummy");
        System.out.println("9. Statistik data");
        System.out.println("0. Keluar");
        System.out.print("> ");
    }

    private static String baca(String label) {
        System.out.print(label);
        return scanner.nextLine().trim();
    }

    private static void tambah() {
        String nim = baca("NIM     : ");
        String nama = baca("Nama    : ");
        String jurusan = baca("Jurusan : ");
        service.tambahMahasiswa(nim, nama, jurusan);
        System.out.println();
    }

    private static void cari() {
        String nim = baca("NIM yang dicari: ");
        Mahasiswa m = service.cariBerdasarkanNim(nim);
        System.out.println(m == null ? "NIM " + nim + " tidak ditemukan." : "Ditemukan: " + m);
        System.out.println();
    }

    private static void tampilkanTerurut() {
        List<Mahasiswa> daftar = service.dapatkanSemuaTerurut();
        if (daftar.isEmpty()) {
            System.out.println("(belum ada data)\n");
            return;
        }
        int batas = 50;
        for (int i = 0; i < daftar.size() && i < batas; i++) {
            System.out.println((i + 1) + ". " + daftar.get(i));
        }
        if (daftar.size() > batas) {
            System.out.println("... dan " + (daftar.size() - batas)
                    + " data lainnya (total " + daftar.size() + ").");
        }
        System.out.println();
    }

    private static void ubah() {
        String nim = baca("NIM yang diubah: ");
        if (service.cariBerdasarkanNim(nim) == null) {
            System.out.println("NIM " + nim + " tidak ditemukan.\n");
            return;
        }
        String nama = baca("Nama baru    : ");
        String jurusan = baca("Jurusan baru : ");
        service.updateMahasiswa(nim, nama, jurusan);
        System.out.println();
    }

    private static void hapus() {
        String nim = baca("NIM yang dihapus: ");
        service.hapusMahasiswa(nim);
        System.out.println();
    }

    /** Membangkitkan n mahasiswa dummy dengan NIM 10 digit unik (awalan "29"). */
    private static void buatDummy() {
        String input = baca("Jumlah data dummy (1 - 100000): ");
        int n;
        try {
            n = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Masukkan angka yang valid.\n");
            return;
        }
        if (n < 1 || n > 100_000) {
            System.out.println("Jumlah harus antara 1 dan 100000.\n");
            return;
        }

        Random rnd = new Random();
        Set<String> nimUnik = new HashSet<>();
        List<Mahasiswa> daftar = new ArrayList<>(n);
        while (daftar.size() < n) {
            String nim = String.format("29%08d", rnd.nextInt(100_000_000));
            if (nimUnik.add(nim)) {
                daftar.add(new Mahasiswa(nim, "Mahasiswa " + (daftar.size() + 1),
                        JURUSAN[rnd.nextInt(JURUSAN.length)]));
            }
        }
        int ditambah = service.tambahBanyak(daftar);
        System.out.println(ditambah + " data dummy berhasil ditambahkan.\n");
    }

    private static void statistik() {
        System.out.println("--- Statistik ---");
        System.out.println("Jumlah data  : " + service.jumlahData());
        System.out.println("Tinggi BST   : " + service.tinggiBst());
        System.out.println("-----------------\n");
    }
}
