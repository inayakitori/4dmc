package com.gmail.inayakitorikhurram.fdmc;

import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

public class FDMCMainPreLaunchEntrypoint implements PreLaunchEntrypoint {

	@Override
	public void onPreLaunch() {
		MixinExtrasBootstrap.init();
	}
}
