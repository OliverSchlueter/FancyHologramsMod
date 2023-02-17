package de.oliver.fancyholograms.mixinInterfaces;

import net.minecraft.entity.data.TrackedData;
import net.minecraft.text.Text;

public interface ITextDisplayEntityMixin{
    boolean isHologram();
    void setIsHologram(boolean isHologram);

    String getHologramName();
    void setHologramName(String hologramName);


    TrackedData<Text> getTextData();
    TrackedData<Integer> getBackgroundData();

}
