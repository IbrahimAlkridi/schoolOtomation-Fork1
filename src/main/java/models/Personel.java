package models;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Personel extends Kullanici {
    private String personelNo;
    private String departman;
    private static final String PERSONEL_DOSYA = "personel.txt";
    private HashMap<String, ArrayList<String>> dersKayitlari; // ders kodu -> öğrenci listesi
    private HashMap<String, String> danismanAtamalari; // öğrenci no -> öğretim üyesi

    public Personel(String ad, String soyad, String kullaniciAdi, String sifre, String personelNo, String departman) {
        super(ad, soyad, kullaniciAdi, sifre);
        this.personelNo = personelNo;
        this.departman = departman;
        this.dersKayitlari = new HashMap<>();
        this.danismanAtamalari = new HashMap<>();
        verileriOku();
    }

    public void ogrenciKaydet(String ad, String soyad, String ogrenciNo) {
        String kullaniciAdi = "ogr_" + ogrenciNo;
        String sifre = ogrenciNo; // İlk şifre öğrenci numarası olsun
        Ogrenci yeniOgrenci = new Ogrenci(ad, soyad, kullaniciAdi, sifre, ogrenciNo);
        yeniOgrenci.verileriKaydet();
        logIslem("Yeni öğrenci kaydedildi: " + ogrenciNo);
    }

    public void dersAc(String dersKodu, String dersAdi, String ogretimUyesi) {
        dersKayitlari.put(dersKodu, new ArrayList<>());
        logIslem("Yeni ders açıldı: " + dersKodu + " - " + dersAdi);
        verileriKaydet();
    }

    public void dersKapat(String dersKodu) {
        dersKayitlari.remove(dersKodu);
        logIslem("Ders kapatıldı: " + dersKodu);
        verileriKaydet();
    }

    public void danismanAta(String ogrenciNo, String ogretimUyesiNo) {
        danismanAtamalari.put(ogrenciNo, ogretimUyesiNo);
        logIslem("Danışman ataması yapıldı: " + ogrenciNo + " -> " + ogretimUyesiNo);
        verileriKaydet();
    }

    public void donemIslemleri(String donemKodu) {
        // Dönem başlangıç işlemleri
        logIslem("Dönem işlemleri başlatıldı: " + donemKodu);
    }

    public void ogretimUyesiKaydet(String ad, String soyad, String sicilNo, String unvan) {
        String kullaniciAdi = "hoca_" + sicilNo;
        String sifre = sicilNo;
        OgretimUyesi yeniOgretimUyesi = new OgretimUyesi(ad, soyad, kullaniciAdi, sifre, unvan);
        yeniOgretimUyesi.verileriKaydet();
        logIslem("Yeni öğretim üyesi kaydedildi: " + sicilNo);
    }

    public void personelKaydet(String ad, String soyad, String personelNo, String departman) {
        String kullaniciAdi = "per_" + personelNo;
        String sifre = personelNo; // İlk şifre personel numarası olsun
        Personel yeniPersonel = new Personel(ad, soyad, kullaniciAdi, sifre, personelNo, departman);
        yeniPersonel.verileriKaydet();
        logIslem("Yeni personel kaydedildi: " + personelNo);
    }

    public void kullaniciSil(String kullaniciAdi) {
        // Kullanıcıyı ilgili dosyalardan sil
        try {
            File kullanicilarFile = new File("kullanicilar.txt");
            File tempFile = new File("temp.txt");
            
            BufferedReader reader = new BufferedReader(new FileReader(kullanicilarFile));
            PrintWriter writer = new PrintWriter(new FileWriter(tempFile));

            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.contains("," + kullaniciAdi + ",")) {
                    writer.println(line);
                }
            }
            
            reader.close();
            writer.close();
            
            kullanicilarFile.delete();
            tempFile.renameTo(kullanicilarFile);
            
            logIslem("Kullanıcı silindi: " + kullaniciAdi);
        } catch (IOException e) {
            System.err.println("Kullanıcı silinemedi: " + e.getMessage());
        }
    }

    public void kullaniciGuncelle(String kullaniciAdi, String yeniAd, String yeniSoyad) {
        try {
            File kullanicilarFile = new File("kullanicilar.txt");
            File tempFile = new File("temp.txt");
            
            BufferedReader reader = new BufferedReader(new FileReader(kullanicilarFile));
            PrintWriter writer = new PrintWriter(new FileWriter(tempFile));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] veriler = line.split(",");
                if (veriler.length >= 4 && veriler[3].equals(kullaniciAdi)) {
                    veriler[1] = yeniAd;
                    veriler[2] = yeniSoyad;
                    writer.println(String.join(",", veriler));
                } else {
                    writer.println(line);
                }
            }
            
            reader.close();
            writer.close();
            
            kullanicilarFile.delete();
            tempFile.renameTo(kullanicilarFile);
            
            logIslem("Kullanıcı güncellendi: " + kullaniciAdi);
        } catch (IOException e) {
            System.err.println("Kullanıcı güncellenemedi: " + e.getMessage());
        }
    }

    private void logIslem(String mesaj) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("islem_log.txt", true))) {
            writer.println(java.time.LocalDateTime.now() + ": " + mesaj);
        } catch (IOException e) {
            System.err.println("Log yazılamadı: " + e.getMessage());
        }
    }

    @Override
    public void profilGoruntule(StringBuilder sb) {
        super.profilGoruntule(sb);
        sb.append("Personel No: ").append(personelNo).append("\n");
        sb.append("Departman: ").append(departman).append("\n");
    }

    @Override
    public void verileriKaydet() {
        // Önce temel kullanıcı bilgilerini kaydet
        File dosya = new File(DOSYA_YOLU);
        try {
            if (!dosya.exists()) {
                dosya.createNewFile();
            }
            
            // Önce mevcut kullanıcıları oku
            HashMap<String, String> mevcutKullanicilar = new HashMap<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(DOSYA_YOLU))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] veriler = line.split(",");
                    if (veriler.length >= 4) {
                        mevcutKullanicilar.put(veriler[3], line);
                    }
                }
            }
            
            // Bu kullanıcının bilgilerini güncelle veya ekle
            String sinifTipi = this.getClass().getSimpleName();
            mevcutKullanicilar.put(this.kullaniciAdi, 
                String.format("%s,%s,%s,%s,%s,%s", sinifTipi, ad, soyad, kullaniciAdi, sifre, departman));
            
            // Tüm kullanıcıları tekrar yaz
            try (PrintWriter writer = new PrintWriter(new FileWriter(DOSYA_YOLU))) {
                for (String kayit : mevcutKullanicilar.values()) {
                    writer.println(kayit);
                }
            }
            
        } catch (IOException e) {
            System.err.println("Veri kaydedilemedi: " + e.getMessage());
        }

        // Personel-spesifik bilgileri kaydet
        try (PrintWriter writer = new PrintWriter(new FileWriter(PERSONEL_DOSYA))) {
            writer.println(String.format("%s,%s,%s", personelNo, departman, 
                String.join(";", dersKayitlari.keySet())));
            
            // Danışman atamalarını kaydet
            for (String ogrNo : danismanAtamalari.keySet()) {
                writer.println(ogrNo + "," + danismanAtamalari.get(ogrNo));
            }
        } catch (IOException e) {
            System.err.println("Personel verisi kaydedilemedi: " + e.getMessage());
        }
    }

    @Override
    public void verileriOku() {
        super.verileriOku();
        try (BufferedReader reader = new BufferedReader(new FileReader(PERSONEL_DOSYA))) {
            String line = reader.readLine();
            if (line != null) {
                String[] veriler = line.split(",");
                this.personelNo = veriler[0];
                this.departman = veriler[1];
                if (veriler.length > 2) {
                    String[] dersler = veriler[2].split(";");
                    for (String ders : dersler) {
                        if (!ders.isEmpty()) {
                            dersKayitlari.put(ders, new ArrayList<>());
                        }
                    }
                }
            }
            // Danışman atamalarını oku
            while ((line = reader.readLine()) != null) {
                String[] veriler = line.split(",");
                if (veriler.length == 2) {
                    danismanAtamalari.put(veriler[0], veriler[1]);
                }
            }
        } catch (IOException e) {
            System.err.println("Personel verisi okunamadı: " + e.getMessage());
        }
    }
} 