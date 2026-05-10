package com.yusa.autolink.data

// Dünya genelinde yaygın 37 otomobil markası ve her birinin modelleri.
// Kullanıcı araç eklerken veya randevu alırken buradan seçim yapar.
// LinkedHashMap kullanıldığı için markalar alfabetik sırada kalır.
val CAR_BRANDS_MODELS: Map<String, List<String>> = linkedMapOf(
    "Abarth"         to listOf("500", "595", "695", "124 Spider"),
    "Alfa Romeo"     to listOf("Giulia", "Giulietta", "Stelvio", "Tonale", "147", "156", "159"),
    "Audi"           to listOf("A1", "A3", "A4", "A5", "A6", "A7", "A8", "Q2", "Q3", "Q5", "Q7", "Q8", "TT", "R8", "e-tron"),
    "BMW"            to listOf("1 Serisi", "2 Serisi", "3 Serisi", "4 Serisi", "5 Serisi", "6 Serisi", "7 Serisi", "8 Serisi", "X1", "X2", "X3", "X4", "X5", "X6", "X7", "Z4", "i3", "i4", "iX"),
    "Chevrolet"      to listOf("Aveo", "Cruze", "Malibu", "Camaro", "Corvette", "Traverse", "Tahoe", "Suburban", "Equinox", "Trax"),
    "Chrysler"       to listOf("300C", "Pacifica", "Voyager"),
    "Citroën"        to listOf("C1", "C2", "C3", "C3 Aircross", "C4", "C4 Cactus", "C5", "C5 Aircross", "Berlingo", "DS3", "DS4", "DS5"),
    "Dacia"          to listOf("Sandero", "Duster", "Logan", "Jogger", "Spring", "Dokker", "Lodgy"),
    "Fiat"           to listOf("500", "500X", "500L", "Egea", "Tipo", "Panda", "Punto", "Bravo", "Doblo", "Ducato", "Fiorino"),
    "Ford"           to listOf("Fiesta", "Focus", "Mondeo", "Kuga", "Puma", "EcoSport", "Edge", "Mustang", "Explorer", "Ranger", "Transit", "Transit Connect", "Tourneo"),
    "Honda"          to listOf("Civic", "Accord", "CR-V", "HR-V", "Jazz", "City", "e", "Fit", "Pilot", "Passport"),
    "Hyundai"        to listOf("i10", "i20", "i30", "Elantra", "Sonata", "Tucson", "Santa Fe", "Kona", "Ioniq", "Ioniq 5", "Ioniq 6", "Nexo", "Bayon"),
    "Infiniti"       to listOf("Q50", "Q60", "QX50", "QX60", "QX80"),
    "Jaguar"         to listOf("XE", "XF", "XJ", "E-Pace", "F-Pace", "I-Pace", "F-Type"),
    "Jeep"           to listOf("Renegade", "Compass", "Cherokee", "Grand Cherokee", "Wrangler", "Gladiator"),
    "Kia"            to listOf("Picanto", "Rio", "Ceed", "Cerato", "Stonic", "Niro", "Sportage", "Sorento", "Telluride", "EV6", "EV9"),
    "Land Rover"     to listOf("Discovery Sport", "Discovery", "Defender", "Range Rover Evoque", "Range Rover Velar", "Range Rover Sport", "Range Rover"),
    "Lexus"          to listOf("IS", "ES", "GS", "LS", "UX", "NX", "RX", "GX", "LX", "LC", "CT"),
    "Maserati"       to listOf("Ghibli", "Quattroporte", "Levante", "GranTurismo", "MC20"),
    "Mazda"          to listOf("Mazda2", "Mazda3", "Mazda6", "CX-3", "CX-30", "CX-5", "CX-60", "MX-5", "MX-30"),
    "Mercedes-Benz"  to listOf("A Serisi", "B Serisi", "C Serisi", "E Serisi", "S Serisi", "CLA", "CLS", "GLA", "GLB", "GLC", "GLE", "GLS", "G Serisi", "AMG GT", "EQA", "EQB", "EQC", "EQE", "EQS"),
    "Mini"           to listOf("Cooper", "Cooper S", "Clubman", "Countryman", "Paceman", "Cabrio"),
    "Mitsubishi"     to listOf("Colt", "Lancer", "Galant", "Eclipse Cross", "Outlander", "ASX", "L200", "Pajero"),
    "Nissan"         to listOf("Micra", "Note", "Juke", "Qashqai", "X-Trail", "Leaf", "Ariya", "Navara", "Pathfinder", "370Z", "GT-R"),
    "Opel"           to listOf("Corsa", "Astra", "Insignia", "Mokka", "Crossland", "Grandland", "Zafira", "Combo", "Vivaro"),
    "Peugeot"        to listOf("108", "208", "308", "408", "508", "2008", "3008", "5008", "Partner", "Expert", "Boxer"),
    "Porsche"        to listOf("911", "718 Boxster", "718 Cayman", "Macan", "Cayenne", "Panamera", "Taycan"),
    "Renault"        to listOf("Clio", "Megane", "Taliant", "Laguna", "Kadjar", "Captur", "Duster", "Koleos", "Zoe", "Austral", "Arkana", "Symbol", "Fluence", "Kangoo", "Trafic", "Master"),
    "SEAT"           to listOf("Ibiza", "Leon", "Arona", "Ateca", "Tarraco", "Alhambra", "Toledo"),
    "Škoda"          to listOf("Fabia", "Scala", "Octavia", "Superb", "Kamiq", "Karoq", "Kodiaq", "Enyaq"),
    "Subaru"         to listOf("Impreza", "Legacy", "Outback", "Forester", "XV", "Crosstrek", "BRZ", "WRX"),
    "Suzuki"         to listOf("Alto", "Swift", "Baleno", "Ignis", "S-Cross", "Vitara", "Jimny", "Across"),
    "Tesla"          to listOf("Model 3", "Model S", "Model X", "Model Y", "Cybertruck"),
    "Toyota"         to listOf("Yaris", "Corolla", "Camry", "C-HR", "RAV4", "Land Cruiser", "Hilux", "Prius", "bZ4X", "Aygo", "Avensis", "Auris", "Verso"),
    "Volkswagen"     to listOf("Polo", "Golf", "Passat", "Arteon", "T-Roc", "T-Cross", "Tiguan", "Touareg", "Touran", "Caddy", "Transporter", "ID.3", "ID.4", "ID.5", "ID.7"),
    "Volvo"          to listOf("S60", "S90", "V60", "V90", "XC40", "XC60", "XC90", "C40", "EX30", "EX90")
)

// Sadece marka isimlerinin listesi — dropdown için kullanılır
val CAR_BRAND_NAMES: List<String> = CAR_BRANDS_MODELS.keys.toList()

// Verilen markaya ait modelleri döndürür. Marka bulunamazsa boş liste.
fun modelsFor(brand: String): List<String> =
    CAR_BRANDS_MODELS[brand] ?: emptyList()
