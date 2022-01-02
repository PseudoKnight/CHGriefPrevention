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
		return new SimpleVersion(1, 2, 2);
	}

	@Override
	public void onShutdown() {
		Static.getLogger().log(Level.INFO,"CHGriefPrevention " + getVersion() + " unloaded.");
	}

	@Override
	public void onStartup() {
		try {
			Static.checkPlugin("GriefPrevention", Target.UNKNOWN);
		} catch (CREInvalidPluginException e) {
			Static.getLogger().log(Level.INFO,"CHGriefPrevention Could not find GriefPrevention."
					+ " Please make sure you have it installed.");
		}
		Static.getLogger().log(Level.INFO,"CHGriefPrevention " + getVersion() + " loaded.");
	}
}
