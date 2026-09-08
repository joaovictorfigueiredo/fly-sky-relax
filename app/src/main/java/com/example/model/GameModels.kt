package com.example.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

enum class RealmCategory(
    val title: String,
    val emoji: String,
    val tagColor: Color
) {
    ALL("Todos", "🌐", Color(0xFFB39DDB)),
    SUBTERRANEAN("Subterrâneo", "🌋", Color(0xFFFF7043)),
    NATURE("Natureza", "🌿", Color(0xFF81C784)),
    FUTURISTIC("Tecnologia", "💻", Color(0xFF00E5FF)),
    COSMIC("Cosmos", "🌌", Color(0xFF9FA8DA)),
    MYSTICAL("Místico", "✨", Color(0xFFEA80FC))
}

enum class Realm(
    val title: String,
    val subtitle: String,
    val minAltitude: Float,
    val maxAltitude: Float,
    val skyTopColor: Color,
    val skyBottomColor: Color,
    val accentColor: Color,
    val ambientParticleName: String,
    val hasRain: Boolean = false,
    val isSubterranean: Boolean = false,
    val isCity: Boolean = false,
    val bubblesToUnlock: Int = 0,
    val category: RealmCategory = RealmCategory.NATURE,
    val loreDescription: String = "",
    val features: List<String> = emptyList()
) {
    EARTH_CORE(
        title = "Núcleo da Terra",
        subtitle = "Plasma superdenso e pulsações magnéticas",
        minAltitude = -4000f,
        maxAltitude = -3000f,
        skyTopColor = Color(0xFF330900),      // Deep core thermal dark
        skyBottomColor = Color(0xFFFFD54F),   // Radiant plasma gold
        accentColor = Color(0xFFFFF9C4),      // Pure white-gold radiance
        ambientParticleName = "Fagulhas Magnéticas",
        hasRain = false,
        isSubterranean = true,
        bubblesToUnlock = 45,
        category = RealmCategory.SUBTERRANEAN,
        loreDescription = "O coração pulsante do planeta Terra, onde a gravidade se curva em plasma luminoso e as ondas térmicas ressoam como batimentos primordiais de tranquilidade cósmica.",
        features = listOf("Plasma Térmico", "Pulso Magnético", "Brilho Dourado", "Pressão Gravitacional")
    ),
    MAGMA(
        title = "Oceano de Magma",
        subtitle = "Rios de lava viscosa e geodos incandescentes",
        minAltitude = -3000f,
        maxAltitude = -2000f,
        skyTopColor = Color(0xFF280500),      // Volcanic dark basalt
        skyBottomColor = Color(0xFFFF3D00),   // Glowing molten lava
        accentColor = Color(0xFFFFAB40),      // Amber fire sparks
        ambientParticleName = "Brasas Vulcânicas",
        hasRain = false,
        isSubterranean = true,
        bubblesToUnlock = 30,
        category = RealmCategory.SUBTERRANEAN,
        loreDescription = "Caudais de basalto líquido e cascatas de fogo silencioso que aquecem a alma. Cristais de quartzo ígneo formam arcos gigantescos sobre rios incandescentes.",
        features = listOf("Lava Fluida", "Geodos de Fogo", "Brasas Flutuantes", "Ecos Vulcânicos")
    ),
    EARTH_MANTLE(
        title = "Manto Terrestre",
        subtitle = "Convecção de calor e rochas semiderretidas",
        minAltitude = -2000f,
        maxAltitude = -1000f,
        skyTopColor = Color(0xFF3E1E0E),      // Deep earthen brown
        skyBottomColor = Color(0xFFD84315),   // Warm thermal amber
        accentColor = Color(0xFFFFCC80),      // Geothermal glow
        ambientParticleName = "Pó de Sílica",
        hasRain = false,
        isSubterranean = true,
        bubblesToUnlock = 15,
        category = RealmCategory.SUBTERRANEAN,
        loreDescription = "Uma gigantesca câmara geológica onde camadas de sílica e minerais em convecção criam veios cintilantes de ametista e topázio aquecido pela terra mãe.",
        features = listOf("Sílica Brilhante", "Convecção Geotérmica", "Veios Minerais", "Calor Suave")
    ),
    EARTH_CRUST(
        title = "Crosta Terrestre",
        subtitle = "Cavernas de cristais e raízes ancestrais",
        minAltitude = -1000f,
        maxAltitude = 0f,
        skyTopColor = Color(0xFF1E2822),      // Deep cavern stone
        skyBottomColor = Color(0xFF435B4E),   // Moss and crystal moss
        accentColor = Color(0xFFA7FFEB),      // Bioluminescent emerald/cyan
        ambientParticleName = "Gotas Minerais",
        hasRain = true,
        isSubterranean = true,
        bubblesToUnlock = 5,
        category = RealmCategory.SUBTERRANEAN,
        loreDescription = "Catedrais de estalactites com musgo bioluminescente azul-turquesa e raízes centenárias que absorvem a chuva filtrada pelas rochas.",
        features = listOf("Gotas Cristalinas", "Bioluminescência", "Raízes Antigas", "Cavernas Silenciosas")
    ),
    FOREST(
        title = "Dossel da Floresta",
        subtitle = "Folhas serenas e chuva matinal suave",
        minAltitude = 0f,
        maxAltitude = 1200f,
        skyTopColor = Color(0xFF6E9882),      // Pastel sage mint
        skyBottomColor = Color(0xFFCDE4D6),   // Gentle morning fog mist
        accentColor = Color(0xFFFFE082),      // Warm firefly gold
        ambientParticleName = "Folhas Suaves",
        hasRain = true,
        isSubterranean = false,
        bubblesToUnlock = 0,
        category = RealmCategory.NATURE,
        loreDescription = "O refúgio primordial onde o orvalho da manhã acaricia as copas verdes. Brisas aromatizadas com pinho e chuva mansa restauram o fôlego da mente.",
        features = listOf("Chuva Mansa", "Folhas Flutuantes", "Pássaros & Vento", "Névoa Matinal")
    ),
    SPACE(
        title = "Espaço Sideral",
        subtitle = "Poeira estelar e silêncio relaxante",
        minAltitude = 1200f,
        maxAltitude = 2400f,
        skyTopColor = Color(0xFF2C2449),      // Deep twilight lavender
        skyBottomColor = Color(0xFF6B588E),   // Pastel cosmic violet
        accentColor = Color(0xFFB3E5FC),      // Soft starlight cyan
        ambientParticleName = "Poeira Cósmica",
        hasRain = false,
        isSubterranean = false,
        bubblesToUnlock = 0,
        category = RealmCategory.COSMIC,
        loreDescription = "A serenidade infinita do vácuo iluminado por constelações lilases. Gravidade suave que convida ao desapego e ao fluxo livre pelo éter.",
        features = listOf("Silêncio Cósmico", "Poeira de Estrelas", "Zero Gravidade", "Nebulosas Pastéis")
    ),
    PLANETS_GALAXIES(
        title = "Planetas & Galáxias",
        subtitle = "Espirais celestes e horizontes pastéis",
        minAltitude = 2400f,
        maxAltitude = 3600f,
        skyTopColor = Color(0xFF3F2B63),      // Dreamy cosmic magenta/indigo
        skyBottomColor = Color(0xFFA58BC0),   // Pastel lilac haze
        accentColor = Color(0xFFFFAB91),      // Soft pastel peach
        ambientParticleName = "Cometas Lentos",
        hasRain = false,
        isSubterranean = false,
        bubblesToUnlock = 0,
        category = RealmCategory.COSMIC,
        loreDescription = "Gigantes gasosos com anéis cintilantes em rotação suave e luas distantes suspensas como pérolas em um céu crepuscular aveludado.",
        features = listOf("Anéis Planetários", "Cometas Cadentes", "Cores Rosadas", "Distorção Suave")
    ),
    CELESTIAL_CLOUDS(
        title = "Reino Celestial",
        subtitle = "Mar de nuvens douradas e paz infinita",
        minAltitude = 3600f,
        maxAltitude = 5000f,
        skyTopColor = Color(0xFFB5A9DE),      // Ethereal pastel twilight lavender
        skyBottomColor = Color(0xFFFDE8CD),   // Golden peach cloud glow
        accentColor = Color(0xFFFFF9C4),      // Radiant divine gold
        ambientParticleName = "Plumas de Luz",
        hasRain = false,
        isSubterranean = false,
        bubblesToUnlock = 0,
        category = RealmCategory.MYSTICAL,
        loreDescription = "O topo do cosmos onde a luz solar nunca se extingue, banhando camadas infinitas de nuvens algodão em tons de âmbar, pêssego e ouro divino.",
        features = listOf("Luz Dourada", "Plumas Celestiais", "Nuvens Algodão", "Paz Plena")
    ),
    SUNSET_CITY(
        title = "Metrópole Crepuscular",
        subtitle = "Cidades e prédios sob a chuva do pôr do sol",
        minAltitude = 5000f,
        maxAltitude = 6500f,
        skyTopColor = Color(0xFF2A1B4E),      // Twilight purple skyline
        skyBottomColor = Color(0xFFFF7043),   // Sunset orange glow
        accentColor = Color(0xFFFFD54F),      // City window gold
        ambientParticleName = "Chuva Noturna & Neons",
        hasRain = true,
        isSubterranean = false,
        isCity = true,
        bubblesToUnlock = 50,
        category = RealmCategory.FUTURISTIC,
        loreDescription = "Silhuetas urbanas e arranha-céus sob um pôr do sol violeta permanente, onde janelas douradas se acendem ao ritmo da chuva nascente.",
        features = listOf("Chuva Noturna", "Janelas Iluminadas", "Horizonte Laranja", "Brisa Urbana")
    ),
    CYBERPUNK(
        title = "Ciberpunk Neon",
        subtitle = "Arranha-céus futuristas, hologramas e chuva de néons",
        minAltitude = 6500f,
        maxAltitude = 8000f,
        skyTopColor = Color(0xFF0F051D),      // Dark cyberpunk violet
        skyBottomColor = Color(0xFFE040FB),   // Neon magenta horizon
        accentColor = Color(0xFF00E5FF),      // Cyber cyan
        ambientParticleName = "Fagulhas Holográficas",
        hasRain = true,
        isSubterranean = false,
        isCity = true,
        bubblesToUnlock = 60,
        category = RealmCategory.FUTURISTIC,
        loreDescription = "Megatorres translúcidas com propagandas holográficas em ciano e magenta, aerocarros voando silenciosamente e reflexos de néon nas gotas de chuva.",
        features = listOf("Hologramas Ciano", "Chuva de Néon", "Arranha-céus", "Linhas Cibernéticas")
    ),
    MATRIX(
        title = "Dimensão Matrix",
        subtitle = "Chuva de código binário verde e malha cibernética",
        minAltitude = 8000f,
        maxAltitude = 9500f,
        skyTopColor = Color(0xFF001207),      // Deep terminal darkness
        skyBottomColor = Color(0xFF003815),   // Matrix digital glow
        accentColor = Color(0xFF00E676),      // Phosphor digital green
        ambientParticleName = "Glifos Digitais",
        hasRain = false,
        isSubterranean = false,
        bubblesToUnlock = 70,
        category = RealmCategory.FUTURISTIC,
        loreDescription = "A arquitetura fundamental do ciberespaço renderizada em colunas de glifos verdes fosforescentes e nós digitais que pulsam com dados puros.",
        features = listOf("Cascata de Código", "Malha Tron 3D", "Glifos Verdes", "Ressonância Sintética")
    ),
    MUSHROOMS(
        title = "Floresta Psicodélica de Cogumelos",
        subtitle = "Mundos hipnóticos, esporos arco-íris e ondas caleidoscópicas",
        minAltitude = 9500f,
        maxAltitude = 11000f,
        skyTopColor = Color(0xFF140826),      // Enchanted twilight
        skyBottomColor = Color(0xFF4A148C),   // Violet spore mist
        accentColor = Color(0xFFEA80FC),      // Fluorescent fungus pink
        ambientParticleName = "Esporos Psicodélicos",
        hasRain = false,
        isSubterranean = false,
        bubblesToUnlock = 80,
        category = RealmCategory.NATURE,
        loreDescription = "Uma experiência sensorial transcendente onde o solo líquido ondula em frequências harmônicas, cogumelos gigantes emitem lasers de néon pulsantes e esporos caleidoscópicos desenham mandalas no ar.",
        features = listOf("Esporos Caleidoscópicos", "Solo Líquido Ondulante", "Cores Hipnóticas", "Gatilho Psicodélico")
    ),
    CLOUDS_SEA(
        title = "Mar de Nuvens Sonhadoras",
        subtitle = "Ondas fofas de nuvens algodão e pôr do sol dourado",
        minAltitude = 11000f,
        maxAltitude = 12500f,
        skyTopColor = Color(0xFFF8BBD0),      // Soft cotton candy pink
        skyBottomColor = Color(0xFFFFF9C4),   // Golden cream cloud tops
        accentColor = Color(0xFFFFD54F),      // Warm sunshine amber
        ambientParticleName = "Algodão de Nuvens",
        hasRain = false,
        isSubterranean = false,
        bubblesToUnlock = 90,
        category = RealmCategory.NATURE,
        loreDescription = "Um oceano sem fim feito de espumas de nuvens que ondulam suavemente sob uma abóbada cor-de-rosa e dourada, permitindo surfar pelas correntes aéreas.",
        features = listOf("Ondas de Algodão", "Surfe Aéreo", "Céu Rosa Pastel", "Suavidade Infinita")
    ),
    MOUNTAINS(
        title = "Montanhas Alpinas",
        subtitle = "Picos nevados majestosos, pinheiros e brisa gelada",
        minAltitude = 12500f,
        maxAltitude = 14000f,
        skyTopColor = Color(0xFF263238),      // Cool alpine slate
        skyBottomColor = Color(0xFFECEFF1),   // Snow peak mist
        accentColor = Color(0xFF80DEEA),      // Glacial ice blue
        ambientParticleName = "Flocos de Neve",
        hasRain = false,
        isSubterranean = false,
        bubblesToUnlock = 100,
        category = RealmCategory.NATURE,
        loreDescription = "Cordilheiras imponentes cobertas por geleiras eternas e florestas de coníferas. O ar puro e fresco clareia os pensamentos a cada rasante pelas montanhas.",
        features = listOf("Picos Glaciais", "Flocos de Neve", "Pinheiros Alpinos", "Brisa Revigorante")
    ),
    MARS(
        title = "Planeta Marte",
        subtitle = "Cânions carmesim, dunas de ferrugem e céu vermelho",
        minAltitude = 14000f,
        maxAltitude = 15500f,
        skyTopColor = Color(0xFF2E0D08),      // Deep Martian orbit
        skyBottomColor = Color(0xFFD84315),   // Rust red dunes
        accentColor = Color(0xFFFF7043),      // Red planet ember
        ambientParticleName = "Poeira Marciana",
        hasRain = false,
        isSubterranean = false,
        bubblesToUnlock = 110,
        category = RealmCategory.COSMIC,
        loreDescription = "As deslumbrantes planícies ferruginosas do Planeta Vermelho, com cânions profundos esculpidos por ventos solares e as luas Fobos e Deimos no horizonte.",
        features = listOf("Dunas Carmesim", "Cânions Gigantes", "Luas Marcianas", "Areia de Óxido")
    ),
    TEMPEST(
        title = "Tempestade Elétrica",
        subtitle = "Relâmpagos cortantes, trovoadas e ventania turbulenta",
        minAltitude = 15500f,
        maxAltitude = 17000f,
        skyTopColor = Color(0xFF0D1B2A),      // Midnight thundercloud
        skyBottomColor = Color(0xFF311B92),   // Electric violet storm
        accentColor = Color(0xFF00E5FF),      // Plasma lightning flash
        ambientParticleName = "Fagulhas Elétricas & Chuva",
        hasRain = true,
        isSubterranean = false,
        bubblesToUnlock = 120,
        category = RealmCategory.COSMIC,
        loreDescription = "Um espetáculo eletrizante de energia atmosférica, onde arcos voltaicos violeta e ciano cortam as nuvens escuras ao som abafado de trovões majestosos.",
        features = listOf("Relâmpagos de Plasma", "Tempestade Noturna", "Eletricidade Estática", "Chuva Intensa")
    ),
    ANDROMEDA(
        title = "Galáxia Andrômeda",
        subtitle = "Espirais galácticas colossais e nebulosas violetas",
        minAltitude = 17000f,
        maxAltitude = 18500f,
        skyTopColor = Color(0xFF050014),      // Deep intergalactic abyss
        skyBottomColor = Color(0xFF4A148C),   // Nebula gas violet
        accentColor = Color(0xFFEA80FC),      // Andromeda spiral dust
        ambientParticleName = "Espirais Estelares",
        hasRain = false,
        isSubterranean = false,
        bubblesToUnlock = 130,
        category = RealmCategory.COSMIC,
        loreDescription = "Quatro braços espirais colossais girando ao redor de um núcleo estelar brilhante, envolvendo o voador em fluxos cintilantes de poeira cósmica ancestral.",
        features = listOf("Núcleo Galáctico", "Braços Espirais", "Nebulosas de Gás", "Infinito Cósmico")
    ),
    MEDIEVAL(
        title = "Reino Medieval",
        subtitle = "Castelos de pedra, tochas crepitantes e fortalezas antigas",
        minAltitude = 18500f,
        maxAltitude = 20000f,
        skyTopColor = Color(0xFF1B1B1B),      // Castle night stone
        skyBottomColor = Color(0xFF5D4037),   // Warm torchlight fortress
        accentColor = Color(0xFFFFB300),      // Torch ember gold
        ambientParticleName = "Brasas de Tocha",
        hasRain = false,
        isSubterranean = false,
        bubblesToUnlock = 140,
        category = RealmCategory.MYSTICAL,
        loreDescription = "Altas muralhas góticas sob o céu noturno, com tochas crepitando nos parapeitos e torres de vigia que guardam séculos de histórias lendárias.",
        features = listOf("Muralhas de Pedra", "Tochas Douradas", "Torres Góticas", "Vento da Idade Média")
    ),
    NIGHT_FANTASY(
        title = "Fantasia Noturna",
        subtitle = "Lua colossal, lanternas flutuantes e salgueiros encantados",
        minAltitude = 20000f,
        maxAltitude = 22000f,
        skyTopColor = Color(0xFF020714),      // Ethereal deep midnight
        skyBottomColor = Color(0xFF1A237E),   // Indigo moonlight glow
        accentColor = Color(0xFF80D8FF),      // Moonbeam cyan
        ambientParticleName = "Lanternas Espirituais",
        hasRain = false,
        isSubterranean = false,
        bubblesToUnlock = 150,
        category = RealmCategory.MYSTICAL,
        loreDescription = "Uma enorme lua prateada ilumina salgueiros místicos e centenas de lanternas celestes que ascendem em silêncio levando preces de paz ao universo.",
        features = listOf("Superlua Prateada", "Lanternas Flutuantes", "Névoa Espiritual", "Reflexos Lunares")
    );

    companion object {
        fun fromAltitude(altitude: Float): Realm {
            return when {
                altitude < -3000f -> EARTH_CORE
                altitude < -2000f -> MAGMA
                altitude < -1000f -> EARTH_MANTLE
                altitude < 0f -> EARTH_CRUST
                altitude < 1200f -> FOREST
                altitude < 2400f -> SPACE
                altitude < 3600f -> PLANETS_GALAXIES
                altitude < 5000f -> CELESTIAL_CLOUDS
                altitude < 6500f -> SUNSET_CITY
                altitude < 8000f -> CYBERPUNK
                altitude < 9500f -> MATRIX
                altitude < 11000f -> MUSHROOMS
                altitude < 12500f -> CLOUDS_SEA
                altitude < 14000f -> MOUNTAINS
                altitude < 15500f -> MARS
                altitude < 17000f -> TEMPEST
                altitude < 18500f -> ANDROMEDA
                altitude < 20000f -> MEDIEVAL
                else -> NIGHT_FANTASY
            }
        }
    }
}

enum class CollectibleType {
    LIGHT,
    BUBBLE,
    FLOWER
}

data class Collectible(
    val id: Long,
    var x: Float,
    var y: Float,
    val type: CollectibleType,
    val baseRadius: Float,
    val color: Color,
    var phase: Float,
    var isCollected: Boolean = false
)

data class SparkleParticle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var color: Color,
    var size: Float,
    var life: Float = 0.4f,
    var maxLife: Float = 0.4f
)

data class ShockwaveRing(
    var x: Float,
    var y: Float,
    var currentRadius: Float = 10f,
    val maxRadius: Float = 100f,
    val color: Color = Color.White,
    var alpha: Float = 0.8f
)

data class FloatingFeedback(
    var x: Float,
    var y: Float,
    val text: String,
    val color: Color,
    var alpha: Float = 1f,
    var offsetY: Float = 0f,
    var life: Float = 1f,
    var scale: Float = 1f
)

data class AmbientParticle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var size: Float,
    var alpha: Float,
    var phase: Float,
    val color: Color
)

data class FlyerState(
    var x: Float = 0f,
    var y: Float = 0f,
    var vx: Float = 0f,
    var vy: Float = 0f,
    var angleDeg: Float = 0f,
    var wingPhase: Float = 0f,
    var glowIntensity: Float = 1f,
    var isInvulnerable: Boolean = false,
    var invulnerableTimer: Float = 0f,
    var damageFlashAlpha: Float = 0f,
    val trail: MutableList<Offset> = mutableListOf()
)

data class DimensionalPortal(
    val id: Long = 0L,
    var x: Float,
    var y: Float,
    val targetRealm: Realm,
    var color: Color = targetRealm.accentColor,
    var rotation: Float = 0f,
    var pulsePhase: Float = 0f,
    var radius: Float = 55f
) {
    var phase: Float
        get() = pulsePhase
        set(value) { pulsePhase = value }
}

data class RainDrop(
    var x: Float,
    var y: Float,
    var speed: Float,
    var length: Float,
    var alpha: Float
)

data class MagmaEmber(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var size: Float,
    var color: Color,
    var alpha: Float,
    var phase: Float
) {
    val radius: Float get() = size
}

enum class PredatorCategory {
    CUTE,
    UGLY
}

enum class PredatorType(
    val displayName: String,
    val category: PredatorCategory,
    val description: String,
    val emoji: String,
    val primaryColor: Color,
    val secondaryColor: Color
) {
    // --- PREDADORES FOFINHOS ---
    CUTE_BAT(
        displayName = "Pipoca Estelar",
        category = PredatorCategory.CUTE,
        description = "Morceguinho peludinho com orelhinhas redondas e olhos brilhantes",
        emoji = "🦇💖",
        primaryColor = Color(0xFFCE93D8),
        secondaryColor = Color(0xFFFF80AB)
    ),
    CUTE_JELLY(
        displayName = "Medusinha Cósmica",
        category = PredatorCategory.CUTE,
        description = "Flutua alegre soltando bolhinhas doces e coraçõezinhos",
        emoji = "🪼✨",
        primaryColor = Color(0xFF80DEEA),
        secondaryColor = Color(0xFFF48FB1)
    ),
    CUTE_BABY_DRAGON(
        displayName = "Dragãozinho Nuvem",
        category = PredatorCategory.CUTE,
        description = "Bebê dragão rechonchudo com asinhas minúsculas e bochechas rosadas",
        emoji = "🐲☁️",
        primaryColor = Color(0xFFA7FFEB),
        secondaryColor = Color(0xFFFFE082)
    ),

    // --- PREDADORES FEIOS ---
    UGLY_ABYSS_BEAST(
        displayName = "Devorador das Trevas",
        category = PredatorCategory.UGLY,
        description = "Monstro abissal com olho ciclope ensanguentado e dentes pontiagudos",
        emoji = "👁️👾",
        primaryColor = Color(0xFF1A1A24),
        secondaryColor = Color(0xFFFF1744)
    ),
    UGLY_MAGMA_WORM(
        displayName = "Vorme de Magma",
        category = PredatorCategory.UGLY,
        description = "Carapaça escarpada de rocha negra escorrendo lava fervente",
        emoji = "🪱🔥",
        primaryColor = Color(0xFF3E2723),
        secondaryColor = Color(0xFFFF3D00)
    ),
    UGLY_VOID_CRAWLER(
        displayName = "Rastejador do Vazio",
        category = PredatorCategory.UGLY,
        description = "Besta espinhosa com mandíbulas afiadas e ferrões venenosos",
        emoji = "🕷️💀",
        primaryColor = Color(0xFF0D0221),
        secondaryColor = Color(0xFF76FF03)
    )
}

data class Predator(
    val id: Long,
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    val type: PredatorType,
    val size: Float,
    var phase: Float = 0f,
    var isPacified: Boolean = false,
    var pacifiedTimer: Float = 0f
)

data class BlackHole(
    val id: Long,
    var x: Float,
    var y: Float,
    val radius: Float = 65f,
    var phase: Float = 0f,
    var rotation: Float = 0f,
    val pullRadius: Float = 300f
)

data class MatrixCodeStream(
    var x: Float,
    var y: Float,
    var speed: Float,
    val chars: String,
    var charIndex: Int = 0,
    var alpha: Float = 0.85f
)

