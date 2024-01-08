package com.gmail.inayakitorikhurram.fdmc;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name="fdmc")
public
class FDMCConfig implements ConfigData {

    @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
    public SliceGui slice_gui = new SliceGui();

    public static class SliceGui{
        public boolean render_gui = true;
        @ConfigEntry.BoundedDiscrete(min = 1, max = 6)
        public int gui_scale = 2;
    }

    @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
    public UnderSupport under_support = new UnderSupport();

    public static class UnderSupport{
        public boolean create_support = true;
    }

}