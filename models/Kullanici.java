package models;

import interfaces.IKullanici;
import interfaces.IVeriYonetimi;
import java.io.*;

public abstract class Kullanici implements IKullanici, IVeriYonetimi {
    protected String ad;
    protected String soyad;
    protected String kullaniciAdi;
    protected String sifre;
    protected static int kullaniciSayisi = 0;
    protected static final String DOSYA_YOLU = "kullanicilar.txt";

    public Kullanici(String ad, String soyad, String kullaniciAdi, String sifre) {
        this.ad = ad;
        this.soyad = soyad;
        this.kullaniciAdi = kullaniciAdi;
        this.sifre = sifre;
        kullaniciSayisi++;
    }

    public String getKullaniciAdi() {
        return kullaniciAdi;
    }

    @Override
    public boolean girisYap(String kullaniciAdi, String sifre) {
        return this.kullaniciAdi.equals(kullaniciAdi) && this.sifre.equals(sifre);
    }

    @Override
    public void sifreDegistir(String yeniSifre) {
        this.sifre = yeniSifre;
        verileriKaydet();
    }

    @Override
    public void profilGoruntule() {
        StringBuilder sb = new StringBuilder();
        profilGoruntule(sb);
    }

    public void profilGoruntule(StringBuilder sb) {
        sb.append("Ad: ").append(ad).append("\n");
        sb.append("Soyad: ").append(soyad).append("\n");
        sb.append("Kullanıcı Adı: ").append(kullaniciAdi).append("\n");
    }

    public static int getKullaniciSayisi() {
        return kullaniciSayisi;
    }

    @Override
    public void verileriKaydet() {
        File dosya = new File(DOSYA_YOLU);
        try {
            if (!dosya.exists()) {
                dosya.createNewFile();
            }
            // Dosyayı her seferinde üzerine yazarak güncelleme
            try (PrintWriter writer = new PrintWriter(new FileWriter(DOSYA_YOLU))) {
                writer.println(String.format("%s,%s,%s,%s,%s", 
                    getClass().getSimpleName(), ad, soyad, kullaniciAdi, sifre));
            }
        } catch (IOException e) {
            System.err.println("Veri kaydedilemedi: " + e.getMessage());
        }
    }

    @Override
    public void verileriOku() {
        try (BufferedReader reader = new BufferedReader(new FileReader(DOSYA_YOLU))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] veriler = line.split(",");
                if (veriler[3].equals(this.kullaniciAdi)) {
                    this.ad = veriler[1];
                    this.soyad = veriler[2];
                    this.sifre = veriler[4];
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Veri okunamadı: " + e.getMessage());
        }
    }
} 