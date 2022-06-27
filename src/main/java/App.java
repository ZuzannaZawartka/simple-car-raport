import static spark.Spark.*;

import com.fasterxml.uuid.Generators;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import spark.Request;
import spark.Response;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

class App {
    static ArrayList<Car> cars = new ArrayList<>();
    static Set<String> yearsToSelect = new HashSet<>();
    public static void main(String[] args) {
        port(getHerokuPort());
        staticFiles.location("static");
        post("/add", (req, res) -> add(req, res));
        post("/del", (req, res) -> del(req));
        post("/edit", (req, res) -> edit(req));
        post("/generate", (req, res) -> generate(req));
        get("/generateForAll", (req, res) -> generateForAll(req));
        post("/generateForYears", (req, res) -> generateForYears(req));
        post("/generateForPrice", (req, res) -> generateForPrice(req));

        get("/getYears", (req, res) ->  getYears(req));
        get("/json", (req, res) -> json(res));
        get("/generateCar2", (req, res) -> generateCar2(req,res));
        get("/download/:id", (req, res) -> download(req,res));
    }

    static int getHerokuPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567;
    }

    private static Object getYears(Request req) {
        Gson gson = new Gson();
        return gson.toJson((yearsToSelect));
    }


    public static String add(Request req, Response res){

        Gson gson = new Gson();
        //System.out.println(req.body());
        Car car = gson.fromJson(req.body(), Car.class);
        car.setUUID(Generators.randomBasedGenerator().generate());
        car.setMoreData();
        cars.add(car);
        yearsToSelect.add(String.valueOf(car.getYear()));
        res.type("text/plain");
        System.out.println(gson.toJson(car));
        return gson.toJson(car);

    }


    public static String generateCar2(Request req, Response res){
        String[] models = { "Renault", "Audi", "Opel","Skoda","Mercedes" };
        String[] colors = { "#ff0000", "#dc143a", "#889ac4" };
        int[] vats = { 0, 7, 22 };
        cars.clear();
        yearsToSelect.clear();
        Random rand = new Random();
        for (int i = 0; i < 10; i++) {
            int model = rand.nextInt(5);
            int color = rand.nextInt(3);
            int vat = rand.nextInt(3);
            UUID uuid = Generators.randomBasedGenerator().generate();
            int year = (int) (Math.random()*(2020 - 2000)) + 2000;
            int price = (int) (Math.random()*(100000 - 1000)) + 1000;
            Car car = new Car(i, year, models[model], colors[color],uuid);
            car.setUUID(uuid);
            car.generateRandomAirbag();
            yearsToSelect.add(String.valueOf(car.getYear()));


            cars.add(car);
        }
        return "Dodano nowy samochód";

    }

    public static String del(Request req){
        Gson gson = new Gson();
        UUID uuid = gson.fromJson(req.body(),Car.class).getUUID();
        cars.removeIf(car->car.getUUID().equals(uuid));
        return "usuniety";
    }

    public static String generate(Request req) throws FileNotFoundException, DocumentException {
            Gson gson = new Gson();

            UUID uuid = gson.fromJson(req.body(),Car.class).getUUID();
            cars.forEach(car->{
                if(car.getUUID().equals(uuid)){
                    car.setGeneratedPDF();
                    try {
                        Invoices.generateOneCarPDF(uuid,car);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (DocumentException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            });

        return "dodano";
    }

    public static String generateForAll(Request req) throws FileNotFoundException, DocumentException {
        return Invoices.generateForAllCarsPDF(cars);
    }

    private static Object generateForPrice(Request req) throws DocumentException, FileNotFoundException {
        Integer min = Integer.parseInt(req.body().split("/")[0]);
        Integer max = Integer.parseInt(req.body().split("/")[1]);
        return Invoices.generateForPricePDF(cars,min,max);
    }

    private static Object generateForYears(Request req) throws DocumentException, FileNotFoundException {
        return Invoices.generateForYearsPDF(cars,Integer.valueOf(req.body()));
    }

    public static String edit(Request req){

        Gson gson = new Gson();
        Car car1 = gson.fromJson(req.body(), Car.class);
        //System.out.println(cars);
        cars.forEach(car->{
            if(car.getUUID().equals(car1.getUUID())){
                car.Setall(car1);
            }
            System.out.println(car.getModel());
        });
        return "ustawiono";
    }

    public static String json(Response res){
        Gson gson = new Gson();
        res.type("application/json");
        return gson.toJson(cars, ArrayList.class );
    }

    public static String download(Request req,Response res) throws IOException {

        System.out.println(req.params(":id"));
        System.out.println("dziala");
        res.type("application/octet-stream"); //
        res.header("Content-Disposition", "attachment; filename=plik.pdf"); // nagłówek
        OutputStream outputStream = res.raw().getOutputStream();
        outputStream.write(Files.readAllBytes(Path.of("katalog/"+req.params(":id")))); // response pliku do przeglądarki
        return "pobrano";
    }


}


class Car {

    private int id;
    private UUID uuid;
    private int year;
    private String model;
    private String color;
    private ArrayList<Airbag> airbags;
    private boolean generatedPDF;
    private double price;
    private int vat;
    private  String date;
    private String image;


    public Car(int id, int year, String model, String color,UUID uuid) {
        this.id = id;
        this.year = year;
        this.model = model;
        this.color = color;
        this.uuid = uuid;
        this.generatedPDF = false;
        setMoreData();
    }

    UUID getUUID() {
        return uuid;
    }

    void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

    public void setModel(String model) {
        this.model = model;
    }

    void setAirbags(ArrayList<Airbag> ab) {
        this.airbags = ab;
    }

    public String getModel() {
        return model;
    }

    public void Setall(Car car){
        this.model = car.getModel();
        this.year = car.getYear();
        setAirbags(car.airbags);
        this.color = car.getColor();
        this.uuid = car.getUUID();
    }

    public ArrayList<Airbag> getAirbags() {
        return airbags;
    }

    public String getColor() {
        return color;
    }

    public int getYear() {
        return year;
    }

    public void setGeneratedPDF() {
        this.generatedPDF = true;
        System.out.println("genrated PDF");
    }

    public void setMoreData(){
        int[] vats = { 0, 7, 22 };
        Random rand = new Random();

        int vatx = rand.nextInt(3);

        int pricex = (int) (Math.random()*(100000 - 1000)) + 1000;

        String imagex = String.valueOf(Math.round(Math.random()*(4)));;

        this.date = generateSellDate();
        this.price = pricex;
        this.vat = vats[vatx];
        this.image = imagex+".jpg";

        //System.out.println(this.date + " " + this.price + " " + this.vat + " " + this.image+ " tooo" + imagex );
    }

    public void generateRandomAirbag(){
        Random rand = new Random();
        ArrayList<Airbag> ab = new ArrayList<>();
        ab.add(new Airbag("kierowca",rand.nextBoolean()));
        ab.add(new Airbag("pasażer",rand.nextBoolean()));
        ab.add(new Airbag("kanapa",rand.nextBoolean()));
        ab.add(new Airbag("boczne",rand.nextBoolean()));
        setAirbags(ab);
    }

    public String generateSellDate(){
        Random rand = new Random();
        int minDay = (int) LocalDate.of(this.year, 1, 1).toEpochDay();
        int maxDay = (int) LocalDate.of(2020, 1, 1).toEpochDay();
        long randomDay = minDay + rand.nextInt(maxDay - minDay);

        return  String.valueOf(LocalDate.ofEpochDay(randomDay));
    }

    public double getPrice() {
        return price;
    }

    public int getVat() {
        return vat;
    }

    public String getDate() {
        return date;
    }

    public String getImage() {
        return image;
    }
}



class Airbag {

    private String description;
    private boolean value;

    public Airbag(String description, boolean isSet) {
        this.description = description;
        this.value = isSet;
    }

    public String getDescription() {
        return description;
    }

    public boolean isValue() {
        return value;
    }
}




