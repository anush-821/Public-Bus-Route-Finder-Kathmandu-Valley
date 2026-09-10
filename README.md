# Kathmandu Valley Public Bus Route Finder
### काठमाडौं उपत्यका सार्वजनिक बस रुट खोजकर्ता

A Java application designed to help commuters find public bus routes, interchange transfers, travel time, and official fare estimates across the Kathmandu Valley (Kathmandu, Lalitpur, and Bhaktapur).

---

## 🌟 Features

- **Multi-Criteria Intelligent Route Finder**:
  - ⚡ **Fastest Route**: Minimizes total travel duration, factoring in realistic Kathmandu traffic and transfer wait times (~7 min per bus change).
  - 📏 **Shortest Distance**: Minimizes kilometers travelled across the urban network.
  - 🔄 **Fewest Transfers**: Prioritizes staying on the same vehicle to minimize bus switching.
  - 🎯 **Direct Routes Only**: Identifies direct single-bus options without interchanges.
- **Realistic Kathmandu Valley Transit Dataset**:
  - **47 Bus Stops**: Covers central hubs (Ratnapark, Sundhara, Maitighar), Ring Road junctions (Kalanki, Gongabu, Maharajgunj, Chabahil, Koteshwor, Balkhu, Satdobato), Lalitpur (Lagankhel, Pulchowk, Jawalakhel, Patan Dhoka), Bhaktapur (Sallaghari, Suryabinayak, Kamalbinayak), and valley gateways (Thankot, Budhanilkantha, Jorpati).
  - **10 Core Bus Lines**: Sajha Yatayat Lines 1 & 2, Mahanagar Ring Road (Clockwise & Anti-Clockwise), Nepal Yatayat, Bhaktapur Express, Patan Heritage Line, Safa Tempo Electric Route, Kapan Micro, and Chandragiri Thankot Express.
  - **Vehicle Categories**: Standard Buses (`[BUS]`), Microbuses/HiAce (`[MICRO]`), and Electric 3-wheelers (`[TEMPO]`).
- **Official Nepal DoTM Fare Calculator**:
  - Slabs: 0-5 km (Rs. 20), 5-10 km (Rs. 27), 10-15 km (Rs. 32), 15-20 km (Rs. 35), 20+ km (Rs. 40).
  - Legally mandated 45% student/senior concession calculation.
- **Dual User Interface**:
  - 🖥️ **Desktop GUI (Java Swing)**: Clean card-based trip summary, searchable dropdowns, transfer banners, and transit route explorer tabs.
  - 💻 **Interactive Console CLI**: Menu-driven terminal interface with ASCII transit timeline diagrams and step-by-step boarding instructions.

---

## 📁 Project Architecture

```
Public-Bus-Route-Finder-Kathmandu-Valley/
├── src/
│   └── com/kathmandu/transit/
│       ├── Main.java                          # Universal application entry point
│       ├── model/
│       │   ├── Station.java                   # Station stop with coordinates & hub flag
│       │   ├── VehicleType.java               # Enum: BUS, MICROBUS, SAFA_TEMPO
│       │   ├── BusRoute.java                  # Bus line definition and stop sequence
│       │   ├── RouteSegment.java              # Edge between adjacent stops
│       │   ├── JourneyLeg.java                # Single vehicle leg within a journey
│       │   └── JourneyPlan.java               # Complete route result with legs & fare
│       ├── service/
│       │   ├── TransitGraph.java              # Directed graph with transfer modeling
│       │   ├── RouteFinderService.java        # Dijkstra line-continuity pathfinder
│       │   ├── FareCalculatorService.java     # Official DoTM fare slab calculator
│       │   └── StationDirectoryService.java   # Station search & fuzzy matching
│       ├── data/
│       │   └── KathmanduTransitDataLoader.java# Real Kathmandu transit stations & routes
│       ├── ui/
│       │   ├── ConsoleApp.java                # Interactive CLI with ASCII timeline
│       │   └── BusFinderGUI.java              # Swing Desktop Graphical UI
│       └── test/
│           └── TransitAppTest.java            # Automated routing & fare test suite
├── scripts/
│   ├── compile.bat                            # Compile all Java sources into bin/
│   ├── run-gui.bat                            # Launch Desktop GUI
│   ├── run-cli.bat                            # Launch Interactive Terminal CLI
│   └── test.bat                               # Execute test suite
├── compile.bat                                # Root shortcut to compile
├── run-gui.bat                                # Root shortcut to run GUI
├── run-cli.bat                                # Root shortcut to run CLI
├── test.bat                                   # Root shortcut to run tests
└── README.md                                  # Documentation and guide
```

---

## 🚀 Quick Start Guide

### Prerequisites
- Java Development Kit (JDK 11, 17, 21, or 26+). Verify with `javac -version` and `java -version`.
- No third-party dependencies required (built with Java standard libraries).

### 1. Compile
Double-click `compile.bat` or run in terminal:
```bat
compile.bat
```

### 2. Launch Desktop GUI
Double-click `run-gui.bat` or run in terminal:
```bat
run-gui.bat
```
Or directly with Java:
```bat
java -cp bin com.kathmandu.transit.Main --gui
```

### 3. Launch Interactive Terminal CLI
Double-click `run-cli.bat` or run in terminal:
```bat
run-cli.bat
```
Or directly with Java:
```bat
java -cp bin com.kathmandu.transit.Main --cli
```

### 4. Run Automated Tests
Double-click `test.bat` or run:
```bat
java -cp bin com.kathmandu.transit.Main --test
```

---

## 🚍 Pre-Loaded Transit Lines

| Code | Line Name | Operator | Vehicle | Route Corridor |
|---|---|---|---|---|
| `S-01` | Sajha Line 1 | Sajha Yatayat | Standard Bus | Lagankhel ↔ Pulchowk ↔ Ratnapark ↔ Maharajgunj ↔ Budhanilkantha |
| `S-02` | Sajha Line 2 | Sajha Yatayat | Standard Bus | Swayambhu ↔ Kalanki ↔ Maitighar ↔ Koteshwor ↔ Suryabinayak |
| `M-CW` | Ring Road (CW) | Mahanagar Yatayat | Standard Bus | Kalanki → Gongabu → Maharajgunj → Chabahil → Koteshwor → Balkhu → Kalanki |
| `M-CCW`| Ring Road (CCW) | Mahanagar Yatayat | Standard Bus | Kalanki → Balkhu → Satdobato → Koteshwor → Chabahil → Gongabu → Kalanki |
| `NY-01`| Nepal Yatayat | Nepal Yatayat | Standard Bus | Balkhu ↔ Kalimati ↔ Ratnapark ↔ Chabahil ↔ Bauddha ↔ Jorpati |
| `BK-01`| Bhaktapur Express | Bhaktapur Minibus | Microbus | Ratnapark ↔ Maitighar ↔ Koteshwor ↔ Sallaghari ↔ Kamalbinayak |
| `LT-01`| Patan City Route | Lalitpur Yatayat | Microbus | Ratnapark ↔ Sundhara ↔ Pulchowk ↔ Jawalakhel ↔ Lagankhel |
| `ST-01`| Safa Tempo Green | Clean Energy Assoc.| Safa Tempo | Ratnapark ↔ Sundhara ↔ Thapathali ↔ Patan Dhoka ↔ Pulchowk |
| `KP-01`| Kapan Micro Line | Kapan Yatayat | Microbus | Ratnapark ↔ Durbarmarg ↔ Lazimpat ↔ Sukedhara ↔ Kapan |
| `TH-01`| Thankot Express | Chandragiri Yatayat | Standard Bus | Thankot ↔ Kalanki ↔ Kalimati ↔ Sundhara ↔ Ratnapark |

---

## 💰 Official Fare Table (DoTM Nepal)

| Distance (km) | Standard Single Fare (NPR) | Concession (Student / Senior 45%) |
|---|---|---|
| 0 to 5 km | Rs. 20 | Rs. 11 |
| 5 to 10 km | Rs. 27 | Rs. 15 |
| 10 to 15 km | Rs. 32 | Rs. 18 |
| 15 to 20 km | Rs. 35 | Rs. 19 |
| Above 20 km | Rs. 40 | Rs. 22 |