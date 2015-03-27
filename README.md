Algoritmi Paraleli si Distribuiti - 
Indexarea documentelor folosind paradigma Map-Reduce 
-Popa Ramona


1. Continutul arhivei (surse)
-----------------------------
	Main.java - contine metoda main - citire din fisiere, creare thread pools si procesare date, scriere in fisier
	Mapper.java - contine implementarea unui task Map
	MapperInput.java - wrapper pentru intrarea unui task Map
	MapperOutput.java - wrapper pentru iesirea unui task Map
	Reducer.java - implementarea unui task Reduce
	ReducerInput.java - wrapper pentru intrarea unui task Reduce
	ReducerOutput.java - wrapper pentru iesirea unui task Reduce

2. Implementare
---------------
	Programul foloseste paradigma Map - Reduce pentru a indexa un numar de documente si a calcula similaritatea dintre ele. 
Conform acestei paradigme, procesarea se face in 2 etape - Map si Reduce.

2.1 Thread pool
	Am folosit o implementare a interfetei ExecutorService (din java.util.concurrent) care permite crearea unui thread pool de dimensiune fixa.
Clasele care realizeaza task-urile Map si Reduce implementeaza interfata Callable, deoarece aceasta permite, pe langa executia dintr-un alt thread (functionalitate
oferita si de Runnable), intoarcerea unui rezultat la finalizarea task-ului / aruncarea de exceptii (nu a fost cazul).

2.2 Map
	Input-ul pentru mapper consta in continutul documentului de indexat, precum si pozitiile de inceput si sfarsit. Documentul este citit in thread-ul
principal (inainte de crearea task-urilor), intrucat citirea lui de catre fiecare thread este ineficienta si presupune mecanisme de sincronizare suplimentare.
Pozitiile de inceput si sfarsit sunt ajustate conform specificatiilor din enunt (fragmentul sa nu inceapa sau sa se termine in mijlocul unui cuvant).
Textul este transformat in lower-case si toate caracterele care nu sunt litere sau spatii sunt inlocuite cu spatii, pentru a facilita tokenizarea.
	Output-ul acestei etape contine numele fisierului din care face parte fragmentul (pentru a putea fi identificat / folosit in faza de reduce) si lista de
perechi (cuvant, numar_aparitii) calculate pentru fragmentul curent.

2.3 Reduce
	Un task reduce consta in calculul similaritatii dintre doua documente. Reducer-ul primeste ca input lista de vectori partiali obtinuti in urma fazei Map.
In aceasta faza, se face reuniunea listelor de perechi (cuvant, numar aparitii) pentru fiecare document, obtinandu-se valorile finale cu care se calculeaza
similaritatea.
	Output-ul acestei etape consta intr-o pereche (nume_fisier, similaritate). Similaritatea este exprimata in procente si se refera la fisierul curent si fisierul
de comparat / verificat impotriva plagierii.

3. Testare
----------
	Am testat tema pe calculatorul personal (java 1.7, 64-bit, IDE: Eclipse) folosind fisierele de intrare furnizate de echipa APD, si variind numarul de thread-uri worker.
Nu am intampinat probleme. Output-ul nu depinde de numarul de thread-uri folosite. Output-ul este diferit de cel oferit ca referinta, cel mai probabil din cauza utilizarii
altor delimitatori.
	
