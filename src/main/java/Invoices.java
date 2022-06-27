import com.fasterxml.uuid.Generators;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class Invoices {

    public static String generateOneCarPDF(UUID uuid, Car car) throws IOException, DocumentException {
//        Font font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.RED);
//        Font font2 = FontFactory.getFont(FontFactory.COURIER, 20, BaseColor.BLACK);
        Invoice doc = new Invoice(String.valueOf(uuid));
        doc.write("\n");
        Chunk p3= new Chunk("FAKTURA: "+car.getUUID(),doc.getFont2());

        doc.getDocument().add(p3);
        doc.write("Model :" + car.getModel());
        doc.write("Kolor: " + car.getColor());
        doc.write("Rok: " + car.getYear());
        //System.out.println(car.getImage());

        for(Airbag ab : car.getAirbags()){
            doc.write("Poduszka: " + ab.getDescription() + " - " + ab.isValue());
        }


        Image img = Image.getInstance("images/"+car.getImage());
        doc.getDocument().add(img);
       // doc.addImage(car.getImage());
        doc.close();

        return "dodano";
    }


    public static String generateForAllCarsPDF(ArrayList<Car> cars) throws FileNotFoundException, DocumentException {

        UUID uuid = Generators.randomBasedGenerator().generate();
        Invoice doc = new Invoice("wszystkie"+uuid);
        DecimalFormat df = new DecimalFormat("#.##");
        Calendar calendar = Calendar.getInstance();
        Date dateBeforeJava8 = calendar.getTime(); // pobranie daty z obiektu kalendarza
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd ");
        Chunk p3= new Chunk("FAKTURA: VAT/"+simpleDateFormat.format(dateBeforeJava8),doc.getFont2());

        doc.getDocument().add(p3);
        doc.write("Nabywca : firma sprzedajaca auta" );
        doc.write("Sprzedawca: Romek" );
        doc.write("Faktura za wszystkie auta ");
        Chunk p2 = new Chunk("\n");

        doc.getDocument().add(p2);
        PdfPTable table = new PdfPTable(4);

        table.addCell("lp");
        table.addCell("cena");
        table.addCell("vat");
        table.addCell("wartosc");
        System.out.println(cars.size());
        int licz = 0;
        double sum = 0;
        for(Car car : cars){

            licz++;
            System.out.println(car);
            table.addCell(String.valueOf(licz));
            table.addCell(String.valueOf(car.getPrice()));
            table.addCell(String.valueOf(car.getVat())+"%");
            double priceAfterVat = (car.getVat()/100.0);
            System.out.println(priceAfterVat);
            double price =( priceAfterVat* car.getPrice())+ car.getPrice();
            table.addCell(String.valueOf(df.format((price))));
            sum+= price;

        }
        Chunk p1 = new Chunk("DO ZAPLATY : "+ String.valueOf(df.format(sum))+"PLN",doc.getFont());


        doc.getDocument().add(table);
        doc.getDocument().add(p1);
        doc.close();

        return "wszystkie"+String.valueOf(uuid);
    }

    public static String generateForYearsPDF(ArrayList<Car> cars,Integer years) throws FileNotFoundException, DocumentException {


        UUID uuid = Generators.randomBasedGenerator().generate();
        Invoice doc = new Invoice("rocznikowo"+uuid);
        DecimalFormat df = new DecimalFormat("#.##");
        Calendar calendar = Calendar.getInstance();
        Date dateBeforeJava8 = calendar.getTime(); // pobranie daty z obiektu kalendarza
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd ");
        Chunk p3= new Chunk("FAKTURA: VAT/"+simpleDateFormat.format(dateBeforeJava8),doc.getFont2());

        doc.getDocument().add(p3);
       // doc.write("FAKTURA: VAT/"+simpleDateFormat.format(dateBeforeJava8) );
        doc.write("Nabywca : firma sprzedajaca auta" );
        doc.write("Sprzedawca: Romek" );
        doc.write("Faktura za auta z roku " + String.valueOf(years));
        Chunk p2 = new Chunk("\n");

        doc.getDocument().add(p2);
        PdfPTable table = new PdfPTable(5);

        table.addCell("lp");
        table.addCell("rok");
        table.addCell("cena");
        table.addCell("vat");
        table.addCell("wartosc");
        System.out.println(cars.size());
        int licz = 0;
        double sum = 0;
        for(Car car : cars){
            if(car.getYear()==years){
                licz++;
                System.out.println(car);
                table.addCell(String.valueOf(licz));
                table.addCell(String.valueOf(car.getYear()));
                table.addCell(String.valueOf(car.getPrice()));
                table.addCell(String.valueOf(car.getVat())+"%");
                double priceAfterVat = (car.getVat()/100.0);
                System.out.println(priceAfterVat);
                double price =( priceAfterVat* car.getPrice())+ car.getPrice();
                table.addCell(String.valueOf(df.format((price))));
                sum+= price;
            }


        }

        Chunk p1 = new Chunk("DO ZAPLATY : "+ String.valueOf(df.format(sum))+"PLN",doc.getFont());


        doc.getDocument().add(table);
        doc.getDocument().add(p1);
        doc.close();

        return "rocznikowo"+String.valueOf(uuid);
    }

    public static String generateForPricePDF(ArrayList<Car> cars,Integer min, Integer max) throws FileNotFoundException, DocumentException {


        UUID uuid = Generators.randomBasedGenerator().generate();
        Invoice doc = new Invoice("cenowo"+uuid);
        DecimalFormat df = new DecimalFormat("#.##");
        Calendar calendar = Calendar.getInstance();
        Date dateBeforeJava8 = calendar.getTime(); // pobranie daty z obiektu kalendarza
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd ");
        Chunk p4 = new Chunk("FAKTURA: VAT/"+simpleDateFormat.format(dateBeforeJava8) ,doc.getFont2());
        doc.getDocument().add(p4);
        doc.write("Nabywca : firma sprzedajaca auta" );
        doc.write("Sprzedawca: Romek" );

        Chunk p3 = new Chunk("Faktura za auta z przedzialu cenowego :  " + String.valueOf(min) + " - " +String.valueOf(max),doc.getFont());
        doc.getDocument().add(p3);
        Chunk p2 = new Chunk("\n");

        doc.getDocument().add(p2);
        PdfPTable table = new PdfPTable(5);

        table.addCell("lp");
        table.addCell("rok");
        table.addCell("cena");
        table.addCell("vat");
        table.addCell("wartosc");
        System.out.println(cars.size());
        int licz = 0;
        double sum = 0;
        for(Car car : cars){
            if(car.getPrice()>min && car.getPrice() < max){
                licz++;
                System.out.println(car);
                table.addCell(String.valueOf(licz));
                table.addCell(String.valueOf(car.getYear()));
                table.addCell(String.valueOf(car.getPrice()));
                table.addCell(String.valueOf(car.getVat())+"%");
                double priceAfterVat = (car.getVat()/100.0);
                System.out.println(priceAfterVat);
                double price =( priceAfterVat* car.getPrice())+ car.getPrice();
                table.addCell(String.valueOf(df.format((price))));
                sum+= price;
            }


        }
        //Font font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.RED);
        Chunk p1 = new Chunk("DO ZAPLATY : "+ String.valueOf(df.format(sum))+"PLN",doc.getFont());


        doc.getDocument().add(table);
        doc.getDocument().add(p1);
        doc.close();

        return "cenowo"+String.valueOf(uuid);
    }
}


