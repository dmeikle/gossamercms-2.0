package gossamercms.globals.events.users;


import java.time.Instant;
import java.util.UUID;


public record UserLoggedInEvent(
        UUID userId,
        Instant loginTime
) {}