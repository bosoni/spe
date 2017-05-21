SPEditori
by mjt, 2007
mixut@hotmail.com

Seikkailupeli editori, jonka avulla asetat huoneiden esteet, teht‰v‰t, tavarat, kaikki katsomista ja koskettamista varten olevat kohdat.

K‰ynnistys:

  start_spe.bat tai kirjoittamalla
    java -jar SPE.jar
  k‰ynnistyy editori.

  start_project tai kirjoittamalla
    java -jar Test_project.jar
  k‰ynnistyy projekti.


Test_project k‰ytt‰‰ samaa l‰hdekoodia kuin jujeman -peli.

Se lataa project/rooms/start.sc tiedot ja toimii sen mukaan, lataa kaikki kuvat ja
datat.


SPE ohjelmasta:

"Lataa huone" voit ladata tallennetun huoneen tiedot ja muokata niit‰.
Tallennus menee project/rooms/ hakemistoon.
pics/ hakemistossa on huoneen k‰ytt‰m‰t kuvat. Lue sitten alla oleva ohje huoneen luomiseen niin ymm‰rr‰t mit‰ eriv‰riset polygonit kuvassa tekee ym. Hypp‰‰ uuden huoneen luonnin yli jos lataat valmiin.
(myˆhemmin jos teet oman projektin, voit poistaa vanhat kuvat ja projektit tai muuttaa hakemiston nime‰)

Jos et lataa huonetta niin ensin t‰ytyy luoda huone.

// UUDEN HUONEEN LUONTI
"Uusi huone" ja annat huoneelle nimen (kuja1, baari, vessa, tj). Nimi jonka annat, sill‰ nimell‰ "Tallenna huone" tiedot tallentaa project/rooms/ hakemiston alle.
Sitten "Aseta taustakuva", valitset jonkun taustan. Tausta avautuu erilliseen ikkunaan.
"Aseta syvyyskartta" voit asettaa zbuffer kuvan jos peliohjelma tukee sit‰ (Aamukrapula peliss‰ ei viel‰ tueta).
// HUONEEN LUONTI LOPPUU

"Lataa animaatio" kysyy animaation nime‰ (ei samannimisi‰ sitten), ja sen j‰lkeen open-dialogista valitset animaatioon kuuluvat tiedostot (saat valikoitua useampia painamalla shift/ctrl n‰pp‰imen pohjaan). Tiedot tallennetaan samantien project/rooms/anim.cfg tiedostoon josta pelikoodi voi ne ladata.

"Aseta reitti", k‰ytet‰‰n animoinnissa. Ensin annat reitille nimen (ei samannimisi‰) ja sitten klikkailet reitin pisteit‰ ruudulle. Peliss‰ voi animoida jonkun hahmon liikkumaan t‰t‰ reitti‰ pitkin. Hiiren oikea lopettaa.

Jos haluat n‰kyvi‰ esineit‰ asettaa ruudulle, "Aseta esine", anna esineelle nimi (samannimisi‰ ei sitten saa olla) ja asetat sen hiirell‰ haluttuun kohtaan ruudulla.

Voit piirt‰‰ polygoneja kuvaan, jotka m‰‰r‰v‰t estett‰, tavaraa, mit‰ vain, kun klikkailet hiirell‰ ruutua. Ensin tulee 1 piste, sitten 2. piste ja viiva v‰liin, 3. piste, jne. ja hiiren oikea sulkee polygonin, eli viimeisest‰ pisteest‰ ensinm‰iseen.

"Listaa kaikki" n‰ytt‰‰ piirretyt polyt ja esineet. Viimeisin polygoni on automaattisesti valittuna, keltaisena, muut vihrein‰.

"Aseta toiminto" ikkunassa voitkin sitten asettaa onko polyn tiedot. Asetetaan polygonin sis‰lle olevalle alueelle toiminnot. Se voi olla este jonka l‰pi ei silloin voi kulkea. Selitykseen kirjoitetaan mit‰ tulostetaan jos siihen kohtaan katsoo. Jos ota/k‰yt‰ toiminto suoritetaan siihen kohtaan, "Ota/k‰yt‰ toiminnon palaute" voidaan kirjoittaa. 

Ja jos ota/k‰yt‰, h‰vitet‰‰nkˆ polygoni (eli jos ottaa jonkun esineen joka ensin est‰‰ kulun), polygonin
h‰vitetty‰ siit‰ voi kulkea (esimerkkin‰ suljettu ovi, avattua se ovi este poistetaan).

Esineen tiedot:
Voidaan valita mihin esineeseen vaikutaa ja mill‰ tavalla. Jos esineeseen Ota/k‰yt‰, siirtyykˆ se tavaroihin ja pit‰‰kˆ sen h‰vit‰ ruudulta. Jos k‰ytet‰‰n esinett‰, voi m‰‰r‰t‰ h‰vi‰‰kˆ se tavaroista. Ja sille oma teksti.
Esineiden nimet tallentuu project/rooms/items.lst tiedostoon jota ei tarvita peliss‰ mutta projektikohtaisesti
t‰ss‰ editorissa.

"Aseta paikka", annetaan paikan nimi. Klikkaat ruudulle paikan johon haluat ukon siirtyv‰n huoneenvaihdossa. N‰it‰ paikkoja voi olla useampia, eri nimet annat vain kaikille. Siihen kohtaan tulee ruksi.

Huoneen vaihto asetus:
"Huone:" textboxiin kirjoitetaan huoneen nimi (saman niminen tiedosto) ja sen per‰‰n linkki toisen huoneen aloituspaikkaan. Eli joka "Aseta paikka" napilla toisessa huoneessa m‰‰r‰t‰‰n. 
Esim baarin ulkopuolella, ovessa on linkki baarin sis‰lle:
  baari_sisa ovella
jolloin se lataa baari_sisa huoneen, asettaa ukon ovella kohtaan.


Voit myˆs ohjata toiminnon menem‰‰n skriptille. Pist‰t Huone, Selitys tai Ota/k‰yt‰ textboxeihin $funktio_nimi jolloin sen nimist‰ funktiota kutsutaan peliss‰ jos siihen polyyn tekee tietyn toiminnon.

Esim lukittu ovi.
Ota/k‰yt‰: $openDoor

skriptitiedostossa (pit‰‰ luoda tiedosto joka on samanniminen kuin huone ja per‰‰n .sc)

func openDoor()
  if action["ovi1Avattu"]==0 // jos ovi on kiinni
	action["ovi1Avattu"]=1 // aseta ovi avatuksi
    endif

  if action[""ovi1Avattu]==1
     action["ovi1Avattu"]=0 // sulje ovi
  endif
(fscripti‰)
	
"Lataa huone" ladataan huone kuvineen ja polygoneineen editoitavaksi. Koska SPE hakee kuvat project/pics/ hakemistosta, kopioi sinne kaikki k‰ytt‰m‰si kuvat niin saat ne uudelleen n‰kyviin. Tallentaessa kun ei tallenneta kuin tiedostonimi eik‰ hakemistopolkua (muuten pit‰isi pelikoodissa s‰‰t‰‰ turhia).

Muista tallentaa lopuksi, ohjelma ei huomauta ellet ole tallentanut!
--

src.rar paketissa spe:n l‰hdekoodit.
