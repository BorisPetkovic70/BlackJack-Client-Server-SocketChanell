import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.print.DocFlavor;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

public class Server {



    private HashMap<SocketChannel,String> mapSocketKlijent = new HashMap<>();

    private ArrayList<SesijaThread> listaSesija = new ArrayList<>();

    public static void main(String[] args) {
        Server s = new Server();
        s.execute();
    }

    public void gotovaSesija(SesijaThread sesija)
    {
        Iterator<Map.Entry<SocketChannel,String>> iterator = mapSocketKlijent.entrySet().iterator();

        while(iterator.hasNext())
        {
            Map.Entry<SocketChannel,String> entry = iterator.next();

            if(sesija.getPlayers().contains(entry.getValue())) {
                try {
                    entry.getKey().close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                iterator.remove();
            }
        }

        int x = -1;
        for(int i=0;i<listaSesija.size();i++)
        {
            if(listaSesija.get(i).getHost().equals(sesija.getHost()))
            {
                x = i;
                break;
            }
        }

        this.listaSesija.remove(x);
        System.out.println();
    }



    private String getNamesList() {
        StringBuilder sb = new StringBuilder();

        sb.append(1).append("\n");
        if(!mapSocketKlijent.isEmpty())
        {
            for (SocketChannel sc : mapSocketKlijent.keySet()) {
                sb.append(mapSocketKlijent.get(sc)).append("\n");
            }
        }


        return sb.toString();
    }

    private String getSesijeList(){
        StringBuilder sb = new StringBuilder();
        sb.append(2).append("\n");

        if(!listaSesija.isEmpty())
        {
            for(SesijaThread s:listaSesija)
                sb.append(s.getHost()).append("\n");
        }



        return sb.toString();
    }

    private void KreirajIgru(String s)
    {
            for(SocketChannel sc :mapSocketKlijent.keySet())
            {
                if(mapSocketKlijent.get(sc).equals(s))
                {
                    SesijaThread sesija = new SesijaThread(s,this,sc);
                    listaSesija.add(sesija);
                    sesija.start();
                    sendSesijeToClients();
                    break;
                }
            }


    }

    private void PridruziIgri(String host,String name)
    {   int x = 0;
        for(int i=0;i<listaSesija.size();i++) {
            if (listaSesija.get(i).getHost().equals(host))
            {
                for(SocketChannel sc: mapSocketKlijent.keySet())
                {
                    if(mapSocketKlijent.get(sc).equals(name))
                    {
                        synchronized (listaSesija.get(i))
                        {

                            listaSesija.get(i).addStringPl(name);
                            listaSesija.get(i).addMapIgraciSocket(name,sc);
                            if(listaSesija.get(i).getPlayers().size() == 3)
                            {
                                listaSesija.get(i).setHost(listaSesija.get(i).getHost() + " - (igra u toku)");
                            }
                        }
                        sendSesijeToClients();
                        x =1;
                        break;
                    }
                }
                if(x == 1)
                    break;
            }
        }
    }



    private void sendSesijeToClients(){
        String sesijeList = getSesijeList();
        ByteBuffer buffer = ByteBuffer.wrap(sesijeList.getBytes());


        for (SocketChannel client : mapSocketKlijent.keySet()) {
            for(SesijaThread s: listaSesija)
            {
                if(!s.getPlayers().contains(mapSocketKlijent.get(client)))
                {
                    try {
                        buffer.rewind();
                        client.write(buffer);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }
    private void sendNamesToClients() {
        String namesList = getNamesList();

        ByteBuffer buffer = ByteBuffer.wrap(namesList.getBytes());

        for (SocketChannel client : mapSocketKlijent.keySet()) {

            if(!listaSesija.isEmpty())
            {
                for(SesijaThread s: listaSesija)
                {
                    if(!s.getPlayers().contains(mapSocketKlijent.get(client)))
                    {
                        try {
                            buffer.rewind();  // Resetovanje pozicije buffera na početak
                            client.write(buffer);  // Slanje podataka
                        } catch (IOException e) {
                            e.printStackTrace();  // Logovanje greške
                        }
                    }

                }
            }else{
                try {
                    buffer.rewind();  // Resetovanje pozicije buffera na početak
                    client.write(buffer);  // Slanje podataka
                } catch (IOException e) {
                    e.printStackTrace();  // Logovanje greške
                }
            }

        }
    }



    public void execute(){
        try(ServerSocketChannel server = ServerSocketChannel.open();
            Selector selector = Selector.open();
        ) {

            server.bind(new InetSocketAddress(12345));
            server.configureBlocking(false);
            server.register(selector, SelectionKey.OP_ACCEPT);


            ByteBuffer buffer = ByteBuffer.allocate(2048);
            while(true) {
                selector.select();
                Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                while (it.hasNext())
                {
                    SelectionKey key = it.next();
                    it.remove();
                    try{
                        if(key.isAcceptable())
                        {
                            ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
                            SocketChannel client = serverChannel.accept();

                            client.configureBlocking(false);
                            client.register(selector,SelectionKey.OP_READ);

                            buffer.clear();
                            String x = getNamesList();
                            buffer.put(x.getBytes());
                            buffer.flip();
                            while(buffer.hasRemaining())
                                client.write(buffer);

                            this.mapSocketKlijent.put(client,null);
                            System.out.println("ACCEPT");

                        }else if(key.isReadable())
                        {
                            SocketChannel client = (SocketChannel) key.channel();
                            buffer.clear();

                            int b = client.read(buffer);
                            System.out.println("b = " + b);
                            if(b >0)
                            {
                                buffer.flip();
                                String dummy = new String(buffer.array(),0,b);


                                if(dummy.startsWith("PokrecemSesiju"))
                                {

                                    buffer.clear();


                                    String help1 = dummy.split("PokrecemSesiju")[1];


                                    KreirajIgru(help1);

                                }
                                else if(dummy.startsWith("PridruzujemSesiju"))
                                {

                                    buffer.clear();

                                    String[] niz = dummy.split("!");
                                    PridruziIgri(niz[1],niz[2]);

                                }
                                else if(dummy.startsWith("ULOZENO")){

                                    String[] niz = dummy.split("!");

                                    for(int i=0;i<listaSesija.size();i++)
                                    {
                                        if(listaSesija.get(i).getPlayers().contains(niz[1]))
                                        {
                                            synchronized (listaSesija.get(i))
                                            {
                                                listaSesija.get(i).setUlozenoIgraci(niz[1],Double.parseDouble(niz[2]));
                                                listaSesija.get(i).setIndikatorInvest(niz[1],1);

                                            }
                                            break;
                                        }
                                    }

                                }else if(dummy.startsWith("STAND"))
                                {

                                    String[] niz = dummy.split("!");
                                    for(int i=0;i<listaSesija.size();i++)
                                    {
                                        if(listaSesija.get(i).getPlayers().contains(niz[1]))
                                        {
                                            synchronized (listaSesija.get(i))
                                            {
                                                listaSesija.get(i).setMapIndikatorStandHit(niz[1],1);
                                                listaSesija.get(i).setStandOrHit(niz[1],"STAND");
                                            }
                                            break;
                                        }
                                    }
                                }else if(dummy.startsWith("HIT"))
                                {

                                    String[] niz = dummy.split("!");
                                    for(int i=0;i<listaSesija.size();i++)
                                    {
                                        if(listaSesija.get(i).getPlayers().contains(niz[1]))
                                        {
                                            synchronized (listaSesija.get(i))
                                            {
                                                listaSesija.get(i).setMapIndikatorStandHit(niz[1],1);
                                                listaSesija.get(i).setStandOrHit(niz[1],"HIT");
                                            }
                                            break;
                                        }
                                    }

                                }
                                else if(dummy.startsWith("SaljemIme")){

                                    System.out.println(dummy);

                                    String[] nizIme = dummy.split("!");
                                    System.out.println(nizIme[1]);


                                    this.mapSocketKlijent.put(client,nizIme[1]);

                                    System.out.println(mapSocketKlijent);

                                    sendNamesToClients();
                                    Thread.sleep(200);
                                    sendSesijeToClients();
                                }
                            }else if(b == -1)
                            {
                                mapSocketKlijent.remove(client);
                                key.cancel();
                                client.close();

                            }


                        }
                    } catch (IOException e) {
                        //
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
