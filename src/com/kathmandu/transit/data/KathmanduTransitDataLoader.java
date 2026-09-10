package com.kathmandu.transit.data;

import com.kathmandu.transit.model.BusRoute;
import com.kathmandu.transit.model.Station;
import com.kathmandu.transit.model.VehicleType;
import com.kathmandu.transit.service.TransitGraph;

import java.util.*;

/**
 * Loads real-world transit stations and public bus routes of Kathmandu Valley.
 */
public class KathmanduTransitDataLoader {

    public static TransitGraph loadData() {
        TransitGraph graph = new TransitGraph();
        Map<String, Station> s = createStations();

        for (Station station : s.values()) {
            graph.addStation(station);
        }

        List<BusRoute> routes = createRoutes(s);
        for (BusRoute route : routes) {
            graph.addRoute(route);
        }

        return graph;
    }

    private static Map<String, Station> createStations() {
        Map<String, Station> s = new LinkedHashMap<>();

        // Central Kathmandu Hubs
        s.put("RATNAPARK", new Station("RATNAPARK", "Ratnapark", "रत्नपार्क", "Central Kathmandu", 27.7058, 85.3144, true, "Central Valley Transit Hub & Old Bus Park"));
        s.put("SUNDHARA", new Station("SUNDHARA", "Sundhara", "सुन्धारा", "Central Kathmandu", 27.7003, 85.3122, true, "Dharahara & CTC Mall area"));
        s.put("TRIPURESHWOR", new Station("TRIPURESHWOR", "Tripureshwor", "त्रिपुरेश्वर", "Central Kathmandu", 27.6942, 85.3168, true, "Dasharath Stadium junction"));
        s.put("THAPATHALI", new Station("THAPATHALI", "Thapathali", "थापाथली", "Kathmandu/Lalitpur Border", 27.6912, 85.3217, false, "Bagmati Bridge & Norvic Hospital"));
        s.put("MAITIGHAR", new Station("MAITIGHAR", "Maitighar", "माइतीघर", "Central Kathmandu", 27.6926, 85.3262, true, "Maitighar Mandala, Supreme Court"));
        s.put("DURBARMARG", new Station("DURBARMARG", "Durbarmarg", "दरबारमार्ग", "Central Kathmandu", 27.7110, 85.3175, false, "Narayanhiti Palace entrance"));
        s.put("LAZIMPAT", new Station("LAZIMPAT", "Lazimpat", "लाजिम्पाट", "North Central", 27.7215, 85.3185, false, "Embassies & Radisson Hotel"));

        // East Kathmandu & Araniko Highway Corridor
        s.put("NEW_BANESHWOR", new Station("NEW_BANESHWOR", "New Baneshwor", "नयाँ बानेश्वर", "East Kathmandu", 27.6915, 85.3420, true, "Federal Parliament building"));
        s.put("MINBHAWAN", new Station("MINBHAWAN", "Minbhawan", "मीनभवन", "East Kathmandu", 27.6872, 85.3458, false, "Civil Hospital"));
        s.put("TINKUNE", new Station("TINKUNE", "Tinkune", "तीनकुने", "East Kathmandu", 27.6845, 85.3490, true, "Subhash Chowk & Airport junction"));
        s.put("KOTESHWOR", new Station("KOTESHWOR", "Koteshwor", "कोटेश्वर", "East Kathmandu Hub", 27.6775, 85.3498, true, "Major Eastern Gateway & Ring Road interchange"));
        s.put("JADIBUTI", new Station("JADIBUTI", "Jadibuti", "जडीबुटी", "Kathmandu-Bhaktapur border", 27.6740, 85.3570, false, "Industrial area, highway link"));
        s.put("LOKANTHALI", new Station("LOKANTHALI", "Lokanthali", "लोकन्थली", "Bhaktapur", 27.6750, 85.3650, false, "Western Bhaktapur entrance"));
        s.put("KAUSHALTAR", new Station("KAUSHALTAR", "Kaushaltar", "कौशलटार", "Bhaktapur", 27.6765, 85.3720, false, "Araniko Highway commercial hub"));
        s.put("GATTHAGHAR", new Station("GATTHAGHAR", "Gatthaghar", "गठ्ठाघर", "Bhaktapur", 27.6778, 85.3790, false, "Citizen Hospital area"));
        s.put("THIMI", new Station("THIMI", "Thimi", "थिमि", "Bhaktapur", 27.6795, 85.3880, false, "Historic Pottery Square & Radhe Radhe"));
        s.put("SALLAGHARI", new Station("SALLAGHARI", "Sallaghari", "सल्लाघारी", "Bhaktapur", 27.6742, 85.4050, true, "Central Bhaktapur junction"));
        s.put("SURYABINAYAK", new Station("SURYABINAYAK", "Suryabinayak", "सूर्यविनायक", "Bhaktapur", 27.6698, 85.4245, true, "Araniko Highway Bus Terminal & Temple"));
        s.put("KAMALBINAYAK", new Station("KAMALBINAYAK", "Kamalbinayak", "कमलबिनायक", "Bhaktapur", 27.6760, 85.4385, true, "Bhaktapur Heritage Bus Park"));

        // Lalitpur (Patan)
        s.put("KUPONDOLE", new Station("KUPONDOLE", "Kupondole", "कुपण्डोल", "Lalitpur", 27.6878, 85.3180, false, "Bagmati Bridge south entrance"));
        s.put("PATAN_DHOKA", new Station("PATAN_DHOKA", "Patan Dhoka", "पाटनढोका", "Lalitpur", 27.6830, 85.3210, false, "Historic gate to Lalitpur"));
        s.put("PULCHOWK", new Station("PULCHOWK", "Pulchowk", "पुल्चोक", "Lalitpur", 27.6792, 85.3165, true, "Pulchowk Engineering Campus, UN House"));
        s.put("JAWALAKHEL", new Station("JAWALAKHEL", "Jawalakhel", "जावलाखेल", "Lalitpur", 27.6738, 85.3122, true, "Central Zoo & Administrative hub"));
        s.put("MANGALBAZAR", new Station("MANGALBAZAR", "Mangalbazar", "मंगलबजार", "Lalitpur", 27.6728, 85.3255, false, "Patan Durbar Square Heritage area"));
        s.put("LAGANKHEL", new Station("LAGANKHEL", "Lagankhel", "लगनखेल", "Lalitpur Central Hub", 27.6675, 85.3218, true, "Lalitpur Primary Bus Terminal"));

        // South & West Ring Road & West Kathmandu
        s.put("SATDOBATO", new Station("SATDOBATO", "Satdobato", "सातदोबाटो", "South Ring Road", 27.6588, 85.3255, true, "Swimming Complex & Ring Road junction"));
        s.put("GWARKO", new Station("GWARKO", "Gwarko", "ग्वार्को", "South Ring Road", 27.6660, 85.3370, false, "B&B Hospital junction & Flyover"));
        s.put("BALKUMARI", new Station("BALKUMARI", "Balkumari", "बालकुमारी", "East Ring Road", 27.6725, 85.3440, false, "Ring Road transit corridor"));
        s.put("EKANTAKUNA", new Station("EKANTAKUNA", "Ekantakuna", "एकान्तकुना", "South-West Ring Road", 27.6680, 85.3075, false, "Department of Transport Management"));
        s.put("BALKHU", new Station("BALKHU", "Balkhu", "बल्खु", "South-West Ring Road", 27.6840, 85.2970, true, "Dakshinkali & Hetauda highway junction"));
        s.put("KALIMATI", new Station("KALIMATI", "Kalimati", "कालिमाटी", "West Kathmandu", 27.6975, 85.2995, true, "Central Fruits & Vegetable Market"));
        s.put("KALANKI", new Station("KALANKI", "Kalanki", "कलंकी", "West Valley Gateway Hub", 27.6938, 85.2818, true, "Major Valley Gateway, Underpass & Bus Terminal"));
        s.put("SWAYAMBHU", new Station("SWAYAMBHU", "Swayambhu", "स्वयम्भु", "West Ring Road", 27.7125, 85.2855, false, "Monkey Temple & Ring Road bypass"));
        s.put("THANKOT", new Station("THANKOT", "Thankot", "थानकोट", "Far West Gateway", 27.6830, 85.2050, true, "Chandragiri Cable Car entrance"));

        // North & North-East Ring Road & Valley
        s.put("BALAJU", new Station("BALAJU", "Balaju", "बालाजु", "North-West Ring Road", 27.7330, 85.3015, true, "Balaju Baise Dhara & Bypass junction"));
        s.put("GONGABU", new Station("GONGABU", "Gongabu / New Buspark", "गोंगबु (नयाँ बसपार्क)", "North Ring Road", 27.7380, 85.3115, true, "National Transit & Valley Terminal"));
        s.put("SAMAKHUSI", new Station("SAMAKHUSI", "Samakhusi", "सामाखुसी", "North Ring Road", 27.7320, 85.3180, false, "Town planning corridor"));
        s.put("MAHARAJGUNJ", new Station("MAHARAJGUNJ", "Maharajgunj", "महाराजगंज", "North Ring Road Hub", 27.7375, 85.3340, true, "Teaching Hospital (TUTH) & Ring Road North"));
        s.put("BUDHANILKANTHA", new Station("BUDHANILKANTHA", "Budhanilkantha", "बुढानीलकण्ठ", "Far North Valley", 27.7780, 85.3610, true, "Sleeping Vishnu Temple & Shivapuri Base"));
        s.put("SUKEDHARA", new Station("SUKEDHARA", "Sukedhara", "सुकेधारा", "North-East Ring Road", 27.7315, 85.3470, false, "Ring Road junction"));
        s.put("KAPAN", new Station("KAPAN", "Kapan", "कपर्न", "North-East", 27.7385, 85.3605, false, "Kapan Monastery area"));
        s.put("CHABAHIL", new Station("CHABAHIL", "Chabahil", "चाबहिल", "East Central Hub", 27.7175, 85.3485, true, "Stupa Chowk & transit junction to Jorpati"));
        s.put("GAUSHALA", new Station("GAUSHALA", "Gaushala", "गौशाला", "East Central", 27.7085, 85.3490, true, "Pashupatinath Temple area"));
        s.put("AIRPORT", new Station("AIRPORT", "Airport (TIA)", "विमानस्थल (टीआईए)", "East Kathmandu", 27.7000, 85.3565, false, "Tribhuvan International Airport Gate"));
        s.put("SINAMANGAL", new Station("SINAMANGAL", "Sinamangal", "सिनामंगल", "East Kathmandu", 27.6960, 85.3520, false, "KMC Hospital & Bagmati bridge"));
        s.put("BAUDDHA", new Station("BAUDDHA", "Bauddha", "बौद्ध", "North-East Kathmandu", 27.7215, 85.3620, true, "Boudhanath World Heritage Stupa"));
        s.put("JORPATI", new Station("JORPATI", "Jorpati", "जोरपाटी", "North-East Gateway", 27.7235, 85.3780, true, "Gateway to Gokarna & Sundarijal"));

        return s;
    }

    private static List<BusRoute> createRoutes(Map<String, Station> s) {
        List<BusRoute> routes = new ArrayList<>();

        // 1. Sajha Yatayat Line 1: Lagankhel - Budhanilkantha
        routes.add(new BusRoute(
                "SAJHA_01",
                "S-01",
                "Sajha Yatayat Line 1 (Lagankhel - Budhanilkantha)",
                "Sajha Yatayat",
                VehicleType.BUS,
                Arrays.asList(
                        s.get("LAGANKHEL"),
                        s.get("JAWALAKHEL"),
                        s.get("PULCHOWK"),
                        s.get("KUPONDOLE"),
                        s.get("TRIPURESHWOR"),
                        s.get("SUNDHARA"),
                        s.get("RATNAPARK"),
                        s.get("LAZIMPAT"),
                        s.get("MAHARAJGUNJ"),
                        s.get("BUDHANILKANTHA")
                ),
                10,
                "05:30 AM - 08:30 PM",
                "#2E7D32",
                true
        ));

        // 2. Sajha Yatayat Line 2: Swayambhu - Suryabinayak (East-West Express)
        routes.add(new BusRoute(
                "SAJHA_02",
                "S-02",
                "Sajha Yatayat Line 2 (Swayambhu - Suryabinayak Express)",
                "Sajha Yatayat",
                VehicleType.BUS,
                Arrays.asList(
                        s.get("SWAYAMBHU"),
                        s.get("KALANKI"),
                        s.get("KALIMATI"),
                        s.get("TRIPURESHWOR"),
                        s.get("MAITIGHAR"),
                        s.get("NEW_BANESHWOR"),
                        s.get("MINBHAWAN"),
                        s.get("TINKUNE"),
                        s.get("KOTESHWOR"),
                        s.get("JADIBUTI"),
                        s.get("LOKANTHALI"),
                        s.get("KAUSHALTAR"),
                        s.get("GATTHAGHAR"),
                        s.get("THIMI"),
                        s.get("SALLAGHARI"),
                        s.get("SURYABINAYAK")
                ),
                12,
                "06:00 AM - 08:00 PM",
                "#1B5E20",
                true
        ));

        // 3. Mahanagar Ring Road Parikrama (Clockwise)
        routes.add(new BusRoute(
                "MAHANAGAR_CW",
                "M-CW",
                "Mahanagar Yatayat (Ring Road Clockwise Loop)",
                "Mahanagar Yatayat",
                VehicleType.BUS,
                Arrays.asList(
                        s.get("KALANKI"),
                        s.get("BALAJU"),
                        s.get("GONGABU"),
                        s.get("SAMAKHUSI"),
                        s.get("MAHARAJGUNJ"),
                        s.get("SUKEDHARA"),
                        s.get("CHABAHIL"),
                        s.get("GAUSHALA"),
                        s.get("AIRPORT"),
                        s.get("SINAMANGAL"),
                        s.get("KOTESHWOR"),
                        s.get("BALKUMARI"),
                        s.get("GWARKO"),
                        s.get("SATDOBATO"),
                        s.get("EKANTAKUNA"),
                        s.get("BALKHU"),
                        s.get("KALANKI")
                ),
                8,
                "05:00 AM - 09:00 PM",
                "#0D47A1",
                false // Loop is one-way clockwise; anti-clockwise is separate
        ));

        // 4. Mahanagar Ring Road Parikrama (Anti-Clockwise)
        routes.add(new BusRoute(
                "MAHANAGAR_CCW",
                "M-CCW",
                "Mahanagar Yatayat (Ring Road Anti-Clockwise Loop)",
                "Mahanagar Yatayat",
                VehicleType.BUS,
                Arrays.asList(
                        s.get("KALANKI"),
                        s.get("BALKHU"),
                        s.get("EKANTAKUNA"),
                        s.get("SATDOBATO"),
                        s.get("GWARKO"),
                        s.get("BALKUMARI"),
                        s.get("KOTESHWOR"),
                        s.get("SINAMANGAL"),
                        s.get("AIRPORT"),
                        s.get("GAUSHALA"),
                        s.get("CHABAHIL"),
                        s.get("SUKEDHARA"),
                        s.get("MAHARAJGUNJ"),
                        s.get("SAMAKHUSI"),
                        s.get("GONGABU"),
                        s.get("BALAJU"),
                        s.get("KALANKI")
                ),
                8,
                "05:00 AM - 09:00 PM",
                "#1976D2",
                false
        ));

        // 5. Nepal Yatayat: Balkhu - Jorpati Corridor
        routes.add(new BusRoute(
                "NEPAL_YATAYAT",
                "NY-01",
                "Nepal Yatayat (Balkhu - Jorpati Line)",
                "Nepal Yatayat",
                VehicleType.BUS,
                Arrays.asList(
                        s.get("BALKHU"),
                        s.get("KALIMATI"),
                        s.get("TRIPURESHWOR"),
                        s.get("SUNDHARA"),
                        s.get("RATNAPARK"),
                        s.get("CHABAHIL"),
                        s.get("BAUDDHA"),
                        s.get("JORPATI")
                ),
                7,
                "05:45 AM - 08:30 PM",
                "#C62828",
                true
        ));

        // 6. Bhaktapur Express: Ratnapark - Kamalbinayak
        routes.add(new BusRoute(
                "BHAKTAPUR_EXP",
                "BK-01",
                "Bhaktapur Minibus Sewa (Ratnapark - Kamalbinayak)",
                "Bhaktapur Minibus Byawasayi",
                VehicleType.MICROBUS,
                Arrays.asList(
                        s.get("RATNAPARK"),
                        s.get("SUNDHARA"),
                        s.get("MAITIGHAR"),
                        s.get("NEW_BANESHWOR"),
                        s.get("TINKUNE"),
                        s.get("KOTESHWOR"),
                        s.get("JADIBUTI"),
                        s.get("LOKANTHALI"),
                        s.get("KAUSHALTAR"),
                        s.get("GATTHAGHAR"),
                        s.get("THIMI"),
                        s.get("SALLAGHARI"),
                        s.get("KAMALBINAYAK")
                ),
                5,
                "05:30 AM - 08:30 PM",
                "#E65100",
                true
        ));

        // 7. Lalitpur Patan City Line: Ratnapark - Lagankhel
        routes.add(new BusRoute(
                "LALITPUR_CITY",
                "LT-01",
                "Patan Heritage Route (Ratnapark - Lagankhel)",
                "Lalitpur Yatayat",
                VehicleType.MICROBUS,
                Arrays.asList(
                        s.get("RATNAPARK"),
                        s.get("SUNDHARA"),
                        s.get("TRIPURESHWOR"),
                        s.get("KUPONDOLE"),
                        s.get("PULCHOWK"),
                        s.get("JAWALAKHEL"),
                        s.get("MANGALBAZAR"),
                        s.get("LAGANKHEL")
                ),
                6,
                "06:00 AM - 08:30 PM",
                "#6A1B9A",
                true
        ));

        // 8. Safa Tempo Green Route: Ratnapark - Pulchowk via Thapathali
        routes.add(new BusRoute(
                "SAFA_PATAN",
                "ST-01",
                "Safa Tempo Electric (Ratnapark - Pulchowk)",
                "Clean Energy Transport Assoc.",
                VehicleType.SAFA_TEMPO,
                Arrays.asList(
                        s.get("RATNAPARK"),
                        s.get("SUNDHARA"),
                        s.get("TRIPURESHWOR"),
                        s.get("THAPATHALI"),
                        s.get("PATAN_DHOKA"),
                        s.get("PULCHOWK")
                ),
                5,
                "06:30 AM - 07:30 PM",
                "#00897B",
                true
        ));

        // 9. Kapan Yatayat: Ratnapark - Kapan
        routes.add(new BusRoute(
                "KAPAN_LINE",
                "KP-01",
                "Kapan Micro Service (Ratnapark - Kapan)",
                "Kapan Yatayat",
                VehicleType.MICROBUS,
                Arrays.asList(
                        s.get("RATNAPARK"),
                        s.get("DURBARMARG"),
                        s.get("LAZIMPAT"),
                        s.get("SUKEDHARA"),
                        s.get("KAPAN")
                ),
                7,
                "06:00 AM - 08:00 PM",
                "#F57F17",
                true
        ));

        // 10. Chandragiri Thankot Express: Thankot - Ratnapark
        routes.add(new BusRoute(
                "THANKOT_EXP",
                "TH-01",
                "Chandragiri Thankot Express (Thankot - Ratnapark)",
                "Chandragiri Yatayat",
                VehicleType.BUS,
                Arrays.asList(
                        s.get("THANKOT"),
                        s.get("KALANKI"),
                        s.get("KALIMATI"),
                        s.get("TRIPURESHWOR"),
                        s.get("SUNDHARA"),
                        s.get("RATNAPARK")
                ),
                10,
                "05:30 AM - 08:00 PM",
                "#37474F",
                true
        ));

        return routes;
    }
}
