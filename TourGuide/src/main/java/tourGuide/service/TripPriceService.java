package tourGuide.service;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Service;
import tripPricer.Provider;
import tripPricer.TripPricer;

import java.util.List;
import java.util.UUID;

@Service
public class TripPriceService {
    private final TripPricer tripPricer;

    public TripPriceService(TripPricer tripPricer) {

        this.tripPricer = tripPricer;
    }



    public List<Provider> getPrice(String tripPricerApiKey, UUID userId, int numberOfAdults, int numberOfChildren, int tripDuration, int cumulatativeRewardPoints) {
        throw new NotImplementedException("");
    }

}
