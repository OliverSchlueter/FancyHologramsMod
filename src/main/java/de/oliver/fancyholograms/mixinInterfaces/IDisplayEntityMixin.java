package de.oliver.fancyholograms.mixinInterfaces;

public interface IDisplayEntityMixin {
    boolean isHologram();
    void setIsHologram(boolean isHologram);

    String getHologramName();
    void setHologramName(String hologramName);
}
