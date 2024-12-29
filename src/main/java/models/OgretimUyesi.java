package models;

import interfaces.IAkademikIslemler;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class OgretimUyesi extends Kullanici implements IAkademikIslemler {
    private String unvan;
    private ArrayList<String> verdigiDersler;
    private HashMap<String, ArrayList<Ogrenci>> dersiAlanOgrenciler;
    private HashMap<String, HashMap<String, Integer>> devamsizliklar; // ders -> (öğrenci -> devamsızlık)
    private HashMap<String, ArrayList<Sinav>> sinavlar; // ders -> sınavlar listesi
    private ArrayList<String> danismanlikOgrencileri;
    private static final String OGRETIM_UYESI_DOSYA = "ogretim_uyesi.txt";

    public static class Sinav {
        String dersKodu;
        String tarih;
        String tur; // Vize, Final, Büt
        HashMap<String, Double> notlar; // öğrenci no -> not

        public Sinav(String dersKodu, String tarih, String tur) {
            this.dersKodu = dersKodu;
            this.tarih = tarih;
            this.tur = tur;
            this.notlar = new HashMap<>();
        }
    }

    public OgretimUyesi(String ad, String soyad, String kullaniciAdi, String sifre, String unvan) {
        super(ad, soyad, kullaniciAdi, sifre);
        this.unvan = unvan;
        this.verdigiDersler = new ArrayList<>();
        this.dersiAlanOgrenciler = new HashMap<>();
        this.devamsizliklar = new HashMap<>();
        this.sinavlar = new HashMap<>();
        this.danismanlikOgrencileri = new ArrayList<>();
        verileriOku();
    }

    public void sinavTanimla(String dersKodu, String tarih, String tur) {
        if (!sinavlar.containsKey(dersKodu)) {
            sinavlar.put(dersKodu, new ArrayList<>());
        }
        sinavlar.get(dersKodu).add(new Sinav(dersKodu, tarih, tur));
        verileriKaydet();
    }

    public void notGir(String dersKodu, String ogrenciNo, double not) {
        if (verdigiDersler.contains(dersKodu)) {
            for (Sinav sinav : sinavlar.get(dersKodu)) {
                sinav.notlar.put(ogrenciNo, not);
            }
            verileriKaydet();
        }
    }

    public void devamsizlikGir(String dersKodu, String ogrenciNo, int devamsizlik) {
        if (!devamsizliklar.containsKey(dersKodu)) {
            devamsizliklar.put(dersKodu, new HashMap<>());
        }
        devamsizliklar.get(dersKodu).put(ogrenciNo, devamsizlik);
        verileriKaydet();
    }

    public void dersProgramiGir(String dersKodu, String gun, String saat) {
        // Ders programı güncelleme
        verileriKaydet();
    }

    public void dersMateryaliEkle(String dersKodu, String dosyaAdi, String icerik) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("materyaller/" + dersKodu + "_" + dosyaAdi))) {
            writer.println(icerik);
        } catch (IOException e) {
            System.err.println("Materyal eklenemedi: " + e.getMessage());
        }
    }

    @Override
    public void dersKayit(String dersKodu) {
        verdigiDersler.add(dersKodu);
        dersiAlanOgrenciler.put(dersKodu, new ArrayList<>());
        devamsizliklar.put(dersKodu, new HashMap<>());
        verileriKaydet();
    }

    @Override
    public void sinavSonucGoruntule(String dersKodu) {
        if (sinavlar.containsKey(dersKodu)) {
            for (Sinav sinav : sinavlar.get(dersKodu)) {
                System.out.println("\nSınav: " + sinav.tur + " - " + sinav.tarih);
                for (String ogrenciNo : sinav.notlar.keySet()) {
                    System.out.println(ogrenciNo + ": " + sinav.notlar.get(ogrenciNo));
                }
            }
        }
    }

    @Override
    public void devamsizlikDurumu(String dersKodu) {
        if (devamsizliklar.containsKey(dersKodu)) {
            System.out.println("\nDevamsızlık Durumu - " + dersKodu);
            for (String ogrenciNo : devamsizliklar.get(dersKodu).keySet()) {
                System.out.println(ogrenciNo + ": " + devamsizliklar.get(dersKodu).get(ogrenciNo));
            }
        }
    }

    @Override
    public void dersProgramiGoruntule() {
        System.out.println("\nDers Programı:");
        for (String ders : verdigiDersler) {
            System.out.println(ders);
        }
    }

    @Override
    public void mesajGonder(String alici, String mesaj) {
        String yeniMesaj = "Gönderen: " + this.getKullaniciAdi() + "\nAlıcı: " + alici + "\nMesaj: " + mesaj;
        try (PrintWriter writer = new PrintWriter(new FileWriter("mesajlar.txt", true))) {
            writer.println(yeniMesaj);
        } catch (IOException e) {
            System.err.println("Mesaj gönderilemedi: " + e.getMessage());
        }
    }

    @Override
    public void mesajlariGoruntule() {
        try (BufferedReader reader = new BufferedReader(new FileReader("mesajlar.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("Alıcı: " + this.getKullaniciAdi())) {
                    System.out.println(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Mesajlar okunamadı: " + e.getMessage());
        }
    }

    @Override
    public void verileriKaydet() {
        super.verileriKaydet();
        try (PrintWriter writer = new PrintWriter(new FileWriter(OGRETIM_UYESI_DOSYA))) {
            // Temel bilgiler
            writer.println(String.format("%s,%s", unvan, String.join(";", verdigiDersler)));
            
            // Sınavlar
            for (String ders : sinavlar.keySet()) {
                for (Sinav sinav : sinavlar.get(ders)) {
                    writer.println("SINAV," + ders + "," + sinav.tarih + "," + sinav.tur);
                    for (String ogrenci : sinav.notlar.keySet()) {
                        writer.println("NOT," + ogrenci + "," + sinav.notlar.get(ogrenci));
                    }
                }
            }
            
            // Devamsızlıklar
            for (String ders : devamsizliklar.keySet()) {
                for (String ogrenci : devamsizliklar.get(ders).keySet()) {
                    writer.println("DEVAMSIZLIK," + ders + "," + ogrenci + "," + 
                        devamsizliklar.get(ders).get(ogrenci));
                }
            }
            
            // Danışmanlık öğrencileri
            writer.println("DANISMAN," + String.join(";", danismanlikOgrencileri));
        } catch (IOException e) {
            System.err.println("Öğretim üyesi verisi kaydedilemedi: " + e.getMessage());
        }
    }

    @Override
    public void verileriOku() {
        super.verileriOku();
        try (BufferedReader reader = new BufferedReader(new FileReader(OGRETIM_UYESI_DOSYA))) {
            String line;
            Sinav currentSinav = null;
            
            while ((line = reader.readLine()) != null && !line.trim().isEmpty()) {
                String[] veriler = line.split(",");
                if (veriler.length == 0) continue;
                
                switch (veriler[0]) {
                    case "SINAV":
                        if (veriler.length >= 4) {
                            String ders = veriler[1];
                            if (!sinavlar.containsKey(ders)) {
                                sinavlar.put(ders, new ArrayList<>());
                            }
                            currentSinav = new Sinav(ders, veriler[2], veriler[3]);
                            sinavlar.get(ders).add(currentSinav);
                        }
                        break;
                    case "NOT":
                        if (veriler.length >= 3 && currentSinav != null) {
                            currentSinav.notlar.put(veriler[1], Double.parseDouble(veriler[2]));
                        }
                        break;
                    case "DEVAMSIZLIK":
                        if (veriler.length >= 4) {
                            String ders = veriler[1];
                            String ogrenci = veriler[2];
                            int devamsizlik = Integer.parseInt(veriler[3]);
                            if (!devamsizliklar.containsKey(ders)) {
                                devamsizliklar.put(ders, new HashMap<>());
                            }
                            devamsizliklar.get(ders).put(ogrenci, devamsizlik);
                        }
                        break;
                    case "DANISMAN":
                        if (veriler.length >= 2) {
                            String[] ogrenciler = veriler[1].split(";");
                            for (String ogrenci : ogrenciler) {
                                if (!ogrenci.isEmpty()) {
                                    danismanlikOgrencileri.add(ogrenci);
                                }
                            }
                        }
                        break;
                    default:
                        // İlk satır - temel bilgiler
                        if (veriler.length >= 2) {
                            String[] dersler = veriler[1].split(";");
                            for (String ders : dersler) {
                                if (!ders.isEmpty()) {
                                    verdigiDersler.add(ders);
                                    dersiAlanOgrenciler.put(ders, new ArrayList<>());
                                    devamsizliklar.put(ders, new HashMap<>());
                                }
                            }
                        }
                        break;
                }
            }
        } catch (IOException e) {
            System.err.println("Öğretim üyesi verisi okunamadı: " + e.getMessage());
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            System.err.println("Dosya formatı hatalı: " + e.getMessage());
        }
    }
} 