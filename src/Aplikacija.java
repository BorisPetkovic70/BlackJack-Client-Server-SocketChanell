import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Aplikacija extends Application {

    private static ListView<String> lvAktivniKlijenti = new ListView<>();
    //Prikaz aktivnih klijenata

    private static ListView<String> lvSesije = new ListView<>();
    //Prikaz aktivnih sesija
    private static Client klijent;
    //Thread klijent povezan sa aplikacijom

    private static Stage stage;
    //Glavni stage

    private static Scene igra;
    //Scena za prikaz igre

    private static VBox vbIgra;
    //Vbox za prikaz igre

    //---------------objekti za prozor igre------------------------------------------------

    private static Label lblNovac1;
    private static Label lblNovac2;
    private static Label lblNovac3;
    private static Label lblUlozeno1 = new Label("Ulozeno u ovoj rundi: 0");
    private static Label lblUlozeno2 = new Label("Ulozeno u ovoj rundi: 0");
    private static Label lblUlozeno3 = new Label("Ulozeno u ovoj rundi: 0");

    private static TextField tfUnos1 = new TextField();

    private static Button btnInvest1 = new Button("INVEST");

    private static Button btnhit1 = new Button("HIT");

    private static Button btnstand1 = new Button("STAND");

    private static HBox hbMiniDelaler;

    private static Label lblimeKlijenta;
    private static Label lblSaigrac2;
    private static Label lblSaigrac3;

    private static Label lblPoruka = new Label();

    private static HBox hbMini12;

    private static HBox hbMini22;

    private static HBox hbMini32;

    //-------------------------------------------------------------------------------


    //------------------------------getteri-------------------------------------------

    public static Label getLblSaigrac2() {
        return lblSaigrac2;
    }

    public static Label getLblSaigrac3() {
        return lblSaigrac3;
    }


    //---------------------------------------------------------------------------------

    //----------------------------------setteri------------------------------------------
    public static void setLblPoruka(String s)
    {
        lblPoruka.setText(s);
    }

    //------------------------------------------------------------------------------------

    //-----------------------------metode za izmjenu gui-a---------------------------------------

    public static void addHBmini12(String s) //Dodavanje nove karte
    {
        Image karta1 = new Image("./cards/" + s +".png");
        ImageView ivKarta1 = new ImageView(karta1);
        ivKarta1.setFitWidth(110);
        ivKarta1.setFitHeight(154);
        ivKarta1.setPreserveRatio(true);
       hbMini12.getChildren().add(ivKarta1);
    }

    public static void addHBmini22(String s)//Dodavanje nove karte
    {
        Image karta1 = new Image("./cards/" + s +".png");
        ImageView ivKarta1 = new ImageView(karta1);
        ivKarta1.setFitWidth(110);
        ivKarta1.setFitHeight(154);
        ivKarta1.setPreserveRatio(true);
        hbMini22.getChildren().add(ivKarta1);
    }

    public static void addHBmini32(String s) //Dodavanje nove karte
    {
        Image karta1 = new Image("./cards/" + s +".png");
        ImageView ivKarta1 = new ImageView(karta1);
        ivKarta1.setFitWidth(110);
        ivKarta1.setFitHeight(154);
        ivKarta1.setPreserveRatio(true);
        hbMini32.getChildren().add(ivKarta1);
    }
    public static void InvestSetFalse() //Omogucava koriscenje dugmeta invest
    {
        Platform.runLater(() -> {
            btnInvest1.setDisable(false);
        });

    }



    public static void HitSetFalse() //Omogucava koriscenje dugmeta hit
    {
        Platform.runLater(() -> {
            btnhit1.setDisable(false);
        });

    }

    public static void HitSetTrue() //Onemogucava koriscenje dugmeta hit
    {
        Platform.runLater(() -> {
            btnhit1.setDisable(true);
        });

    }

    public static void StandSetTrue()  //Onemogucava koriscenje dugmeta stand
    {
        Platform.runLater(() -> {
            btnstand1.setDisable(true);
        });

    }

    public static void StandSetFalse()  //Omogucava koriscenje dugmeta stand
    {
        Platform.runLater(() -> {
            btnstand1.setDisable(false);
        });

    }

    //--------------------------------------klijent----------------------------



    public static void izmjeniKlijentaUlogNovac(String ulozeno,String novac)
    {

        Platform.runLater(() -> {
            lblUlozeno1.setText("Ulozeno u ovoj rundi: "+ ulozeno);
            lblNovac1.setText("Kolicina novca: " + novac);

        });

    }

    public static void izbrisiHbKlijentKarte()
    {
        Platform.runLater(() -> {

            hbMini12.getChildren().clear();
        });
    }

    //----------------------------------------------------------

    //-----------------------------igrac2------------------------------------


    public static void izmjeniSaigrac2UlogNovac(String ulozeno,String novac)
    {

        Platform.runLater(() -> {
            lblUlozeno2.setText("Ulozeno u ovoj rundi: "+ ulozeno);
            lblNovac2.setText("Kolicina novca: " + novac);

        });

    }
    public static void izmjeniSaigrac2Novac(String novac)
    {

        Platform.runLater(() -> {
            lblNovac2.setText("Kolicina novca: " + novac);

        });

    }

    public static void izmjeniSaigrac2Ulozeno(String ulozeno)
    {

        Platform.runLater(() -> {
            lblUlozeno2.setText("Kolicina novca: " + ulozeno);

        });

    }

    public static void izbrisiSaigrac2Karte()
    {
        Platform.runLater(() -> {

            hbMini22.getChildren().clear();
        });
    }

    //-----------------------------------------------------------------------

    //-----------------------------igrac3-----------------------
    public static void izmjeniSaigrac3Ulozeno(String ulozeno)
    {

        Platform.runLater(() -> {
            lblUlozeno3.setText("Kolicina novca: " + ulozeno);

        });

    }

    public static void izmjeniSaigrac3Novac(String novac)
    {

        Platform.runLater(() -> {
            lblNovac3.setText("Kolicina novca: " + novac);

        });

    }

    public static void izmjeniSaigrac3UlogNovac(String ulozeno,String novac)
    {

        Platform.runLater(() -> {
            lblUlozeno3.setText("Ulozeno u ovoj rundi: "+ ulozeno);
            lblNovac3.setText("Kolicina novca: " + novac);

        });

    }

    public static void izbrisiSaigrac3Karte()
    {
        Platform.runLater(() -> {

            hbMini32.getChildren().clear();
        });
    }

    //--------------------------------------------------------------------


    //------------------------------------------dealer-------------------------

    public static void izbrisiHbDealerKarte()
    {
        Platform.runLater(() -> {

            hbMiniDelaler.getChildren().clear();
        });
    }

    public static void izmjeniHbDealerKarte(String s)
    {

        Platform.runLater(() -> {
            Image karta1 = new Image("./cards/" + s +".png");
            ImageView ivKarta1 = new ImageView(karta1);
            ivKarta1.setFitWidth(110);
            ivKarta1.setFitHeight(154);
            ivKarta1.setPreserveRatio(true);
            hbMiniDelaler.getChildren().add(ivKarta1);

        });

    }

    //-------------------------------------------------------------------------



    public static void azurirajListuSesija(){
        Platform.runLater(() -> {
            if(klijent.getListaSesija() != null){
                String[] lista = klijent.getListaSesija().split("\n");
                ObservableList<String> olLista = FXCollections.observableArrayList();
                for (String s : lista) {
                    if(s.equals("2"))
                        continue;
                    olLista.add(s);
                }
                lvSesije.setItems(olLista);
            }

        });
    }

    public static void azurirajListuKlijenata()
    {
        Platform.runLater(() -> {
            String[] lista = klijent.getListaKlijenata().split("\n");
            ObservableList<String> olLista = FXCollections.observableArrayList();
            for (String s : lista) {
                if(s.equals(klijent.getIme()) || s.equals("1"))
                    continue;
                olLista.add(s);
            }
            lvAktivniKlijenti.setItems(olLista);
        });
    }

    public static void zatvoriProzor()
    {
        Platform.runLater(()->{
            stage.close();
            Thread.currentThread().interrupt();
        });
    }

    public static void prozorPobjednik(String s)
    {
        Platform.runLater(()->{
            Stage stage1 = new Stage();

            stage1.initModality(Modality.APPLICATION_MODAL);
            stage1.setTitle("GOTOVA IGRA!");

            Label lbl1 = new Label(s);
            Button closeButton = new Button("Zavrsi");
            closeButton.setOnAction(e -> stage1.close());

            VBox vb1 = new VBox(10);
            vb1.getChildren().addAll(lbl1, closeButton);

            Scene scena1 = new Scene(vb1, 250, 150);
            stage1.setScene(scena1);

            // Show the pop-up
            stage1.showAndWait();
        });

    }

    public static  void prikaziKarte(){
        Platform.runLater(()->{
            Image backCard = new Image("./cards/BACK.png");
            ImageView ivBackCard = new ImageView(backCard);
            ivBackCard.setFitWidth(110);
            ivBackCard.setFitHeight(154);

            Image dealerCard = new Image("./cards/" + klijent.getDealerOtkrivenaKarta().toString() +".png");
            ImageView ivDealerCard = new ImageView(dealerCard);
            ivDealerCard.setFitWidth(110);
            ivDealerCard.setFitHeight(154);
            ivDealerCard.setPreserveRatio(true);

            HashMap<String,ArrayList<ImageView>> mapKarteIgraca = new HashMap<>();

            ArrayList<ImageView> dummyLista = new ArrayList<>();
            for(int j=0;j<2;j++)
            {
                Image karta1 = new Image("./cards/" + klijent.getDeckKarte().get(j).toString() +".png");
                ImageView ivKarta1 = new ImageView(karta1);
                ivKarta1.setFitWidth(110);
                ivKarta1.setFitHeight(154);
                ivKarta1.setPreserveRatio(true);
                dummyLista.add(ivKarta1);
            }
            mapKarteIgraca.put(klijent.getIme(),dummyLista);

            for(String ime:klijent.getMapDeckoviIgraca().keySet())
            {
                dummyLista = new ArrayList<>();
                for(int j=0;j<2;j++)
                {
                    Image karta1 = new Image("./cards/" + klijent.getMapDeckoviIgraca().get(ime).get(j) +".png");
                    ImageView ivKarta1 = new ImageView(karta1);
                    ivKarta1.setFitWidth(110);
                    ivKarta1.setFitHeight(154);
                    ivKarta1.setPreserveRatio(true);
                    dummyLista.add(ivKarta1);
                }
                mapKarteIgraca.put(ime,dummyLista);

            }

            hbMini12.getChildren().addAll(mapKarteIgraca.get(lblimeKlijenta.getText()).get(0),mapKarteIgraca.get(lblimeKlijenta.getText()).get(1));
            hbMini22.getChildren().addAll(mapKarteIgraca.get(lblSaigrac2.getText()).get(0),mapKarteIgraca.get(lblSaigrac2.getText()).get(1));
            hbMini32.getChildren().addAll(mapKarteIgraca.get(lblSaigrac3.getText()).get(0),mapKarteIgraca.get(lblSaigrac3.getText()).get(1));
        });

    }

    public static void promjeniScenuIgra(){
        Platform.runLater(() -> {
            vbIgra = new VBox(10);
            vbIgra.setPadding(new Insets(50,100,100,50));

            lblimeKlijenta = new Label(klijent.getIme());
            lblSaigrac2 = new Label(klijent.getSaigraci().get(0));
            lblSaigrac3 = new Label(klijent.getSaigraci().get(1));

            lblNovac1 = new Label("Kolicina novca: " + klijent.getNovac());
            lblNovac2 = new Label("Kolicina novca: " + klijent.getNovacIgrac(lblSaigrac2.getText()));
            lblNovac3 = new Label("Kolicina novca: " + klijent.getNovacIgrac(lblSaigrac3.getText()));

            btnInvest1.setDisable(true);

            btnInvest1.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    try {
                        double x = Double.parseDouble(tfUnos1.getText());
                        if(x>klijent.getNovac())
                        {

                            lblPoruka.setText("Nemate dovoljno novca");
                        }
                        else{
                            synchronized (klijent)
                            {
                                btnInvest1.setDisable(true);
                                klijent.setNovac(klijent.getNovac()-x);
                                klijent.setTrenutnoUlozeno(x);
                                klijent.setIndikatorInvest(0);

                                tfUnos1.setText("");
                                lblUlozeno1.setText("Ulozeno u ovoj rundi: " + x);
                                lblNovac1.setText("Kolicina novca:" + klijent.getNovac());
                                lblPoruka.setText(" ");

                            }

                        }
                    }catch (Exception e)
                    {
                        lblPoruka.setText("Nekorektan unos");
                    }
                }
            });

            btnhit1.setDisable(true);

            btnhit1.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    synchronized (klijent)
                    {
                        klijent.setIndikatorOdigraj(1);
                        klijent.setIndikatorHit(1);
                    }

                }
            });


            btnstand1.setDisable(true);
            btnstand1.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    synchronized (klijent)
                    {
                        klijent.setIndikatorStand(1);
                        klijent.setIndikatorOdigraj(1);
                        btnstand1.setDisable(true);
                        btnhit1.setDisable(true);
                    }
                }
            });








            Image backCard = new Image("./cards/BACK.png");
            ImageView ivBackCard = new ImageView(backCard);
            ivBackCard.setFitWidth(110);
            ivBackCard.setFitHeight(154);

            Image dealerCard = new Image("./cards/" + klijent.getDealerOtkrivenaKarta().toString() +".png");
            ImageView ivDealerCard = new ImageView(dealerCard);
            ivDealerCard.setFitWidth(110);
            ivDealerCard.setFitHeight(154);
            ivDealerCard.setPreserveRatio(true);

            HashMap<String,ArrayList<ImageView>> mapKarteIgraca = new HashMap<>();

            ArrayList<ImageView> dummyLista = new ArrayList<>();
            for(int j=0;j<2;j++)
            {
                Image karta1 = new Image("./cards/" + klijent.getDeckKarte().get(j).toString() +".png");
                ImageView ivKarta1 = new ImageView(karta1);
                ivKarta1.setFitWidth(110);
                ivKarta1.setFitHeight(154);
                ivKarta1.setPreserveRatio(true);
                dummyLista.add(ivKarta1);
            }
            mapKarteIgraca.put(klijent.getIme(),dummyLista);

            for(String ime:klijent.getMapDeckoviIgraca().keySet())
            {
                 dummyLista = new ArrayList<>();
                for(int j=0;j<2;j++)
                {
                    Image karta1 = new Image("./cards/" + klijent.getMapDeckoviIgraca().get(ime).get(j) +".png");
                    ImageView ivKarta1 = new ImageView(karta1);
                    ivKarta1.setFitWidth(110);
                    ivKarta1.setFitHeight(154);
                    ivKarta1.setPreserveRatio(true);
                    dummyLista.add(ivKarta1);
                }
                mapKarteIgraca.put(ime,dummyLista);

            }





            vbIgra.setStyle("-fx-background-color: green;");

            VBox vbIgrac1 = new VBox(10);
            vbIgrac1.setPadding(new Insets(50,100,100,50));

            HBox hbMini11 = new HBox(10);
            hbMini11.getChildren().addAll(tfUnos1,btnInvest1);

             hbMini12 = new HBox(10);
           // hbMini12.getChildren().addAll(mapKarteIgraca.get(lblimeKlijenta.getText()).get(0),mapKarteIgraca.get(lblimeKlijenta.getText()).get(1));

            HBox hbMini13 = new HBox(10);
            hbMini13.getChildren().addAll(btnhit1,btnstand1);

            vbIgrac1.getChildren().addAll(lblimeKlijenta,lblNovac1,lblUlozeno1,lblPoruka,hbMini11,hbMini12,hbMini13);

            VBox vbIgrac2 = new VBox(10);
            vbIgrac2.setPadding(new Insets(50,100,100,50));


             hbMini22 = new HBox(10);
            //hbMini22.getChildren().addAll(mapKarteIgraca.get(lblSaigrac2.getText()).get(0),mapKarteIgraca.get(lblSaigrac2.getText()).get(1));



            vbIgrac2.getChildren().addAll(lblSaigrac2,lblNovac2,lblUlozeno2,hbMini22);

            VBox vbIgrac3 = new VBox(10);
            vbIgrac3.setPadding(new Insets(50,100,100,50));



             hbMini32 = new HBox(10);
           // hbMini32.getChildren().addAll(mapKarteIgraca.get(lblSaigrac3.getText()).get(0),mapKarteIgraca.get(lblSaigrac3.getText()).get(1));



            vbIgrac3.getChildren().addAll(lblSaigrac3,lblNovac3,lblUlozeno3,hbMini32);

            HBox hbIgraci = new HBox(10);
            hbIgraci.getChildren().addAll(vbIgrac1,vbIgrac2,vbIgrac3);

            VBox vbDealer = new VBox(10);
            Label lblDealer = new Label("Dealer");
            hbMiniDelaler = new HBox(10);
            hbMiniDelaler.getChildren().addAll(ivBackCard,ivDealerCard);

            vbDealer.getChildren().addAll(lblDealer,hbMiniDelaler);

            vbIgra.getChildren().addAll(vbDealer,hbIgraci);
            Aplikacija.igra = new Scene(vbIgra);
            Aplikacija.stage.setScene(igra);


        });
    }





    public static Client getKlijent() {
        return klijent;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
            //-------------------------------------------LOGIN EKRAN------------------------------
        Aplikacija.stage = stage;
        VBox vbox1 = new VBox(10);
            vbox1.setPadding(new Insets(50,100,100,50));



        Label ime1 = new Label("Unesi svoje ime");
        TextField tf1 = new TextField();
        tf1.setMaxWidth(450);

        Label greska1 = new Label("");
        greska1.setTextFill(Color.RED);

        Button loginBTN = new Button();
        loginBTN.setText("login");




        vbox1.getChildren().addAll(ime1,tf1,greska1,loginBTN);
        Scene scena1 = new Scene(vbox1);
        stage.setScene(scena1);


        //------------------------------------------- KRAJ LOGIN EKRAN------------------------------

        //-----------------------------------------POCETNI EKRAN---------------------------------
        HBox hbox1 = new HBox(10);

        VBox vbox2 = new VBox(10);
        vbox2.setPadding(new Insets(50,100,100,50));

        VBox vbox3 = new VBox(10);
        vbox3.setPadding(new Insets(50,100,100,50));

        Label lblKlijenti = new Label("Lista aktivnih klijenata");
        Label lblImeKlijenta = new Label();
        vbox2.getChildren().addAll(lblImeKlijenta,lblKlijenti,lvAktivniKlijenti);

        HBox hboxHelp = new HBox(10);
        Button btnKreirajSesiju = new Button("Kreiraj sesiju");
        Button btnUdjiSesisju = new Button("Pridruzi se sesiji");
        hboxHelp.getChildren().addAll(btnKreirajSesiju,btnUdjiSesisju);


        vbox3.getChildren().addAll(hboxHelp,lvSesije);

        hbox1.getChildren().addAll(vbox2,vbox3);


        Scene Scena2 = new Scene(hbox1);

        loginBTN.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {


                if(tf1.getText() != null || !tf1.getText().contains("\n") || !Character.isDigit(tf1.getText().charAt(0))){
                    Aplikacija.klijent = new Client(tf1.getText());
                    klijent.start();

                    try {
                        klijent.waitForUpdate();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    if (klijent.getValidnoIme() == 1)
                    {
                        greska1.setText("Postoji vec korisnik sa takvim imenom");
                        klijent.interrupt();
                    }
                    else{
                        greska1.setText("");


                        lblImeKlijenta.setText(klijent.getIme());
                        azurirajListuKlijenata();
                        azurirajListuSesija();

                        tf1.clear();
                        stage.setScene(Scena2);
                    }

                }else{
                    greska1.setText("Nekorektan unos imenax!");
                }

            }
        });

        //-------------------------------------------KRAJ POCETNI EKRAN--------------------------------------

        //--------------------------------------------WAITING EKRAN------------------------------------
        VBox vBoxCekaj1 = new VBox(10);
        vBoxCekaj1.setPadding(new Insets(50,100,100,50));

        Label lblIgracIme = new Label();
        Label lblcekaj1 = new Label("Cekamo ostale igrace da se prikljuce");

        vBoxCekaj1.getChildren().addAll(lblIgracIme,lblcekaj1);

        Scene cekajKreiraj = new Scene(vBoxCekaj1);

        btnKreirajSesiju.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                synchronized (klijent) {
                    klijent.setIndikatorKreiraj(1);
                }

                lblIgracIme.setText(Aplikacija.getKlijent().getIme());
                stage.setScene(cekajKreiraj);
            }
        });


        btnUdjiSesisju.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if(lvSesije.getSelectionModel().getSelectedItem() != null && !lvSesije.getSelectionModel().getSelectedItem().contains(" - (igra u toku)"))
                {
                    synchronized (klijent) {
                        klijent.setIndikatorPridruzi(1);
                        klijent.setHostIme(lvSesije.getSelectionModel().getSelectedItem());
                    }
                    lblIgracIme.setText(Aplikacija.getKlijent().getIme());
                    stage.setScene(cekajKreiraj);
                }

            }
        });

        //--------------------------------------------KRAJ WAITING EKRAN------------------------------------

        stage.show();

    }

}
