package interfaces;

public interface IAkademikIslemler {
    void dersKayit(String dersKodu);
    void sinavSonucGoruntule(String dersKodu);
    void devamsizlikDurumu(String dersKodu);
    void dersProgramiGoruntule();
    void mesajGonder(String alici, String mesaj);
    void mesajlariGoruntule();
} 