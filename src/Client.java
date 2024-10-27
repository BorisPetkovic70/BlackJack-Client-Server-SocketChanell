import javafx.application.Platform;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

public class Client extends Thread{
    private String ime;

    private String hostIme; // ime hosta iz sesije

    private String listaKlijenata;

    private String listaSesija;

    private ArrayList<Card> deckKarte = new ArrayList<>();

    private ArrayList<Card> dealerDeck = new ArrayList<>();

    private HashMap<String,ArrayList<Card>> mapDeckoviIgraca = new HashMap<>();

    private HashMap<String,Double> mapNovacIgraci = new HashMap<>();

    private ArrayList<String> saigraci = new ArrayList<>();

    private Card dealerOtkrivenaKarta;

    private double Novac;

    private double trenutnoUlozeno = 0;

    private int indikatorKreiraj = 0;

    private int indikatorPridruzi = 0;

    private int validnoIme = 0;

    //--------------------------------getteri----------------------------------------------
    public String getNovacIgrac(String s){
        return mapNovacIgraci.get(s)+"";
    }

    public double getNovac() {
        return Novac;
    }

    public int getValidnoIme(){
        return validnoIme;
    }

    public ArrayList<String> getSaigraci() {
        return saigraci;
    }

    public Card getDealerOtkrivenaKarta() {
        return dealerOtkrivenaKarta;
    }

    public ArrayList<Card> getDeckKarte() {
        return deckKarte;
    }

    public String getListaKlijenata() {
        return listaKlijenata;
    }

    public String getListaSesija() {
        return listaSesija;
    }

    public synchronized String getIme() {
        return ime;
    }

    public HashMap<String, ArrayList<Card>> getMapDeckoviIgraca() {
        return mapDeckoviIgraca;
    }

    //---------------------------------------------------------------------------

    //---------------------------------setteri-----------------------------------
    public synchronized void setIndikatorKreiraj(int indikatorKreiraj) {
        this.indikatorKreiraj = indikatorKreiraj;
    }

    public synchronized void setIndikatorPridruzi(int indikatorPridruzi) {
        this.indikatorPridruzi = indikatorPridruzi;
    }

    public synchronized void setTrenutnoUlozeno(double trenutnoUlozeno) {
        this.trenutnoUlozeno = trenutnoUlozeno;
    }

    public void setNovac(double novac) {
        Novac = novac;
    }


    public synchronized void setHostIme(String hostIme) {
        this.hostIme = hostIme;
    }

    private int indikatorInvest = 0;

    private final Object lock2 = new Object();
    public synchronized void setIndikatorInvest(int indikatorInvest) {
        this.indikatorInvest = indikatorInvest;
        synchronized (lock2)
        {
            lock2.notifyAll();
        }

    }

    private int indikatorOdigraj = 0;
    private final Object lock3 = new Object();

    public void setIndikatorOdigraj(int indikatorOdigraj) {
        this.indikatorOdigraj = indikatorOdigraj;
        synchronized (lock3)
        {
            lock3.notifyAll();
        }
    }


    private int indikatorStand = 0;
    private final Object lock4 = new Object();
    public void setIndikatorStand(int x) {
        this.indikatorStand = x;
        synchronized (lock4)
        {
            lock4.notifyAll();
        }
    }


    private int indikatorHit = 0;

    private final Object lock5 = new Object();

    public void setIndikatorHit(int x)
    {
        indikatorHit = x;
        synchronized (lock5)
        {
            lock5.notifyAll();
        }
    }



    //-------------------------------------------------------------------------


    public Client(String ime) {
        this.ime = ime;
        this.Novac = 200;
    }




    @Override
    public String toString() {
        return this.ime;
    }

    private final Object lock = new Object();

    private boolean updated = false;

    public void waitForUpdate() throws InterruptedException {
        synchronized (lock) {
            while (!updated) {
                lock.wait(); 
            }
        }
    }




    @Override
    public void run() {
        try {
            InetSocketAddress addr = new InetSocketAddress("localhost",12345);
            SocketChannel client = SocketChannel.open(addr);

            client.configureBlocking(true);

            ByteBuffer buffer = ByteBuffer.allocate(2048);

            Thread.sleep(200);


                buffer.clear();

                int b;


                    buffer.clear();
                    b = client.read(buffer);
                    System.out.println(b);


                    if(b>0)
                    {
                        buffer.flip();

                        this.listaKlijenata = new String(buffer.array(), 0, b);
                        System.out.println(listaKlijenata);
                        if(listaKlijenata.length() == 2)
                            this.listaKlijenata = "";
                        else{
                            this.listaKlijenata = this.listaKlijenata.substring(2);
                        }

                    }


            client.configureBlocking(false);

                if(!listaKlijenata.isEmpty())
                {
                    String[] nizKlijenti = listaKlijenata.split("\n");
                    for(int i=0;i<nizKlijenti.length;i++)
                    {
                        if(nizKlijenti[i].equals(ime))
                        {
                            synchronized (lock)
                            {
                                validnoIme = 1;
                                updated = true;
                                lock.notify();
                                client.close();
                                break;
                            }

                        }
                    }

                    if(validnoIme == 0)
                    {
                        synchronized (lock)
                        {
                            updated = true;
                            lock.notify();
                        }
                    }

                }else{
                    synchronized (lock)
                    {
                        updated = true;
                        lock.notify();
                    }
                }

                System.out.println(validnoIme);
                Thread.sleep(1000);



                buffer.clear();
                buffer.put(("SaljemIme!"+ime).getBytes());
                buffer.flip();
                while (buffer.hasRemaining())
                    client.write(buffer);

                buffer.clear();



                while(true)
                {

                    b = client.read(buffer);

                    if(b>0)
                    {
                        buffer.flip();
                        String help = new String(buffer.array(), 0, b);
                        if(help.startsWith("1"))
                        {
                            this.listaKlijenata = help;
                            Platform.runLater(Aplikacija::azurirajListuKlijenata);

                        }else if(help.startsWith("2"))
                        {
                            if(help.length() == 2) {
                                this.listaSesija = "";
                            }
                            else{
                                this.listaSesija = help;
                                Platform.runLater(Aplikacija::azurirajListuSesija);
                            }

                        }

                        buffer.clear();
                    }

                    if(indikatorKreiraj == 1 || indikatorPridruzi == 1)
                        break;
                }


                if(indikatorKreiraj == 1)
                {
                    buffer.clear();
                    buffer.put(("PokrecemSesiju"+this.ime).getBytes());
                    buffer.flip();
                    client.write(buffer);


                }else if(indikatorPridruzi == 1)
                {
                    buffer.clear();
                    buffer.put(("PridruzujemSesiju"+"!" + this.hostIme + "!" + this.ime).getBytes());
                    buffer.flip();
                    client.write(buffer);
                }

                buffer.clear();


                client.configureBlocking(true);


                    b = client.read(buffer);
                    if(b>0)
                    {
                        buffer.flip();
                        String x = new String(buffer.array(), 0, b);
                        if(x.startsWith("kreceIgra")) {

                                System.out.println(x);
                                String[] niz = x.split("!");
                                for(int i=1;i<niz.length;i++)
                                {
                                    if(!niz[i].equals(this.getIme())) {
                                        saigraci.add(niz[i]);
                                        mapDeckoviIgraca.put(niz[i],null);

                                    }
                                }

                                buffer.clear();
                        }
                    }
                    buffer.clear();








                    for(int runda =0;runda<3;runda++)
                    {
                        buffer.clear();

                        b = client.read(buffer);

                        System.out.println("B = " + b);
                        buffer.flip();
                        String karte = new String(buffer.array(),0,b);
                        System.out.println("KARTE === " + karte);
                        String[] nizIgraci = karte.split("__");

                        for(int i=0;i<nizIgraci.length;i++)
                        {
                            String[] nizKarte = nizIgraci[i].split("!");
                            if(i == 0)
                            {
                                this.deckKarte.add(new Card(nizKarte[0]));
                                this.deckKarte.add(new Card(nizKarte[1]));
                            }else if(i == (nizIgraci.length-1))
                            {
                                this.dealerOtkrivenaKarta = new Card(nizKarte[0]);
                            }
                            else{
                                ArrayList<Card> pomocnaLista = new ArrayList<>();
                                pomocnaLista.add(new Card(nizKarte[0]));
                                pomocnaLista.add(new Card(nizKarte[1]));

                                mapDeckoviIgraca.put(nizKarte[2],pomocnaLista);
                                mapNovacIgraci.put(nizKarte[2],Double.parseDouble(nizKarte[3]));
                            }
                        }
                        Platform.runLater(Aplikacija::promjeniScenuIgra);
                        buffer.clear();

                        while(true)
                        {
                            b = client.read(buffer);
                            if(b>0)
                            {
                                buffer.flip();
                                String s = new String(buffer.array(),0,b);
                                System.out.println(s);
                                if(s.equals("Ulozi"))
                                {
                                    indikatorInvest = 1;
                                    Platform.runLater(Aplikacija::InvestSetFalse);

                                    synchronized (lock2)
                                    {
                                        while(indikatorInvest == 1)
                                            lock2.wait();
                                    }

                                    String help = "ULOZENO!" + getIme() + "!" + trenutnoUlozeno;

                                    buffer.clear();


                                    buffer.put(help.getBytes());
                                    buffer.flip();
                                    while (buffer.hasRemaining())
                                        client.write(buffer);



                                }else if(s.startsWith("Novi Ulog")) {
                                String[] niz1 = s.split("__");


                                for(int i=1;i<niz1.length;i++)
                                {
                                    String[] niz2 = niz1[i].split("!");
                                    System.out.println(niz2[0]);
                                    if(Aplikacija.getLblSaigrac2().getText().equals(niz2[0]))
                                    {
                                        Platform.runLater(() -> Aplikacija.izmjeniSaigrac2UlogNovac(niz2[2],niz2[1]));
                                        System.out.println("saigrac2 gg izmjena");
                                    }else if(Aplikacija.getLblSaigrac3().getText().equals(niz2[0]))
                                    {
                                        Platform.runLater(() -> Aplikacija.izmjeniSaigrac3UlogNovac(niz2[2],niz2[1]));
                                        System.out.println("saigrac3 gg izmjena");
                                    }
                                }
                            }
                                else if(s.startsWith("GotovUlog"))
                            {
                                Platform.runLater(Aplikacija::prikaziKarte);
                                break;
                            }

                            }
                            buffer.clear();


                        }

                        buffer.clear();
                        
                    int izadjiIzPetlje = 0;
                    client.configureBlocking(false);

                    while(true)
                    {
                        b = client.read(buffer);
                        if(b>0)
                        {
                            buffer.flip();
                            String s = new String(buffer.array(),0,b);
                            System.out.println(s);
                            buffer.clear();
                            int x = 0;

                            if(s.equals("StandHit"))
                            {
                                while(true) {

                                    if (Card.sumaKarata(deckKarte) < 21) {

                                        Platform.runLater(Aplikacija::StandSetFalse);
                                        Platform.runLater(Aplikacija::HitSetFalse);

                                        synchronized (lock3)
                                        {
                                            while(indikatorOdigraj ==0)
                                                lock3.wait();
                                        }

                                        if(indikatorStand == 1)
                                        {
                                            buffer.clear();
                                            buffer.put(("STAND" + "!" + getIme()).getBytes());
                                            buffer.flip();
                                            while(buffer.hasRemaining())
                                                client.write(buffer);
                                            buffer.clear();

                                            izadjiIzPetlje = 1;
                                            Platform.runLater(Aplikacija::HitSetTrue);
                                            Platform.runLater(Aplikacija::StandSetTrue);
                                            break;

                                        }else if(indikatorHit == 1)
                                        {

                                            buffer.clear();
                                            buffer.put(("HIT" + "!" + getIme()).getBytes());
                                            buffer.flip();
                                            while(buffer.hasRemaining())
                                                client.write(buffer);
                                            buffer.clear();


                                            client.configureBlocking(true);

                                            b = client.read(buffer);
                                            if(b>0)
                                            {
                                                buffer.flip();
                                                Card novaKarta = new Card(new String(buffer.array(),0,b));
                                                deckKarte.add(novaKarta);
                                                Platform.runLater(()-> Aplikacija.addHBmini12(novaKarta.toString()));
                                                System.out.println("kita");
                                            }


                                            buffer.clear();
                                            indikatorHit = 0;
                                            indikatorOdigraj = 0;
                                            client.configureBlocking(false);
                                        }
                                        x+=1;

                                    } else if (Card.sumaKarata(deckKarte) > 21) {

                                        Platform.runLater(() -> Aplikacija.setLblPoruka("Izgubili ste (presli ste 21)!"));
                                        Platform.runLater(Aplikacija::HitSetTrue);
                                        Platform.runLater(Aplikacija::StandSetTrue);
                                        izadjiIzPetlje = 1;
                                        break;

                                    } else if (Card.sumaKarata(deckKarte) == 21)
                                    {
                                        if(x == 0)
                                        {
                                            Platform.runLater(() -> Aplikacija.setLblPoruka("Cestitam! Dobili ste Black Jack!"));
                                            this.setNovac(this.getNovac() +2.5*trenutnoUlozeno);
                                            Platform.runLater(()->Aplikacija.izmjeniKlijentaUlogNovac(this.trenutnoUlozeno+"",this.getNovac()+""));

                                        }else{

                                            Platform.runLater(() -> Aplikacija.setLblPoruka("Cestitam! Pobjedili ste!"));
                                            this.setNovac(this.getNovac() + 2*this.trenutnoUlozeno);
                                            Platform.runLater(()->Aplikacija.izmjeniKlijentaUlogNovac(this.trenutnoUlozeno+"",this.getNovac()+""));

                                        }
                                        Platform.runLater(Aplikacija::HitSetTrue);
                                        Platform.runLater(Aplikacija::StandSetTrue);
                                        izadjiIzPetlje = 1;
                                        break;
                                    }

                                    buffer.clear();
                                    b = client.read(buffer);
                                    if(b>0)
                                    {
                                        buffer.flip();
                                        String saigracKarta = new String(buffer.array(),0,b);
                                        if(saigracKarta.startsWith("AzuriranjeApk"))
                                        {
                                            String[] nizHelp = saigracKarta.split("!");
                                            if(Aplikacija.getLblSaigrac2().getText().equals(nizHelp[1]))
                                            {
                                                ArrayList<Card> dummyList = mapDeckoviIgraca.get(nizHelp[1]);
                                                dummyList.add(new Card(nizHelp[2]));
                                                mapDeckoviIgraca.put(nizHelp[1],dummyList);

                                                Platform.runLater(()->Aplikacija.addHBmini22(nizHelp[2]));
                                            }else{
                                                ArrayList<Card> dummyList = mapDeckoviIgraca.get(nizHelp[1]);
                                                dummyList.add(new Card(nizHelp[2]));
                                                mapDeckoviIgraca.put(nizHelp[1],dummyList);

                                                Platform.runLater(()->Aplikacija.addHBmini32(nizHelp[2]));
                                            }
                                        }
                                    }



                                }
                            }else if(s.startsWith("AzuriranjeApk"))
                            {

                                String[] nizHelp = s.split("!");
                                if(Aplikacija.getLblSaigrac2().getText().equals(nizHelp[1]))
                                {
                                    ArrayList<Card> dummyList = mapDeckoviIgraca.get(nizHelp[1]);
                                    dummyList.add(new Card(nizHelp[2]));
                                    mapDeckoviIgraca.put(nizHelp[1],dummyList);

                                    Platform.runLater(()->Aplikacija.addHBmini22(nizHelp[2]));
                                }else{
                                    ArrayList<Card> dummyList = mapDeckoviIgraca.get(nizHelp[1]);
                                    dummyList.add(new Card(nizHelp[2]));
                                    mapDeckoviIgraca.put(nizHelp[1],dummyList);

                                    Platform.runLater(()->Aplikacija.addHBmini32(nizHelp[2]));
                                }
                            }
                        }
                        buffer.clear();
                        if(izadjiIzPetlje == 1)
                            break;

                    }




                        buffer.clear();
                        while(true){
                            b = client.read(buffer);

                            if(b>0)
                            {
                                buffer.flip();
                                String pomocniString = new String(buffer.array(),0,b);
                                System.out.println("saigrac karta izvan "+pomocniString);
                                buffer.clear();
                                if(pomocniString.startsWith("DealerDeck"))
                                {
                                    String[] pomocniNiz = pomocniString.split("!");
                                    Platform.runLater(Aplikacija::izbrisiHbDealerKarte);

                                    dealerDeck.add(dealerOtkrivenaKarta);
                                    Platform.runLater(() -> Aplikacija.izmjeniHbDealerKarte(dealerOtkrivenaKarta.toString()));

                                    for(int i=1;i<pomocniNiz.length;i++)
                                    {
                                        dealerDeck.add(new Card(pomocniNiz[i]));
                                        String s = pomocniNiz[i];
                                        Platform.runLater(() -> Aplikacija.izmjeniHbDealerKarte(s));
                                    }


                                    buffer.clear();
                                    break;

                                }else if(pomocniString.startsWith("AzuriranjeApk")){
                                    String[] nizHelp = pomocniString.split("!");

                                    if(Aplikacija.getLblSaigrac2().getText().equals(nizHelp[1]))
                                    {
                                        ArrayList<Card> dummyList = mapDeckoviIgraca.get(nizHelp[1]);
                                        dummyList.add(new Card(nizHelp[2]));
                                        mapDeckoviIgraca.put(nizHelp[1],dummyList);
                                        Platform.runLater(()->Aplikacija.addHBmini22(nizHelp[2]));
                                    }else{

                                        ArrayList<Card> dummyList = mapDeckoviIgraca.get(nizHelp[1]);
                                        dummyList.add(new Card(nizHelp[2]));
                                        mapDeckoviIgraca.put(nizHelp[1],dummyList);

                                        Platform.runLater(()->Aplikacija.addHBmini32(nizHelp[2]));
                                    }
                                }


                            }
                            buffer.clear();
                        }


                        if(indikatorStand == 1)
                        {
                            if(Card.sumaKarata(dealerDeck)>21)
                            {
                                Platform.runLater(() -> Aplikacija.setLblPoruka("Cestitam! Pobjedili ste delare!"));
                                this.setNovac(this.getNovac() + 2*this.trenutnoUlozeno);
                                Platform.runLater(()->Aplikacija.izmjeniKlijentaUlogNovac(this.trenutnoUlozeno+"",this.getNovac()+""));

                            }
                            else if(Card.sumaKarata(dealerDeck) > Card.sumaKarata(deckKarte))
                            {
                                Platform.runLater(() -> Aplikacija.setLblPoruka("Izgubili ste od Dealera!"));
                            }else if(Card.sumaKarata(dealerDeck) <= Card.sumaKarata(deckKarte))
                            {
                                Platform.runLater(() -> Aplikacija.setLblPoruka("Cestitam! Pobjedili ste delare!"));
                                this.setNovac(this.getNovac() +  2*this.trenutnoUlozeno);
                                Platform.runLater(()->Aplikacija.izmjeniKlijentaUlogNovac(this.trenutnoUlozeno+"",this.getNovac()+""));
                            }
                        }

                        buffer.clear();
                        client.configureBlocking(true);
                        b = client.read(buffer);
                        if(b>0)
                        {
                            String pomocniString = new String(buffer.array(),0,b);
                            if(pomocniString.startsWith("PareIgraca__"))
                            {
                                System.out.println(pomocniString);
                                String[] pomocniNiz1 = pomocniString.split("__");
                                for(int i=1;i<pomocniNiz1.length;i++)
                                {
                                    String[] pomocniNiz2 = pomocniNiz1[i].split("!");
                                    if(Aplikacija.getLblSaigrac2().getText().equals(pomocniNiz2[0]))
                                    {
                                        Platform.runLater(()->Aplikacija.izmjeniSaigrac2Novac(pomocniNiz2[1]));
                                    }else{
                                        Platform.runLater(()->Aplikacija.izmjeniSaigrac3Novac(pomocniNiz2[1]));
                                    }
                                }
                            }
                        }
                        buffer.clear();

                        Thread.sleep(15000);

                        Platform.runLater(()->Aplikacija.izmjeniSaigrac2Ulozeno("0"));
                        Platform.runLater(()->Aplikacija.izmjeniSaigrac3Ulozeno("0"));
                        Platform.runLater(()->Aplikacija.izmjeniKlijentaUlogNovac("0",getNovac()+""));

                        Platform.runLater(()->Aplikacija.setLblPoruka(""));

                        Platform.runLater(Aplikacija::izbrisiHbDealerKarte);
                        Platform.runLater(Aplikacija::izbrisiHbKlijentKarte);
                        Platform.runLater(Aplikacija::izbrisiSaigrac2Karte);
                        Platform.runLater(Aplikacija::izbrisiSaigrac3Karte);

                        this.deckKarte.clear();
                        this.dealerDeck.clear();
                        this.trenutnoUlozeno = 0;
                        this.indikatorStand = 0;
                        this.indikatorHit = 0;
                        this.indikatorOdigraj = 0;
                        this.indikatorInvest = 0;



                    }

                    client.configureBlocking(true);
                    buffer.clear();
                    b = client.read(buffer);
                    if(b>0)
                    {
                        buffer.flip();
                        String poruka = new String(buffer.array(),0,b);
                        if(poruka.startsWith("PobjednikIgre"))
                        {
                            String[] pomocniNiz = poruka.split("!");
                            if(pomocniNiz[1].equals(ime))
                            {
                                Platform.runLater(()->Aplikacija.prozorPobjednik("Cestitam pobjedili ste u ovoj sesiji!"));
                            }else{
                                Platform.runLater(()->Aplikacija.prozorPobjednik("Pobjednik u ovoj sesiji je: " + pomocniNiz[1]));
                            }
                        }
                    }

                    Thread.sleep(10000);
                    client.close();
                    Platform.runLater(Aplikacija::zatvoriProzor);





        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }


}
