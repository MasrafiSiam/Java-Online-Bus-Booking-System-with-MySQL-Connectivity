<div align="center">

<img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white"/>
<img src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white"/>
<img src="https://img.shields.io/badge/Swing-GUI-007396?style=for-the-badge&logo=java&logoColor=white"/>
<img src="https://img.shields.io/badge/License-MIT-green?style=for-the-badge"/>
<img src="https://img.shields.io/badge/Status-Active-brightgreen?style=for-the-badge"/>

<br/><br/>

```
██████╗ ████████╗██████╗ ███████╗
██╔══██╗╚══██╔══╝██╔══██╗██╔════╝
██████╔╝   ██║   ██████╔╝███████╗
██╔══██╗   ██║   ██╔══██╗╚════██║
██████╔╝   ██║   ██║  ██║███████║
╚═════╝    ╚═╝   ╚═╝  ╚═╝╚══════╝
```

# 🚌 Bus Ticket Reservation System

**A full-stack desktop application built with Java Swing, MySQL, and a custom dark-theme UI framework.**  
Book seats, search routes, manage bookings — all in a professional, pixel-crafted interface.

<br/>

[📸 Screenshots](#-screenshots) · [✨ Features](#-features) · [🏗️ Architecture](#️-architecture) · [⚡ Quick Start](#-quick-start) · [🗄️ Database](#️-database-setup) · [📁 Project Structure](#-project-structure) · [🛠️ Tech Stack](#️-tech-stack)

<br/>

---

</div>

<br/>

## 📸 Screenshots

<br/>

<div align="center">

| Login Screen | Dashboard |
|:---:|:---:|
| Split-panel login with feature highlights | Sidebar navigation with welcome banner |

| Search Buses | Seat Selector |
|:---:|:---:|
| Route filter with live availability badges | Visual 2+2 bus layout — red/blue/gray seats |

| My Bookings | Register |
|:---:|:---:|
| Full booking history with cancel | Live password strength indicator |

</div>

> **Note:** All screens use a custom-painted dark theme (`#0a0e17` base) with zero default Swing metal look-and-feel.

<br/>

---

## ✨ Features

<br/>

### 🔐 Authentication
- **Secure login** with email + password, Enter-key submit, show/hide password toggle
- **Registration** with live password strength bar, confirm-password matching, and email format validation
- **Duplicate email** detection before any INSERT hits the database
- **Session management** via a static `Session` class — userId, email, and display name persisted across all screens

### 🔍 Bus Search
- Filter by **origin city**, **destination city**, and optional **travel date**
- **Swap button (⇄)** instantly reverses the From/To selection
- Results table shows real-time **available seat counts** (color-coded: 🟢 >10 / 🟡 low / 🔴 full)
- Click any row to go directly to **seat selection**

### 💺 Seat Booking
- **Visual 2+2 seat map** — 10 rows × 4 seats with an aisle gap
- Seats rendered as custom-painted `JButton`s with a seat-back detail
- 🔵 Available · 🟦 Selected · 🔴 Booked — all repainted live
- **Double-check race guard**: re-queries the DB right before INSERT to handle concurrent bookings
- Animated ✅ success dialog on confirmation

### 🎟️ My Bookings
- Full booking history with bus name, route, date, time, and seat number
- **One-click cancellation** with confirmation dialog
- `DELETE WHERE id=? AND user_id=?` — users can only cancel their own bookings
- Instant UI row removal without re-querying

### 🏠 Dashboard
- **Sidebar navigation** with active-state indicator bar
- Welcome banner with user's name loaded from DB
- Quick-action cards for fast navigation
- All panels embed **inside the same JFrame** — no jarring window switches

<br/>

---

## 🏗️ Architecture

<br/>

The project follows a strict **4-layer MVC architecture**:

```
┌─────────────────────────────────────────────────────────┐
│  VIEW  (package view.*)                                  │
│  LoginGUI · RegisterGUI · DashboardGUI                   │
│  SearchBusPanel · SeatBookingGUI · MyBookingsPanel       │
│                ↓ calls                                   │
├─────────────────────────────────────────────────────────┤
│  CONTROLLER  (package controller.*)                      │
│  AuthController · BusController · BookingController      │
│                ↓ delegates                               │
├─────────────────────────────────────────────────────────┤
│  DAO  (package dao.*)                                    │
│  UserDAO · BusDAO · BookingDAO                           │
│                ↓ SQL                                     │
├─────────────────────────────────────────────────────────┤
│  MODEL + UTIL  (package model.* / util.*)                │
│  DBConnection (singleton) · Session · Theme              │
│                ↓ JDBC                                    │
├─────────────────────────────────────────────────────────┤
│  DATABASE  MySQL — btrs_db                               │
│  users · buses · bookings                                │
└─────────────────────────────────────────────────────────┘
```

### 🔄 Key Call Flows

<details>
<summary><b>Login Flow</b> (click to expand)</summary>

```
User clicks "Sign In"
  → LoginGUI.doLogin()
      → AuthController.login(email, pass)
          → UserDAO.login(email, pass)
              → DBConnection.getConnection()   [singleton]
              → SQL: SELECT * FROM users WHERE email=? AND password=?
              ← boolean (row found?)
          ← boolean
      → [if true] AuthController.getUserId(email)
      → [if true] AuthController.getName(email)
      → Session.userId = id
      → Session.email  = email
      → Session.userName = name
      → new DashboardGUI(); dispose()
```

</details>

<details>
<summary><b>Seat Booking Flow</b> (click to expand)</summary>

```
User clicks a bus row in SearchBusPanel
  → new SeatBookingGUI(busId, name, from, to)
      → buildUI()
      → loadBookedSeats()
          → SQL: SELECT seat_number FROM bookings WHERE bus_id=?
          → seatBtns[s-1].setEnabled(false)   [renders red]

User clicks a seat button
  → selectSeat(seatNo)
      → callSetSelected(prev, false)    [deselect old]
      → callSetSelected(curr, true)     [select new — renders blue]
      → bookBtn.setEnabled(true)

User clicks "Confirm Booking"
  → bookSeat()
      → Theme.confirm()                 [dialog]
      → SQL: SELECT id … (race-condition guard)
      → SQL: INSERT INTO bookings(user_id, bus_id, seat_number)
             user_id from Session.userId
      → showSuccessDialog()
```

</details>

<details>
<summary><b>Cancel Booking Flow</b> (click to expand)</summary>

```
User selects row + clicks "Cancel"
  → table.getSelectedRow() → bookingId from col 0
  → Theme.confirm()
  → BookingController.cancel(bookingId, Session.userId)
      → BookingDAO.cancel(bookingId, userId)
          → SQL: DELETE FROM bookings WHERE id=? AND user_id=?
                 (user_id prevents cancelling others' bookings)
          ← boolean
  → tableModel.removeRow(row)    [instant UI update]
```

</details>

<br/>

### 🔌 DBConnection — Singleton Pattern

```java
public static Connection getConnection() {
    if (conn == null || conn.isClosed()) {
        Class.forName("com.mysql.cj.jdbc.Driver");
        conn = DriverManager.getConnection(URL, USER, PASS);
    }
    return conn;  // all DAOs share this ONE connection
}
```

All three DAOs (`UserDAO`, `BusDAO`, `BookingDAO`) plus `SearchBusPanel` and `SeatBookingGUI` call `DBConnection.getConnection()` — they all receive the same singleton `Connection` object.

<br/>

---

## ⚡ Quick Start

<br/>

### Prerequisites

| Requirement | Version |
|---|---|
| Java JDK | 17 or higher |
| MySQL Server | 8.0+ |
| MySQL Connector/J | 8.x (JDBC driver) |

<br/>

### 1. Clone the repository

```bash
git clone https://github.com/yourusername/btrs.git
cd btrs
```

### 2. Set up the database

```bash
mysql -u root -p < btrs_db.sql
```

Or run the contents of `btrs_db.sql` manually in MySQL Workbench / phpMyAdmin.

### 3. Configure database credentials

Open `src/model/DBConnection.java` and update:

```java
private static final String URL  = "jdbc:mysql://localhost:3306/btrs_db?useSSL=false&serverTimezone=UTC";
private static final String USER = "root";       // ← your MySQL username
private static final String PASS = "";           // ← your MySQL password
```

### 4. Compile

```bash
# Place mysql-connector-java-8.x.x.jar in a lib/ folder, then:
javac -cp "lib/mysql-connector-java-8.x.x.jar" \
      -sourcepath src \
      -d out \
      Main.java
```

### 5. Run

```bash
java -cp "out:lib/mysql-connector-java-8.x.x.jar" Main

# Windows:
java -cp "out;lib\mysql-connector-java-8.x.x.jar" Main
```

> **IDE users:** Add the JDBC jar to your project's build path (IntelliJ: File → Project Structure → Libraries · Eclipse: Build Path → Add External JARs), then run `Main.java` directly.

<br/>

---

## 🗄️ Database Setup

<br/>

The file `btrs_db.sql` creates the full schema and loads 12 sample buses. Here's the structure:

```sql
-- USERS
CREATE TABLE users (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(100)  NOT NULL,
    email      VARCHAR(150)  NOT NULL UNIQUE,
    password   VARCHAR(255)  NOT NULL,
    created_at TIMESTAMP     DEFAULT CURRENT_TIMESTAMP
);

-- BUSES
CREATE TABLE buses (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    bus_name     VARCHAR(100) NOT NULL,
    route_from   VARCHAR(100) NOT NULL,
    route_to     VARCHAR(100) NOT NULL,
    travel_date  DATE         NOT NULL,
    travel_time  TIME         NOT NULL,
    total_seats  INT          NOT NULL DEFAULT 40
);

-- BOOKINGS  (enforces unique seat per bus at DB level)
CREATE TABLE bookings (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    user_id      INT NOT NULL,
    bus_id       INT NOT NULL,
    seat_number  INT NOT NULL,
    booked_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)  ON DELETE CASCADE,
    FOREIGN KEY (bus_id)  REFERENCES buses(id)  ON DELETE CASCADE,
    UNIQUE KEY uq_seat (bus_id, seat_number)      -- prevents double booking
);
```

### Sample routes included

| Bus Name | From | To | Date | Time |
|---|---|---|---|---|
| Green Line Express | Dhaka | Chittagong | 2026-04-01 | 07:00 |
| Shyamoli Paribahan | Dhaka | Chittagong | 2026-04-01 | 10:30 |
| Eagle Travels | Dhaka | Sylhet | 2026-04-01 | 08:00 |
| Royal Coach | Dhaka | Cox Bazar | 2026-04-03 | 05:30 |
| *(8 more…)* | | | | |

<br/>

---

## 📁 Project Structure

<br/>

```
btrs/
├── Main.java                          ← Entry point
├── btrs_db.sql                        ← Full database schema + sample data
│
└── src/
    ├── controller/
    │   ├── AuthController.java        ← login(), register(), getUserId(), getName()
    │   ├── BusController.java         ← getBuses(), searchBuses()
    │   └── BookingController.java     ← book(), cancel(), countBooked()
    │
    ├── dao/
    │   ├── UserDAO.java               ← SQL for users table
    │   ├── BusDAO.java                ← SQL for buses table
    │   └── BookingDAO.java            ← SQL for bookings table
    │
    ├── model/
    │   └── DBConnection.java          ← JDBC singleton connection
    │
    ├── util/
    │   ├── Session.java               ← Global user state (userId, email, userName)
    │   └── Theme.java                 ← Color palette, fonts, custom-painted widgets
    │
    └── view/
        ├── BaseFrame.java             ← Base JFrame with dark gradient background
        ├── LoginGUI.java              ← Split-panel login screen
        ├── RegisterGUI.java           ← Registration with password strength bar
        ├── DashboardGUI.java          ← Main shell with sidebar + panel swapping
        ├── SearchBusPanel.java        ← Route search + availability table
        ├── SeatBookingGUI.java        ← Visual 40-seat bus map
        ├── MyBookingsPanel.java       ← Booking history + cancellation
        └── SearchBusGUI.java          ← Standalone wrapper (backward compat)
```

<br/>

---

## 🛠️ Tech Stack

<br/>

| Layer | Technology | Details |
|---|---|---|
| Language | **Java 17+** | Core application logic |
| GUI Framework | **Java Swing** | Fully custom-painted — no default L&F |
| Database | **MySQL 8+** | Relational storage with FK constraints |
| DB Driver | **MySQL Connector/J 8.x** | JDBC type-4 driver |
| UI Design | **Custom Theme.java** | Hand-painted buttons, fields, cards, tables |
| Build | **javac / IDE** | No build tool required (Gradle/Maven optional) |

<br/>

---

## 🎨 Theme System

<br/>

All UI components are rendered through `Theme.java` — a centralized design system:

```java
// Color palette
BG_DARK      = new Color(10, 14, 23)      // main background
BG_CARD      = new Color(18, 24, 38)      // card surfaces
ACCENT       = new Color(99, 179, 237)    // sky blue — primary action color
SUCCESS      = new Color(72, 199, 142)    // green
DANGER       = new Color(252, 100, 100)   // red
TEXT_PRIMARY = new Color(226, 232, 240)   // near-white text

// Factory methods
Theme.primaryButton("Sign In")     → custom-painted rounded blue button
Theme.styledField("")              → dark rounded input field with focus glow
Theme.styledCombo(String[])        → themed dropdown
Theme.card(int arc)                → rounded card panel
Theme.styleTable(JTable)           → styled table with dark header
Theme.confirm(parent, msg)         → styled YES/NO dialog
```

Every component overrides `paintComponent(Graphics g)` using `Graphics2D` with anti-aliasing and `RoundRectangle2D` — no borders, no native OS rendering.

<br/>

---

## 🔒 Security Notes

<br/>

> This project is built for **educational purposes**. Before deploying to production, consider the following improvements:

- **Password hashing** — currently stored as plain text. Use `BCrypt` or `SHA-256` with salt:
  ```java
  // Recommended: BCrypt
  String hashed = BCrypt.hashpw(password, BCrypt.gensalt(12));
  ```
- **SQL injection** — all queries use `PreparedStatement` ✅ (already safe)
- **Connection pooling** — replace the singleton with HikariCP for concurrent users
- **Input sanitization** — email format validation exists; add server-side checks for all fields

<br/>

---

## 🗺️ Supported Routes

<br/>

The following cities are available in the route dropdowns:

```
Dhaka  ·  Chittagong  ·  Sylhet  ·  Khulna  ·  Rajshahi
Barisal  ·  Cox Bazar  ·  Mymensingh  ·  Comilla  ·  Jessore
```

To add more cities, edit the `CITIES` array in `SearchBusPanel.java`:
```java
private static final String[] CITIES = {
    "Dhaka", "Chittagong", "Sylhet", ...  // add here
};
```

<br/>

---

## 🤝 Contributing

<br/>

Contributions, bug reports, and feature requests are welcome!

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/my-new-feature`
3. Commit your changes: `git commit -m 'Add some feature'`
4. Push to the branch: `git push origin feature/my-new-feature`
5. Open a Pull Request

<br/>

---

## 📋 Roadmap

<br/>

- [ ] Password hashing with BCrypt
- [ ] Admin panel — add/edit/delete buses
- [ ] PDF ticket generation on booking
- [ ] Booking confirmation email
- [ ] Search by bus name
- [ ] Multi-language support (Bengali / English)
- [ ] Dark/light theme toggle
- [ ] Connection pooling (HikariCP)

<br/>

---

## 📄 License

<br/>

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.

```
MIT License — free to use, modify, and distribute with attribution.
```

<br/>

---

<div align="center">

Made with ☕ and Java

**[⬆ Back to top](#-bus-ticket-reservation-system)**

</div>
