public class TrafficLight {

    private LightColor color;

    public TrafficLight(LightColor color) {
        this.color = color;
    }

    public LightColor getColor() {
        return color;
    }

    public void setColor(LightColor color) {
        this.color = color;
    }

    public boolean isGreen() {
        return color == LightColor.GREEN;
    }

    public boolean isRed() {
        return color == LightColor.RED;
    }
}