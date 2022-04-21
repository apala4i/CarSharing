package carsharing;

import carsharing.DbData.Car.Car;
import carsharing.DbData.Car.CarDAO;
import carsharing.DbData.Company.Company;
import carsharing.DbData.Company.CompanyDAO;
import carsharing.DbData.Customer.Customer;
import carsharing.DbData.Customer.CustomerDAO;

import java.util.List;
import java.util.Scanner;

public class Menu{
    static CompanyDAO aCompanyDao;

    static void Connect(String URL) {
        aCompanyDao = new CompanyDAO(URL);
        CarProcessingMenu.Connect(URL);
        CustomerProcessMenu.Connect(URL);
    }


    static void Run() {
        var aScanner = new Scanner(System.in);
        int choice = 2;
        while (choice != 0)
        {
            System.out.println("" +
                    "1. Log in as a manager\n" +
                    "2. Log in as a customer\n" +
                    "3. Create a customer\n" +
                    "0. Exit");
            choice = aScanner.nextInt();
            switch (choice) {
                case 1:
                    managerMenu();
                    break;
                case 2:
                    CustomerProcessMenu.customerGeneralMenu();
                    break;
                case 3:
                    CustomerProcessMenu.createCustomer();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Wrong choice");
            }
        }
    }

    static private void managerMenu() {
        var aScanner = new Scanner(System.in);
        int choice = 2;

        while (choice != 0)
        {
            System.out.println("" +
                    "1. Company list\n" +
                    "2. Create a company\n" +
                    "3. current condition\n" +
                    "0. Back");
            choice = aScanner.nextInt();
            switch (choice) {
                case 1:
                    companyListManager();
                    break;
                case 2:
                    createCompany(aScanner);
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Wrong choice");
            }
        }
    }

    public static void createCompany(Scanner aScanner) {
        System.out.print("\nEnter the company name:\n" +
                "> ");
        aScanner.nextLine();
        String companyName = aScanner.nextLine();
        aCompanyDao.insert(new Company(companyName));
        System.out.println("The company was created!\n");
    }

    public static void companyListManager() {
        var aScanner = new Scanner(System.in);
        var companyList = aCompanyDao.getAll();
        if (companyList.isEmpty()) {
            System.out.println("\nThe company list is empty!\n");
        } else {
            System.out.println("\nChoose the company:");
            companyList.forEach(x -> System.out.println(companyList.indexOf(x) + 1 + ". " + x.getName()));
            System.out.println("0. Back");

            int company_id = aScanner.nextInt();
            aScanner.nextLine();

            if (company_id != 0) {
                CarProcessingMenu.Run(companyList.get(company_id - 1));
            }

            System.out.println();
        }
    }

    public static int companyListOpinion() {
        int company_id = 0;
        var aScanner = new Scanner(System.in);
        var companyList = aCompanyDao.getAll();
        if (companyList.isEmpty()) {
            System.out.println("\nThe company list is empty!\n");
        } else {
            System.out.println("\nChoose the company:");
            companyList.forEach(x -> System.out.println(companyList.indexOf(x) + 1 + ". " + x.getName()));
            System.out.println("0. Back");

            company_id = aScanner.nextInt();
            aScanner.nextLine();

            System.out.println();
        }
        return companyList.get(company_id - 1).getId();
    }
}

class CustomerProcessMenu {
    static CustomerDAO customerDAO;

    static void Connect (String URL) {
        customerDAO = new CustomerDAO(URL);
    }

    static public void customerGeneralMenu() {
        var aScanner = new Scanner(System.in);
        int choice;
        var allCustomers = customerDAO.getAll();
        if (allCustomers.isEmpty()) {
            System.out.println("The customer list is empty!");
        } else {
            System.out.println("Customer list:");
            allCustomers.forEach(x -> System.out.println(x.getId() + ". " + x.getName()));
            System.out.println("0. Back");
            choice = aScanner.nextInt();
            aScanner.nextLine();
            if (choice > 0 && choice < allCustomers.size()) {
                customerMenu(allCustomers.get(choice - 1));
            }
        }
    }

    static private void customerMenu(Customer customer) {
        var scanner = new Scanner(System.in);
        int choice = -1;
        while (choice != 0)
        {
            System.out.println("1. Rent a car\n" +
                    "2. Return a rented car\n" +
                    "3. My rented car\n" +
                    "0. Back");
            choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    RentCar(customer);
                    break;
                case 2:
                    ReturnRentedCar(customer);
                    break;
                case 3:
                    PrintRentedCar(customer);
                    break;
                case 0:
                    break;
                default:
                    System.out.println("There are no such option!\n");
            }
            customer = customerDAO.getById(customer.getId());
        }
    }

    static private void RentCar(Customer customer) {
        if (customer.getRented_car_id() == 0)
        {
            int company_id = Menu.companyListOpinion();
            if (company_id != 0) {
                var Car = CarProcessingMenu.carListOpinion(company_id, "Choose a car:");
                if (!Car.isEmpty())
                {
                    customerDAO.updateInfo(customer, new Customer(customer.getName(), Car.getId()));
                    System.out.println("You rented '" + Car.getName() + "'\n");
                }
            }
        } else {
            System.out.println("\nYou've already rented a car!\n");
        }

    }

    static private void ReturnRentedCar(Customer customer) {
        if (customer.getRented_car_id() == 0) {
            System.out.println("You didn't rent a car!");
        } else {
            customerDAO.updateInfo(customer, new Customer(customer.getName(), 0));
            System.out.println("You've returned a rented car!");
        }
    }

    static private void PrintRentedCar(Customer customer) {
        if (customer.getRented_car_id() == 0) {
            System.out.println("You didn't rent a car!");
        } else {
            System.out.println("Your rented car:");
            var car = CarProcessingMenu.aCarDAO.getById(customer.getRented_car_id());
            var comp = Menu.aCompanyDao.getById(car.getCompany_id());
            System.out.println(car.getName());
            System.out.println("Company:");
            System.out.println(comp.getName());
        }
    }

    static void createCustomer() {
        System.out.print("Enter the customer name:\n> ");
        var scanner = new Scanner(System.in);
        String name = scanner.nextLine();
        customerDAO.insertWithoutCarId(new Customer(name));
        System.out.println("The customer was added!");
    }

}

class CarProcessingMenu {
    static CarDAO aCarDAO;
    public static void Connect(String URl){
        aCarDAO = new CarDAO(URl);
    }

    public static void Run(Company subCompany) {

        int choice = -1;
        var aScanner = new Scanner(System.in);
        while (choice != 0)
        {
            System.out.println("'" + subCompany.getName() + "' " + "company");
            System.out.println("1. Car list\n" +
                    "2. Create a car\n" +
                    "0. Back");
            choice = aScanner.nextInt();
            aScanner.nextLine();
            switch (choice) {
                case 1:
                    listByIdChoice(subCompany.getId(), "Car list: ");
                    break;
                case 2:
                    insertChoice(subCompany.getId());
                    break;
                case 0:
                    break;
                default:
                    System.out.println("There are no such opinion, try again");
            }
        }
    }
    private static List<Car> listByIdChoice(int company_id, String text) {
        var carList = aCarDAO.getAllByCompanyId(company_id);
        if (carList.isEmpty()) {
            System.out.println("The car list is empty!");
        } else {
            System.out.println(text);
            int i = 1;
            for (Car car : carList) {
                System.out.println(i++ + ". " + car.getName());
            }
        }
        return carList;
    }

    private static void insertChoice(int company_id) {
        var aScanner = new Scanner(System.in);
        System.out.print("Enter the car name:\n>");
        var name = aScanner.nextLine();
        aCarDAO.insert(new Car(name, company_id));
        System.out.println("The car was added!\n");
    }

    public static Car carListOpinion(int company_id, String text) {
        var carList = listByIdChoice(company_id, text);
        System.out.println("0. Back");
        var scanner = new Scanner(System.in);
        var choice = scanner.nextInt();
        if (choice != 0) {
            return carList.get(choice - 1);
        } else {
            return new Car("0", 0);
        }
    }
}
