package com.aczchef.chgp;

import com.laytonsmith.PureUtilities.SimpleVersion;
import com.laytonsmith.PureUtilities.Version;
import com.laytonsmith.core.Static;
import com.laytonsmith.core.constructs.Target;
import com.laytonsmith.core.exceptions.CRE.CREInvalidPluginException;
import com.laytonsmith.core.extensions.AbstractExtension;
import com.laytonsmith.core.extensions.MSExtension;

import java.util.logging.Level;

/**
 *
 * @author cgallarno
 */
@MSExtension("CHGriefPrevention")
public class LifeCycle extends AbstractExtension {

	public Version getVersion() {
		return new SimpleVersion(2, 0, 0);
	}

	@Override
	public void onShutdown() {
		Static.getLogger().info("CHGriefPrevention " + getVersion() + " unloaded.");
	}

	@Override
	public void onStartup() {
		try {
			Static.checkPlugin("GriefPrevention", Target.UNKNOWN);
			Static.getLogger().info("CHGriefPrevention " + getVersion() + " loaded.");
		} catch (CREInvalidPluginException e) {
			Static.getLogger().warning("CHGriefPrevention could not find GriefPrevention."
					+ " Please make sure you have it installed.");
		}
	}
}
