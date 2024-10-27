import javafx.application.Platform;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class SesijaThread extends Thread{


    private ArrayList<String> players = new ArrayList<>();//Lista sa imenima igraca



    private HashMap<String,SocketChannel> mapIgraciSocket = new HashMap<>(); //Mapa (Imena igraca: Socketi)

    private ArrayList<Card> deckKarte = new ArrayList<>(); // Spil od 52 karte

    private Card hiddenDealerCard; //Skrivena karta Dealera

    private ArrayList<Card> deckDealer = new ArrayList<>(); // Deck Dealera

    private HashMap<String,ArrayList<Card>> mapDeckoviIgraca = new HashMap<>(); // Mapa (Ime igraca: njegov Deck)

    private HashMap<String,Double> mapNovacIgraci = new HashMap<>(); // Mapa (Ime igraca: njegova kolicina Novca)

    private HashMap<String,Integer> mapIndikatorInvest = new HashMap<>();// Mapa (Ime igraca: 0[nije ulozio novac] | 1[ulozio je novac])

    private HashMap<String,Integer> mapIndikatorStandHit = new HashMap<>(); // Mapa (Ime igraca: 0[nije stisnuo dgume stand/hit] | 1[ stisnuo dgume stand/hit])

    private HashMap<String,String> mapStringStandOrHit = new HashMap<>(); // Mapa (Ime igraca: govori sta je igrac stisnuo, hit ili stand)

    private HashMap<String,Double> mapUlozenoIgraci = new HashMap<>(); // Mapa (Ime igraca: koliko je novca ulozio u toj rundi)

    private HashMap<String,Integer> mapKrajIgraci = new HashMap<>(); // Mapa (Ime igraca: da li igrac zavrsio sa sa tom rundom)



    private String host; //Ime hosta



    private SocketChannel socketHost;
    private Server server;


    public SesijaThread(String host,Server server,SocketChannel socket)
    {
        this.host = host;
        players.add(host);
        this.socketHost = socket;
        this.mapIgraciSocket.put(host,socket);
        this.server = server;
        this.deckKarte = Card.createDeck();

    }


    public synchronized String getHost() {
        return host;
    }

    public synchronized void setHost(String s)
    {
        this.host = s;
    }




    public ArrayList<String> getPlayers() {
        return players;
    }

    public synchronized void addMapIgraciSocket(String s,SocketChannel sc)
    {
        this.mapIgraciSocket.put(s,sc);
    }


    private final Object lock = new Object();
    public synchronized void addStringPl(String s)
    {
        this.players.add(s);
        synchronized (lock) {
            lock.notifyAll();
        }
    }




    private final Object lock2 = new Object();
    public synchronized void setIndikatorInvest(String s,int x)
    {
        mapIndikatorInvest.put(s,x);
        synchronized (lock2) {
            lock2.notifyAll();
        }
    }


    private final Object lock3 = new Object();
    public synchronized void setMapIndikatorStandHit(String s,int x)
    {
        mapIndikatorStandHit.put(s,x);
        synchronized (lock3)
        {
            lock3.notifyAll();
        }
    }

    public synchronized void setStandOrHit(String s1,String s2)
    {
        mapStringStandOrHit.put(s1,s2);
    }

    public synchronized void setUlozenoIgraci(String s,double x)
    {
        mapUlozenoIgraci.put(s,x);
    }



    @Override
    public void run() {
        try {

        ByteBuffer buffer = ByteBuffer.allocate(2048);

        synchronized (lock) {
            while (players.size() < 3) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        for(String s:players)
        {
            mapNovacIgraci.put(s,200.00);
            mapIndikatorInvest.put(s,0);
            mapUlozenoIgraci.put(s,0.0);
            mapIndikatorStandHit.put(s,0);
            mapStringStandOrHit.put(s," ");
            mapKrajIgraci.put(s,0);
        }

        for(String ime:mapIgraciSocket.keySet())
        {
            StringBuilder s = new StringBuilder();
            s.append("kreceIgra").append("!");
            for(int j=0;j<players.size();j++)
            {
                s.append(players.get(j));
                mapDeckoviIgraca.put(players.get(j),null);
                if(j != 2)
                    s.append("!");
            }

            buffer.clear();
            buffer.put(s.toString().getBytes());

            buffer.flip();
            while (buffer.hasRemaining()) {
                mapIgraciSocket.get(ime).write(buffer);
            }
        }



        buffer.clear();
            System.out.println(this.deckKarte);
        //------------------------------------POCINJE IGRA-------------------------------------
            Random r = new Random();
            for(int runda = 0;runda<3;runda++)
            {
                this.hiddenDealerCard = this.deckKarte.remove(r.nextInt(deckKarte.size()));
                Card otkrivenaKarta = deckKarte.remove(r.nextInt(deckKarte.size()));

                this.deckDealer.add(hiddenDealerCard);
                this.deckDealer.add(otkrivenaKarta);

                for(int i=0;i<3;i++)
                {
                    Card karta1 = deckKarte.remove(r.nextInt(deckKarte.size()));
                    Card karta2 = deckKarte.remove(r.nextInt(deckKarte.size()));
                    ArrayList<Card> dummyList = new ArrayList<>();
                    dummyList.add(karta1);
                    dummyList.add(karta2);

                    mapDeckoviIgraca.put(players.get(i),dummyList);
                }

                for(String ime:mapIgraciSocket.keySet())
                {
                    StringBuilder s = new StringBuilder();
                    s.append(mapDeckoviIgraca.get(ime).get(0).toString()).append("!").append(mapDeckoviIgraca.get(ime).get(1).toString()).append("__");
                    for(String ime2: mapDeckoviIgraca.keySet())
                    {
                        if (!ime2.equals(ime))
                            s.append(mapDeckoviIgraca.get(ime2).get(0).toString()).append("!").append(mapDeckoviIgraca.get(ime2).get(1).toString()).append("!").append(ime2).append("!").append(mapNovacIgraci.get(ime2)).append("__");

                    }

                    s.append(otkrivenaKarta);
                    System.out.println(s);
                    buffer.clear();
                    buffer.put(s.toString().getBytes());
                    buffer.flip();
                    while(buffer.hasRemaining())
                        mapIgraciSocket.get(ime).write(buffer);
                    buffer.clear();

                }
                buffer.clear();

                for(String s:mapIgraciSocket.keySet())
                {
                    System.out.println(s);
                    buffer.put("Ulozi".getBytes());
                    buffer.flip();
                    while(buffer.hasRemaining())
                        mapIgraciSocket.get(s).write(buffer);

                    synchronized (lock2){
                        while (mapIndikatorInvest.get(s) == 0)
                            lock2.wait();
                    }


                    mapNovacIgraci.put(s,mapNovacIgraci.get(s)-mapUlozenoIgraci.get(s));
                    buffer.clear();

                }



                for(String s:mapIgraciSocket.keySet())
                {
                    StringBuilder dummy = new StringBuilder();
                    dummy.append("Novi Ulog__");

                    for(String s2:mapNovacIgraci.keySet())
                    {

                        dummy.append(s2).append("!").append(mapNovacIgraci.get(s2)).append("!").append(mapUlozenoIgraci.get(s2)).append("__");
                    }

                    for(String s1:mapIgraciSocket.keySet())
                    {
                        if(!s1.equals(s))
                        {

                            buffer.put(dummy.toString().getBytes());
                            buffer.flip();
                            while (buffer.hasRemaining())
                                mapIgraciSocket.get(s1).write(buffer);
                            buffer.clear();
                        }
                    }
                }





                buffer.clear();
                for(String s:mapIgraciSocket.keySet())
                {
                    buffer.put("GotovUlog".getBytes());
                    buffer.flip();
                    while (buffer.hasRemaining())
                        mapIgraciSocket.get(s).write(buffer);
                    buffer.clear();

                }



                Thread.sleep(200);
                for(String s:mapIgraciSocket.keySet())
                {
                    System.out.println(s);
                    buffer.put("StandHit".getBytes());
                    buffer.flip();
                    while(buffer.hasRemaining())
                        mapIgraciSocket.get(s).write(buffer);
                    Thread.sleep(200);
                    int x = 0;

                    while(true)
                    {
                        if(Card.sumaKarata(mapDeckoviIgraca.get(s))>21)
                        {   mapKrajIgraci.put(s,1);
                            break;

                        }else if(Card.sumaKarata(mapDeckoviIgraca.get(s)) == 21)
                        {
                            if(x == 0)
                                mapNovacIgraci.put(s,mapNovacIgraci.get(s) + 2.5*mapUlozenoIgraci.get(s)) ;
                            else
                                mapNovacIgraci.put(s,mapNovacIgraci.get(s) + 2*mapUlozenoIgraci.get(s)) ;
                            mapKrajIgraci.put(s,1);
                            break;

                        }else if(Card.sumaKarata(mapDeckoviIgraca.get(s))<21)
                        {
                            synchronized (lock3)
                            {
                                while(mapIndikatorStandHit.get(s) == 0)
                                    lock3.wait();
                            }

                            if(mapStringStandOrHit.get(s).equals("STAND"))
                            {
                                break;

                            }else if(mapStringStandOrHit.get(s).equals("HIT"))
                            {
                                Card novaKarta = deckKarte.remove(r.nextInt(deckKarte.size()));
                                ArrayList<Card> novaLista = mapDeckoviIgraca.get(s);
                                novaLista.add(novaKarta);
                                mapDeckoviIgraca.put(s,novaLista);
                                System.out.println("nova karta");

                                buffer.clear();
                                buffer.put(novaKarta.toString().getBytes());
                                buffer.flip();
                                while(buffer.hasRemaining())
                                    mapIgraciSocket.get(s).write(buffer);

                                for(String s1:mapIgraciSocket.keySet())
                                {
                                    if(!s.equals(s1))
                                    {
                                        buffer.clear();
                                        buffer.put((new String("AzuriranjeApk!"+s+"!"+novaKarta)).getBytes());
                                        buffer.flip();
                                        while (buffer.hasRemaining())
                                            mapIgraciSocket.get(s1).write(buffer);
                                        buffer.clear();

                                    }
                                }

                                Thread.sleep(200);

                                mapIndikatorStandHit.put(s,0);
                                mapStringStandOrHit.put(s,"");
                                x+=1;

                            }


                        }


                    }


                    buffer.clear();

                }

                while(true)
                {
                    if(Card.sumaKarata(deckDealer)<17)
                    {
                        Card novaKarta = deckKarte.remove(r.nextInt(deckKarte.size()));
                        deckDealer.add(novaKarta);

                    }if(Card.sumaKarata(deckDealer)>16)
                    {
                    break;
                    }
                }

                StringBuilder dealerDeckString = new StringBuilder();
                dealerDeckString.append("DealerDeck").append("!");

                for(int i=0;i<deckDealer.size();i++)
                {   if(!deckDealer.get(i).toString().equals(otkrivenaKarta.toString()))
                        dealerDeckString.append(deckDealer.get(i).toString()).append("!");
                }

                for(String s:mapIgraciSocket.keySet())
                {
                    buffer.clear();
                    buffer.put(dealerDeckString.toString().getBytes());
                    buffer.flip();
                    while(buffer.hasRemaining())
                        mapIgraciSocket.get(s).write(buffer);
                }

                Thread.sleep(500);
                for(String s:mapIgraciSocket.keySet())
                {
                    if(mapKrajIgraci.get(s) == 0)
                    {
                        if(Card.sumaKarata(deckDealer)>21 || (Card.sumaKarata(deckDealer)<= Card.sumaKarata(mapDeckoviIgraca.get(s))))
                        {
                            mapNovacIgraci.put(s,mapNovacIgraci.get(s) + 2*mapUlozenoIgraci.get(s));
                        }
                    }
                }



                for(String s:mapIgraciSocket.keySet())
                {   StringBuilder pomocniString = new StringBuilder();
                    pomocniString.append("PareIgraca__");

                    for(String s1:mapNovacIgraci.keySet())
                    {
                        if(!s1.equals(s))
                            pomocniString.append(s1).append("!").append(mapNovacIgraci.get(s1)).append("__");
                    }

                    buffer.clear();
                    buffer.put(pomocniString.toString().getBytes());
                    buffer.flip();
                    while(buffer.hasRemaining())
                        mapIgraciSocket.get(s).write(buffer);

                    buffer.clear();
                }
                Thread.sleep(500);


                Thread.sleep(15000);

                this.deckKarte.clear();
                this.deckKarte = Card.createDeck();
                this.deckDealer.clear();

               for(String s:mapIgraciSocket.keySet())
               {
                   mapIndikatorInvest.put(s,0);
                   mapUlozenoIgraci.put(s,0.0);
                   mapIndikatorStandHit.put(s,0);
                   mapKrajIgraci.put(s,0);
                   mapStringStandOrHit.put(s,"");
               }



            }

            double max = 0;
            String pobjednik = "";
            for(String s:mapNovacIgraci.keySet())
            {
                if(mapNovacIgraci.get(s)>max) {
                    max = mapNovacIgraci.get(s);
                    pobjednik = s;
                }
            }

            for(String s:mapIgraciSocket.keySet())
            {
                buffer.clear();
                buffer.put(("PobjednikIgre!" + pobjednik).getBytes());
                buffer.flip();
                while(buffer.hasRemaining())
                    mapIgraciSocket.get(s).write(buffer);
            }

            Thread.sleep(36000);
            this.server.gotovaSesija(this);



        }catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}
