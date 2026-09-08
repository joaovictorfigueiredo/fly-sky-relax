package com.example.model

import androidx.compose.ui.graphics.Color

enum class WingStyle {
    SILK_SWALLOW,   // Classical graceful spirit
    BUTTERFLY,      // Rounded soft twin petal wings
    DRAGONFLY,      // Double sleek elongated ethereal wings
    ANGELIC_FEATHER,// Layered feathered wings
    PHOENIX_CREST,  // Pointed flaming aesthetic tips
    MANTA_GLIDE,    // Wide diamond oceanic wings
    STAR_CREST,     // Stellar geometric radiant wings
    MAGMA_FLAME,    // Flaming molten lava plumes
    GEODE_CRYSTAL,  // Faceted angular crystalline prism wings
    CYBER_NEON,     // Holographic digital laser wing blades
    RAIN_TEMPEST    // Translucent storm cloud droplet wings
}

data class Skin(
    val id: String,
    val name: String,
    val title: String,
    val icon: String,
    val requiredCollections: Int,
    val primaryColor: Color,
    val wingColor: Color,
    val auraColor: Color,
    val trailColor: Color,
    val eyeColor: Color,
    val wingStyle: WingStyle = WingStyle.SILK_SWALLOW,
    val description: String
)

object SkinRepository {
    val allSkins: List<Skin> = listOf(
        Skin(
            id = "skin_01_dawn",
            name = "Espírito da Alvorada",
            title = "Pássaro de Luz",
            icon = "🕊️",
            requiredCollections = 0,
            primaryColor = Color(0xFFFFFFFF),
            wingColor = Color(0xFFFFF9C4), // Pastel pale yellow
            auraColor = Color(0xFFFFECB3),
            trailColor = Color(0xFFFFF9C4),
            eyeColor = Color(0xFFFFD54F),
            wingStyle = WingStyle.SILK_SWALLOW,
            description = "O guia inicial da serenidade, suave como a brisa matinal."
        ),
        Skin(
            id = "skin_black_hole_singularity",
            name = "Viajante do Vazio",
            title = "Singularidade Cósmica",
            icon = "🕳️",
            requiredCollections = 15,
            primaryColor = Color(0xFF100E17), // Deep singularity core
            wingColor = Color(0xFF7C4DFF),    // Accretion disk purple
            auraColor = Color(0xFF00E5FF),    // Gravitational lensing cyan
            trailColor = Color(0xFFB388FF),
            eyeColor = Color(0xFF00E5FF),
            wingStyle = WingStyle.STAR_CREST,
            description = "Nascido no horizonte de eventos de um buraco negro. Plana imune à gravidade cósmica com rastro de partículas quânticas."
        ),
        Skin(
            id = "skin_magma_phoenix",
            name = "Fênix de Magma",
            title = "Fogo Vulcânico",
            icon = "🌋",
            requiredCollections = 20,
            primaryColor = Color(0xFF261815), // Obsidian volcanic body
            wingColor = Color(0xFFFF5722),    // Molten lava orange
            auraColor = Color(0xFFFFAB40),    // Fiery golden embers
            trailColor = Color(0xFFFF6E40),
            eyeColor = Color(0xFFFFD54F),
            wingStyle = WingStyle.MAGMA_FLAME,
            description = "Nascida nas correntes incandescentes do Manto e Magma terrestre. Asas de fogo puro e rastro de cinzas reluzentes."
        ),
        Skin(
            id = "skin_matrix_cipher",
            name = "Andarilho Matrix",
            title = "Código Cibernético",
            icon = "💻",
            requiredCollections = 30,
            primaryColor = Color(0xFF001207), // Terminal black body
            wingColor = Color(0xFF00E676),    // Phosphor green glyph wings
            auraColor = Color(0xFF69F0AE),    // Digital code pulse
            trailColor = Color(0xFF00E676),
            eyeColor = Color(0xFF00E676),
            wingStyle = WingStyle.CYBER_NEON,
            description = "Sintonizado com a dimensão Matrix. Flui em códigos binários puros e quebra as regras da física clássica."
        ),
        Skin(
            id = "skin_earth_geode",
            name = "Guardião Geodo",
            title = "Cristal Terrestre",
            icon = "💎",
            requiredCollections = 40,
            primaryColor = Color(0xFF2E2C33), // Deep earthy mineral body
            wingColor = Color(0xFFCE93D8),    // Amethyst crystalline prism
            auraColor = Color(0xFFA7FFEB),    // Bioluminescent emerald glow
            trailColor = Color(0xFFB39DDB),
            eyeColor = Color(0xFF69F0AE),
            wingStyle = WingStyle.GEODE_CRYSTAL,
            description = "Esculpido na Crosta Terrestre entre geodos de ametista e quartzo. Asas angulares que refratam a luz mineral."
        ),
        Skin(
            id = "skin_mush_fairy",
            name = "Fada dos Fungos",
            title = "Bioluminescência Mística",
            icon = "🍄",
            requiredCollections = 45,
            primaryColor = Color(0xFFF3E5F5), // Pale spore body
            wingColor = Color(0xFFE040FB),    // Neon purple fungal cap
            auraColor = Color(0xFF80DEEA),    // Glowing cyan spores
            trailColor = Color(0xFFEA80FC),
            eyeColor = Color(0xFF00E5FF),
            wingStyle = WingStyle.BUTTERFLY,
            description = "Guardiã da floresta encantada de cogumelos gigantes. Asas aveludadas que liberam esporos curativos e relaxantes."
        ),
        Skin(
            id = "skin_02_sakura",
            name = "Pétala de Sakura",
            title = "Brisa Floral",
            icon = "🌸",
            requiredCollections = 50,
            primaryColor = Color(0xFFFFF0F5),
            wingColor = Color(0xFFFFC1E3), // Pastel pink
            auraColor = Color(0xFFFFB6C1),
            trailColor = Color(0xFFFFC0CB),
            eyeColor = Color(0xFFFF69B4),
            wingStyle = WingStyle.BUTTERFLY,
            description = "Libera ao coletar 50 orbes. Desperta uma aura perfumada de cerejeira em flor."
        ),
        Skin(
            id = "skin_mars_explorer",
            name = "Sonda Marciana",
            title = "Dunas de Marte",
            icon = "🔴",
            requiredCollections = 65,
            primaryColor = Color(0xFF3E2723), // Martian alloy
            wingColor = Color(0xFFFF5722),    // Rust crimson
            auraColor = Color(0xFFFFAB91),    // Warm dust storm
            trailColor = Color(0xFFFF7043),
            eyeColor = Color(0xFFFFD54F),
            wingStyle = WingStyle.MANTA_GLIDE,
            description = "Inspirado nas grandes dunas e cânions de Marte. Plana com facilidade na tênue atmosfera avermelhada."
        ),
        Skin(
            id = "skin_cyber_spirit",
            name = "Viajante Crepuscular",
            title = "Neo-Metrópole",
            icon = "🌆",
            requiredCollections = 80,
            primaryColor = Color(0xFF181A26), // Sleek cyber carbon
            wingColor = Color(0xFF00E5FF),    // Neon electric cyan
            auraColor = Color(0xFFFF4081),    // Cyberpunk magenta pulse
            trailColor = Color(0xFF18FFFF),
            eyeColor = Color(0xFFFF80AB),
            wingStyle = WingStyle.CYBER_NEON,
            description = "Oriundo das cidades com arranha-céus ao pôr do sol. Asas holográficas com rastro de lasers e néon suave."
        ),
        Skin(
            id = "skin_tempest_valkyrie",
            name = "Valquíria Elétrica",
            title = "Olho da Tempestade",
            icon = "⚡",
            requiredCollections = 90,
            primaryColor = Color(0xFFECEFF1), // Silver ozone body
            wingColor = Color(0xFF00E5FF),    // Plasma lightning wings
            auraColor = Color(0xFF7C4DFF),    // Thundercloud violet
            trailColor = Color(0xFF80D8FF),
            eyeColor = Color(0xFF00B0FF),
            wingStyle = WingStyle.RAIN_TEMPEST,
            description = "Domina os raios e a chuva da Tempestade cósmica. Canaliza relâmpagos suaves que energizam o voo."
        ),
        Skin(
            id = "skin_03_mint",
            name = "Orvalho da Floresta",
            title = "Brisa Esmeralda",
            icon = "🍃",
            requiredCollections = 100,
            primaryColor = Color(0xFFF0FFF4),
            wingColor = Color(0xFFA7D7C5), // Pastel mint
            auraColor = Color(0xFF80CBC4),
            trailColor = Color(0xFFA8E6CF),
            eyeColor = Color(0xFF4DB6AC),
            wingStyle = WingStyle.SILK_SWALLOW,
            description = "Libera ao coletar 100 orbes. Em harmonia pura com as copas das árvores ancestrais."
        ),
        Skin(
            id = "skin_tempest_rain",
            name = "Espírito da Chuva",
            title = "Tempestade Suave",
            icon = "🌧️",
            requiredCollections = 150,
            primaryColor = Color(0xFFECEFF1), // Rain droplet mist
            wingColor = Color(0xFF90CAF9),    // Translucent storm blue
            auraColor = Color(0xFF80DEEA),    // Wet starlight shimmer
            trailColor = Color(0xFFBBDEFB),
            eyeColor = Color(0xFF42A5F5),
            wingStyle = WingStyle.RAIN_TEMPEST,
            description = "Domina as dimensões chuvosas da floresta e da cidade crepuscular. Asas de água viva e gotas ressonantes."
        ),
        Skin(
            id = "skin_core_prime",
            name = "Núcleo Primordial",
            title = "Coração da Terra",
            icon = "⚡",
            requiredCollections = 250,
            primaryColor = Color(0xFFFFFDE7), // Superdense golden core
            wingColor = Color(0xFFFFD54F),    // Magnetic plasma wings
            auraColor = Color(0xFFFFAB00),    // Pure geothermal field
            trailColor = Color(0xFFFFF176),
            eyeColor = Color(0xFFFF6D00),
            wingStyle = WingStyle.STAR_CREST,
            description = "Sintonizado com o Núcleo da Terra. Irradia campos magnéticos dourados e pulsações puras de energia do planeta."
        ),
        Skin(
            id = "skin_04_twilight",
            name = "Crepúsculo Lavanda",
            title = "Península Noturna",
            icon = "🪻",
            requiredCollections = 1000,
            primaryColor = Color(0xFFF8F5FF),
            wingColor = Color(0xFFD1C4E9), // Pastel lavender
            auraColor = Color(0xFFB39DDB),
            trailColor = Color(0xFFCE93D8),
            eyeColor = Color(0xFF9575CD),
            wingStyle = WingStyle.BUTTERFLY,
            description = "Libera ao coletar 1.000 orbes. Carrega o sussurro relaxante do anoitecer."
        ),
        Skin(
            id = "skin_05_starlight",
            name = "Cometa Prateado",
            title = "Via Láctea",
            icon = "✨",
            requiredCollections = 2000,
            primaryColor = Color(0xFFF0F4FF),
            wingColor = Color(0xFFCFD8DC), // Silvery starlight
            auraColor = Color(0xFFB0BEC5),
            trailColor = Color(0xFFECEFF1),
            eyeColor = Color(0xFF81D4FA),
            wingStyle = WingStyle.STAR_CREST,
            description = "Libera ao coletar 2.000 orbes. Lapidado pela poeira estelar mais pura do cosmos."
        ),
        Skin(
            id = "skin_06_ocean",
            name = "Manta Celestial",
            title = "Abismo Calmo",
            icon = "🌊",
            requiredCollections = 3000,
            primaryColor = Color(0xFFE0F7FA),
            wingColor = Color(0xFF80DEEA), // Cyan pastel
            auraColor = Color(0xFF4DD0E1),
            trailColor = Color(0xFFB2EBF2),
            eyeColor = Color(0xFF00ACC1),
            wingStyle = WingStyle.MANTA_GLIDE,
            description = "Libera ao coletar 3.000 orbes. Plana sobre correntes etéreas oceânicas nas nuvens."
        ),
        Skin(
            id = "skin_07_aurora",
            name = "Aurora Boreal",
            title = "Véu do Norte",
            icon = "🌌",
            requiredCollections = 4000,
            primaryColor = Color(0xFFE8F5E9),
            wingColor = Color(0xFF80E27E), // Light aurora green
            auraColor = Color(0xFF81C784),
            trailColor = Color(0xFFA7FFEB),
            eyeColor = Color(0xFF26A69A),
            wingStyle = WingStyle.DRAGONFLY,
            description = "Libera ao coletar 4.000 orbes. Ondula com luzes magnéticas dançantes do céu ártico."
        ),
        Skin(
            id = "skin_08_golden_hour",
            name = "Poente Dourado",
            title = "Calor Solar",
            icon = "🌅",
            requiredCollections = 5000,
            primaryColor = Color(0xFFFFFDE7),
            wingColor = Color(0xFFFFE082), // Golden warm
            auraColor = Color(0xFFFFD54F),
            trailColor = Color(0xFFFFF59D),
            eyeColor = Color(0xFFFFB300),
            wingStyle = WingStyle.PHOENIX_CREST,
            description = "Libera ao coletar 5.000 orbes. Aquece a alma com a luz dourada do final de tarde."
        ),
        Skin(
            id = "skin_09_nebula",
            name = "Nebulosa Cósmica",
            title = "Poente Violeta",
            icon = "🪐",
            requiredCollections = 6000,
            primaryColor = Color(0xFFF3E5F5),
            wingColor = Color(0xFFBA68C8), // Soft violet
            auraColor = Color(0xFFAB47BC),
            trailColor = Color(0xFFE1BEE7),
            eyeColor = Color(0xFFEA80FC),
            wingStyle = WingStyle.BUTTERFLY,
            description = "Libera ao coletar 6.000 orbes. Reflete o nascimento suave de constelações distantes."
        ),
        Skin(
            id = "skin_10_moonlight",
            name = "Luar Serena",
            title = "Guardião Lunar",
            icon = "🌙",
            requiredCollections = 7000,
            primaryColor = Color(0xFFF9FBE7),
            wingColor = Color(0xFFE6EE9C), // Moonlight lime pastel
            auraColor = Color(0xFFDCE775),
            trailColor = Color(0xFFF0F4C3),
            eyeColor = Color(0xFFC0CA33),
            wingStyle = WingStyle.ANGELIC_FEATHER,
            description = "Libera ao coletar 7.000 orbes. Banhado pelo brilho prateado da lua cheia."
        ),
        Skin(
            id = "skin_11_peach",
            name = "Orquídea Pêssego",
            title = "Brisa Doce",
            icon = "🍑",
            requiredCollections = 8000,
            primaryColor = Color(0xFFFFF3E0),
            wingColor = Color(0xFFFFCC80), // Pastel peach
            auraColor = Color(0xFFFFB74D),
            trailColor = Color(0xFFFFE0B2),
            eyeColor = Color(0xFFFB8C00),
            wingStyle = WingStyle.SILK_SWALLOW,
            description = "Libera ao coletar 8.000 orbes. Doçura e leveza que restauram o coração."
        ),
        Skin(
            id = "skin_12_glacier",
            name = "Gelo Cristalino",
            title = "Sopro Polar",
            icon = "❄️",
            requiredCollections = 9000,
            primaryColor = Color(0xFFF0F8FF),
            wingColor = Color(0xFFB3E5FC), // Icy light blue
            auraColor = Color(0xFF81D4FA),
            trailColor = Color(0xFFE1F5FE),
            eyeColor = Color(0xFF29B6F6),
            wingStyle = WingStyle.DRAGONFLY,
            description = "Libera ao coletar 9.000 orbes. Cristalizado em paz silenciosa e claridade mental."
        ),
        Skin(
            id = "skin_13_celestial_angel",
            name = "Querubim Celeste",
            title = "Graça Alada",
            icon = "🪽",
            requiredCollections = 10000,
            primaryColor = Color(0xFFFFFFFF),
            wingColor = Color(0xFFFFF176), // Heavenly bright yellow
            auraColor = Color(0xFFFFEE58),
            trailColor = Color(0xFFFFF9C4),
            eyeColor = Color(0xFFFFCA28),
            wingStyle = WingStyle.ANGELIC_FEATHER,
            description = "Libera ao coletar 10.000 orbes. Asas emplumadas abençoadas pelo Reino Celestial."
        ),
        Skin(
            id = "skin_14_amethyst",
            name = "Ametista Sagrada",
            title = "Vibração Cristalina",
            icon = "💎",
            requiredCollections = 11000,
            primaryColor = Color(0xFFF3E5F5),
            wingColor = Color(0xFFCE93D8), // Pastel purple
            auraColor = Color(0xFFBA68C8),
            trailColor = Color(0xFFE1BEE7),
            eyeColor = Color(0xFF8E24AA),
            wingStyle = WingStyle.STAR_CREST,
            description = "Libera ao coletar 11.000 orbes. Canaliza vibrações curativas de serenidade profunda."
        ),
        Skin(
            id = "skin_15_jade",
            name = "Lótus de Jade",
            title = "Equilíbrio Zen",
            icon = "🪷",
            requiredCollections = 12000,
            primaryColor = Color(0xFFE8F5E9),
            wingColor = Color(0xFFA5D6A7), // Soft jade green
            auraColor = Color(0xFF81C784),
            trailColor = Color(0xFFC8E6C9),
            eyeColor = Color(0xFF43A047),
            wingStyle = WingStyle.SILK_SWALLOW,
            description = "Libera ao coletar 12.000 orbes. Desabrocha a paz interior nos jardins do espírito."
        ),
        Skin(
            id = "skin_16_supernova",
            name = "Fulgor Cósmico",
            title = "Chama Estelar",
            icon = "☄️",
            requiredCollections = 13000,
            primaryColor = Color(0xFFFFF8E1),
            wingColor = Color(0xFFFFAB91), // Warm salmon stardust
            auraColor = Color(0xFFFF8A65),
            trailColor = Color(0xFFFFCCBC),
            eyeColor = Color(0xFFF4511E),
            wingStyle = WingStyle.PHOENIX_CREST,
            description = "Libera ao coletar 13.000 orbes. Uma explosão pacífica de cores que ilumina o vácuo."
        ),
        Skin(
            id = "skin_17_solar_eclipse",
            name = "Eclipse Suave",
            title = "Véu do Sol",
            icon = "☀️",
            requiredCollections = 14000,
            primaryColor = Color(0xFFFFFDE7),
            wingColor = Color(0xFFFFD54F), // Amber gold
            auraColor = Color(0xFFFFCA28),
            trailColor = Color(0xFFFFF59D),
            eyeColor = Color(0xFFFF6F00),
            wingStyle = WingStyle.STAR_CREST,
            description = "Libera ao coletar 14.000 orbes. O encontro harmônico entre a luz solar e a sombra suave."
        ),
        Skin(
            id = "skin_18_zenith",
            name = "Zênite Infinito",
            title = "Mestre das Alturas",
            icon = "🔮",
            requiredCollections = 15000,
            primaryColor = Color(0xFFEDE7F6),
            wingColor = Color(0xFFB39DDB), // High realm lilac
            auraColor = Color(0xFF9575CD),
            trailColor = Color(0xFFD1C4E9),
            eyeColor = Color(0xFF7E57C2),
            wingStyle = WingStyle.MANTA_GLIDE,
            description = "Libera ao coletar 15.000 orbes. Alçou os picos mais elevados de transcendência pacífica."
        ),
        Skin(
            id = "skin_19_transcendence",
            name = "Iluminação Arcana",
            title = "Essência Pura",
            icon = "🕊️",
            requiredCollections = 16000,
            primaryColor = Color(0xFFF0FDF4),
            wingColor = Color(0xFF6EE7B7), // Emerald neon pastel
            auraColor = Color(0xFF34D399),
            trailColor = Color(0xFFA7F3D0),
            eyeColor = Color(0xFF059669),
            wingStyle = WingStyle.ANGELIC_FEATHER,
            description = "Libera ao coletar 16.000 orbes. Transcendeu toda a turbulência terrestre para voar livre."
        ),
        Skin(
            id = "skin_20_nirvana",
            name = "Espírito Nirvana",
            title = "A Plenitude Absoluta",
            icon = "👑",
            requiredCollections = 17000,
            primaryColor = Color(0xFFFFFBF0),
            wingColor = Color(0xFFFFD700), // Pure golden radiance
            auraColor = Color(0xFFFFC107),
            trailColor = Color(0xFFFFECB3),
            eyeColor = Color(0xFFFF8F00),
            wingStyle = WingStyle.STAR_CREST,
            description = "Libera ao coletar 17.000 orbes. A forma suprema da serenidade e do relaxamento supremo."
        )
    )

    fun getSkinById(id: String): Skin {
        return allSkins.find { it.id == id } ?: allSkins[0]
    }
}
