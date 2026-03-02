# LAB211 - E-Commerce Simulation (Shopee/Lazada)

This repo contains the Java Web core (Servlet/JSP) for the LAB211 simulation project.

## Structure

```
/Student_Project
├── /data
├── /src
│   ├── /core_app
│   └── /simulator
├── /docs
│   ├── /analysis
│   └── /ai_logs
└── README.md
```

## Tech Stack
- Java Web (Servlet/JSP)
- Apache Tomcat
- MySQL (local)

## Core App (Maven WAR)
Location: `src/core_app`

### Database Setup
1. Start MySQL local server.
2. Run schema and seed scripts:

```
source data/schema.sql;
source data/seed.sql;
```

Database name: `Database`
User: `root`
Password: `1234`
Host: `localhost`
Port: `3306`

### Run (Tomcat)
1. Import `src/core_app` as a Maven project in IntelliJ/Eclipse.
2. Configure Tomcat 9+.
3. Deploy the WAR or run via server integration.
4. Visit: `http://localhost:8080/ecommerce-sim`

### Default Accounts
- Admin: `admin@local` / `123456`
- Customer: `user@local` / `123456`

## Features Implemented
- Register/Login/Logout
- Product listing + variants
- Cart (session-based)
- Checkout with voucher
- Admin product + variant management

## Notes
- Update DB settings in `src/core_app/src/main/resources/db.properties` if needed.
- Simulator (Project B) is not yet implemented.

