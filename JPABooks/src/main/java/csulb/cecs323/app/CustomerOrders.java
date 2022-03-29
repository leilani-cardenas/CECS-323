/*
 * Licensed under the Academic Free License (AFL 3.0).
 *     http://opensource.org/licenses/AFL-3.0
 *
 *  This code is distributed to CSULB students in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, other than educational.
 *
 *  2018 Alvaro Monge <alvaro.monge@csulb.edu>
 *
 */

package csulb.cecs323.app;

// Import all of the entity classes that we have written for this application.
import csulb.cecs323.model.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.Scanner;
import java.time.LocalDateTime;
import java.util.logging.Level;

/**
 * A simple application to demonstrate how to persist an object in JPA.
 * <p>
 * This is for demonstration and educational purposes only.
 * </p>
 * <p>
 *     Originally provided by Dr. Alvaro Monge of CSULB, and subsequently modified by Dave Brown.
 * </p>
 * Licensed under the Academic Free License (AFL 3.0).
 *     http://opensource.org/licenses/AFL-3.0
 *
 *  This code is distributed to CSULB students in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, other than educational.
 *
 *  2021 David Brown <david.brown@csulb.edu>
 *
 */
public class CustomerOrders {
   /**
    * You will likely need the entityManager in a great many functions throughout your application.
    * Rather than make this a global variable, we will make it an instance variable within the CustomerOrders
    * class, and create an instance of CustomerOrders in the main.
    */
   private EntityManager entityManager;

   /**
    * The Logger can easily be configured to log to a file, rather than, or in addition to, the console.
    * We use it because it is easy to control how much or how little logging gets done without having to
    * go through the application and comment out/uncomment code and run the risk of introducing a bug.
    * Here also, we want to make sure that the one Logger instance is readily available throughout the
    * application, without resorting to creating a global variable.
    */
   private static final Logger LOGGER = Logger.getLogger(CustomerOrders.class.getName());

   /**
    * The constructor for the CustomerOrders class.  All that it does is stash the provided EntityManager
    * for use later in the application.
    * @param manager    The EntityManager that we will use.
    */
   public CustomerOrders(EntityManager manager) {
      this.entityManager = manager;
   }

   public static void main(String[] args) {
      // Turning off logging
      LOGGER.setLevel(Level.OFF);

      LOGGER.fine("Creating EntityManagerFactory and EntityManager");
      EntityManagerFactory factory = Persistence.createEntityManagerFactory("CustomerOrders");
      EntityManager manager = factory.createEntityManager();

      // Create an instance of CustomerOrders and store our new EntityManager as an instance variable.
      CustomerOrders customerOrders = new CustomerOrders(manager);

      // Any changes to the database need to be done within a transaction.
      // See: https://en.wikibooks.org/wiki/Java_Persistence/Transactions
      LOGGER.fine("Begin of Transaction");
      EntityTransaction tx = manager.getTransaction();
      tx.begin();

      // Menu starts here
      Scanner scnr = new Scanner(System.in);
      int menuOption = -1;
      boolean validMenuOption = false;
      boolean menuDone = false;

      while (!menuDone) {
         String menu = "-- Main Menu --" + "\nEnter an option: "+ "\n1. Find Customer Details"
                 + "\n2. Find Product Details" + "\n3. Place Order" + "\n4. Quit";
         System.out.println(menu);
         while (!validMenuOption) {
            try {
               menuOption = scnr.nextInt();
               validMenuOption = true;

            } catch (Exception e) {
               System.out.println("Re-enter a valid option.");
               System.out.println(menu);
               scnr.nextLine();
            }
         }

         switch (menuOption) {
            case 1:
               customerOrders.availableCustomers();
               validMenuOption = false;
               scnr.nextLine();
               break;
            case 2:
               customerOrders.availableProducts();
               validMenuOption = false;
               scnr.nextLine();
               break;
            case 3:
               customerOrders.placeOrder();
               validMenuOption = false;
               scnr.nextLine();
               break;
            case 4:
               System.out.println("Quitting...");
               // menuDone = true; // this should also let the system quit
               System.exit(0);
               break;
            default:
               System.out.println("Re-enter a valid option.");
               validMenuOption = false;
               scnr.nextLine();
               break;
         }
      }

      // Commit the changes so that the new data persists and is visible to other users.
      tx.commit();
      LOGGER.fine("End of Transaction");

   } // End of the main method

   /**
    * Create and persist a list of objects to the database.
    * @param entities   The list of entities to persist.  These can be any object that has been
    *                   properly annotated in JPA and marked as "persistable."  I specifically
    *                   used a Java generic so that I did not have to write this over and over.
    */
   public <E> void createEntity(List <E> entities) {
      for (E next : entities) {
         LOGGER.info("Persisting: " + next);
         // Use the CustomerOrders entityManager instance variable to get our EntityManager.
         this.entityManager.persist(next);
      }

      // The auto generated ID (if present) is not passed in to the constructor since JPA will
      // generate a value.  So the previous for loop will not show a value for the ID.  But
      // now that the Entity has been persisted, JPA has generated the ID and filled that in.
      for (E next : entities) {
         LOGGER.info("Persisted object after flush (non-null id): " + next);
      }
   } // End of createEntity member method

   /**
    * This function takes in a upc and returns the product it corresponds to by using the
    * ReturnStock query that is declared in the Products class. The single Product it returns allows us to check the
    * units in stock.
    * @param UPC        The upc of the product that you are looking for.
    * @return           The Products instance corresponding to that UPC.
    */
   public Products getStock (String UPC) {
      // Run the native query that we defined in the Products entity to find the right product.
      List<Products> products = this.entityManager.createNamedQuery("ReturnStock",
              Products.class).setParameter(1, UPC).getResultList();

      if (products.size() == 0) {
         // No products in DB
         return null;
      }
      else {
         // Return the object that they asked for
         return products.get(0);
      }
   } // End of getStock member method

   /**
    * This function takes in a customer ID and prints all the details that correspond to it by using the
    * ReturnCustomer query that is declared in the Customer class.
    * @param id        The id of the customer that you are looking for.
    */
   public void getCustomer (String id) {
      // Run the native query that we defined in the Customers entity to find the right customer.
      List<Customers> customers = this.entityManager.createNamedQuery("ReturnCustomer",
              Customers.class).setParameter(1, id).getResultList();

      if (customers.size() == 0) {
         System.out.println("Customer cannot be found.");
      }
      else {
         // Prints the customer details
         System.out.println(customers.toString());
      }
   } // End of getCustomer member method

   /**
    * This function takes in a product upc and prints out all the details that correspond to it by using the
    * ReturnProduct query that is decalred in the Products class.
    * @param UPC       The upc of the product that you are looking for.
    */
   public void getProduct (String UPC) {
      // Run the native query that we defined in the Products entity to find the right product.
      List<Products> products = this.entityManager.createNamedQuery("ReturnProduct",
              Products.class).setParameter(1, UPC).getResultList();

      if (products.size() == 0) {
         System.out.println("Product cannot be found.");
      }
      else {
         // Return the product details
         System.out.println(products.toString());
      }
   } // End of getProduct member method

   /**
    * This function takes in a customer ID and returns the customer it corresponds to by using the
    * ReturnCustomer query that is declared in the Customers class. The single Customer it returns allows us to
    * access all the customer's details.
    * @param id        The id of the customer that you are looking for.
    * @return           The Customers instance corresponding to that ID.
    */
   public Customers getCustomerDetails (String id) {
      // Run the native query that we defined in the Customers entity to find the right customer.
      List<Customers> customers = this.entityManager.createNamedQuery("ReturnCustomer",
              Customers.class).setParameter(1, id).getResultList();

      if (customers.size() == 0) {
         return null;
      }
      else {
         // Return the style object that they asked for.
         return customers.get(0);
      }
   } // End of getCustomerDetails member method

   /**
    * This function runs the ReturnAllCustomers query that is declared in the Customers class, which prints a list of
    * all existing instances in the Customers table. This list is shown to the user to help ensure they don't enter an
    * invalid customer ID when trying to place an order.
    */
   public void availableCustomers() {

      // Run the native query that we defined in the Customers entity to find all existing customers.
      List<Customers> customers = this.entityManager.createNamedQuery("ReturnAllCustomers", Customers.class).getResultList();

      if (customers.size() == 0) {
         System.out.println("No customers available.");
      }
      else {
         System.out.println("Customer Information:" +
                 "-------------------------------------------");
         for (int i = 0; i < customers.size(); i++) {
            System.out.println(customers.get(i).toString());
         }

         int x = checkValidCustomer();
         getCustomer(Integer.toString(x));
      }
   } // End of availableCustomers member method

   /**
    * This function check for validity of user input for product upc.
    * @return     An integer equivalent to the correct product's upc.
    */
   public long checkValidProduct(){
      Scanner scnr = new Scanner(System.in);
      boolean validProductOption = false;
      String upc = "";
      long validUPC = 0;

      if (!validProductOption) {
         while (!validProductOption) {
            System.out.println("Enter the Product UPC: ");
            upc = scnr.nextLine();

            // check for valid upc by checking if upc is an actual upc number
            try {
               validUPC = Long.parseLong(upc);
               System.out.println("Found Product: ");
               getProduct(upc);
               validProductOption = true;
            } catch (Exception e) {
               System.out.println("Invalid UPC. Try again.");
            }
         }
      }
      else {
         System.out.println("Product cannot be found.");
      }
      return validUPC;
   } // End of checkValidProduct member method

   /**
    * This function runs the ReturnAllProducts query that is declared in the Products class, which prints a list of
    * all existing instances in the Products table. This list is shown to the user to help ensure they don't enter an
    * invalid UPC when trying to place an order.
    */
   public void availableProducts() {

      // Run the native query that we defined in the Products entity to find all existing products.
      List<Products> products = this.entityManager.createNamedQuery("ReturnAllProducts", Products.class).getResultList();

      if (products.size() == 0) {
         System.out.println("No products available.");
      }
      else {
         System.out.println("Product Information:" +
                 "-------------------------------------------");
         for (int i = 0; i < products.size(); i++) {
            System.out.println(products.get(i).toString());
         }
         checkValidProduct();

      }

   } // End of availableProducts member method

   /**
   * This function checks for the validity of user input for customer id.
    * @return        An integer equivalent to the customer's id.
    */
   public int checkValidCustomer(){
      Scanner scnr = new Scanner(System.in);
      boolean validCustomerOption = false;
      String custID ;
      int custId =0;

      while (!validCustomerOption) {
         System.out.println("Enter the Customer ID: ");
         custID = scnr.nextLine();
            try {
            custId = Integer.parseInt(custID);
            validCustomerOption = true;
            }
            catch (Exception e) {
               System.out.println("Invalid Customer ID. Try again.");
            }
      }
      return custId;
   }

   /**
    * This function prompts the user with details needed to complete an order, then calculates the total of an order,
    * and either commits the instances of those orders/order_lines to the db or allows the user to abort the order.
    */
   public void placeOrder() {
      Scanner scnr = new Scanner(System.in);
      boolean orderDone = false; // flag that checks if customer is done placing all orders
      int updatedStock = 0;
      Products availableStock = new Products();
      int quantityWanted = 0;
      String keepAdding = "";
      LocalDateTime orderDate; // keep track of date/time for EVERY order placed
      List<Orders> orders = new ArrayList<Orders>(); // all orders the user creates
      List<Order_lines> order_lines = new ArrayList<Order_lines>(); // all order lines the user creates


      System.out.println("Placing Order...");
      Customers c = this.getCustomerDetails(String.valueOf(checkValidCustomer()));


      // keeps asking to add products until user is done with the order
      while(!orderDone) {
         long validProduct = checkValidProduct();

         boolean validQuantity = false;
         while (!validQuantity) {
            System.out.println("Enter the quantity of the product: ");
            try {
               quantityWanted = scnr.nextInt();
               validQuantity = true;
            }
            catch (Exception e) {
               System.out.println("Invalid quantity. Try again.");
               scnr.nextLine();
            }

         }

         availableStock = this.getStock(Long.toString(validProduct));
         if (quantityWanted > availableStock.getUnits_in_stock()) {
            System.out.println("Not enough stock on hand. Adding all that is available.");
            quantityWanted = availableStock.getUnits_in_stock();
         }

         orderDate = LocalDateTime.now();
         Orders o = new Orders(c, orderDate, availableStock.getMfgr());
         orders.add(o);
         order_lines.add(new Order_lines(quantityWanted, availableStock.getUnit_list_price(), o, c, availableStock));
         this.createEntity(orders);
         this.createEntity(order_lines);
         scnr.nextLine();

         System.out.println("Would you like to keep adding products to your order? Y/N");
         keepAdding = scnr.nextLine();

         if (!keepAdding.equalsIgnoreCase("y")) {
            updatedStock = availableStock.getUnits_in_stock() - quantityWanted;
            Products currentProd = this.getStock(order_lines.get(order_lines.size()-1).getProduct().getUPC());
            currentProd.setUnits_in_stock(updatedStock);
            orderDone = true;
         }
         else {
            // update the product stock in db
            updatedStock = availableStock.getUnits_in_stock() - quantityWanted;
            Products currentProd = this.getStock(order_lines.get(order_lines.size()-1).getProduct().getUPC());
            currentProd.setUnits_in_stock(updatedStock);
         }
      }

      // calculating total
      float total = 0;
      for (int i = 0; i < order_lines.size(); i++) {
         total += order_lines.get(i).getQuantity() * order_lines.get(i).getUnit_sale_price();
      }
      System.out.println("Your order total is: $" + total);
      System.out.println("Are you sure you want to place this order? Y/N");
      String confirm = scnr.nextLine();

      if (!confirm.equalsIgnoreCase("y")) {
         // user wants to abort
         // remove all the orders/order_lines we've created so far
         for (int i = 0; i < orders.size(); i++) {
            orders.remove(i);
            order_lines.remove(i);
         }
      }
      else {
         // Commit changes to db
         this.entityManager.getTransaction().commit();
      }
   } // End of placeOrder member method

} // End of CustomerOrders class
