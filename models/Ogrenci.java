package models;

import interfaces.IAkademikIslemler;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Ogrenci extends Kullanici implements IAkademikIslemler {
    private String ogrenciNo;
    private HashMap<String, Double> notlar;
    private ArrayList<String> alinanDersler;
    private String danisman;
    private HashMap<String, Integer> devamsizliklar;
    private ArrayList<String> mesajlar;
    private HashMap<String, String> dersProgrami;
    private static final String OGRENCI_DOSYA = "ogrenciler.txt";

    public Ogrenci(String ad, String soyad, String kullaniciAdi, String sifre, String ogrenciNo) {
        super(ad, soyad, kullaniciAdi, sifre);
        this.ogrenciNo = ogrenciNo;
        this.notlar = new HashMap<>();
        this.alinanDersler = new ArrayList<>();
        this.devamsizliklar = new HashMap<>();
        this.mesajlar = new ArrayList<>();
        this.dersProgrami = new HashMap<>();
        verileriOku();
    }

    @Override
    public void dersKayit(String dersKodu) {
        alinanDersler.add(dersKodu);
        devamsizliklar.put(dersKodu, 0);
        verileriKaydet();
    }

    @Override
    public void sinavSonucGoruntule(String dersKodu) {
        if (notlar.containsKey(dersKodu)) {
            System.out.println(dersKodu + " dersi notu: " + notlar.get(dersKodu));
        }
    }

    @Override
    public void devamsizlikDurumu(String dersKodu) {
        if (devamsizliklar.containsKey(dersKodu)) {
            System.out.println(dersKodu + " dersi devamsızlık: " + devamsizliklar.get(dersKodu));
        }
    }

    @Override
    public void dersProgramiGoruntule() {
        for (String ders : dersProgrami.keySet()) {
            System.out.println(ders + ": " + dersProgrami.get(ders));
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

    public void transkriptGoruntule(StringBuilder sb) {
        sb.append("\n=== TRANSKRİPT ===\n");
        double toplamNot = 0;
        int dersSayisi = 0;
        
        for (String ders : notlar.keySet()) {
            double not = notlar.get(ders);
            sb.append(ders).append(": ").append(not).append("\n");
            toplamNot += not;
            dersSayisi++;
        }
        
        if (dersSayisi > 0) {
            double ortalama = toplamNot / dersSayisi;
            sb.append("\nGenel Ortalama: ").append(String.format("%.2f", ortalama)).append("\n");
        }
    }

    @Override
    public void verileriKaydet() {
        super.verileriKaydet();
        File dosya = new File(OGRENCI_DOSYA);
        try {
            if (!dosya.exists()) {
                dosya.createNewFile();
            }
            try (PrintWriter writer = new PrintWriter(new FileWriter(OGRENCI_DOSYA))) {
                // Temel bilgiler
                writer.println(String.format("%s,%s", ogrenciNo, danisman));
                
                // Dersler ve notlar
                StringBuilder derslerStr = new StringBuilder();
                StringBuilder notlarStr = new StringBuilder();
                for (String ders : alinanDersler) {
                    derslerStr.append(ders).append(";");
                    if (notlar.containsKey(ders)) {
                        notlarStr.append(ders).append(":").append(notlar.get(ders)).append(";");
                    }
                }
                writer.println(derslerStr.toString());
                writer.println(notlarStr.toString());
                
                // Devamsızlıklar
                StringBuilder devamsizlikStr = new StringBuilder();
                for (String ders : devamsizliklar.keySet()) {
                    devamsizlikStr.append(ders).append(":").append(devamsizliklar.get(ders)).append(";");
                }
                writer.println(devamsizlikStr.toString());
                
                // Ders programı
                StringBuilder programStr = new StringBuilder();
                for (String ders : dersProgrami.keySet()) {
                    programStr.append(ders).append(":").append(dersProgrami.get(ders)).append(";");
                }
                writer.println(programStr.toString());
            }
        } catch (IOException e) {
            System.err.println("Öğrenci verisi kaydedilemedi: " + e.getMessage());
        }
    }

    @Override
    public void verileriOku() {
        super.verileriOku();
        try (BufferedReader reader = new BufferedReader(new FileReader(OGRENCI_DOSYA))) {
            String line;
            int lineNo = 0;
            while ((line = reader.readLine()) != null) {
                switch (lineNo) {
                    case 0: // Temel bilgiler
                        String[] temel = line.split(",");
                        if (temel[0].equals(this.ogrenciNo)) {
                            this.danisman = temel[1];
                        }
                        break;
                    case 1: // Dersler
                        for (String ders : line.split(";")) {
                            if (!ders.isEmpty()) {
                                alinanDersler.add(ders);
                            }
                        }
                        break;
                    case 2: // Notlar
                        for (String not : line.split(";")) {
                            if (!not.isEmpty()) {
                                String[] dersNot = not.split(":");
                                notlar.put(dersNot[0], Double.parseDouble(dersNot[1]));
                            }
                        }
                        break;
                    case 3: // Devamsızlıklar
                        for (String dev : line.split(";")) {
                            if (!dev.isEmpty()) {
                                String[] dersDev = dev.split(":");
                                devamsizliklar.put(dersDev[0], Integer.parseInt(dersDev[1]));
                            }
                        }
                        break;
                    case 4: // Ders programı
                        for (String prog : line.split(";")) {
                            if (!prog.isEmpty()) {
                                String[] dersProg = prog.split(":");
                                dersProgrami.put(dersProg[0], dersProg[1]);
                            }
                        }
                        break;
                }
                lineNo++;
            }
        } catch (IOException e) {
            System.err.println("Öğrenci verisi okunamadı: " + e.getMessage());
        }
    }
} 