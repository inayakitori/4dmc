package com.gmail.inayakitorikhurram.fdmc;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.minecraft.util.Colors;

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
    public Screenshot screenshot = new Screenshot();

    public static class Screenshot{
        public boolean png_enabled = true;
        public boolean gif_enabled = true;
        public int gif_wait_time = 500;
    }

    @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
    public SliceRotation slice_rotation = new SliceRotation();

    public enum FixedDirection {
        FORWARD, RIGHT
    }

    public enum AxisWColors {
        ANA(FDMCClientConstants.ANA_COLOR),
        KATA(FDMCClientConstants.KATA_COLOR),
        WHITE(Colors.WHITE),
        BLACK(Colors.BLACK),
        GRAY(Colors.LIGHT_GRAY),
        YELLOW(Colors.YELLOW),
        PURPLE(Colors.PURPLE),
        CYAN(Colors.CYAN),
        PINK(Colors.LIGHT_PINK);

        public final int color;
        AxisWColors(int color) { this.color = color; }
    }

    public static class SliceRotation {
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        public FixedDirection fixed_direction = FixedDirection.FORWARD;

        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.DROPDOWN)
        public AxisWColors axis_w_color = AxisWColors.ANA;
    }
}