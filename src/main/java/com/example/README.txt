Plan för Chatetris – chat med tetrisinspirerat UI  (version 2.0)

Denna README är tänkt att uppdateras i takt med att nya funktioner implementerats. Den senaste versionen innehåller en målbild över det som har högst prioritet för tillfället. När målen som sammanfattats i denna text är uppfyllda uppdateras målbilden, och så kommer det att fortsätta tills inlämningsdatum eftersom alla funktioner inte kommer att hinna uppnås.

---------

Nytt sedan senast:

* Textinmatning skrivs ut på labels.

* Labelns bredd påverkas av meddelandets längd.

---------

Nya mål:

* Uppdatera utseendet så att att varje label får en ”trä-bakgrund”, olika bakgrundsbilder, beroende på meddelandets längd.

* Importera fin font och hitta lämplig storlek och placering för meddelandet på labeln.

* Lägg till scrollfunktion.

---------

Lager (räknade från det ”understa” till det ”översta”):

1 – Scenen

Programmets scen är 640x640 pixlar. I det understa lagret ligger en .png-bild, som visuellt består av 10x10 ”rutor”, arrangerade i 10 rader och 10 kolumner. Varje rutas area är således 64x64 pixlar.

På det tredje lagret ligger en till bild med en transparent rektangel i sig med dimensionerna 512x448 pixlar, och denna rektangel är placerad i förhållande till bakgrundsbilden så att 8x7 rutor (512/64 = 8 och 448/64 = 7) är ”synliga”.

Enligt schackterminologi kan man beskriva dessa rutor som A-H (horisontellt) x 1-7 (vertikalt).

2 – Meddelanden

För att skicka ett meddelande till Ntfy-servern skriver man det i ett textfält och trycker på en knapp, detta medför att meddelandet syns lokalt, på en label. En label är utformad som en rektangel, 64 pixlar hög och med en längd mellan 64 och 512 pixlar, alltså för att passa med rutmönstret.

Meddelandet får som mest vara ett bestämt antal chars långt, så att det kan skrivas fint på upp till 512 pixlar. Den tillåtna mängden chars delas upp i 8 segment för att avgöra var tröskelvärdena för en ”expansion” går, alltså vid vilket antal chars labeln utökas med 64 pixlar.

Labeln täcker som minst ruta A7, där den skapas. Eftersom den expanderas horisontellt, åt höger, kan den som mest täcka rutorna A7 till H7.

För varje knapptryck flyttas meddelandena ner, och så fortsätter det, men på grund av ramen slutar de synas 128 pixlar upp, det är där den första synliga raden börjar.

3 – Ram

Ramen utgörs av en .png-bild med en transparent rektangel i mitten. Den transparenta rektangelns kanter börjar 64 pixlar ”in” från scenens kanter på vänster- höger och övre sidor. Längst ner gäller alltså istället 128 pixlars marginal.

4 – Interaktiva styrfunktioner

Ramens nedre del är extra tjock för att ge utrymme åt textfält och knappar.