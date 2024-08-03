package app.xlog.ggbond.strategy.service.filter.factory;

import app.xlog.ggbond.strategy.service.filter.BlacklistRaffleFilter;
import app.xlog.ggbond.strategy.service.filter.InventoryFilter;
import app.xlog.ggbond.strategy.service.filter.RaffleTimesRaffleFilter;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

public class FilterFactory {

    public static BlacklistRaffleFilter createBlacklistRaffleFilter() {
        return new BlacklistRaffleFilter();
    }

    public static RaffleTimesRaffleFilter createRaffleTimesRaffleFilter() {
        return new RaffleTimesRaffleFilter();
    }

    public static InventoryFilter createInventoryFilter() {
        return new InventoryFilter();
    }
}
