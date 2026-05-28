<div align="center">

<!-- Animated banner using SVG -->
<img src="https://readme-typing-svg.demolab.com?font=Poppins&weight=700&size=42&duration=3000&pause=1000&color=1A4DC9&center=true&vCenter=true&width=600&height=80&lines=%F0%9F%8E%B9+NadaKu;Piano+Virtual+Android;Belajar+Musik+Interaktif" alt="NadaKu Typing SVG" />

<br/>



<br/>

<!-- Badges row 1 -->
[![Android](https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-26%20(Android%208)-orange?style=for-the-badge&logo=android)](https://developer.android.com)

<!-- Badges row 2 -->
[![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)](LICENSE)
[![Version](https://img.shields.io/badge/Version-1.0.0-blue?style=for-the-badge)](https://github.com)
[![PRs Welcome](https://img.shields.io/badge/PRs-Welcome-brightgreen?style=for-the-badge)](CONTRIBUTING.md)
[![Stars](https://img.shields.io/github/stars/username/nadaku?style=for-the-badge&color=FFD84D)](https://github.com)

<br/>

</div>

---

## 🎵 Tentang NadaKu

**NadaKu** adalah aplikasi piano virtual Android yang dirancang untuk membuat belajar musik menjadi **menyenangkan, interaktif, dan mudah** bagi semua kalangan — dari anak-anak hingga orang dewasa.

> *"Musik adalah bahasa yang bisa dipelajari siapa saja — NadaKu hadir untuk membuktikannya."*

<div align="center">

<!-- Animated counter stats -->
<img src="https://readme-typing-svg.demolab.com?font=Poppins&weight=600&size=14&duration=2000&pause=500&color=1A4DC9&center=true&vCenter=true&multiline=true&width=600&height=60&lines=50%2B+Lagu+Tersedia+%7C+10+Halaman+UI+%7C+3+Mode+Bermain+%7C+100%25+Offline" alt="Stats"/>

</div>

---

## ✨ Fitur Unggulan

<div align="center">

| 🎹 Piano Virtual | 🎯 Mode Latihan | 📊 Progres |
|:---:|:---:|:---:|
| 3 oktaf keyboard dengan tampilan DO RE MI | Falling notes dengan panduan visual | Streak harian, XP, dan badge |

| 🎵 50+ Lagu | 🎙 Rekam & Bagikan | 🌙 Dark Mode |
|:---:|:---:|:---:|
| Dari klasik hingga pop modern | Simpan permainanmu sebagai audio | Tampilan terang dan gelap |

</div>

---

## 📱 Screenshot & UI Flow

<div align="center">

```
╔══════════╗    ╔══════════╗    ╔══════════╗    ╔══════════╗
║  Splash  ║ →  ║Onboarding║ →  ║   Home   ║ →  ║  Piano   ║
║          ║    ║          ║    ║          ║    ║ DO RE MI ║
╚══════════╝    ╚══════════╝    ╚══════════╝    ╚══════════╝
                                      ↕
╔══════════╗    ╔══════════╗    ╔══════════╗    ╔══════════╗
║  Profil  ║ ←  ║  Progres ║ ←  ║ Latihan  ║ ←  ║  Lagu   ║
║ Settings ║    ║ Streak🔥 ║    ║ Scoring  ║    ║ Katalog  ║
╚══════════╝    ╚══════════╝    ╚══════════╝    ╚══════════╝
```

<!-- Animated typing for keyboard layout -->
<img src="https://readme-typing-svg.demolab.com?font=Courier+New&weight=700&size=13&duration=3000&pause=2000&color=1A4DC9&center=true&vCenter=true&multiline=true&width=560&height=100&lines=%E2%96%88+%E2%96%88+%E2%96%88%E2%96%88+%E2%96%88+%E2%96%88+%E2%96%88;%5B1%5D%5B2%5D%5B3%5D%5B4%5D%5B5%5D%5B6%5D%5B7%5D%5B1%5D;DO+RE+MI+FA+SOL+LA+TI+DO" alt="Keyboard Layout"/>

</div>

---

## 🏗 Arsitektur

<div align="center">

```
┌─────────────────────────────────────────────────┐
│                   UI Layer                       │
│         Jetpack Compose + Navigation             │
├─────────────────────────────────────────────────┤
│               ViewModel Layer                    │
│           StateFlow + LiveData                   │
├───────────────────┬─────────────────────────────┤
│   Domain Layer    │      Data Layer              │
│   Use Cases       │   Repository + Room DB       │
├───────────────────┴─────────────────────────────┤
│              Audio Engine                        │
│          Oboe + SoundPool + MIDI                 │
└─────────────────────────────────────────────────┘
```

</div>

---

## 🛠 Tech Stack

<div align="center">

<!-- Animated tech stack badges -->
![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=flat-square&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-4285F4?style=flat-square&logo=jetpackcompose&logoColor=white)
![Android](https://img.shields.io/badge/Android_Studio-3DDC84?style=flat-square&logo=androidstudio&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-FFCA28?style=flat-square&logo=firebase&logoColor=black)
![Hilt](https://img.shields.io/badge/Hilt-DI-orange?style=flat-square)
![Room](https://img.shields.io/badge/Room-Database-blue?style=flat-square)
![Retrofit](https://img.shields.io/badge/Retrofit-Network-red?style=flat-square)
![Oboe](https://img.shields.io/badge/Oboe-Audio_Engine-purple?style=flat-square)

</div>

| Layer | Library / Tool | Fungsi |
|-------|---------------|--------|
| 🎨 UI | Jetpack Compose | Declarative UI framework |
| 🧭 Navigasi | Navigation Component | Screen routing |
| 💉 DI | Hilt | Dependency injection |
| 💾 Database | Room (SQLite) | Local data persistence |
| 🔊 Audio | Oboe + SoundPool | Low-latency audio playback |
| 🌐 Network | Retrofit + OkHttp | API communication |
| 📊 Analytics | Firebase Analytics | User behavior tracking |
| 🐛 Crash | Firebase Crashlytics | Error monitoring |
| 🖼 Image | Coil | Image loading & caching |
| 🎼 MIDI | MIDI Parser | Sheet music parsing |

---

## 📁 Struktur Proyek

```
NadaKu/
├── 📂 app/
│   ├── 📂 src/main/
│   │   ├── 📂 java/com/nadaku/
│   │   │   ├── 📂 ui/
│   │   │   │   ├── 📂 splash/          # Splash screen
│   │   │   │   ├── 📂 onboarding/      # Onboarding slides
│   │   │   │   ├── 📂 home/            # Home dashboard
│   │   │   │   ├── 📂 piano/           # Piano keyboard UI ⭐
│   │   │   │   ├── 📂 songs/           # Song list & detail
│   │   │   │   ├── 📂 practice/        # Mode latihan
│   │   │   │   ├── 📂 progress/        # Statistik & progres
│   │   │   │   └── 📂 profile/         # Profil & pengaturan
│   │   │   ├── 📂 domain/
│   │   │   │   ├── 📂 model/           # Data models
│   │   │   │   └── 📂 usecase/         # Business logic
│   │   │   ├── 📂 data/
│   │   │   │   ├── 📂 local/           # Room database
│   │   │   │   ├── 📂 remote/          # API services
│   │   │   │   └── 📂 repository/      # Data repositories
│   │   │   └── 📂 audio/
│   │   │       ├── 📄 AudioEngine.kt   # Oboe audio engine
│   │   │       ├── 📄 SoundPool.kt     # SoundPool manager
│   │   │       └── 📄 MidiParser.kt    # MIDI file parser
│   │   ├── 📂 res/
│   │   │   ├── 📂 raw/                 # Audio samples (.ogg)
│   │   │   └── 📂 assets/midi/         # MIDI song files
│   │   └── 📄 AndroidManifest.xml
│   └── 📄 build.gradle.kts
├── 📄 README.md
├── 📄 PRD.md
└── 📄 LICENSE
```

---

## 🚀 Cara Menjalankan

### Prasyarat

```bash
# Pastikan sudah terinstall:
- Android Studio Hedgehog (2023.1.1) atau lebih baru
- JDK 17+
- Android SDK 26+
- NDK (untuk Oboe audio engine)
```

### Clone & Build

```bash
# 1. Clone repository
git clone https://github.com/username/nadaku.git
cd nadaku

# 2. Buka di Android Studio
# File → Open → pilih folder nadaku/

# 3. Sync Gradle
./gradlew sync

# 4. Build debug APK
./gradlew assembleDebug

# 5. Install ke device
./gradlew installDebug
```

### Setup Firebase (opsional)

```bash
# 1. Buat project di https://console.firebase.google.com
# 2. Download google-services.json
# 3. Taruh di app/google-services.json
# 4. Build ulang project
```

---

## 🎮 Cara Penggunaan

<div align="center">

```
1. Buka Aplikasi        2. Pilih Lagu          3. Mainkan Piano
     ↓                       ↓                       ↓
 [Splash Screen]    →   [Daftar Lagu]    →   [Tekan Tuts DO-TI]
     ↓                       ↓                       ↓
 [Onboarding]       →   [Detail Lagu]    →   [Ikuti Panduan 🎵]
     ↓                       ↓                       ↓
 [Home Dashboard]   →  [Pilih Mode]      →   [Lihat Skor ⭐⭐⭐]
```

</div>

### Mode Bermain

| Mode | Deskripsi | Cocok untuk |
|------|-----------|-------------|
| 🎹 **Bebas** | Main sesuka hati tanpa panduan | Eksplorasi & improvisasi |
| 🎯 **Latihan** | Falling notes dengan highlight tuts | Pemula yang belajar lagu |
| ⏱ **Tantangan** | Mode timed dengan scoring ketat | Pengguna advanced |

---

## 🎨 Design System

<div align="center">

### Palet Warna

![Primary](https://img.shields.io/badge/Primary-1A4DC9-1A4DC9?style=flat-square)
![Key White](https://img.shields.io/badge/Key%20White-F4F6FB-F4F6FB?style=flat-square&labelColor=999)
![Key Black](https://img.shields.io/badge/Key%20Black-3D4E6B-3D4E6B?style=flat-square)
![Accent](https://img.shields.io/badge/Accent-FFD84D-FFD84D?style=flat-square&labelColor=999)
![Success](https://img.shields.io/badge/Success-28C76F-28C76F?style=flat-square)
![Error](https://img.shields.io/badge/Error-EA5455-EA5455?style=flat-square)

</div>

```kotlin
// Design tokens — colors.kt
object NadaKuColors {
    val Primary     = Color(0xFF1A4DC9)  // Biru Royal
    val KeyWhite    = Color(0xFFF4F6FB)  // Putih Soft
    val KeyBlack    = Color(0xFF3D4E6B)  // Abu Gelap
    val Accent      = Color(0xFFFFD84D)  // Kuning Aktif
    val Background  = Color(0xFFF0F2F8)  // Off-white
    val Success     = Color(0xFF28C76F)  // Hijau
    val Error       = Color(0xFFEA5455)  // Merah
}
```

---

## 📊 Milestone Pengembangan

<div align="center">

```
Fase 1 ████████████████████ 100% ✅  Piano Core + Audio Engine
Fase 2 ████████████████░░░░  80% 🔄  Home, Lagu, Detail
Fase 3 ████████░░░░░░░░░░░░  40% 🔄  Mode Latihan (Falling Notes)
Fase 4 ░░░░░░░░░░░░░░░░░░░░   0% ⏳  Profil, Progres, Settings
Fase 5 ░░░░░░░░░░░░░░░░░░░░   0% ⏳  Onboarding, Polish UI
Fase 6 ░░░░░░░░░░░░░░░░░░░░   0% ⏳  QA + Play Store Release
```

| Fase | Target | Status |
|------|--------|--------|
| Fase 1 — Piano Core | Minggu 1–4 | ✅ Selesai |
| Fase 2 — Home & Lagu | Minggu 5–7 | 🔄 Dalam Progress |
| Fase 3 — Mode Latihan | Minggu 8–10 | ⏳ Belum Mulai |
| Fase 4 — Profil & Stats | Minggu 11–12 | ⏳ Belum Mulai |
| Fase 5 — UI Polish | Minggu 13–14 | ⏳ Belum Mulai |
| Fase 6 — QA & Release | Minggu 15–16 | ⏳ Belum Mulai |

</div>

---

## 💰 Monetisasi

```
FREE TIER                    PREMIUM (Rp 29.000/bln)
─────────────────            ──────────────────────────
✅ 20 lagu gratis            ✅ 50+ lagu semua genre
✅ Piano bebas               ✅ Semua mode latihan
✅ Mode latihan dasar        ✅ Rekam & ekspor audio
❌ Iklan tampil              ✅ Bebas iklan
❌ Lagu premium terkunci     ✅ Download offline
❌ Ekspor audio terbatas     ✅ Statistik lengkap
```

---

## 🤝 Kontribusi

Kontribusi sangat disambut! Silakan ikuti langkah berikut:

```bash
# 1. Fork repository ini
# 2. Buat branch fitur baru
git checkout -b feature/nama-fitur

# 3. Commit perubahanmu
git commit -m "feat: tambah fitur X"

# 4. Push ke branch
git push origin feature/nama-fitur

# 5. Buat Pull Request
```

### Konvensi Commit

| Prefix | Keterangan |
|--------|-----------|
| `feat:` | Fitur baru |
| `fix:` | Bug fix |
| `ui:` | Perubahan tampilan |
| `audio:` | Perubahan audio engine |
| `docs:` | Update dokumentasi |
| `refactor:` | Refactoring kode |

---

## 📄 Lisensi

```
MIT License — Copyright (c) 2026 NadaKu Team

Permission is hereby granted, free of charge, to any person obtaining
a copy of this software to use, copy, modify, merge, publish, and
distribute, subject to the following conditions:

The above copyright notice and this permission notice shall be included
in all copies or substantial portions of the Software.
```

---

<div align="center">

<!-- Animated footer -->
<img src="https://capsule-render.vercel.app/api?type=waving&color=1A4DC9&height=100&section=footer" width="100%"/>

<img src="https://readme-typing-svg.demolab.com?font=Poppins&size=14&duration=3000&pause=1000&color=1A4DC9&center=true&vCenter=true&width=500&lines=Dibuat+dengan+%F0%9F%8E%B9+oleh+NadaKu+Team;Belajar+Musik%2C+Kapan+Saja%2C+Di+Mana+Saja" alt="Footer typing"/>

<br/>

[![Made with ❤️](https://img.shields.io/badge/Made%20with-%E2%9D%A4%EF%B8%8F-red?style=for-the-badge)](https://github.com)
[![Kotlin](https://img.shields.io/badge/Powered%20by-Kotlin-7F52FF?style=for-the-badge&logo=kotlin)](https://kotlinlang.org)
[![Android](https://img.shields.io/badge/Built%20for-Android-3DDC84?style=for-the-badge&logo=android)](https://developer.android.com)

**⭐ Jika proyek ini bermanfaat, jangan lupa beri bintang! ⭐**

</div>
