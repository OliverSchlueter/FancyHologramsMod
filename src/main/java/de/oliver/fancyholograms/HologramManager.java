package de.oliver.fancyholograms;

import net.minecraft.entity.Entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HologramManager {

    private static final Map<String, Entity> holograms = new HashMap<>();

    public static void addHologram(String name, Entity entity){
        holograms.put(name, entity);
    }

    public static void removeHologram(String name){
        holograms.remove(name);
    }

    public static Entity getHologram(String name){
        return holograms.getOrDefault(name, null);
    }

    public static Set<String> getAllNames(){
        return holograms.keySet();
    }
}
