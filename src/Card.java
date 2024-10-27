import java.util.ArrayList;
import java.util.Random;

public class Card {

    private String type;

    private String value;

    private int rezultat;
    public Card(String t, String v) {
        this.type = t;
        this.value = v;
        setRezultat();
    }

    public Card(String s)
    {
        String[] niz = s.split("-");
        this.value = niz[0];
        this.type = niz[1];
        setRezultat();
    }



    public void setRezultat() {
        if("AJKQ".contains(this.value))
        {
            if(this.value.equals("A"))
                this.rezultat = 11;
            else this.rezultat = 10;
        }else {
            this.rezultat = Integer.parseInt(this.value);
        }
    }

    public int getBrojKarte() {
        return rezultat;
    }

    private static void shuffleDeck(ArrayList<Card> list)
    {
        Random r = new Random();
        for(int i=0;i<list.size();i++)
        {
            int j = r.nextInt(list.size());
            Card c = list.get(i);
            Card x = list.get(j);
            list.set(i,x);
            list.set(j,c);
        }

    }

    public static ArrayList<Card> createDeck(){
        String[] value = {"A","2","3","4","5","6","7","8","9","10","J","Q","K"};
        String[] type = {"C","D","H","S"};
        ArrayList<Card> dummy = new ArrayList<>();

        for(int i=0;i<value.length;i++)
        {
            for(int j =0;j<type.length;j++)
            {
                dummy.add(new Card(type[j],value[i]));
            }
        }

        shuffleDeck(dummy);
        return dummy;
    }

    public static int sumaKarata(ArrayList<Card> lista) {
        int suma = 0;
        int brAs = 0;

        for (Card c : lista)
        {
            if(c.getBrojKarte() == 11)
                brAs+=1;

            suma+=c.getBrojKarte();
        }


            while(true)
            {
                if(suma<=21)
                {
                    break;
                }else if(suma>=22 && brAs==0)
                {
                    break;
                }else if(suma>21 && brAs>0){
                    suma-=10;
                    brAs-=1;
                }
            }


        return suma;
    }

    @Override
    public String toString() {
        return this.value + "-" + this.type;
    }
}
