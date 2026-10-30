# Project Data Structure - Group 4

Project ini dibuat untuk memenuhi tugas praktikum mata kuliah Struktur Data. Program dikembangkan menggunakan Java dengan Maven dan menerapkan beberapa struktur data untuk mengelola data mahasiswa.

## Struktur Folder

```text
Project-Data-Structure-Group-4/
├── pom.xml
├── README.md
├── .gitignore
├── main/
│   ├── Main.java
│   ├── hash/
│   │   └── HashTable.java
│   ├── graph/
│   │   └── Graph.java
│   ├── tree/
│   │   └── BST.java
│   ├── model/
│   │   └── Mahasiswa.java
│   ├── service/
│   │   └── MahasiswaService.java
│   ├── data/
│   │   └── CSVHandler.java
│   └── benchmark/
│       └── Benchmark.java
├── test/
│   ├── hash/
│   │   └── HashTableTest.java
│   └── graph/
│       └── GraphTest.java
└── resources/
    └── data/
        └── mahasiswa.csv
```

## Struktur Data yang Digunakan

Beberapa struktur data yang diterapkan dalam project ini, yaitu:

- **Hash Table** – digunakan untuk menyimpan dan mencari data menggunakan pasangan key dan value.
- **Graph** – digunakan untuk merepresentasikan hubungan antarobjek atau data.
- **Binary Search Tree (BST)** – digunakan untuk menyimpan data secara terstruktur agar proses pencarian lebih mudah.

## Teknologi

- Java
- Apache Maven
- JUnit untuk pengujian program
- CSV untuk penyimpanan data mahasiswa

## Cara Menjalankan Program

Pastikan Java sudah terpasang di komputer. Buka terminal pada folder project, kemudian jalankan:

```powershell
.\mvnw.cmd compile
```

## Menjalankan Pengujian

Untuk menjalankan pengujian menggunakan JUnit, gunakan perintah:

```powershell
.\mvnw.cmd test
```

Hasil pengujian akan ditampilkan melalui terminal.

## Pembagian Folder

| Folder | Keterangan |
|---|---|
| `main/hash` | Implementasi Hash Table |
| `main/graph` | Implementasi Graph |
| `main/tree` | Implementasi Binary Search Tree |
| `main/model` | Class untuk data mahasiswa |
| `main/service` | Pengolahan data mahasiswa |
| `main/data` | Pembacaan dan pengelolaan file CSV |
| `main/benchmark` | Pengujian performa struktur data |
| `test` | File pengujian program |
| `resources/data` | Penyimpanan data mahasiswa |

## Anggota Kelompok

**Group 4 – Praktikum Struktur Data**