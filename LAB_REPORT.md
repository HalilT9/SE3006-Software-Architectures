# Lab 01 - Katmanlı Mimari Raporu
## SE 3006: Software Architecture

---

## 📋 Özet
Bu laboratuvarda, Spring Boot gibi framework'ler kullanmadan, katmanlı mimarilerin (Layered Architecture) temel prensiplerini uyguladık. Sistem, manuel bağımlılık enjeksiyonu (Dependency Injection) ve katı katmanlama kuralları ile tasarlandı.

---

## 🎯 Hedefler ve Sonuçlar

### ✅ TASK 1: Veri Erişim Katmanı (Persistence Layer)
**Hedef:** ProductRepository sınıfında veri depolama ve erişim metodları yazılması

**Yapılanlar:**
- HashMap veri tabanı: `Map<Long, Product> database`
- `findById(Long id)`: Ürünü ID'ye göre bulup döndürme
- `save(Product product)`: Ürünü veritabanında kaydetme
- Test: Başarılı ✅

---

### ✅ TASK 2: İş Mantığı Katmanı (Business Layer)
**Hedef:** OrderService'de sipariş işlemeyi sağlayan logic yazılması

**Yapılanlar:**
- ProductRepository bağımlılığı: Constructor Injection
- `placeOrder(Long productId, int quantity)`:
  - Ürünü repository'den bulma
  - Stok kontrolü (yetersiz stok → Exception)
  - Stok azaltma ve kaydetme
- Test: Başarılı ✅

---

### ✅ TASK 3: Sunum Katmanı (Presentation Layer)
**Hedef:** OrderController'da kullanıcı isteklerini işleme

**Yapılanlar:**
- OrderService bağımlılığı: Constructor Injection
- `handleUserRequest(Long productId, int quantity)`:
  - Try-catch bloğu ile hata yönetimi
  - Başarı: "✅ Order Confirmed"
  - Hata: "❌ ERROR: [hata mesajı]"
- Test: Başarılı ✅

---

### ✅ TASK 4: Sistem Önyüklemesi (Main Class)
**Hedef:** Tüm katmanları bottom-to-top sırasında oluşturma

**Yapılanlar:**
- ProductRepository oluşturma
- OrderService oluşturma ve repository injection
- OrderController oluşturma ve service injection
- 4 farklı test senaryosu çalıştırma

---

## 🏗️ Mimarı Doğrulama

| Gereksinim | Durum | Açıklama |
|-----------|-------|---------|
| HashMap kullanımı | ✅ | ProductRepository'de HashMap vardır |
| Constructor Injection | ✅ | OrderService ve OrderController'de uygulandı |
| Stok yönetimi | ✅ | Satışlarda stok güncelleniyor, kontrol ediliyor |
| Hata işleme | ✅ | Yetersiz stok ve ürün bulunamadı durumları handle edildi |
| Strict Layering | ✅ | Veri akışı yalnızca yukarıdan aşağıya (Presentation→Business→Persistence) |
| Düşük katmanlara hiç bağımlılık yok | ✅ | Persistence hiçbir üst katmanı bilmiyor |

---

## 🧪 Test Senaryoları

### Test 1: Başarılı Sipariş
```
İstek: Product ID=1, Quantity=2
Sonuç: ✅ Order Confirmed
Stok: 5 → 3 (MacBook Pro)
```

### Test 2: Ardışık Sipariş
```
İstek: Product ID=1, Quantity=1
Sonuç: ✅ Order Confirmed
Stok: 3 → 2 (MacBook Pro)
```

### Test 3: Stok Yetersiz
```
İstek: Product ID=1, Quantity=5
Sonuç: ❌ ERROR: Insufficient stock for product: MacBook Pro
Stok: 2 → 2 (değişmedi)
```

### Test 4: Ürün Bulunamadı
```
İstek: Product ID=999, Quantity=1
Sonuç: ❌ ERROR: Product not found
```

### Test 5: İkinci Ürün Başarılı
```
İstek: Product ID=2, Quantity=10
Sonuç: ✅ Order Confirmed
Stok: 20 → 10 (Logitech Mouse)
```

---

## 🎓 Öğrenilen Kavramlar

1. **Katmanlı Mimari (Layered Architecture):** Sistemin işlevsel katmanlara bölünmesi
2. **Manual Dependency Injection:** Framework yardımı olmadan nesne bağımlılıklarını yönetme
3. **Strict Layering:** Katmanlar arasında katı kurallar ve haberleşme protokolü
4. **Hata Yönetimi:** Exception kullanarak hataları uygun şekilde işleme
5. **Veri Tutarlılığı:** Transaksiyonel işlemlerde veri bütünlüğünün korunması

---

## 📂 Proje Yapısı

```
lab01/
├── src/tr/edu/mu/se3006/
│   ├── Main.java                    (Bootstrapping)
│   ├── domain/
│   │   ├── Product.java
│   │   └── Order.java
│   ├── persistence/
│   │   └── ProductRepository.java   (Veri erişim)
│   ├── business/
│   │   └── OrderService.java        (İş mantığı)
│   └── presentation/
│       └── OrderController.java     (Sunum)
└── bin/                              (Derlenmiş .class dosyaları)
```

---

## ✨ Sonuç

Tüm görevler başarıyla tamamlanmıştır. Sistem, katmanlı mimari prensiplerini tam olarak uygulayarak, kolay bakım edebilir ve test edilebilir bir yapı sağlamaktadır.

---

**Tarih:** 8 Nisan 2026  
**Derste:** SE 3006 - Software Architecture
