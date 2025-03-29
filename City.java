import java.util.ArrayList;

/**
 * City is a class where the data fields are names, X and Y coordinates of the city,
 * an ArrayList of other cities which the city has connections to, the shortest distance to the starting city,
 * and the previous city which should be visited in order to have the shortest pathway between the starting city.
 *
 * Also, these data fields are private and accessed through their getter/setter methods.
 */
public class City {
    public String cityName;
    public int x;
    public int y;
    private ArrayList<City> connections;
    private double shortestDistanceToStart;
    private City previousCity;

    public City(String cityName, int x, int y) {
        this.cityName = cityName;
        this.x = x;
        this.y = y;
        this.connections = new ArrayList<City>();
        this.shortestDistanceToStart = Double.MAX_VALUE;
    }

    public String getCityName() {
        return cityName;
    }
    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getX() {
        return x;
    }
    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }
    public void setY(int y) {
        this.y = y;
    }

    public ArrayList<City> getConnections() {
        return this.connections;
    }
    public void addToConnections(City city) {
        this.connections.add(city);
    }

    public void setPreviousCity(City city) {
        this.previousCity = city;
    }
    public City getPreviousCity() {
        return this.previousCity;
    }

    public void setShortestDistanceToStart(double shortestDistanceToStart) {
        this.shortestDistanceToStart = shortestDistanceToStart;
    }
    public double getShortestDistanceToStart() {
        return shortestDistanceToStart;
    }
}
