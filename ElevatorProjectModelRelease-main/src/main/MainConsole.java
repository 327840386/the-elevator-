package main;

import building.Building;
import java.util.Scanner;
import scanerzus.Request;

/**
 * The driver for the elevator system.
 * This class will create the elevator system and run it.
 * this is for testing the elevator system.
 * <p>
 * It provides a user interface to the elevator system.
 */
public class MainConsole {

  /**
   * The main method for the elevator system.
   * This method creates the elevator system and runs it.
   *
   * @param args the command line arguments
   */
  public static void main(String[] args) {

    // the number of floors, the number of elevators, and the number of people.

    final int numFloors = 11;
    final int numElevators = 8;
    final int numPeople = 3;


    String[] introText = {
        "Welcome to the Elevator System!",
        "This system will simulate the operation of an elevator system.",
        "The system will be initialized with the following parameters:",
        "Number of floors: " + numFloors,
        "Number of elevators: " + numElevators,
        "Number of people: " + numPeople,
        "The system will then be run and the results will be displayed.",
        "",
        "Press enter to continue."
    };

    for (String line : introText) {
      System.out.println(line);

    }
    Scanner scanner = new Scanner(System.in);
    scanner.nextLine();

    Building building = new Building(numFloors, numElevators, numPeople);

    building.startElevatorSystem();
    System.out.println("Elevator system started.");

    boolean running = true;
    while (running) {
      System.out.print("*      Enter 1 to add a request     *\n"
          + "*Enter 2 to step the elevator system*\n"
          + "*Enter 3 to stop the elevator system*\n");
      int choice = scanner.nextInt();
      switch (choice) {
        case 1:
          System.out.print("Enter the starting floor: ");
          int startFloor = scanner.nextInt();
          System.out.print("Enter the destination floor: ");
          int destinationFloor = scanner.nextInt();
          building.addRequestToElevatorSystem(new Request(startFloor, destinationFloor));
          System.out.println(building.getStatusElevatorSystem());
          break;

        case 2:
          building.stepElevatorSystem();
          System.out.println(building.getStatusElevatorSystem());
          break;

        case 3:
          running = false;
          building.stopElevatorSystem();
          System.out.println("Elevator system stopped.");
          break;

        default:
          System.out.println("Invalid choice. Please try again.");
          break;
      }
    }
  }
}