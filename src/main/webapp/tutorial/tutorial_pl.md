## Tutorial DataNet

### Wprowadzenie

Niniejszy tutorial prowadzi czytelnika przez proces tworzenia modelu danych oraz zapisu danych dla wizualizacji
algorytmu sortowania. Użyty zostanie algorytm sortowania przez wstawianie a wszystkie pośrednie kroki zostaną
zapamiętane aby wygenerować końcowy film. Jako platforma wykonawcza zostanie użyta  platforma GridSpace
(https://gs2.plgrid.pl).

### Model danych

W naszym eksperymencie sortującym będziemy chcieli zapisać wejściową serię liczb oraz końcowy film wizualizujący
proces sortowania jak również wszystkie pośrednie kroki sortowania. Do tego celu użyjemy modelu składającego się
z trzech powiązanych encji. Proszę podążać za krokami poniżej w celu wykonania tego zadania.

1. Proszę zalogować się do DataNet [tutaj](../../) (potrzebne będą login i hasło PLGrid).

2. Utwórz nowy model przez kliknięcie przycisku <button class="btn btn-success"><i class="icon-plus"></i> Dodaj nowy model</button>.

3. Stwórz nową encję za pomocą przycisku <button class="btn btn-success"><i class="icon-plus"> Nowa encja</i></button> i nazwij ją *Input*.

4. Za pomocą przycisku <button class="btn btn-success"><i class="icon-plus"> Nowe pole</i></button> dodaj nowe pole o nazwie *input* i ustaw jego typ na *Integer[]*
   (ten typ reprezentuje tablicę liczb całkowitych).

5. Dodaj kolejne pole o nazwie *description*, pozostaw jego typ ustawiony na *String* i odznacz pole *wymagane*.

6. Zapisz model przez kliknięcie przycisku <button class="btn"><i class="icon-save"> Zapisz</i></button>.

    Model powinien przypominać ten z obrazka poniżej.
    
    ![Model sortowania z jedną encją](resources/tut-model-one-entity.png)

7. Dodaj dwie kolejne encje według poniższej struktury.

    * encja o nazwie **Step** i nastepującymi polami:
        * wymagane pole o nazwie **ordinal** i typie **Integer** - zawiera kolejny numer kroku
        * wymagane pole o nazwie **snapshot** i typie **Integer[]** - zawiera sortowaną sekwencję liczb w danym kroku
        * wymagane pole o nazwie **input** i typie **Input** - zawiera referencję do encji *Input*
        * nie wymagane pole o nazwie **image** i typie **File** - zawiera obraz jednego kroku sortowania będącego jedną ramką końcowego filmu
    * encja o nazwie **Result** i nastepującymi polami:
        * wymagane pole o nazwie **input** i typie **Input** - zawiera referencję do encji *Input*
        * wymagane pole o nazwie **movie** i typie **File** - zawiera końcowy film

8. Zapisz model, który powinien wyglądać podobnie do tego przedstawionego poniżej.

    ![Końcowy model sortowania](resources/tut-final-sort-model.png)

### Tworzenie wersji i udostępnianie repozytorium

Kiedy już model danych jest gotowy należy stworzyć jego wersję, aby zapisać niemodyfikowalny obraz modelu i później
udostępnić wersję jako działające repozytorium. Ten etap jest realizowany przez poniższe kroki.

1. Wybierz model sortowania z drzewka w widoku po lewej stronie i za pomocą przycisku
   <button class="btn btn-success"><i class="icon-briefcase"> Nowa wersja...</i></button> stwórz nową wersję.

2. W wyświetlonym okienku wprowadź nazwę nowej wersji (np. *v1*) i kliknij przycisk
   <button class="btn btn-primary"><i class="icon-briefcase"> Stwórz wersję</i></button>.

    Ten krok spowoduje stworzenie nowej wersji i przejście głownego widoku do widoku wersji. Widok ten przypomina
    widok tworzenia modelu z tą różnicą, że model nie być już modyfikowany. Widok powinien przypominać ten przedstawiony
    poniżej.
    
    ![Widok wersji modelu](resources/tut-model-version.png)

3. W widoku wersji użyj przycisku <button class="btn btn-success"><i class="icon-cloud-upload"> Udostępnij repozytorium...</i></button>
   aby udostępnić repozytorium.

4. W wyświetlonym oknie wprowadź nazwę repozytorium (np. *sortrepo*) i potwierdź za pomocą przycisku
   <button class="btn btn-primary"><i class="icon-file"> Udostępnij repozytorium</i></button>.

    Udostępnienie może chwilę zająć, więc prosimy o cierpliwość. Należy zauważyć, że wprowadzona nazwa repozytorium będzie
    częścią adresu URL wskazującego na dane repozytorium. Po pomyślnym udostępnieniu zostaniesz przeniesiony do widoku
    repozytorium gdzie dostępny będzie pełny jego adres, gdzie można skonfigurować politykę dostępu (domyślnie repozytorium
    jest publiczne), gdzie można obejrzeć przykładowe kody dostępu do repozytorium i gdzie można podglądnąć dane zapisane
    dla poszczególnych encji (na razie repozytorium będzie jednak puste). Dla porównania poniżej zamieszczono zrzut widoku
    repozytorium.
    
    ![Widok repozytorium](resources/tut-model-repository.png)

### Wykonanie kodu sortującego

Kod zapisujący dane w repozytorium zostanie uruchomiony za pomoca platformy GridSpace. Aby użyć platformy potrzebne będą
dane aktywnego konta PLGrid, dostęp do jednej z infrastruktur obliczeniowych PLGrid oraz eksperyment sortujący.

1. Ściągnij eksperyment sortujący na lokalną maszynę za pomocą [tego linku](resources/sorting-experiment.exp.xml).

2. Zapisz proxy przez kliknięcie przycisku <button class="btn"><i class="icon-download"> Zapisz proxy</i></button>, który znajduje się w prawym górnym rogu aplikacji DataNet.

3. Upewnij się, że masz dostęp do jednej z infrastruktur obliczeniowych PLGrid sprawdzając listę usług w [portalu PLGrid](https://portal.plgrid.pl).

4. Zaloguj się do GridSpace Experiment Workbench za pomocą [tej strony](https://gs2.plgrid.pl) klikając w przycisk *Login »*.

5. Używając przycisku *Upload file* w panelu *Files* wgraj plik eksperymentu zapisany w pierwszym kroku.

6. Ponowanie wgraj plik z proxy zapisany w kroku drugim.

7. Otwórz eksperyment kliknąwszy go w panelu *Files*.

    Zostaną wyświetlone trzy główne fragmenty kodów, które generują losowy ciąg liczb (Ruby), sortują ciąg zapisując poszególne kroki
    sortowania (Python) i generują końcowy film (zadanie w języku Bash). Każdy z kodów odwołuje się do zdalnego repozytorium DataNet
    w celu zapisania lub pobrania odpowiednich danych. Pierwszy fragment kodu służy do rozpropagowania adresu repozytorium do innych
    fragmentów.

8. Zaktualizuj adres swojego repozytorium w pierwszym fragmencie kodu

9. Uruchom poszczególne fragmenty kodów zaczynając odpierwszego i obserwuj dane w widoku repozytorium dla każdej z encji (widok
   można odświeżyć poprzez odświeżenie całej strony). Tabela z danymi powinna zostać uzupełniona danymi z repozytorium. Końcowy
   film może zostać ściągnięty z tabeli dla encji *Result*.