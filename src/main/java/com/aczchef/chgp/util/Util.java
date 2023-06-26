package com.aczchef.chgp.util;

import com.laytonsmith.abstraction.MCLocation;
import com.laytonsmith.abstraction.bukkit.BukkitMCLocation;
import com.laytonsmith.core.constructs.CArray;
import com.laytonsmith.core.constructs.CString;
import com.laytonsmith.core.constructs.Target;
import java.util.ArrayList;
import org.bukkit.Location;

public class Util {
	public static Location Location(MCLocation l){
		return ((BukkitMCLocation)l).asLocation();
	}
	
	public static MCLocation Location(Location l){
		BukkitMCLocation Bl = new BukkitMCLocation(l);
		return(Bl.clone());
	}

	public static CArray formPermissions(ArrayList<String> builders, ArrayList<String> containers, ArrayList<String> accessors, ArrayList<String> managers, Target tar) {
		CArray permissions = new CArray(tar);
		CArray Cbuilders = new CArray(tar);
		CArray Ccontainers = new CArray(tar);
		CArray Caccessors = new CArray(tar);
		CArray Cmanagers = new CArray(tar);
		for(String builder : builders) {
			Cbuilders.push(new CString(builder, tar), tar);
		}
		permissions.set("builders", Cbuilders, tar);
		for(String container : containers) {
			Ccontainers.push(new CString(container, tar), tar);
		}
		permissions.set("containers", Ccontainers, tar);
		for(String accessor : accessors) {
			Caccessors.push(new CString(accessor, tar), tar);
		}
		permissions.set("accessors", Caccessors, tar);
		for(String manager : managers) {
			Cmanagers.push(new CString(manager, tar), tar);
		}
		permissions.set("managers", Cmanagers, tar);
		return permissions;
	}
}
