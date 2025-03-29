import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * This program finds the closest pathway between two cities and displays the map.
 * Uses StdDraw library for visualization.
 * @author Akin Tuna Sakalli
 * @since 3.04.2024
 */

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        // The file where the coordinates of the cities are present is opened.
        File cityCoordinates = new File("MapData/city_coordinates.txt");
        // An ArrayList named cities is created which contains all the city objects given in the city coordinates file.
        ArrayList<City> cities = processCityCoordinates(cityCoordinates);
        // The file where the connections between cities are provided is opened.
        File cityConnections = new File("MapData/city_connections.txt");
        // All the city connections are implemented in the data fields of city objects.
        processCityConnections(cityConnections, cities);

        // The program asks for a starting city name as input.
        Scanner input = new Scanner(System.in);
        System.out.print("Enter starting city: ");
        String startingCityInput = input.next();
        // The program keeps asking for input until the user gives a proper starting city name as input.
        while (!isCityPresent(cities, startingCityInput)) {
            System.out.println("City named '" + startingCityInput + "' not found. Please enter a valid city name.");
            System.out.print("Enter starting city: ");
            startingCityInput = input.next();
        }

        // The program asks for a destination city name as input.
        System.out.print("Enter destination city: ");
        String destinationCityInput = input.next();
        // The program keeps asking for input until the user gives a proper starting city name as input.
        while (!isCityPresent(cities, destinationCityInput)) {
            System.out.println("City named '" + destinationCityInput + "' not found. Please enter a valid city name.");
            System.out.print("Enter destination city: ");
            destinationCityInput = input.next();
        }

        // Starting city and destination city objects are found from their names which the user has inputted.
        City startingCity = findCityFromName(cities, startingCityInput);
        City destinationCity = findCityFromName(cities, destinationCityInput);

        // All the cities and their connections are examined and the shortest path between the starting city and the destination city is detected.
        shortestPath(startingCity, destinationCity, cities);

        // In order to display the shortest path to user, we first add the cities to an arraylist.
        ArrayList<City> shortestPath = new ArrayList<>();
        City lastCity = destinationCity;
        // Firstly, destination city is appended to the list.
        shortestPath.add(destinationCity);
        // Then, the previous cities which are on the shortest path from the starting city to the destination city are appended to the list until the starting city, whose previousCity data field is null, is reached.
        if (lastCity.getPreviousCity() != null) {
            while (!lastCity.getCityName().equals(startingCity.getCityName())) {
                lastCity = lastCity.getPreviousCity();
                shortestPath.add(lastCity);
            }
        }

        // The minimum distance between the destination city and the starting city is obtained.
        double minDistance = destinationCity.getShortestDistanceToStart();

        // If the destination city cannot be reached from the starting city
        if (minDistance > Integer.MAX_VALUE) {
            System.out.print("No path could be found.");
            System.exit(0);
        }
        // If the destination city can be reachable from the starting city, total distance and the shortest path is printed to console.
        else {
            System.out.printf("Total Distance: %.2f. Path: ", minDistance);
            for (int i = shortestPath.size() -1; i > 0; i--) {
                System.out.print(shortestPath.get(i).getCityName() + " -> ");
            }
            System.out.print(shortestPath.get(0).getCityName());
        }

        // The map is displayed to screen.
        displayMap(cities, shortestPath);

    }

    /**
     * This method processes the given file and creates a city object for every city, appends each of them in an arraylist and eventually return that arraylist.
     * @param cityCoordinates is a file which contains the city name, X and Y coordinate of each city.
     * @return an ArrayList named cities, which contains all the city objects.
     */
    public static ArrayList<City> processCityCoordinates(File cityCoordinates) throws FileNotFoundException {
        ArrayList<City> cities = new ArrayList<>();
        Scanner inputFile = new Scanner(cityCoordinates); // A scanner object is created in order to process the file.
        while (inputFile.hasNextLine()) {
            String line = inputFile.nextLine();
            String[] lineSplit = line.split(", ");
            String cityName = lineSplit[0];
            int x = Integer.parseInt(lineSplit[1]);
            int y = Integer.parseInt(lineSplit[2]);
            cities.add(new City(cityName, x, y)); // A new city object is created in each iteration.
        }
        return cities;
    }

    /**
     * For every pair in the city connections file, this method appends both cities to each other's connections ArrayList attribute.
     * @param cityConnections is a file which contains all the pairs of cities which are connected with roads.
     * @param cities is the ArrayList which contains all the cities as its objects.
     */
    public static void processCityConnections(File cityConnections, ArrayList<City> cities) throws FileNotFoundException {
        Scanner inputFile = new Scanner(cityConnections); // A scanner object is created in order to process the file.
        while (inputFile.hasNextLine()) {
            String line = inputFile.nextLine();
            String[] lineSplit = line.split(","); // In the provided file, cities are split with comma, therefore I split these cities and put them in a string array.
            String city1 = lineSplit[0];
            String city2 = lineSplit[1];
            int city1Index = -1;
            int city2Index = -1;
            // We iterate through the arraylist where all the city objects are present,
            // and when we encounter the two cities which we are reviewing at each iteration of the loop, we record their index.
            for (City city: cities) {
                if (city.getCityName().equals(city1))
                    city1Index = cities.indexOf(city);
                else if (city.getCityName().equals(city2))
                    city2Index = cities.indexOf(city);
            }
            // We use the indexes which we have recorded before and add each city in each other's connections ArrayList.
            cities.get(city1Index).addToConnections(cities.get(city2Index));
            cities.get(city2Index).addToConnections(cities.get(city1Index));
        }
    }

    /**
     * This is the shortest path finding algorithm, where you give the starting and destination cities as inputs,
     * then all the connections between the cities are examined and the shortest path is detected.
     * This method does not return anything, but what it does is changing the previousCity data fields in the city objects,
     * which the program then uses to collect them into a list and eventually display that path to screen.
     * @param startingCity is the city object where the user wants the path to start from.
     * @param destinationCity is the city object where the user want the path to end.
     * @param cities is the arraylist where all the city objects are present.
     */
    public static void shortestPath(City startingCity, City destinationCity, ArrayList<City> cities) {
        // Shortest distance from the starting city to starting city is set to 0 for the algorithm's sake
        startingCity.setShortestDistanceToStart(0);
        // ArrayLists called visitedCities and unvisitedCities are created
        ArrayList<City> visitedCities = new ArrayList<>();
        ArrayList<City> unvisitedCities = new ArrayList<>();
        // Each city is added to the unvisitedCities arraylist at the starting of the algorithm.
        for (City city: cities) {
            unvisitedCities.add(city);
        }

        City initialCity = startingCity;
        // A boolean condition is declared in order to control whether the loop should continue or not.
        boolean isLoopContinue = true;

        // The pathfinding algorithm keeps iterating until every city is visited.
        while (!unvisitedCities.isEmpty() && isLoopContinue) {
            isLoopContinue = false;
            double shortestDistance = Double.MAX_VALUE;
            // The closest city is found via looping through the unvisited cities and checking if they are closer to the starting city than the closest city that is found lastly.
            for (City city: unvisitedCities) {
                if (city.getShortestDistanceToStart() < shortestDistance) {
                    initialCity = city;
                    shortestDistance = city.getShortestDistanceToStart();
                    isLoopContinue = true;
                }
            }
            // The algorithm loops through the closest unvisited cities' neighbors, and if one neighbor is not visited before,
            // it is checked whether the distance would be less if one tries to go to that neighbor city through the city we are examining (the one in the initialCity variable),
            // compared to the present length of the shortest path from the starting city to that neighbor city;
            // and if the distance would be less via going through this city, the neighbor city's shortest distance to the starting city and the previous city data field are updated.
            for (City neighborCity: initialCity.getConnections()) {
                if (unvisitedCities.contains(neighborCity)) {
                    double distance = calculateDistance(initialCity, neighborCity);
                    if (initialCity.getShortestDistanceToStart() + distance < neighborCity.getShortestDistanceToStart()) {
                        neighborCity.setShortestDistanceToStart(initialCity.getShortestDistanceToStart() + distance);
                        neighborCity.setPreviousCity(initialCity);
                    }
                }
            }
            // After a city and its neighbors are examined, that city is removed from the unvisitedCities list and added to the visitedCities list, hence is no longer examined in the following iterations.
            unvisitedCities.remove(initialCity);
            visitedCities.add(initialCity);
        }
    }

    /**
     * This method calculates the distance between the two cities.
     * @param initialCity is the first city from which we want to calculate the distance.
     * @param neighborCity is the second city from which we want to calculate the distance.
     * @return the shortest distance between the two cities which is a double value.
     */
    public static double calculateDistance(City initialCity, City neighborCity) {
        return Math.pow(Math.pow(neighborCity.getX() - initialCity.getX(), 2) + Math.pow(neighborCity.getY() -initialCity.getY(), 2) , 0.5);
    }

    /**
     * This method checks if there is a city in the city list named as the cityName parameter.
     * @param cityList is the array list where all the city objects are present.
     * @param cityName is the name of the city that the method checks if it is present in the list.
     * @return true if there is a city object with the given name, false if there is not.
     */
    public static boolean isCityPresent(ArrayList<City> cityList, String cityName) {
        for (City city: cityList) {
            if (city.getCityName().equals(cityName))
                return true;
        }
        return false;
    }

    /**
     * This method returns the city object whose name is given as a parameter.
     * @param cityList is the arraylist where all the city objects are present.
     * @param cityName is the city name we want to access the object which has the same name.
     * @return the city object with the given name.
     */
    public static City findCityFromName(ArrayList<City> cityList,String cityName) {
        for (City city: cityList) {
            if (city.getCityName().equals(cityName))
                return city;
        }
        return cityList.get(0);
    }

    /**
     * This method displays the cities and the shortest path on a StdDraw canvas.
     * All the cities that are present in the shortest path have cyan colors, and all the other cities have gray colors.
     * @param cities is the arraylist where all the city objects are present.
     * @param shortestPath is the arraylist where all the cities from starting city to the destination city is present.
     */
    public static void displayMap(ArrayList<City> cities, ArrayList<City> shortestPath) {
        // Canvas is adjusted.
        StdDraw.setCanvasSize(2377/2,1055/2);
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0,2377);
        StdDraw.setYscale(0,1055);
        // Map picture is displayed.
        StdDraw.picture(2377/2.0,1055/2.0,"MapData/map.png", 2377, 1055);

        // All the cities are drawn in gray color.
        StdDraw.setPenColor(StdDraw.GRAY);
        StdDraw.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        for (int i = 0; i < cities.size(); i++) {
            StdDraw.filledCircle(cities.get(i).getX(), cities.get(i).getY(), 5.0);
            StdDraw.text(cities.get(i).getX(), cities.get(i).getY() + 13, cities.get(i).getCityName());
            for (int j = 0; j < cities.get(i).getConnections().size(); j++) {
                StdDraw.line(cities.get(i).getX(), cities.get(i).getY(), cities.get(i).getConnections().get(j).getX(), cities.get(i).getConnections().get(j).getY());
            }
        }

        // All the cities in the shortest path are drawn in cyan color.
        StdDraw.setPenColor(StdDraw.BOOK_LIGHT_BLUE);
        StdDraw.setPenRadius(0.01);
        for (int i = 0; i < shortestPath.size() -1; i++) {
            StdDraw.filledCircle(shortestPath.get(i).getX(), shortestPath.get(i).getY(), 5.0);
            StdDraw.line(shortestPath.get(i).getX(), shortestPath.get(i).getY(), shortestPath.get(i+1).getX(), shortestPath.get(i+1).getY());
            StdDraw.text(shortestPath.get(i).getX(), shortestPath.get(i).getY() + 13, shortestPath.get(i).getCityName());
        }
        StdDraw.text(shortestPath.get(shortestPath.size()-1).getX(), shortestPath.get(shortestPath.size()-1).getY() + 13, shortestPath.get(shortestPath.size()-1).getCityName());
        StdDraw.filledCircle(shortestPath.get(shortestPath.size()-1).getX(), shortestPath.get(shortestPath.size()-1).getY(), 5.0);

        // Map is displayed to screen.
        StdDraw.show();
    }
}

