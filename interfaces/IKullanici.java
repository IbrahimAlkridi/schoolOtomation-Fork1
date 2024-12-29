package interfaces;

public interface IKullanici {
    boolean girisYap(String kullaniciAdi, String sifre);
    void sifreDegistir(String yeniSifre);
    void profilGoruntule();
} 