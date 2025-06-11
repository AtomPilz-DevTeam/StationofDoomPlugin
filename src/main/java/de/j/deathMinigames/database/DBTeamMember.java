package de.j.deathMinigames.database;

import java.util.UUID;

public class DBTeamMember {
    public boolean isInTeam;
    public UUID uuid;
    public boolean isTeamOperator;

    public DBTeamMember(boolean isInTeam, String uuid, boolean isTeamOperator) {
        this.isInTeam = isInTeam;
        this.uuid = UUID.fromString(uuid);
        this.isTeamOperator = isTeamOperator;
    }
}
