package de.oliver.fancyholograms.mixin;

import de.oliver.fancyholograms.mixinInterfaces.ITextDisplayEntityMixin;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DisplayEntity.TextDisplayEntity.class)
public class TextDisplayEntityMixin extends DisplayEntityMixin implements ITextDisplayEntityMixin {
    @Shadow @Final private static TrackedData<Text> TEXT;
    @Shadow @Final private static TrackedData<Integer> BACKGROUND;
    private String hologramName = "";
    private boolean isHologram = false;

    @Inject(method = "writeCustomDataToNbt", at = @At("HEAD"))
    protected void injectWriteCustomDataToNbt(NbtCompound nbt, CallbackInfo info){
        nbt.putBoolean("fancyholograms.is_hologram", isHologram);
        if(isHologram){
            nbt.putString("fancyholograms.hologram_name", hologramName);
        }
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("HEAD"))
    protected void injectReadCustomDataFromNbt(NbtCompound nbt, CallbackInfo info){
        isHologram = nbt.getBoolean("fancyholograms.is_hologram");
        if(isHologram){
            hologramName = nbt.getString("fancyholograms.hologram_name");
        }
    }

    @Override
    public boolean isHologram() {
        return isHologram;
    }

    @Override
    public void setIsHologram(boolean isHologram) {
        this.isHologram = isHologram;
    }

    @Override
    public String getHologramName() {
        return hologramName;
    }

    @Override
    public void setHologramName(String hologramName) {
        this.hologramName = hologramName;
    }

    @Override
    public TrackedData<Text> getTextData() {
        return TEXT;
    }

    @Override
    public TrackedData<Integer> getBackgroundData() {
        return BACKGROUND;
    }
}
