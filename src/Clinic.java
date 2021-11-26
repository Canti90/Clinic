import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;


public class Clinic {

    static  String url = "jdbc:mysql://localhost:3306/test";
    static  String username = "root";
    static  String password = "root";


    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        List<String> doctors = doctorsList();

        Map<String, List<List<String>>> customerCardsImproved = new HashMap<>();
        // List = 0 - name, 1 - Surname, 2 - date

        int customerCounter = 0;
        int customerNew;

        System.out.println("Очистить базу посетителей? 1 - да, 2 - нет");
        int clear = scanner.nextInt();
        if (clear == 1) {
            clearPatients();
        }


        while(true) {
            System.out.println("Добавить нового посетителя? 1 - да, 2 - нет");
            customerNew = scanner.nextInt();
            if (customerNew == 1) {

                List<String> customerCard = new ArrayList<>();

                System.out.println("Введите имя посетителя :");
                String customerName = scanner.next();
                customerCard.add(0, customerName);

                System.out.println("Введите фамилию посетителя");
                String customerSurname = scanner.next();
                customerCard.add(1, customerSurname);

                System.out.println("Введите Дату");
                String date = scanner.next();
                customerCard.add(2, date);

                System.out.println("Выберите врача:");
                for (String element: doctors
                ) {
                    System.out.print(element + "   ");
                }
                System.out.println("");

                String doctorName;

                while (true){
                    boolean inputCheck =  true;
                    doctorName = scanner.next();

                    for (int i = 0; i < doctors.size(); i++) {
                        if (!doctorName.equals(doctors.get(i))){
                            inputCheck = false;
                        } else {
                            inputCheck = true;
                            break;
                        }
                    }

                    if (!inputCheck) {
                        System.out.println("Некоректный выбор, корректно введите фамилию врача :");
                    } else break;
                }

                switch (doctorName){
                    case "Иванов" :
                        clientCreate(customerName,customerSurname,date,1);
                        break;
                    case "Сидоров" :
                        clientCreate(customerName,customerSurname,date,2);
                        break;
                    case "Петров" :
                        clientCreate(customerName,customerSurname,date,3);
                        break;
                    default:
                        System.out.println("Нет такого врача");
                }
                customerCounter++;
            } else break;
        }

        for (String name: doctorsList()
             ) {
            customerCardsImproved.put(name, patientListByDoctor(name));
        }

        for (Map.Entry<String, List<List<String>>> items :
                customerCardsImproved.entrySet()) {
            System.out.println("Пациенты врача " + items.getKey() + " :");
            for (List patient: items.getValue()
            ) {
                System.out.print(patient.get(0) + "  " + patient.get(1) + "  " + patient.get(2));
                System.out.println("");
            }
            System.out.println("");
        }

    }


    // Create/Insert
    public static int clientCreate(String firstName, String secondName, String date, int doctorId) {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
            try (Connection conn = DriverManager.getConnection(url, username, password)) {
                String sql = "INSERT INTO patients (firstname, secondname, date, doctorid)" +
                        "values (?, ?, ?, ?)";
                try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
                    preparedStatement.setString(1,firstName);
                    preparedStatement.setString(2,secondName);
                    preparedStatement.setString(3,date);
                    preparedStatement.setInt(4, doctorId);
                    return preparedStatement.executeUpdate();
                }
                finally {
                    conn.close();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    // Collection return
    public static List<String> doctorsList(){
        List<String> listofDoctors = new ArrayList<>();
        try{
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
            try (Connection conn = DriverManager.getConnection(url, username, password)){

                String sql = "select distinct secondname from doctors";
                try(PreparedStatement preparedStatement = conn.prepareStatement(sql)){
                    ResultSet resultSet = preparedStatement.executeQuery();
                    while(resultSet.next()){
                        listofDoctors.add(resultSet.getString(1));
                    }
                }
            }


        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        return listofDoctors;
    }


    public static List<List<String>> patientListByDoctor(String doctor){
        List<List<String >> stringList = new ArrayList<>();
        int number = 0;
        try{
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
            try (Connection conn = DriverManager.getConnection(url, username, password)) {

                String sql1 = "SELECT " +
                        "firstname, patients.secondname AS secondname, date " +
                        "FROM patients " +
                        "JOIN doctors " +
                        "ON patients.doctorid = doctors.id " +
                        "WHERE doctors.secondname = ?";
                try(PreparedStatement preparedStatement = conn.prepareStatement(sql1)){
                    preparedStatement.setString(1,doctor);
                    ResultSet rs = preparedStatement.executeQuery();
                    while(rs.next()){
                        List<String> client = new ArrayList<>();
                        client.add(0, rs.getString(1));
                        client.add(1, rs.getString(2));
                        client.add(2, rs.getString(3));
                        stringList.add(client);
                    }
                }
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        return stringList;

    }


    // SELECT
    public static String userExist(int chatId) {
        String ifPresent = "";

        try{
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
            try (Connection conn = DriverManager.getConnection(url, username, password)){
                String sql = "SELECT secondName FROM doctors WHERE id = ?";
                try(PreparedStatement preparedStatement = conn.prepareStatement(sql)){
                    preparedStatement.setInt(1, chatId);
                    ResultSet resultSet = preparedStatement.executeQuery();

                    if (resultSet.next()) {
                        ifPresent = resultSet.getString(1);
                    }

                }
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        return ifPresent;
    }

    public static int clearPatients() {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
            try (Connection conn = DriverManager.getConnection(url, username, password)) {
                String sql = "TRUNCATE FROM patients";
                try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
                    return preparedStatement.executeUpdate();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }
}
