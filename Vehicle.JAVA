public class Vehicle {
  private static final int LENGTH = 20;
  private int x, y;
  private int speed;
  private String color;
  private Lane currentLane;
  private Origin origin;
  private boolean moving = false;
  private Direction direction;

  public Vehicle(int x, int y, int speed, String color, Lane currentLane, Direction direction, Origin origin) {
    this.x = x;
    this.y = y;
    this.speed = speed;
    this.color = color;
    this.currentLane = currentLane;
    this.direction = direction;
    this.origin = origin;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public int getSpeed() {
    return speed;
  }

  public String getColor() {
    return color;
  }

  public Lane getCurrentLane() {
    return currentLane;
  }

  public Origin getOrigin() {
    return origin;
  }

  public boolean isMoving() {
    return moving;
  }

  public Direction getDirection() {
    return direction;
  }

  public void setX(int x) {
    this.x = x;
  }

  public void setY(int y) {
    this.y = y;
  }

  public void setMoving(boolean moving) {
    this.moving = moving;
  }

  public void setCurrentLane(Lane currentLane) {
    this.currentLane = currentLane;
  }

  public static int getLENGTH() {
    return LENGTH;
  }
}