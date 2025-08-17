package net.fabricmc.example;

public interface GradualViewDistance {
    int getGradualTargetDistance();
    boolean isGradualActive();
    void clearGradual();
    void setGradualTargetDistance(int target);
    void setGradualActive(boolean active);
}
