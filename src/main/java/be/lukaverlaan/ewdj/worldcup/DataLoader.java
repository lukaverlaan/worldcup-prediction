package be.lukaverlaan.ewdj.worldcup;

import be.lukaverlaan.ewdj.worldcup.service.MatchSyncService;
import be.lukaverlaan.ewdj.worldcup.service.UserService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class DataLoader {

    private final UserService userService;
    private final MatchSyncService matchSyncService;

    public DataLoader(UserService userService, MatchSyncService matchSyncService) {
        this.userService = userService;
        this.matchSyncService = matchSyncService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void loadData() {
        userService.createAdminUser("admin", "Luk4D3v82*", "admin@worldcup.be");
        matchSyncService.importFixtures();
    }
}
