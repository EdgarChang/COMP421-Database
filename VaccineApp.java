import java.awt.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

class VaccineApp {
    public static void main(String[] args) throws SQLException {
        // Unique table names.  Either the user supplies a unique identifier as a command line argument, or the program makes one up.
        String tableName = "";
        int sqlCode = 0;      // Variable to hold SQLCODE
        String sqlState = "00000";  // Variable to hold SQLSTATE

        // Creates scanner object
        Scanner scanner = new Scanner(System.in);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String date = dtf.format(LocalDateTime.now());

        if (args.length > 0)
            tableName += args[0];
        else
            tableName += "exampletbl";

        // Register the driver.  You must register the driver before you can use it.
        try {
            DriverManager.registerDriver(new com.ibm.db2.jcc.DB2Driver());
        } catch (Exception cnfe) {
            System.out.println("Class not found");
        }

        // This is the url you must use for DB2.
        //Note: This url may not valid now !
        String url = "jdbc:db2://winter2021-comp421.cs.mcgill.ca:50000/cs421";

        //REMEMBER to remove your user id and password before submitting your code!!
        String your_userid = "xxxxxx";
        String your_password = "xxxxxxxx";
        //AS AN ALTERNATIVE, you can just set your password in the shell environment in the Unix (as shown below) and read it from there.
        //$  export SOCSPASSWD=yoursocspasswd
        if (your_userid == null && (your_userid = System.getenv("SOCSUSER")) == null) {
            System.err.println("Error!! do not have a password to connect to the database!");
            System.exit(1);
        }
        if (your_password == null && (your_password = System.getenv("SOCSPASSWD")) == null) {
            System.err.println("Error!! do not have a password to connect to the database!");
            System.exit(1);
        }
        Connection con = DriverManager.getConnection(url, your_userid, your_password);
        Statement statement = con.createStatement();

        // Creating a table
        try {
            int option = 99;
            // dateOfBirth, phoneNum, address, gender, dateRegistered, category
            while (option != 4) {
                System.out.println("VaccineApp Main Menu");
                System.out.println("1. Add a person");
                System.out.println("2. Assign a slot to a person");
                System.out.println("3. Enter vaccination information");
                System.out.println("4. Exit application");
                System.out.println("Please enter your option (number):");
                option = scanner.nextInt();
                scanner.nextLine();
                System.out.println("You have chosen option " + option + " !!");
                switch (option) {
                    case (1): {
                        System.out.println("Let's add a person to the system 😍");
                        System.out.println("Please enter the health insurance number (numbers only):");
                        int hinum = scanner.nextInt();
                        int update = 0;
                        scanner.nextLine();
                        String checkPersonSql = "select name from Persons where healthInsuranceNum = " + hinum;
                        java.sql.ResultSet rs = statement.executeQuery(checkPersonSql);
                        if (rs.next()) {
                            System.out.println("This person already exists in the system!");
                            System.out.println("Would you like to update this person's information??");
                            System.out.println("Enter 1 if yes, 0 if not.");
                            update = scanner.nextInt();
                            scanner.nextLine();
                            if (update == 0) {
                                break;
                            }
                        }
                        System.out.println("Please enter the person's name:");
                        String pname = scanner.nextLine();
                        System.out.println("Please enter the person's date of birth in yyyy-mm-dd format:");
                        String dob = scanner.nextLine();
                        System.out.println("Please enter the person's phone number (numbers only):");
                        int phoneNum = scanner.nextInt();
                        scanner.nextLine();
                        System.out.println("Please enter the person's address:");
                        String address = scanner.nextLine();
                        System.out.println("Please enter the person's gender:");
                        String gender = scanner.nextLine();
                        System.out.println("Please enter the number of the person's category:");
                        System.out.println("1. Healthcare workers");
                        System.out.println("2. Elderly (>= 65 years old)");
                        System.out.println("3. Immunologically compromised");
                        System.out.println("4. Teachers");
                        System.out.println("5. Children (< 10 years old)");
                        System.out.println("6. Essential service workers");
                        System.out.println("7. Everybody else");
                        int categoryNum = scanner.nextInt();
                        scanner.nextLine();
                        String category = "";
                        switch (categoryNum) {
                            case (1): {
                                category = "Health care workers";
                                break;
                            }
                            case (2): {
                                category = "Elderly";
                                break;
                            }
                            case (3): {
                                category = "Immunologically compromised";
                                break;
                            }
                            case (4): {
                                category = "Teachers";
                                break;
                            }
                            case (5): {
                                category = "Children";
                                break;
                            }
                            case (6): {
                                category = "Essential service workers";
                                break;
                            }
                            case (7): {
                                category = "Everybody else";
                                break;
                            }
                        }
                        if (update == 1) {
                            String updateSQL = "UPDATE Persons " +
                                    "SET name =" + "\'" + pname + "\'" +
                                    ", dateOfBirth =" + "\'" + dob + "\'" +
                                    ", phoneNum =" + phoneNum +
                                    ", address =" + "\'" + address + "\'" +
                                    ", gender =" + "\'" + gender + "\'" +
                                    ", category =" + "\'" + category + "\'" + " where healthInsuranceNum =" + hinum;
                            System.out.println(updateSQL);
                            statement.executeUpdate(updateSQL);
                            break;
                        }

                        String insertSQL = "INSERT INTO Persons (healthInsuranceNum, name," +
                                "dateOfBirth, phoneNum, address, gender, dateRegistered, category) values" +
                                "(" + hinum + ",\'" + pname + "\',\'" + dob + "\'," + phoneNum + ",\'" + address + "\', \'" +
                                gender + "\',\'" + date + "\',\'" + category + "\')";
                        System.out.println(insertSQL);
                        statement.executeUpdate(insertSQL);
                        break;
                    }
                    case (2): {
                        System.out.println("Let's assign a slot to a person 👌");
                        System.out.println("What is this person's health insurance number?");
                        int hinum = scanner.nextInt();
                        scanner.nextLine();
                        String checkPersonSql = "select name from Persons where healthInsuranceNum = " + hinum;
                        java.sql.ResultSet rs = statement.executeQuery(checkPersonSql);

                        if (rs.next()) {
                            String name = rs.getString("name");
                            System.out.println("We found " + name + "!");


                            String checkSlotSql = "select * from Slots s where s.id not in (select slotId from SlotAllocations)" +
                                    " and s.date > \'" + date + "\'";
                            java.sql.ResultSet res = statement.executeQuery(checkSlotSql);
                            if (res.next()) {
                                System.out.println("Below are the available slots:");
                                System.out.println();
                                do {
                                    int id = res.getInt("id");
                                    String location = res.getString("location");
                                    String time = res.getString("time");
                                    String slotDate = res.getString("date");
                                    System.out.println("Slot id:  " + id);
                                    System.out.println("Location:  " + location);
                                    System.out.println("Time:  " + time);
                                    System.out.println("Date:  " + slotDate);
                                    System.out.println();
                                } while (res.next());

                                System.out.println("Please enter the slot id of the slot you want: ");
                                int id = scanner.nextInt();
                                scanner.nextLine();

                                String checkSlotAllocationSql = "select count(*) as count , vac.doseNum from VaccineVials v left join SlotAllocations s" +
                                        " on v.slotId = s.slotId left join Vaccines vac on " +
                                        "v.vaccineName = vac.name where s.person = " + hinum + " group by vac.doseNum";
                                res = statement.executeQuery(checkSlotAllocationSql);
                                if (res.next()) {
                                    if (res.getInt("count") >= res.getInt("dosenum")) {
                                        System.out.println("This person has received enough shots.");
                                        break;
                                    }
                                }

                                String allocateSlotSql = "insert into SlotAllocations (person, slotId, dateOfAllocation)" +
                                        "values (" + hinum + ", " + id + ", \'" + date + "\')";

                                try {
                                    statement.executeUpdate(allocateSlotSql);
                                } catch (SQLException e) {
                                    sqlCode = e.getErrorCode(); // Get SQLCODE
                                    sqlState = e.getSQLState(); // Get SQLSTATE

                                    // Your code to handle errors comes here;
                                    // something more meaningful than a print would be good
                                    System.out.println("Code: " + sqlCode + "  sqlState: " + sqlState);
                                    System.out.println("This slot does not exist or is already taken!");
                                    break;
                                }


                                System.out.println("Successfully assigned " + name + " to slot " + id);
                            } else {
                                System.out.println("No slot is available now.");
                            }


                        } else {
                            System.out.println("Cannot find this person in the system, please add the person first!");
                        }
                        break;
                    }
                    case (3): {
                        System.out.println("Let's enter some vaccine information!! 💉");
                        System.out.println("What is the slot id?");
                        int slot = scanner.nextInt();
                        scanner.nextLine();
                        String checkSlotSql = "select * from SlotAllocations where slotId = " + slot + " and slotId not in (select slotId from VaccineVials) ";
                        java.sql.ResultSet rs = statement.executeQuery(checkSlotSql);
                        if (rs.next()) {
                            String checkSlotPersonSql = "select name, person from Persons, SlotAllocations where person =" +
                                    " healthInsuranceNum and slotId = " + slot;
                            rs = statement.executeQuery(checkSlotPersonSql);

                            if (rs.next()) {

                                System.out.println("We found that the slot was assigned to " + rs.getString("name") + "!");
                                System.out.println("What is the name of the vaccine used?");
                                String vaccine = scanner.nextLine();
                                int hinum = rs.getInt("person");
                                String checkVaccineSql = "select v.vaccineName from VaccineVials v left join SlotAllocations s" +
                                        " on v.slotId = s.slotId where s.person = " + hinum;

                                System.out.println("What was the vial number of the vaccine?");
                                int vial = scanner.nextInt();
                                scanner.nextLine();
                                System.out.println("What was the batch number of the vaccine?");
                                int batch = scanner.nextInt();
                                scanner.nextLine();
                                System.out.println("What was the nurse's license number?");
                                int nurse = scanner.nextInt();
                                scanner.nextLine();

                                rs = statement.executeQuery(checkVaccineSql);
                                if (rs.next()) {
                                    String pastVaccine = rs.getString("vaccineName");
                                    if (!vaccine.equals(pastVaccine)) {
                                        System.out.println("Vaccine used is not consistent!");
                                        break;
                                    }
                                }
                                String vaccineVialSql = "insert into VaccineVials (vialNum, batchNum, vaccineName, slotId, nurse)" +
                                        "values(" + vial + ", " + batch + ", \'" + vaccine + "\'," + slot + "," + nurse + ")";
                                try {
                                    statement.executeUpdate(vaccineVialSql);
                                } catch (SQLException e) {
                                    sqlCode = e.getErrorCode(); // Get SQLCODE
                                    sqlState = e.getSQLState(); // Get SQLSTATE

                                    // Your code to handle errors comes here;
                                    // something more meaningful than a print would be good
                                    System.out.println("Code: " + sqlCode + "  sqlState: " + sqlState);
                                    System.out.println("The vial information was incorrect!");
                                    break;
                                }
                                System.out.println("Vaccine information successfully updated!");
                                break;

                            }


                        } else {
                            System.out.println("Make sure that the slot was assigned to someone or doesn't have vaccine" +
                                    " information already!");
                            break;
                        }

                    }
                    case (4): {
                        System.out.println("Thank you, byeeee~~ 🖖");
                        // Finally but importantly close the statement and connection
                        statement.close();
                        con.close();
                    }

                }
            }

        } catch (SQLException e) {
            sqlCode = e.getErrorCode(); // Get SQLCODE
            sqlState = e.getSQLState(); // Get SQLSTATE

            // Your code to handle errors comes here;
            // something more meaningful than a print would be good
            System.out.println("Code: " + sqlCode + "  sqlState: " + sqlState);
            System.out.println(e);
        }
    }
}
