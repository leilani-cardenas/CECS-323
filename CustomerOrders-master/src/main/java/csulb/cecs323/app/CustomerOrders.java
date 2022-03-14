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
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.Scanner;
import java.time.LocalDateTime;

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
      // List of Products that I want to persist.  I could just as easily done this with the seed-data.sql
      List <Products> products = new ArrayList<Products>();
      // Load up my List with the Entities that I want to persist.  Note, this does not put them
      // into the database.
      //products.add(new Products("076174517163", "16 oz. hickory hammer", "Stanely Tools", "1", 9.97, 50));
      products.add(new Products("076174517163", "16 oz. JellyBeans", "YummyCandy", "1", 10.99, 50));
      products.add(new Products("28379287552", "20 oz. Hershe", "HrsCo", "1", 8.98, 120));
      products.add(new Products("91829048104", "16 oz. KitKat", "KitCompany", "1", 6.71, 100));
      customerOrders.createEntity (products);

      customerOrders.mainMenu();

      //Create the list of owners in the database.

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
    * Think of this as a simple map from a String to an instance of Products that has the
    * same name, as the string that you pass in.  To create a new Cars instance, you need to pass
    * in an instance of Products to satisfy the foreign key constraint, not just a string
    * representing the name of the style.
    * @param UPC        The name of the product that you are looking for.
    * @return           The Products instance corresponding to that UPC.
    */
//   public Products getProduct (String UPC) {
//      // Run the native query that we defined in the Products entity to find the right style.
//      List<Products> products = this.entityManager.createNamedQuery("ReturnProduct",
//              Products.class).setParameter(1, UPC).getResultList();
//      if (products.size() == 0) {
//         // Invalid style name passed in.
//         return null;
//      } else {
//         // Return the style object that they asked for.
//         return products.get(0);
//      }
//   }// End of the getStyle method

//   public void placeOrder(Customers name, Products id, Products quantity) {
//
//      Scanner scnr = new Scanner(System.in);
//      String custID;
//      String timeAndDate;
//      String prodUPC;
//      int quantitiesInStock;
//      boolean flag = true;
//      int validID;
//      Customers foundCustomer;
//      Products pd;
//      Order_lines order;
//      //LocalDateTime orderTime = LocalDateTime.now();
//
//
//      if (!flag) {
//         while (!flag) {
//            System.out.println("Enter the Customer ID: ");
//            custID = scnr.nextLine();
//            System.out.println("Enter the Product UPC: ");
//            prodUPC = scnr.nextLine();
//            System.out.println("Enter the quantity of the product: ");
//            quantitiesInStock = scnr.nextInt();
//
//
//            // check for valid input by checking if customer id exists in table? OR is an actual id number?
//            try {
//               validID = Integer.parseInt(custID);
//
//               LocalDateTime dateTime = LocalDateTime.now();
//
//
//               if(quantitiesInStock <= .getUnits_in_stock() ){
//                     order.setQuantity(quantitiesInStock);
//               }
//               //if there is not enough quantity in stock
//               else{
//                  System.out.println("Out of stock. All quantities will be added into your order!");
//                  pd.setUnits_in_stock(pd.getUnits_in_stock());
//                  order.setOrder();
//               }
//
//               flag = true;
//            } catch (Exception e) {
//               System.out.println("Invalid input. Try again.");
//               flag = false;
//               System.out.println("products out of stock");
//            }
//
//         }
//
//      }
//
//
//   }

   public void getTotal(String custID) {
      List<Order_lines> order_lines = this.entityManager.createNamedQuery("ReturnTotal",
              Order_lines.class).getResultList();
      if (order_lines.size() == 0) {
         // Invalid style name passed in.
         System.out.println("No order total.");
      } else {
         // Return the style object that they asked for.
         System.out.println(order_lines.get(0));

      }
   }

   public Products getStock (String UPC) {
      List<Products> products = this.entityManager.createNamedQuery("ReturnStock",
              Products.class).setParameter(1, UPC).getResultList();

      if (products.size() == 0) {
         // Invalid style name passed in.
         return null;
      } else {
         // Return the style object that they asked for.
         return products.get(0);
      }

   }

   public void getProduct (String UPC) {
      // Run the native query that we defined in the Products entity to find the right style.
      List<Products> products = this.entityManager.createNamedQuery("ReturnProduct",
              Products.class).setParameter(1, UPC).getResultList();

      if (products.size() == 0) {
         // Invalid style name passed in.
         System.out.println("Customer cannot be found.");
      } else {
         // Return the style object that they asked for.
         System.out.println("/n"+ products.toString());
      }
   }

   public void getCustomer (String id) {
      // Run the native query that we defined in the Customers entity to find the right person.
      List<Customers> customers = this.entityManager.createNamedQuery("ReturnCustomer",
              Customers.class).setParameter(1, id).getResultList();

      if (customers.size() == 0) {
         // Invalid style name passed in.
         System.out.println("Customer cannot be found.");
      } else {
         // Return the style object that they asked for.
         System.out.println(customers.toString());
      }
   }

   public Customers getCustomerDetails (String id) {
      // Run the native query that we defined in the Customers entity to find the right person.
      List<Customers> customers = this.entityManager.createNamedQuery("ReturnCustomer",
              Customers.class).setParameter(1, id).getResultList();

      if (customers.size() == 0) {
         // Invalid style name passed in.
         return null;
      } else {
         // Return the style object that they asked for.
         return customers.get(0);
      }
   }

   public void availableCustomers() {
      Scanner scnr = new Scanner(System.in);
      boolean flag = true;
      String input = "";
      int validInput;
      Customers foundCustomer;

      List<Customers> customers = this.entityManager.createNamedQuery("ReturnAllCustomers", Customers.class).getResultList();

      if (customers.size() == 0) {
         System.out.println("No customers available.");
      }
      else {
         flag = false;
         System.out.println("Customer Information:" +
                 "-------------------------------------------");
         for (int i = 0; i < customers.size(); i++) {
            System.out.println(customers.get(i).toString());
         }
      }

      if (!flag) {
         while (!flag) {
            System.out.println("Enter the Customer ID: ");
            input = scnr.nextLine();

            // check for valid input by checking if customer id exists in table? OR is an actual id number?
            try {
               validInput = Integer.parseInt(input);
               flag = true;
            } catch (Exception e) {
               System.out.println("Invalid input. Try again.");
               flag = false;
            }
         }
         System.out.println("Found customer: ");
         getCustomer(input);

      } else {
         System.out.println("Cannot search for customer.");
      }

   }

   public void availableProducts() {
      Scanner scnr = new Scanner(System.in);
      boolean flag = true;
      String input = "";
      long validInput;

      List<Products> products = this.entityManager.createNamedQuery("ReturnAllProducts", Products.class).getResultList();

      if (products.size() == 0) {
         System.out.println("No products available.");
      }
      else {
         flag = false;
         System.out.println("Product Information:" +
                 "-------------------------------------------");
         for (int i = 0; i < products.size(); i++) {
            System.out.println(products.get(i).toString());
         }
      }

      if (!flag) {
         while (!flag) {
            System.out.println("Enter the Product UPC: ");
            input = scnr.nextLine();

            // check for valid input by checking if upc exists in table? OR is an actual upc number?
            try {
               validInput = Long.parseLong(input);
               flag = true;
            } catch (Exception e) {
               System.out.println("Invalid input. Try again.");
               flag = false;
            }
         }
         System.out.println("Found Product: ");
         getProduct(input);

      } else {
         System.out.println("Cannot search for product.");
      }

   }

   public void mainMenu() {
      Scanner scnr = new Scanner(System.in);
      int menuOption = -1;
      boolean flag = false;
      boolean done = false;
      boolean stop = false;
      boolean loop = false;

      while (!done) {
         String menu = "Main Menu" + "\nEnter an option"+ "\n1. Find Customer Details"
                 + "\n2. Find Product Details" + "\n3. Place Order" + "\n4. Quit";
         System.out.println(menu);
         while (!flag) {
            try {
               //scnr.nextLine();
               menuOption = scnr.nextInt();
               flag = true;

            } catch (Exception e) {
               System.out.println("Re-enter a valid option.");
               scnr.nextLine();
            }
         }

         switch (menuOption) {
            case 1:
               System.out.println("Finding Customer Details...");
               this.availableCustomers();
               break;

            case 2:
               System.out.println("Finding Product Details...");
               this.availableProducts();
               break;
            case 3:
               System.out.println("Placing Order...");
               String custID;
               String prodUPC = "";
               int quantityWanted;
               LocalDateTime dateTime = LocalDateTime.now();
               //String in = "";
               List<Orders> orders = new ArrayList<Orders>();
               List<Order_lines> order_lines = new ArrayList<Order_lines>();
               Long validUPC;
               String cont = "";

               scnr.nextLine();
               System.out.println("Enter the Customer ID: ");
               custID = scnr.nextLine();
               Customers c = this.getCustomerDetails(custID);

               // loop for input until user is done adding products
               while(!stop) {

                  while (!loop) {
                     System.out.println("Enter the Product UPC: ");
                     prodUPC = scnr.nextLine();

                     // check for valid input by checking if upc is an actual upc number
                     try {
                        validUPC = Long.parseLong(prodUPC);
                        loop = true;
                     } catch (Exception e) {
                        System.out.println("Invalid input. Try again.");
                     }
                  }

                  //System.out.println("Enter the Product UPC: ");
                  //prodUPC = scnr.nextLine();

                  System.out.println("Enter the quantity of the product: ");
                  quantityWanted = scnr.nextInt();

                  Products availableStock = this.getStock(prodUPC);
                  if (quantityWanted > availableStock.getUnits_in_stock()) {
                     quantityWanted = availableStock.getUnits_in_stock();
                  }

                  Orders o = new Orders(c, dateTime, availableStock.getMfgr());
                  orders.add(o);
                  order_lines.add(new Order_lines(quantityWanted, availableStock.getUnit_list_price(), o, c, availableStock));
                  this.createEntity(orders);
                  this.createEntity(order_lines);

                  scnr.nextLine();

                  System.out.println("Would you like to keep adding products to your order? Y/N");
//                  Scanner input = new Scanner(System.in);
//                  String in = input.nextLine();
//                  in = input.nextLine();
                  cont = scnr.nextLine();

                  if (!cont.equalsIgnoreCase("y")) {
                     stop = true;
                     done = true;
                  } else {
                     loop = false;
                  }


               }

               System.out.println("Your order total is: $");
               float total = 0;

               this.getTotal(custID);

               break;
            default:
               //System.out.println("quit");
               System.exit(0);
               break;

         }
      }
   }



} // End of CustomerOrders class
