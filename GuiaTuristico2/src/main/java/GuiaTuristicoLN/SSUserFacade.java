package GuiaTuristicoLN;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SSUserFacade implements IGestUser {
    private Map<String, User> users;    // chave: userId, objeto: user

    ConnectionDB db = new ConnectionDB();

    public void saveData() throws SQLException, ParseException {
        Map<String,Review> allRevs = new HashMap<>();
        Map<String,Plan> allPlans = new HashMap<>();
        Map.Entry<String,Review> rev;
        Map.Entry<String,Plan> pla;
        Map<String,Review> auxR = null;
        Map<String,Plan> auxP = null;
        for(User u : this.users.values()){
            auxR =u.getReviews();
            if( auxR != null) {
                if (u.getReviews().entrySet().size() != 0) {
                    rev = u.getReviews().entrySet().iterator().next();
                    allRevs.put(u.getId(), rev.getValue().clone());
                } else allRevs.put(u.getId(), null);
            }
            auxP = u.getPlans();
            if(auxP != null) {
                if (u.getPlans().entrySet().size() != 0) {
                    pla = u.getPlans().entrySet().iterator().next();
                    allPlans.put(u.getId(), pla.getValue().clone());
                } else allPlans.put(u.getId(), null);
            }
        }
        db.saveUsers(users);
        db.saveReviews(allRevs);
        db.savePlans(allPlans);
    }

    public void saveUser(User u) throws SQLException {
        db.saveOneUser(u);
    }

    public void savePlan(Plan p) throws SQLException{
        db.saveOnePlan(p);
    }

    public void savePlace(Place p) throws SQLException{
        db.saveOnePlace(p);
    }

    public void saveReview(Review r) throws SQLException{
        db.saveOneReview(r);
    }

    public boolean login(String id, String password) {
        boolean res = false;
        if (this.users.containsKey(id)) {
            User user = this.users.get(id);
            if (password.equals(user.getPassword())) {
                res = true;
            }
        }
        return res;
    }

    public boolean register(String password, String name, String email) {
            int id = this.users.values().size()+1;
            User user = new User(name, String.valueOf(id), password, email);
            this.users.put(String.valueOf(id), user.clone());
            return true;
    }

    public SSUserFacade() throws SQLException, ClassNotFoundException {
        this.users = db.loadUsers();
        for(User user: this.users.values()){
            Map<String,Plan> plansOfUser = new HashMap<>();      // chave: userId
            Map<String,Review> reviewsOfUser = new HashMap<>();  // chave: userId

            Plan plan = db.loadPlans().get(user.getId());
            Review rev = db.loadReviews().get(user.getId());

            if (plan != null) {
                plansOfUser.put(plan.getName(),plan.clone());
            }

            if ( rev != null) {
                reviewsOfUser.put(rev.getPlaceId(),rev.clone());
            }

            user.setPlans(plansOfUser);
            user.setReviews(reviewsOfUser);
        }
    }

    public SSUserFacade(Map<String, User> users) throws SQLException, ClassNotFoundException {
        this.users = new HashMap<>(users);
    }

    public SSUserFacade(SSUserFacade facade) throws SQLException, ClassNotFoundException {
        this.users = facade.getUsers();
    }

    public Map<String, User> getUsers() {
        return this.users;
    }

    public boolean create_review(String userId, String placeId, float classification, String comment) {
        if (this.users.containsKey(userId)) {
            Review rev = new Review(userId, placeId, classification, comment);
            this.users.get(userId).getReviews().put(placeId, rev);
            return true;
        } else return false;
    }

    @Override
    public List<Review> get_reviews_by_user(String userId) {
        if (this.users.containsKey(userId)) {
            return this.users.get(userId).getReviews().values().stream().map(Review::clone).collect(Collectors.toList());
        }
        return null;
    }


    @Override
    public boolean create_plan(String userId, String name, LocalDateTime start_time, LocalDateTime finish_time, String day, String city) {
        if (this.users.containsKey(userId)) {
            Plan plan = new Plan(userId, name, start_time, finish_time, day, city);
            Map<String, Plan>  plans = this.users.get(userId).getPlans();
            for(Plan p : plans.values()){
                remove_plan(userId,p.getName());
            }
            this.users.get(userId).getPlans().put(name, plan);
            return true;
        }
        return false;
    }

    @Override
    public boolean update_plan(String userId, Plan plan) {
        if (this.users.containsKey(userId) && this.users.get(userId).getPlans().containsKey(plan.getName())) {
            this.users.get(userId).getPlans().put(plan.getName(), plan);
            return true;
        } else return false;
    }

    @Override
    public boolean remove_plan(String userId, String planName) {
        if (this.users.containsKey(userId)) {
            this.users.get(userId).remove_plan(planName);
            return true;
        } else return false;
    }

    @Override
    public User get_user(String userId) {
        if(this.users.containsKey(userId)){
            return this.users.get(userId);
        }
        return null;
    }

    @Override
    public boolean update_user(User user) {
        if (this.users.containsKey(user.getId())) {
            this.users.put(user.getId(), user);
            return true;
        } else return false;
    }

    @Override
    public boolean delete_review(String userId, String placeId) {
        if (this.users.containsKey(userId)) {
            this.users.get(userId).getReviews().remove(placeId);
            return true;
        } else return false;
    }

    @Override
    public Review getReviewUserPlace(String userId, String placeId){
        if (this.users.containsKey(userId)){
            return this.users.get(userId).getReviews().get(placeId).clone();
        }
        else return null;
    }

    @Override
    public List<Plan> get_plans_by_user(String userId) {
        if (this.users.containsKey(userId)) {
            return this.users.get(userId).getPlans().values().stream().map(Plan::clone).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public Place get_place(String placeID) {
        for(User u : this.users.values()){
            for(Plan pla : u.getPlans().values()){
                for(Place pl : pla.getPlaces())
                    if(pl.getName().equals(placeID)) return pl.clone();
            }
        }
        return null;
    }

    public void updateOnePlan(Plan p) throws SQLException {
        db.updateOnePlan(p);
    }

    public void updateOneReview(Review r) throws SQLException{
        db.updateOneReview(r);
    }

}
