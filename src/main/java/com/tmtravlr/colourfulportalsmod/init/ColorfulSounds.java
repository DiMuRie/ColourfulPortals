package com.tmtravlr.colourfulportalsmod.init;

import net.minecraft.init.Bootstrap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public class ColorfulSounds {
    public static final SoundEvent TELEPORT;
    
    private static SoundEvent getRegisteredSoundEvent(String id)
    {
        SoundEvent soundevent = (SoundEvent)SoundEvent.REGISTRY.getObject(new ResourceLocation(id));

        if (soundevent == null)
        {
            throw new IllegalStateException("Invalid Sound requested: " + id);
        }
        else
        {
            return soundevent;
        }
    }
    
    static
    {
        if (!Bootstrap.isRegistered())
        {
            throw new RuntimeException("Accessed Sounds before Bootstrap!");
        }
        else
        {
            TELEPORT = getRegisteredSoundEvent("teleport");
        }
    }
}
