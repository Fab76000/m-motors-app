# Politique de sécurité — M-Motors

## Signaler une vulnérabilité
Contacter : fabienne.berges@mmotors.com

## Versions supportées
| Version | Supportée |
|---------|-----------|
| 1.0.x   | ✅        |

## CVE identifiées via Trivy (trivy fs .)

### CVE-2026-1225 — logback-core (LOW)
- **Bibliothèque** : logback-core
- **Sévérité** : LOW
- **Statut** :  Corrigé → version 1.5.25 via dependencyManagement

### CVE-2026-29062 — jackson-core (HIGH)
- **Bibliothèque** : jackson-core
- **Sévérité** : HIGH
- **Statut** : Non corrigé
- **Raison** : fix incompatible avec Spring Boot 4.0.x
- **Décision** : surveillance active, correctif appliqué dès 
  qu'une version compatible sera disponible
