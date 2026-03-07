# M-Motors

Application web de gestion de vente et location de véhicules d'occasion.

## Aperçu

M-Motors permet aux clients de rechercher des véhicules, déposer des dossiers 100% dématérialisés et suivre leur avancement. Les administrateurs gèrent le catalogue et traitent les dossiers depuis un back-office dédié.

## Fonctionnalités

- Recherche véhicules avec filtres (marque, type, prix) et pagination
- Inscription / connexion sécurisée avec consentement RGPD
- Dépôt de dossier achat ou location + upload de documents justificatifs
- Suivi de l'avancement du dossier depuis l'espace client
- Back-office admin : gestion catalogue, validation/rejet des dossiers
- Conformité RGPD : export données, droit à l'oubli, audit logs
- Alerting automatique par email sur erreur critique

## Stack technique

| Composant | Technologie |
|-----------|-------------|
| Back-end | Java 17 + Spring Boot 4.0.1 |
| Front-end | Thymeleaf + Bootstrap 5 |
| Base de données | PostgreSQL (Heroku) |
| Sécurité | Spring Security (Session/Cookie) |
| Build | Maven |
| Cloud | Heroku (PaaS) |
| CI/CD | GitHub Actions |
| Mail | Mailjet SMTP |

## Installation locale

**Prérequis :** Java 17, Maven, PostgreSQL 15
```bash
git clone https://github.com/Fab76000/m-motors-app.git
cd m-motors-app
```

Créer la base de données :
```sql
CREATE DATABASE mmotors_db;
```

Configurer `src/main/resources/application-dev.properties` :
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/mmotors_db
spring.datasource.username=your_user
spring.datasource.password=your_password
app.upload.dir=/tmp/mmotors/uploads
```

Lancer :
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Accéder : http://localhost:8080

## Variables d'environnement (production)

| Variable | Description |
|----------|-------------|
| `DATABASE_URL` | URL PostgreSQL (fournie par Heroku) |
| `SPRING_PROFILES_ACTIVE` | `prod` |
| `MAIL_HOST` | `in-v3.mailjet.com` |
| `MAIL_USERNAME` | Clé API Mailjet |
| `MAIL_PASSWORD` | Clé secrète Mailjet |
| `MAIL_FROM` | Adresse expéditeur |
| `ADMIN_EMAIL` | Email destinataire alertes admin |
| `APP_UPLOAD_DIR` | `/tmp/mmotors/uploads` |

## Tests
```bash
mvn clean verify
```

## Déploiement
```bash
git push heroku main
```

## Licence

MIT
