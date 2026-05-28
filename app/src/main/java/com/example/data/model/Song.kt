package com.example.data.model

import androidx.annotation.Keep

@Keep
enum class Difficulty {
    MUDAH, SEDANG, SULIT
}

@Keep
data class Song(
    val id: String,
    val title: String,
    val artist: String,
    val difficulty: Difficulty,
    val category: String,
    val notes: List<Note>
) {
    companion object {
        // Predefined default songs
        val DEFAULT_SONGS = listOf(
            Song(
                id = "river_flows_in_you",
                title = "River Flows In You",
                artist = "Yiruma",
                difficulty = Difficulty.SEDANG,
                category = "Populer",
                notes = listOf(
                    // Intro
                    Note(81, 0, 300), // LA (Tinggi)
                    Note(83, 300, 300), // SI (Tinggi)
                    Note(84, 600, 500), // DO (Tinggi-Tinggi)
                    Note(83, 1100, 300), // SI (Tinggi)
                    Note(84, 1400, 500), // DO (Tinggi-Tinggi)
                    Note(81, 1900, 500), // LA (Tinggi)
                    Note(79, 2400, 500), // SOL (Tinggi)
                    
                    // Main Theme
                    Note(76, 3000, 300), // MI (Tinggi)
                    Note(77, 3300, 300), // FA (Tinggi)
                    Note(79, 3600, 500), // SOL (Tinggi)
                    Note(77, 4100, 300), // FA (Tinggi)
                    Note(79, 4400, 500), // SOL (Tinggi)
                    Note(76, 4900, 500), // MI (Tinggi)
                    Note(74, 5400, 500), // RE (Tinggi)
                    
                    Note(72, 6000, 300), // DO (Tinggi)
                    Note(74, 6300, 300), // RE (Tinggi)
                    Note(76, 6600, 500), // MI (Tinggi)
                    Note(74, 7100, 300), // RE (Tinggi)
                    Note(76, 7400, 500), // MI (Tinggi)
                    Note(72, 7900, 500), // DO (Tinggi)
                    Note(71, 8400, 500), // SI
                    
                    Note(69, 9000, 500), // LA
                    Note(72, 9500, 500), // DO (Tinggi)
                    Note(71, 10000, 1000), // SI
                    
                    // Repeat section
                    Note(81, 11500, 300), Note(83, 11800, 300), Note(84, 12100, 500),
                    Note(83, 12600, 300), Note(84, 12900, 500), Note(81, 13400, 500),
                    Note(79, 13900, 500)
                )
            ),
            Song(
                id = "kemesraan",
                title = "Kemesraan",
                artist = "Iwan Fals",
                difficulty = Difficulty.MUDAH,
                category = "Populer",
                notes = listOf(
                    // DO RE MI MI MI
                    Note(60, 0, 400), Note(62, 500, 400), Note(64, 1000, 400), Note(64, 1500, 400), Note(64, 2000, 600),
                    // MI FA SOL SOL SOL
                    Note(64, 2800, 400), Note(65, 3300, 400), Note(67, 3800, 400), Note(67, 4300, 400), Note(67, 4800, 600),
                    // SOL LA SOL FA MI DO
                    Note(67, 5600, 400), Note(69, 6100, 400), Note(67, 6600, 400), Note(65, 7100, 400), Note(64, 7600, 400), Note(60, 8100, 600),
                    // RE MI FA FA FA MI RE
                    Note(62, 8900, 400), Note(64, 9400, 400), Note(65, 9900, 400), Note(65, 10400, 400), Note(65, 10900, 400), Note(64, 11400, 400), Note(62, 11900, 600)
                )
            ),
            Song(
                id = "bintang_kecil",
                title = "Bintang Kecil",
                artist = "Ibu Sud",
                difficulty = Difficulty.MUDAH,
                category = "Anak-anak",
                notes = listOf(
                    // DO RE MI FA SOL LA SOL
                    Note(60, 0, 400), Note(62, 500, 400), Note(64, 1000, 400),
                    Note(65, 1500, 400), Note(67, 2000, 400), Note(69, 2500, 400),
                    Note(67, 3000, 600),
                    // MI FA SOL LA SOL FA MI RE
                    Note(64, 4000, 400), Note(65, 4500, 400), Note(67, 5000, 400),
                    Note(69, 5500, 400), Note(67, 6000, 400), Note(65, 6500, 400),
                    Note(64, 7000, 400), Note(62, 7500, 600),
                    // FA SOL LA SI DO LA SOL SI DO
                    Note(65, 8500, 400), Note(67, 9000, 400), Note(69, 9500, 400),
                    Note(71, 10000, 400), Note(72, 10500, 400), Note(69, 11000, 400),
                    Note(67, 11500, 600), Note(71, 12500, 400), Note(72, 13000, 600)
                )
            ),
            Song(
                id = "ibu_pertiwi",
                title = "Ibu Pertiwi",
                artist = "Kusbini",
                difficulty = Difficulty.MUDAH,
                category = "Tradisional",
                notes = listOf(
                    // SOL SOL LA SOL FA MI DO DO RE MI RE
                    Note(67, 0, 400), Note(67, 500, 400), Note(69, 1000, 400), Note(67, 1500, 400),
                    Note(65, 2000, 400), Note(64, 2500, 400), Note(60, 3000, 400), Note(60, 3500, 400),
                    Note(62, 4000, 400), Note(64, 4500, 400), Note(62, 5000, 600),
                    // SOL SOL LA SOL FA MI DO MI RE RE DO
                    Note(67, 6000, 400), Note(67, 6500, 400), Note(69, 7000, 400), Note(67, 7500, 400),
                    Note(65, 8000, 400), Note(64, 8500, 400), Note(60, 9000, 400), Note(64, 9500, 400),
                    Note(62, 10000, 400), Note(62, 10500, 400), Note(60, 11000, 600)
                )
            ),
            Song(
                id = "fur_elise",
                title = "Für Elise",
                artist = "L. van Beethoven",
                difficulty = Difficulty.SULIT,
                category = "Klasik",
                notes = listOf(
                    Note(76, 0, 200), Note(75, 250, 200), Note(76, 500, 200),
                    Note(75, 750, 200), Note(76, 1000, 200), Note(71, 1250, 200),
                    Note(74, 1500, 200), Note(72, 1750, 200), Note(69, 2000, 400),
                    
                    Note(48, 2200, 200), Note(52, 2450, 200), Note(57, 2700, 200),
                    Note(60, 2950, 200), Note(64, 3200, 200), Note(69, 3450, 200),
                    Note(71, 3700, 400),
                    
                    Note(50, 3900, 200), Note(54, 4150, 200), Note(57, 4400, 200),
                    Note(64, 4650, 200), Note(68, 4900, 200), Note(71, 5150, 200),
                    Note(72, 5400, 400)
                )
            ),
            Song(
                id = "selamat_ulang_tahun",
                title = "Selamat Ulang Tahun",
                artist = "Tradisional",
                difficulty = Difficulty.MUDAH,
                category = "Anak-anak",
                notes = listOf(
                    // SOL SOL LA SOL DO SI
                    Note(67, 0, 300), Note(67, 300, 300), Note(69, 600, 600), Note(67, 1200, 600), Note(72, 1800, 600), Note(71, 2400, 1200),
                    // SOL SOL LA SOL RE DO
                    Note(67, 3800, 300), Note(67, 4100, 300), Note(69, 4400, 600), Note(67, 5000, 600), Note(74, 5600, 600), Note(72, 6200, 1200),
                    // SOL SOL SOL(T) MI DO SI LA
                    Note(67, 7600, 300), Note(67, 7900, 300), Note(79, 8200, 600), Note(76, 8800, 600), Note(72, 9400, 600), Note(71, 10000, 600), Note(69, 10600, 1200),
                    // FA(T) FA(T) MI DO RE DO
                    Note(77, 12000, 300), Note(77, 12300, 300), Note(76, 12600, 600), Note(72, 13200, 600), Note(74, 13800, 600), Note(72, 14400, 1200)
                )
            )
        )
    }
}
