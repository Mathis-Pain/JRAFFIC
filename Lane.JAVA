import java.util.List;
import java.util.ArrayList;

public class Lane {
  private List<Vehicle> vehicles;
  private TrafficLight trafficLight;
  private int length;
  private LaneType type;

  public Lane(int length, TrafficLight trafficLight, LaneType type) {
    this.length = length;
    this.trafficLight = trafficLight;
    this.type = type;
    this.vehicles = new ArrayList<>();
  }

  public TrafficLight getTrafficLight() {
    return trafficLight;
  }

  public List<Vehicle> getVehicles() {
    return vehicles;
  }

  public int getLength() {
    return length;
  }

  public LaneType getType() {
    return type;
  }

}