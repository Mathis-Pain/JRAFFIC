public class Intersection {
    private Lane northLane;
    private Lane southLane;
    private Lane eastLane;
    private Lane westLane;

    public Intersection(
            Lane northLane,
            Lane southLane,
            Lane eastLane,
            Lane westLane) {
        this.northLane = northLane;
        this.southLane = southLane;
        this.eastLane = eastLane;
        this.westLane = westLane;
    }
    public Lane getNorthLane(){
        return northLane;
    }
    public Lane getSouthLane(){
        return southLane;
    }
    public Lane getEastLane(){
        return  eastLane;
    }
    public Lane getWestLane(){
        return westLane;
    }
}