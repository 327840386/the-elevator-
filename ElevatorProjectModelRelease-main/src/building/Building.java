package building;

import building.enums.ElevatorSystemStatus;
import elevator.Elevator;
import elevator.ElevatorInterface;
import elevator.ElevatorReport;
import java.util.ArrayList;
import java.util.List;
import scanerzus.Request;


/**
 * This class represents a building.
 */
public class Building implements BuildingInterface {
  private final int numberOfFloors;
  private final int numberOfElevators;
  private final int elevatorCapacity;
  private final ElevatorInterface[] elevators;
  private final List<Request> upRequests = new ArrayList();
  private final List<Request> downRequests = new ArrayList();
  private ElevatorSystemStatus elevatorsStatus;

  /**
  * This constructor is used to create a new Building object.
  *
  * @param numberOfFloors    The number of floors in the building.
  * @param numberOfElevators The number of elevators in the building.
  * @param elevatorCapacity  The capacity of the elevators.
  *
  * @throws IllegalArgumentException if the number of floors is less than 2,
  *        the number of elevators is less than 1, or the elevator capacity is less than 1
  */
  public Building(int numberOfFloors, int numberOfElevators, int elevatorCapacity)
      throws IllegalArgumentException {
    if (numberOfFloors < 2) {
      throw new IllegalArgumentException("numberOfFloors must be greater than or equal to 2");
    } else if (numberOfElevators < 1) {
      throw new IllegalArgumentException("numberOfElevators must be greater than or equal to 1");
    } else if (elevatorCapacity < 1) {
      throw new IllegalArgumentException("maxOccupancy must be greater than or equal to 1");
    } else {
      this.numberOfFloors = numberOfFloors;
      this.numberOfElevators = numberOfElevators;
      this.elevatorCapacity = elevatorCapacity;
      this.elevators = new Elevator[numberOfElevators];

      for (int i = 0; i < numberOfElevators; ++i) {
        this.elevators[i] = new Elevator(numberOfFloors, this.elevatorCapacity);
      }

      this.elevatorsStatus = ElevatorSystemStatus.outOfService;
    }
  }

  /**
   * This method is used to start the elevator system.
   */
  public void startElevatorSystem() {
    if (this.elevatorsStatus != ElevatorSystemStatus.running) {
      if (this.elevatorsStatus == ElevatorSystemStatus.stopping) {
        throw new IllegalStateException("Elevator cannot be started until it is stopped");
      } else {
        ElevatorInterface[] var1 = this.elevators;
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3) {
          ElevatorInterface elevator = var1[var3];
          elevator.start();
        }

        this.elevatorsStatus = ElevatorSystemStatus.running;
      }
    }
  }

  /**
   * This method is used to stop the elevator system.
   */
  public void stopElevatorSystem() {
    if (this.elevatorsStatus != ElevatorSystemStatus.outOfService
        && this.elevatorsStatus != ElevatorSystemStatus.stopping) {
      ElevatorInterface[] var1 = this.elevators;
      int var2 = var1.length;

      for (int var3 = 0; var3 < var2; ++var3) {
        ElevatorInterface elevator = var1[var3];
        elevator.takeOutOfService();
        this.elevatorsStatus = ElevatorSystemStatus.stopping;
        this.upRequests.clear();
        this.downRequests.clear();
      }

    }
  }

  public int getNumberOfFloors() {
    return this.numberOfFloors;
  }

  public int getNumberOfElevators() {
    return this.numberOfElevators;
  }

  public int getElevatorCapacity() {
    return this.elevatorCapacity;
  }

  /**
   * This method is used to get the status of the elevator system.
   *
   * @return the status of the elevator system.
   */
  public BuildingReport getStatusElevatorSystem() {
    ElevatorReport[] elevatorReports = new ElevatorReport[this.elevators.length];

    for (int i = 0; i < this.elevators.length; ++i) {
      elevatorReports[i] = this.elevators[i].getElevatorStatus();
    }

    BuildingReport buildingReport = new BuildingReport(this.numberOfFloors,
        this.numberOfElevators, this.elevatorCapacity, elevatorReports,
        this.upRequests, this.downRequests, this.elevatorsStatus);
    return buildingReport;
  }

  /**
   * This method is used to add a request to the elevator system.
   *
   * @param request The request to be added to the elevator system.
   */
  public void addRequestToElevatorSystem(Request request) {
    if (this.elevatorsStatus != ElevatorSystemStatus.outOfService
        && this.elevatorsStatus != ElevatorSystemStatus.stopping) {
      if (request == null) {
        throw new IllegalArgumentException("Request cannot be null");
      } else if (request.getStartFloor() >= 0 && request.getStartFloor() < this.numberOfFloors) {
        if (request.getEndFloor() >= 0 && request.getEndFloor() < this.numberOfFloors) {
          if (request.getStartFloor() == request.getEndFloor()) {
            throw new IllegalArgumentException("Start floor and end floor cannot be the same");
          } else {
            if (request.getStartFloor() < request.getEndFloor()) {
              this.upRequests.add(request);
            } else {
              this.downRequests.add(request);
            }

          }
        } else {
          throw new IllegalArgumentException("End floor must be between 0 and "
              + (this.numberOfFloors - 1));
        }
      } else {
        throw new IllegalArgumentException("Start floor must be between 0 and "
            + (this.numberOfFloors - 1));
      }
    } else {
      throw new IllegalStateException("Elevator system not accepting requests.");
    }
  }

  /**
   * This method is used to step the elevator system.
   */
  public void stepElevatorSystem() {
    if (this.elevatorsStatus != ElevatorSystemStatus.outOfService) {
      if (this.elevatorsStatus != ElevatorSystemStatus.stopping) {
        this.distributeRequests();
      }

      ElevatorInterface[] var1 = this.elevators;
      int var2 = var1.length;

      int var3;
      for (var3 = 0; var3 < var2; ++var3) {
        ElevatorInterface elevator = var1[var3];
        elevator.step();
      }

      if (this.elevatorsStatus == ElevatorSystemStatus.stopping) {
        boolean allElevatorsOnGroundFloor = true;
        ElevatorInterface[] var7 = this.elevators;
        var3 = var7.length;

        for (int var8 = 0; var8 < var3; ++var8) {
          ElevatorInterface elevator = var7[var8];
          if (elevator.getCurrentFloor() != 0) {
            allElevatorsOnGroundFloor = false;
            break;
          }
        }

        if (allElevatorsOnGroundFloor) {
          this.elevatorsStatus = ElevatorSystemStatus.outOfService;
        }
      }

    }
  }

  private void distributeRequests() {
    if (!this.upRequests.isEmpty() || !this.downRequests.isEmpty()) {
      ElevatorInterface[] var1 = this.elevators;
      int var2 = var1.length;

      for (int var3 = 0; var3 < var2; ++var3) {
        ElevatorInterface elevator = var1[var3];
        if (elevator.isTakingRequests()) {
          List downRequestsForElevator;
          if (elevator.getCurrentFloor() == 0) {
            downRequestsForElevator = this.getRequests(this.upRequests);
            elevator.processRequests(downRequestsForElevator);
          } else if (elevator.getCurrentFloor() == this.numberOfFloors - 1) {
            downRequestsForElevator = this.getRequests(this.downRequests);
            elevator.processRequests(downRequestsForElevator);
          }
        }
      }
    }
  }

  private List<Request> getRequests(List<Request> requests) {
    List<Request> requestsToReturn = new ArrayList();

    while (!requests.isEmpty() && requestsToReturn.size() < this.elevatorCapacity) {
      requestsToReturn.add(requests.remove(0));
    }

    return requestsToReturn;
  }
}


