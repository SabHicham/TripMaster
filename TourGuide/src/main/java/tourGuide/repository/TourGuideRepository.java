package tourGuide.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import tourGuide.service.TourGuideService;
import tourGuide.user.User;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Repository
public class TourGuideRepository {
    private Logger logger = LoggerFactory.getLogger(TourGuideService.class);

    private final Map<String, User> internalUserMap = new HashMap<>();

    public User getUser(String userName) {
        return internalUserMap.get(userName);
    }
    public List<User> getAllUsers() {
        return internalUserMap.values().stream().collect(Collectors.toList());
    }
    public void addUser(User user) {
        if(!internalUserMap.containsKey(user.getUserName())) {
            internalUserMap.put(user.getUserName(), user);
        }
    }
}
