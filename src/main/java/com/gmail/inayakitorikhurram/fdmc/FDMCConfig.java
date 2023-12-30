package com.gmail.inayakitorikhurram.fdmc;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name="fdmc")
class FDMCConfig implements ConfigData {
    public boolean slice_gui = true;

}