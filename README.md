# 🎵 Music Player za Android

Ova Android aplikacija je jednostavan i funkcionalan **glazbeni player** koji omogućava korisnicima pregled i reprodukciju lokalno pohranjenih glazbenih datoteka — bez reklama i nepotrebnih smetnji.

## 🚀 Značajke
- Pregled lokalnih pjesama pomoću RecyclerView
- Reprodukcija pjesama s osnovnim kontrolama (play/pause, sljedeća, prethodna)
- Prikaz trajanja i trenutnog vremena pjesme
- Mogućnost petlje (loopanja) pjesme
- Uklanjanje pjesama iz prikaza bez brisanja s uređaja
- Animirani prikaz tijekom reprodukcije
- Sučelje dizajnirano pomoću ConstraintLayout-a i XML-a

## 🛠️ Tehnologije
- Java (Android SDK)
- Android Studio
- XML (za UI)
- `MediaPlayer`, `RecyclerView`, `SharedPreferences`
- Gradle za upravljanje ovisnostima i build

## ⚙️ Postavke projekta
- Minimum SDK: **API 21 (Android 5.0 – Lollipop)**
- Nema korištenja interneta – sve datoteke su lokalne
- Bez oglasa / reklama

## 📂 Struktura projekta
- `MainActivity` – dohvat i prikaz svih pjesama
- `AdapterZaPjesme` – prikaz svake pjesme u listi
- `AktivnostPjesama` – prikaz i kontrola trenutno odabrane pjesme
- `Pjesma` – model za pohranu metapodataka o pjesmi

## 🔒 Dozvole
Potrebna dozvola za čitanje vanjske memorije:
```xml
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
```

--- 

## Aplikacija koristi jednostavno i responzivno sučelje prilagođeno različitim veličinama zaslona koristeći ConstraintLayout.
## 📑 Autor

- Fran Bosančić
- Tehnička škola Ruđera Boškovića, Zagreb
- Završni rad, travanj 2023.
