# Gym Booking API

REST API för bokning av gruppträningspass på ett gym.

Applikationen är byggd med Java, Spring Boot och PostgreSQL. Docker Compose används för att köra tre backend-instanser, en gemensam PostgreSQL-databas och Nginx som reverse proxy och load balancer.

## Tekniker

- Java 17
- Spring Boot
- Gradle
- Spring Data JPA
- PostgreSQL
- Docker och Docker Compose
- Nginx

## API-endpoints

| Metod | Endpoint | Beskrivning |
|---|---|---|
| GET | `/bookings` | Hämta alla bokningar |
| GET | `/bookings/{id}` | Hämta en bokning |
| POST | `/bookings` | Skapa en bokning |
| PUT | `/bookings/{id}` | Uppdatera en bokning |
| DELETE | `/bookings/{id}` | Ta bort en bokning |
| GET | `/instance` | Visa vilken backend-instans som svarar |

En bokning innehåller namn, e-postadress, datum och tid, antal personer, status och aktivitet.

Status kan vara `PENDING`, `CONFIRMED` eller `CANCELLED`.

## Starta projektet

Docker Desktop och Git behöver vara installerade.

### 1. Klona repositoryt

```bash
git clone <repository-url>
cd gym-booking-api
```

### 2. Konfigurera miljövariabler

Kopiera `.env.example` till en ny fil med namnet `.env`.

I PowerShell:

```powershell
Copy-Item .env.example .env
```

Filen innehåller följande variabler:

```env
GYM_DB_HOST=localhost
GYM_DB_PORT=5434
GYM_DB_NAME=gymdb
GYM_DB_USER=gymuser
GYM_DB_PASSWORD=change-me

PORT=8080
INSTANCE_NAME=api-1
```

Ange ett eget lösenord i `.env`.

Docker Compose använder databasuppgifterna från `.env`. Backend-containrarna ansluter till databasen via tjänstenamnet `db` och port `5432`.

`.env` är exkluderad från Git genom `.gitignore`.

### 3. Starta systemet med Docker Compose

Starta Docker Desktop och kör:

```bash
docker compose up --build -d
```

Kommandot bygger backend-applikationen och startar fem tjänster:

- `nginx`
- `api1`
- `api2`
- `api3`
- `db` (PostgreSQL)

Alla tre API-instanser använder samma Docker-image och PostgreSQL-databas.

Kontrollera att tjänsterna körs:

```bash
docker compose ps
```

PostgreSQL startas automatiskt genom Docker Compose.

## API-adress

API:et nås genom Nginx på:

```text
http://localhost:8085
```

Exempel:

```text
http://localhost:8085/bookings
http://localhost:8085/instance
```

## Testa API:et

API:et kan testas med REST Client i VS Code eller Bruno.

Exempel på en bokning:

```http
POST http://localhost:8085/bookings
Content-Type: application/json

{
  "name": "Sara Andersson",
  "email": "sara@example.com",
  "dateTime": "2026-12-01T18:00:00",
  "numberOfPeople": 1,
  "status": "CONFIRMED",
  "activity": "Spinning"
}
```

En lyckad POST-request returnerar `201 Created`.

Bokningen kan sedan hämtas genom:

```http
GET http://localhost:8085/bookings
```

## Testa load balancing

Nginx fördelar inkommande requests mellan de tre backend-instanserna med round robin.

För att kontrollera detta kan `/instance` anropas flera gånger.

Kör följande i PowerShell:

```powershell
1..9 | ForEach-Object {
    Invoke-RestMethod http://localhost:8085/instance
}
```

Svaren ska visa att olika instanser hanterar förfrågningarna, exempelvis:

```json
{
  "instance": "api-1"
}
```

```json
{
  "instance": "api-2"
}
```

```json
{
  "instance": "api-3"
}
```

Ordningen kan variera.

## Testa att stoppa en backend-instans

Stoppa den andra instansen:

```bash
docker compose stop api2
```

Kontrollera status:

```bash
docker compose ps -a
```

Skicka sedan nya förfrågningar genom Nginx:

```powershell
1..9 | ForEach-Object {
    Invoke-RestMethod http://localhost:8085/instance
}
```

Förfrågningarna ska fortfarande fungera och hanteras av `api1` och `api3`.

Kontrollera även att bokningarna fortfarande kan hämtas:

```http
GET http://localhost:8085/bookings
```

Starta sedan instansen igen:

```bash
docker compose start api2
```

## Persistent datalagring

Alla backend-instanser använder samma PostgreSQL-databas.

Bokningar lagras i en Docker-volym och finns kvar även efter att containrarna startas om.

Detta kan testas genom att:

1. Skapa en bokning via API:et.
2. Kontrollera att bokningen finns.
3. Stoppa och starta systemet.
4. Hämta bokningen igen.

Stoppa systemet:

```bash
docker compose stop
```

Starta igen:

```bash
docker compose start
```

Bokningen ska fortfarande finnas kvar.

## Stoppa systemet

```bash
docker compose down
```

Det stoppar och tar bort projektets containrar och nätverk, men behåller databasvolymen.

Använd inte `docker compose down -v` om databasens innehåll ska sparas.