# BrogrammerBrigade
RENDER LINK:
https://brogrammerbrigade-frontend.onrender.com/

## Our users

### Brian Brogrammer
- not attending any event and not an admin
- studentID: 35
- username: BrianBrogrammer
- password: brogrammer1

### Steph Srogrammer
- Admin of a club
- studentID: 36
- username: StephSrogrammer
- password: brogrammer2
**Admin of**
- Srogrammers
 
### Faculty Fergus
- Faculty member
- facultyMemberID: 31
- username: FacultyFergus
- password: password

**Note**: to modify an event, navigate to the club that is hosing the event select and edit from its entry in the list.

### Father Faculty
- Loves to reject funding applicaitons
- userID: 35
- username: FatherFaculty
- password: 12345678
### Goals
  - Reject funding applications and destroying dreams 
  - If I cant have fun nobody can

## Software design and architecture
Developing architecture and systems design for an online university club portal
## Group members
- Kah Meng Lee
- Henry Hamer
- Kevin Wu
- James Launder

## Structure
- diagrams
  - This is a mass store of our diagrams that we have made throughout the semester. It will be used as a dump instead of organised. Once the diagrams are finalised they will be moved into the docs folder for our submissions
- docs
  - This contains our final submissions sorted into sub directories
  - example
  - docs/part1
- Application: contains our source code
  - Frontend
  - backend
  - DB
- API: contains our api testing route

## Setup Database
` docker network create -d bridge brogrammerbrigade`

`docker run --network=brogrammerbrigade -e POSTGRES_DB=brogrammerbrigade -e POSTGRES_USER=brogammerbrigade_owner -e POSTGRES_PASSWORD=password --name databaseB -dti -p 5433:5432 postgres:13`

## Setup Backend
`docker build --rm -t brogrammerbrigade-backend .`

`docker run --network=brogrammerbrigade -dti -p 8080:8080 -e JAVA_OPTS='-Djdbc.uri=jdbc:postgresql://databaseB:5432/brogrammerbrigade -Djdbc.username=brogammerbrigade_owner -Djdbc.password=password -Dcors.origins.ui=http://localhost:5173' brogrammerbrigade-backend`

## Setup UI
`npm i`
`npm run dev`

## Our use case: Adding a club
To register a club and add it to our database:
1. Follow the link above to access our deployed application
2. Click 'register clubs' in the navbar
3. Enter information for a new club
4. Click 'submit'
5. Click 'fetch data'
6. The new club should appear in the list
