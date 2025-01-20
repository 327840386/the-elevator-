package building;

import scanerzus.Request;

/**
 * This interface is used to represent a building.
 */
public interface BuildingInterface {
  int getNumberOfFloors();

  int getNumberOfElevators();

  int getElevatorCapacity();

  void startElevatorSystem();

  void stopElevatorSystem();

  BuildingReport getStatusElevatorSystem();

  void addRequestToElevatorSystem(Request var1);

  void stepElevatorSystem();


}
