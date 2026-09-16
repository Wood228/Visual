package pulsetest;

import java.util.concurrent.ThreadLocalRandom;

public final class LegitSimulationEngine {
    public enum Profile { VANILLA, CASUAL, FAST_PLAYER, BORDERLINE, STRESS }
    private Profile profile = Profile.VANILLA;
    public void setProfile(Profile profile) { this.profile = profile; }
    public Profile getProfile() { return profile; }
    public int reactionDelay() {
        return switch (profile) {
            case VANILLA -> 150; case CASUAL -> 120; case FAST_PLAYER -> 90; case BORDERLINE -> 55; case STRESS -> 20;
        } + ThreadLocalRandom.current().nextInt(-10, 11);
    }
    public int actionDelay() {
        return switch (profile) {
            case VANILLA -> 100; case CASUAL -> 80; case FAST_PLAYER -> 55; case BORDERLINE -> 30; case STRESS -> 5;
        } + ThreadLocalRandom.current().nextInt(-5, 6);
    }
}
