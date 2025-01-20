package building;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import building.enums.ElevatorSystemStatus;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import scanerzus.Request;

/**
 * This is the testing class for the building.
 */
public class BuildingTest {
  private Building building;

  @Before
  public void setUp() {
    building = new Building(8, 3, 5);
  }

  @Test
  public void testConstructorValidInputs() {
    assertEquals(8, building.getNumberOfFloors());
    assertEquals(3, building.getNumberOfElevators());
    assertEquals(5, building.getElevatorCapacity());
  }

  @Test
  public void testConstructorInvalidInputs() {
    assertThrows(IllegalArgumentException.class, () -> {
      new Building(0, 3, 5);
    });
    assertThrows(IllegalArgumentException.class, () -> {
      new Building(8, 0, 5);
    });
    assertThrows(IllegalArgumentException.class, () -> {
      new Building(8, 3, 0);
    });
  }

  @Test
  public void testStartElevatorSystem() {
    building.stopElevatorSystem();
    building.startElevatorSystem();
    assertEquals(ElevatorSystemStatus.running,
        building.getStatusElevatorSystem().getSystemStatus());
  }

  @Test
  public void testStopElevatorSystem() {
    building.stopElevatorSystem();
    assertEquals(ElevatorSystemStatus.outOfService,
        building.getStatusElevatorSystem().getSystemStatus());
  }

  @Test
  public void testGetNumberOfFloors() {
    assertEquals(8, building.getNumberOfFloors());
  }

  @Test
  public void testGetNumberOfElevators() {
    assertEquals(3, building.getNumberOfElevators());
  }

  @Test
  public void testGetElevatorCapacity() {
    assertEquals(5, building.getElevatorCapacity());
  }

  @Test
  public void testAddValidRequest() {
    building.startElevatorSystem();
    building.addRequestToElevatorSystem(new Request(2, 5));
    assertEquals(1, building.getStatusElevatorSystem().getUpRequests().size());
  }

  @Test
  public void testAddInvalidRequestStartFloorNegative() {
    building.startElevatorSystem();
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      building.addRequestToElevatorSystem(new Request(-1, 5));
    });
    assertEquals("Start floor must be between 0 and 7", exception.getMessage());
  }

  @Test
  public void testAddInvalidRequestEndFloorOutOfRange() {
    building.startElevatorSystem();
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      building.addRequestToElevatorSystem(new Request(2, 12));
    });
    assertEquals("End floor must be between 0 and 7", exception.getMessage());
  }

  @Test
  public void testAddInvalidRequestStartFloorEqualsEndFloor() {
    building.startElevatorSystem();
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      building.addRequestToElevatorSystem(new Request(3, 3));
    });
    assertEquals("Start floor and end floor cannot be the same", exception.getMessage());
  }

  @Test
  public void testAddInvalidRequestSystemNotAcceptingRequests() {
    building.stopElevatorSystem();
    IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
      building.addRequestToElevatorSystem(new Request(2, 5));
    });
    assertEquals("Elevator system not accepting requests.", exception.getMessage());
  }
}
