Plan för Chatetris – chat med tetrisinspirerat UI  (version 4.0)

Denna README är tänkt att uppdateras i takt med att nya funktioner implementerats. Den senaste versionen innehåller en målbild över det som har högst prioritet för tillfället. När målen som sammanfattats i denna text är uppfyllda uppdateras målbilden, och så kommer det att fortsätta tills inlämningsdatum eftersom alla funktioner inte kommer att hinna uppnås.

---------

Nytt sedan senast:

* Skriv ut inkomna meddelandet på UI:t, men med scenens högersida som utgångspunkt, och med annan bakgrundsbild än de lokala meddelandenas.

---------

Nya mål:

* Implementera ny "historik-funktion" med scrollbar lista över meddelanden och deras tidsstämplar, istället för att göra den primära scenen scrollbar.
* Få meddelanden att animeras ner till lägsta lediga rad, snarare än att de alltid bara glider till nästa rad.

---------

Lager (räknade från det ”understa” till det ”översta”):

1 – Scenen

Programmets scen är 640x640 pixlar. I det understa lagret ligger en .png-bild, som visuellt består av 10x10 ”rutor”, arrangerade i 10 rader och 10 kolumner. Varje rutas area är således 64x64 pixlar.

På det tredje lagret ligger en till bild med en transparent rektangel i sig med dimensionerna 512x448 pixlar, och denna rektangel är placerad i förhållande till bakgrundsbilden så att 8x7 rutor (512/64 = 8 och 448/64 = 7) är ”synliga”.

Enligt schackterminologi kan dessa rutor beskrivas som A-H (horisontellt) x 1-7 (vertikalt).

2 – Meddelanden

När man trycker på knappen för att skicka ett meddelande till Ntfy-servern syns det lokalt i form av ett tetrisinspirerat "block", detta block består av en label, som lagrar meddelandets text. Varje label visas visuellt som en rektangel, 64 pixlar hög och med en bredd mellan 64- och 512 pixlar, alltså för att passa med rutmönstret.

Dessa rektanglar/"tetrisblock" kan således anta 8 möjliga storlekar - ju längre meddelande - ju bredare block.

Blocken täcker som minst ruta A8, den osynliga raden där de skapas. Eftersom de expanderas horisontellt, åt höger, kan de som mest täcka rutorna A8 till H8. Blocken syns alltså först på rad 7, den översta av de 7 synliga raderna, men genereras på raden ovanför för att kunna animeras ner till sin första fastställda position.

3 – Ram

Ramen utgörs av en .png-bild med en transparent rektangel i mitten. Den transparenta rektangelns kanter börjar 64 pixlar ”in” från scenens kanter på vänster- höger och övre sidor. Längst ner gäller alltså istället 128 pixlars marginal.

4 – Interaktiva styrfunktioner

Ramens nedre del är extra tjock för att ge utrymme åt textfält och knappar. I testningssyfte finns två separata knappar, en som är kopplad till servern och en som är kopplad till UI:t. Slutproduktens ”skicka”-knapp är kopplad till båda.