# Öğrenci İşleri Otomasyonu

Bu proje, bir üniversitenin öğrenci işleri otomasyonunu simüle eden bir Java uygulamasıdır. Sistem, öğrenciler, öğretim üyeleri ve personel için çeşitli işlevler sunar.

## Özellikler

### Öğrenci İşlevleri
- Not görüntüleme
- Ders seçimi
- Transkript görüntüleme
- Devamsızlık durumu kontrolü
- Ders programı görüntüleme
- Danışman ile mesajlaşma

### Öğretim Üyesi İşlevleri
- Not girişi
- Sınav tanımlama
- Devamsızlık girişi
- Ders programı düzenleme
- Öğrenci listesi görüntüleme
- Ders materyali ekleme
- Danışmanlık mesajları

### Personel İşlevleri
- Öğrenci kaydı
- Öğretim üyesi kaydı
- Personel kaydı
- Kullanıcı silme/güncelleme
- Ders açma/kapatma
- Danışman atama
- Dönem işlemleri

## Kurulum

1. Projeyi klonlayın:
```bash
git clone https://github.com/isarat117/schoolOtomation.git
```

2. Java IDE'sinde (Eclipse, IntelliJ IDEA veya NetBeans) projeyi açın

3. Projeyi derleyin ve çalıştırın:
```bash
javac src/main/java/OgrenciIsleriOtomasyonu.java
java -cp src/main/java OgrenciIsleriOtomasyonu
```

## Kullanım

### Giriş Yapma
Sistem üç farklı kullanıcı tipini destekler:

1. Öğrenci Girişi:
   - Kullanıcı adı formatı: ogr_ogrencino
   - Örnek: ogr_2023001
   - İlk şifre: Öğrenci numarası

2. Öğretim Üyesi Girişi:
   - Kullanıcı adı formatı: hoca_sicilno
   - Örnek: hoca_1001
   - İlk şifre: Sicil numarası

3. Personel Girişi:
   - Kullanıcı adı formatı: per_personelno
   - Örnek: per_admin
   - İlk şifre: Personel numarası

### Örnek Hesaplar
- Öğrenci: ogr_2023001 / 2023001
- Öğretim Üyesi: hoca_1001 / 1001
- Personel: per_admin / admin

## Veri Saklama

Sistem, verileri aşağıdaki text dosyalarında saklar:
- kullanicilar.txt: Tüm kullanıcı bilgileri
- ogrenciler.txt: Öğrenci detayları
- ogretim_uyesi.txt: Öğretim üyesi detayları
- personel.txt: Personel detayları
- mesajlar.txt: Kullanıcılar arası mesajlar
- islem_log.txt: Sistem log kayıtları

## Teknik Detaylar

- Programlama Dili: Java
- GUI: Swing
- Veri Saklama: Text dosyaları
- Tasarım Desenleri: 
  - Inheritance (Kalıtım)
  - Interface segregation
  - Single responsibility

## Katkıda Bulunma

1. Bu repository'yi fork edin
2. Feature branch'i oluşturun (`git checkout -b feature/YeniOzellik`)
3. Değişikliklerinizi commit edin (`git commit -m 'Yeni özellik eklendi'`)
4. Branch'inizi push edin (`git push origin feature/YeniOzellik`)
5. Pull Request oluşturun
