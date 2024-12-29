import models.*;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.io.*;

public class OgrenciIsleriOtomasyonu {
    private static HashMap<String, Kullanici> kullanicilar = new HashMap<>();
    private static JFrame frame;
    private static JPanel panel;
    private static Kullanici aktifKullanici;
    private static JTextArea logArea;

    public static void main(String[] args) {
        // Dosyaları oluştur
        createFiles();
        kullanicilariYukle();
        
        if (kullanicilar.isEmpty()) {
            // Örnek kullanıcılar oluştur
            Ogrenci ogrenci = new Ogrenci("Ali", "Yılmaz", "ali", "1234", "2023001");
            OgretimUyesi ogretimUyesi = new OgretimUyesi("Ayşe", "Demir", "ayse", "1234", "Dr.");
            Personel personel = new Personel("Mehmet", "Kaya", "admin", "admin", "P001", "Öğrenci İşleri");
            
            kullanicilar.put(ogrenci.getKullaniciAdi(), ogrenci);
            kullanicilar.put(ogretimUyesi.getKullaniciAdi(), ogretimUyesi);
            kullanicilar.put(personel.getKullaniciAdi(), personel);

            // İlk kullanıcıları kaydet
            ogrenci.verileriKaydet();
            ogretimUyesi.verileriKaydet();
            personel.verileriKaydet();
        }

        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void createFiles() {
        String[] dosyalar = {"kullanicilar.txt", "ogrenciler.txt", "ogretim_uyesi.txt"};
        for (String dosya : dosyalar) {
            try {
                File file = new File(dosya);
                if (!file.exists()) {
                    file.createNewFile();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void kullanicilariYukle() {
        try (BufferedReader reader = new BufferedReader(new FileReader("kullanicilar.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] veriler = line.split(",");
                if (veriler.length >= 5) {
                    String tip = veriler[0];
                    String ad = veriler[1];
                    String soyad = veriler[2];
                    String kullaniciAdi = veriler[3];
                    String sifre = veriler[4];

                    Kullanici kullanici;
                    if (tip.equals("Ogrenci")) {
                        kullanici = new Ogrenci(ad, soyad, kullaniciAdi, sifre, "2023" + kullaniciAdi);
                    } else if (tip.equals("OgretimUyesi")) {
                        kullanici = new OgretimUyesi(ad, soyad, kullaniciAdi, sifre, "Dr.");
                    } else {
                        continue;
                    }
                    kullanicilar.put(kullaniciAdi, kullanici);
                }
            }
        } catch (IOException e) {
            logToScreen("Kullanıcılar yüklenemedi: " + e.getMessage());
        }
    }

    private static void createAndShowGUI() {
        frame = new JFrame("Öğrenci İşleri Otomasyonu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);

        // Ana panel
        panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Log alanı
        logArea = new JTextArea(10, 40);
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        panel.add(scrollPane, BorderLayout.SOUTH);
        
        showLoginPanel();

        frame.add(panel);
        frame.setVisible(true);
    }

    private static void showLoginPanel() {
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        JTextField kullaniciAdiField = new JTextField(20);
        JPasswordField sifreField = new JPasswordField(20);
        JButton girisButton = new JButton("Giriş Yap");

        gbc.gridx = 0; gbc.gridy = 0;
        loginPanel.add(new JLabel("Kullanıcı Adı:"), gbc);
        gbc.gridx = 1;
        loginPanel.add(kullaniciAdiField, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        loginPanel.add(new JLabel("Şifre:"), gbc);
        gbc.gridx = 1;
        loginPanel.add(sifreField, gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        loginPanel.add(girisButton, gbc);

        girisButton.addActionListener(e -> {
            String kullaniciAdi = kullaniciAdiField.getText();
            String sifre = new String(sifreField.getPassword());
            
            Kullanici kullanici = kullanicilar.get(kullaniciAdi);
            if (kullanici != null && kullanici.girisYap(kullaniciAdi, sifre)) {
                aktifKullanici = kullanici;
                showMainMenu();
            } else {
                JOptionPane.showMessageDialog(frame, "Hatalı kullanıcı adı veya şifre!");
            }
        });

        panel.removeAll();
        panel.add(loginPanel, BorderLayout.CENTER);
        panel.add(logArea.getParent(), BorderLayout.SOUTH);
        panel.revalidate();
        panel.repaint();
    }

    private static void showMainMenu() {
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int gridy = 0;
        
        if (aktifKullanici instanceof Ogrenci) {
            String[] butonlar = {
                "Notları Görüntüle", "Ders Seçimi", "Transkript", 
                "Devamsızlık Durumu", "Ders Programı", "Danışman Mesajları"
            };
            
            for (String butonAdi : butonlar) {
                JButton button = new JButton(butonAdi);
                button.addActionListener(e -> {
                    switch (butonAdi) {
                        case "Notları Görüntüle":
                            String dersKodu = JOptionPane.showInputDialog("Ders Kodu Giriniz:");
                            if (dersKodu != null && !dersKodu.isEmpty()) {
                                ((Ogrenci) aktifKullanici).sinavSonucGoruntule(dersKodu);
                            }
                            break;
                        case "Ders Seçimi":
                            dersKodu = JOptionPane.showInputDialog("Ders Kodu Giriniz:");
                            if (dersKodu != null && !dersKodu.isEmpty()) {
                                ((Ogrenci) aktifKullanici).dersKayit(dersKodu);
                                logToScreen("Ders eklendi: " + dersKodu);
                            }
                            break;
                        case "Transkript":
                            StringBuilder transkript = new StringBuilder();
                            ((Ogrenci) aktifKullanici).transkriptGoruntule(transkript);
                            logToScreen(transkript.toString());
                            break;
                        case "Devamsızlık Durumu":
                            dersKodu = JOptionPane.showInputDialog("Ders Kodu Giriniz:");
                            if (dersKodu != null && !dersKodu.isEmpty()) {
                                ((Ogrenci) aktifKullanici).devamsizlikDurumu(dersKodu);
                            }
                            break;
                        case "Ders Programı":
                            ((Ogrenci) aktifKullanici).dersProgramiGoruntule();
                            break;
                        case "Danışman Mesajları":
                            String mesaj = JOptionPane.showInputDialog("Mesajınızı Giriniz:");
                            if (mesaj != null && !mesaj.isEmpty()) {
                                ((Ogrenci) aktifKullanici).mesajGonder("danisman", mesaj);
                                logToScreen("Mesaj gönderildi");
                            }
                            ((Ogrenci) aktifKullanici).mesajlariGoruntule();
                            break;
                    }
                });
                gbc.gridy = gridy++;
                menuPanel.add(button, gbc);
            }
            
        } else if (aktifKullanici instanceof OgretimUyesi) {
            String[] butonlar = {
                "Not Gir", "Sınav Tanımla", "Devamsızlık Gir", 
                "Ders Programı", "Öğrenci Listesi", "Materyal Ekle",
                "Danışman Mesajları", "Ders Ekle"
            };
            
            for (String butonAdi : butonlar) {
                JButton button = new JButton(butonAdi);
                button.addActionListener(e -> {
                    String dersKodu, ogrenciNo;
                    switch (butonAdi) {
                        case "Not Gir":
                            dersKodu = JOptionPane.showInputDialog("Ders Kodu Giriniz:");
                            if (dersKodu != null && !dersKodu.isEmpty()) {
                                ogrenciNo = JOptionPane.showInputDialog("Öğrenci No Giriniz:");
                                String notStr = JOptionPane.showInputDialog("Not Giriniz:");
                                try {
                                    double not = Double.parseDouble(notStr);
                                    ((OgretimUyesi) aktifKullanici).notGir(dersKodu, ogrenciNo, not);
                                    logToScreen(String.format("Not girildi: %s - %s - %.2f", dersKodu, ogrenciNo, not));
                                } catch (NumberFormatException ex) {
                                    JOptionPane.showMessageDialog(frame, "Geçersiz not formatı!");
                                }
                            }
                            break;
                        case "Sınav Tanımla":
                            dersKodu = JOptionPane.showInputDialog("Ders Kodu Giriniz:");
                            if (dersKodu != null && !dersKodu.isEmpty()) {
                                String tarih = JOptionPane.showInputDialog("Tarih Giriniz (GG.AA.YYYY):");
                                String tur = JOptionPane.showInputDialog("Sınav Türü (Vize/Final/Büt):");
                                ((OgretimUyesi) aktifKullanici).sinavTanimla(dersKodu, tarih, tur);
                                logToScreen(String.format("Sınav tanımlandı: %s - %s - %s", dersKodu, tarih, tur));
                            }
                            break;
                        case "Devamsızlık Gir":
                            dersKodu = JOptionPane.showInputDialog("Ders Kodu Giriniz:");
                            if (dersKodu != null && !dersKodu.isEmpty()) {
                                ogrenciNo = JOptionPane.showInputDialog("Öğrenci No Giriniz:");
                                String devamsizlikStr = JOptionPane.showInputDialog("Devamsızlık Sayısı:");
                                try {
                                    int devamsizlik = Integer.parseInt(devamsizlikStr);
                                    ((OgretimUyesi) aktifKullanici).devamsizlikGir(dersKodu, ogrenciNo, devamsizlik);
                                    logToScreen(String.format("Devamsızlık girildi: %s - %s - %d", dersKodu, ogrenciNo, devamsizlik));
                                } catch (NumberFormatException ex) {
                                    JOptionPane.showMessageDialog(frame, "Geçersiz devamsızlık sayısı!");
                                }
                            }
                            break;
                        case "Ders Programı":
                            dersKodu = JOptionPane.showInputDialog("Ders Kodu Giriniz:");
                            if (dersKodu != null && !dersKodu.isEmpty()) {
                                String gun = JOptionPane.showInputDialog("Gün Giriniz:");
                                String saat = JOptionPane.showInputDialog("Saat Giriniz:");
                                ((OgretimUyesi) aktifKullanici).dersProgramiGir(dersKodu, gun, saat);
                                logToScreen(String.format("Ders programı güncellendi: %s - %s - %s", dersKodu, gun, saat));
                            }
                            break;
                        case "Materyal Ekle":
                            dersKodu = JOptionPane.showInputDialog("Ders Kodu Giriniz:");
                            if (dersKodu != null && !dersKodu.isEmpty()) {
                                String dosyaAdi = JOptionPane.showInputDialog("Dosya Adı:");
                                String icerik = JOptionPane.showInputDialog("İçerik:");
                                ((OgretimUyesi) aktifKullanici).dersMateryaliEkle(dersKodu, dosyaAdi, icerik);
                                logToScreen("Materyal eklendi: " + dersKodu + " - " + dosyaAdi);
                            }
                            break;
                        case "Danışman Mesajları":
                            ogrenciNo = JOptionPane.showInputDialog("Öğrenci No Giriniz:");
                            String mesaj = JOptionPane.showInputDialog("Mesajınızı Giriniz:");
                            if (mesaj != null && !mesaj.isEmpty()) {
                                ((OgretimUyesi) aktifKullanici).mesajGonder(ogrenciNo, mesaj);
                                logToScreen("Mesaj gönderildi");
                            }
                            ((OgretimUyesi) aktifKullanici).mesajlariGoruntule();
                            break;
                        case "Ders Ekle":
                            dersKodu = JOptionPane.showInputDialog("Ders Kodu Giriniz:");
                            if (dersKodu != null && !dersKodu.isEmpty()) {
                                ((OgretimUyesi) aktifKullanici).dersKayit(dersKodu);
                                logToScreen("Ders eklendi: " + dersKodu);
                            }
                            break;
                    }
                });
                gbc.gridy = gridy++;
                menuPanel.add(button, gbc);
            }
        } else if (aktifKullanici instanceof Personel) {
            String[] butonlar = {
                "Öğrenci Kaydet", "Öğretim Üyesi Kaydet", "Personel Kaydet",
                "Kullanıcı Sil", "Kullanıcı Güncelle",
                "Ders Aç", "Ders Kapat", 
                "Danışman Ata", "Dönem İşlemleri"
            };
            
            for (String butonAdi : butonlar) {
                JButton button = new JButton(butonAdi);
                button.addActionListener(e -> {
                    switch (butonAdi) {
                        case "Öğrenci Kaydet":
                            String ad = JOptionPane.showInputDialog("Öğrenci Adı:");
                            String soyad = JOptionPane.showInputDialog("Öğrenci Soyadı:");
                            String ogrenciNo = JOptionPane.showInputDialog("Öğrenci No:");
                            if (ad != null && soyad != null && ogrenciNo != null) {
                                ((Personel) aktifKullanici).ogrenciKaydet(ad, soyad, ogrenciNo);
                                logToScreen("Öğrenci kaydedildi: " + ogrenciNo);
                            }
                            break;
                        case "Öğretim Üyesi Kaydet":
                            ad = JOptionPane.showInputDialog("Öğretim Üyesi Adı:");
                            soyad = JOptionPane.showInputDialog("Öğretim Üyesi Soyadı:");
                            String sicilNo = JOptionPane.showInputDialog("Sicil No:");
                            String unvan = JOptionPane.showInputDialog("Ünvan:");
                            if (ad != null && soyad != null && sicilNo != null && unvan != null) {
                                ((Personel) aktifKullanici).ogretimUyesiKaydet(ad, soyad, sicilNo, unvan);
                                logToScreen("Öğretim üyesi kaydedildi: " + sicilNo);
                            }
                            break;
                        case "Personel Kaydet":
                            ad = JOptionPane.showInputDialog("Personel Adı:");
                            soyad = JOptionPane.showInputDialog("Personel Soyadı:");
                            String personelNo = JOptionPane.showInputDialog("Personel No:");
                            String departman = JOptionPane.showInputDialog("Departman:");
                            if (ad != null && soyad != null && personelNo != null && departman != null) {
                                ((Personel) aktifKullanici).personelKaydet(ad, soyad, personelNo, departman);
                                logToScreen("Personel kaydedildi: " + personelNo);
                            }
                            break;
                        case "Kullanıcı Sil":
                            String kullaniciAdi = JOptionPane.showInputDialog("Silinecek Kullanıcı Adı:");
                            if (kullaniciAdi != null && !kullaniciAdi.isEmpty()) {
                                int onay = JOptionPane.showConfirmDialog(frame, 
                                    "Kullanıcıyı silmek istediğinizden emin misiniz?",
                                    "Kullanıcı Silme Onayı",
                                    JOptionPane.YES_NO_OPTION);
                                if (onay == JOptionPane.YES_OPTION) {
                                    ((Personel) aktifKullanici).kullaniciSil(kullaniciAdi);
                                    logToScreen("Kullanıcı silindi: " + kullaniciAdi);
                                }
                            }
                            break;
                        case "Kullanıcı Güncelle":
                            kullaniciAdi = JOptionPane.showInputDialog("Güncellenecek Kullanıcı Adı:");
                            if (kullaniciAdi != null && !kullaniciAdi.isEmpty()) {
                                ad = JOptionPane.showInputDialog("Yeni Ad:");
                                soyad = JOptionPane.showInputDialog("Yeni Soyad:");
                                if (ad != null && soyad != null) {
                                    ((Personel) aktifKullanici).kullaniciGuncelle(kullaniciAdi, ad, soyad);
                                    logToScreen("Kullanıcı güncellendi: " + kullaniciAdi);
                                }
                            }
                            break;
                        case "Ders Aç":
                            String dersKodu = JOptionPane.showInputDialog("Ders Kodu:");
                            String dersAdi = JOptionPane.showInputDialog("Ders Adı:");
                            String ogretimUyesi = JOptionPane.showInputDialog("Öğretim Üyesi:");
                            if (dersKodu != null && dersAdi != null && ogretimUyesi != null) {
                                ((Personel) aktifKullanici).dersAc(dersKodu, dersAdi, ogretimUyesi);
                            }
                            break;
                        case "Ders Kapat":
                            dersKodu = JOptionPane.showInputDialog("Kapatılacak Ders Kodu:");
                            if (dersKodu != null) {
                                ((Personel) aktifKullanici).dersKapat(dersKodu);
                            }
                            break;
                        case "Danışman Ata":
                            String ogrNo = JOptionPane.showInputDialog("Öğrenci No:");
                            String ogretimUyesiNo = JOptionPane.showInputDialog("Öğretim Üyesi No:");
                            if (ogrNo != null && ogretimUyesiNo != null) {
                                ((Personel) aktifKullanici).danismanAta(ogrNo, ogretimUyesiNo);
                            }
                            break;
                        case "Dönem İşlemleri":
                            String donemKodu = JOptionPane.showInputDialog("Dönem Kodu:");
                            if (donemKodu != null) {
                                ((Personel) aktifKullanici).donemIslemleri(donemKodu);
                            }
                            break;
                    }
                });
                gbc.gridy = gridy++;
                menuPanel.add(button, gbc);
            }
        }

        JButton profilButton = new JButton("Profil Görüntüle");
        JButton cikisButton = new JButton("Çıkış Yap");

        profilButton.addActionListener(e -> {
            StringBuilder profil = new StringBuilder("Profil Bilgileri:\n");
            aktifKullanici.profilGoruntule(profil);
            logToScreen(profil.toString());
        });

        cikisButton.addActionListener(e -> {
            aktifKullanici = null;
            logToScreen("Çıkış yapıldı.");
            showLoginPanel();
        });

        gbc.gridy = gridy++;
        menuPanel.add(profilButton, gbc);
        gbc.gridy = gridy++;
        menuPanel.add(cikisButton, gbc);

        panel.removeAll();
        panel.add(menuPanel, BorderLayout.CENTER);
        panel.add(logArea.getParent(), BorderLayout.SOUTH);
        panel.revalidate();
        panel.repaint();
    }

    private static void logToScreen(String message) {
        logArea.append(message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }
} 