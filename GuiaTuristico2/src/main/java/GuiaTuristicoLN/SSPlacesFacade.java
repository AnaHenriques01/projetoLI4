package GuiaTuristicoLN;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.*;

@RestController
public class SSPlacesFacade implements IGestPlace {
    ConnectionDB db = new ConnectionDB();
    private Map<String, Place> places;  // chave: placeId, objeto: GuiaTuristicoLN.Place


    public void savePlaces() throws SQLException {
        db.savePlaces(places);
    }

    public SSPlacesFacade() throws SQLException, ClassNotFoundException {
        this.places  = db.loadPlaces();
        Map<String,Review> reviews = db.loadReviews();
        for(Place place: this.places.values()){

            Map<String,Review> reviewsOfPlace = new HashMap<>();  // chave: userId

            for(Review  rev : reviews.values()){
                if(rev.getPlaceId().equals(place.getId())){
                    reviewsOfPlace.put(rev.getUserId(),rev);
                }
            }

            place.setReviews(reviewsOfPlace);
        }
    }

    public SSPlacesFacade(Map<String, Place> places) throws SQLException, ClassNotFoundException {
        this.places = new HashMap<>(places);
    }

    public SSPlacesFacade(SSPlacesFacade facade) throws SQLException, ClassNotFoundException {
        this.places = facade.getPlaces();
    }

    public Map<String, Place> getPlaces() {
        return this.places;
    }

    public Set<Place> placesOfCity(String city) {
        Set<Place> placesOfCity = new TreeSet<>();
        for (Place pl : places.values()) {
            if (pl.getCity().equals(city))
                placesOfCity.add(pl);
        }
        return placesOfCity;
    }

    @Override
    public Set<Place> filter_by_name(String name) {
        Set<Place> placesByName = new TreeSet<>();
        for (Place pl : places.values()) {
            if (pl.getName().contains(name))
                placesByName.add(pl);
        }
        return placesByName;
    }

    @RequestMapping("/")
    String hey() {
        return "hey";
    }

    public Set<Place> filter_by_category(String category) {
        Set<Place> placesByCategory = new TreeSet<>();
        for (Place pl : places.values()) {
            if (pl.getCategory().equals(category))
                placesByCategory.add(pl);
        }
        return placesByCategory;
    }

    @Override
    public Set<Place> filter_by_distance(String locationUser, double distance_max) {
        Set<GuiaTuristicoLN.Place> placesByLocation = new TreeSet<>();

        double latitudeUser = RandomUtils.parseLatitude(locationUser);
        double longitudeUser = RandomUtils.parseLongitude(locationUser);

        for(Place pl: places.values()) {
            String locationPlace = pl.getLocation();
            double latitudePlace = RandomUtils.parseLatitude(locationPlace);
            double longitudePlace = RandomUtils.parseLongitude(locationPlace);
            double distance = RandomUtils.calculateDistante(latitudeUser,longitudeUser,latitudePlace,longitudePlace);
            if(distance < distance_max)
                placesByLocation.add(pl);
        }
        return placesByLocation;
    }

    @Override
    public Set<Place> filter_by_classification(float classification_min) {
        Set<Place> placesByClassification = new TreeSet<>();
        for (Place pl : places.values()) {
            if (pl.calculateClassification() >= classification_min)
                placesByClassification.add(pl);
        }
        return placesByClassification;
    }

    @Override
    public List<Place> filter_by_city(String  city) {
        List<Place> placesByCity = new ArrayList<>();
        for (Place pl : places.values()) {
            if (pl.getCity().equals(city))
            placesByCity.add(pl);
        }
        return placesByCity;
    }

    @Override
    public List<Review> get_reviews_by_place(String placeId) {
        return (List<Review>) places.get(placeId).getReviews().values();
    }

    public Place getOnePlace(String id){
        for(Place p : this.places.values()){
            if(p.getId().equals(id)) return p.clone();
        }
        return null;
    }
}
