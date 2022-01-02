package com.aczchef.chgp.functions;

import com.aczchef.chgp.util.Util;
import com.laytonsmith.abstraction.MCLocation;
import com.laytonsmith.abstraction.MCPlayer;
import com.laytonsmith.abstraction.bukkit.BukkitMCLocation;
import com.laytonsmith.abstraction.bukkit.entities.BukkitMCPlayer;
import com.laytonsmith.annotations.api;
import com.laytonsmith.core.MSVersion;
import com.laytonsmith.core.ObjectGenerator;
import com.laytonsmith.core.constructs.CArray;
import com.laytonsmith.core.constructs.CBoolean;
import com.laytonsmith.core.constructs.CInt;
import com.laytonsmith.core.constructs.CNull;
import com.laytonsmith.core.constructs.CString;
import com.laytonsmith.core.constructs.Target;
import com.laytonsmith.core.environments.CommandHelperEnvironment;
import com.laytonsmith.core.environments.Environment;
import com.laytonsmith.core.exceptions.CRE.CRECastException;
import com.laytonsmith.core.exceptions.CRE.CREFormatException;
import com.laytonsmith.core.exceptions.CRE.CREInvalidPluginException;
import com.laytonsmith.core.exceptions.CRE.CREThrowable;
import com.laytonsmith.core.exceptions.ConfigRuntimeException;
import com.laytonsmith.core.functions.AbstractFunction;
import java.util.ArrayList;

import com.laytonsmith.core.natives.interfaces.Mixed;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class Functions {

	@api
	public static class get_claim_id extends AbstractFunction {
	
		public Class<? extends CREThrowable>[] thrown() {
			return new Class[] { CREInvalidPluginException.class, CRECastException.class };
		}

		public boolean isRestricted() {
			return true;
		}

		public Boolean runAsync() {
			return false;
		}

		public Mixed exec(Target tar, Environment env, Mixed... args) throws ConfigRuntimeException {
			MCLocation l;
			Claim c;

			if (args[0] instanceof CArray) {
				l = ObjectGenerator.GetGenerator().location(args[0], null, tar);
				c = GriefPrevention.instance.dataStore.getClaimAt(Util.Location(l), true, null);
			} else {
				throw new CRECastException("Expected argument 1 of get_claim_id to be an array", tar);
			}

			if (c == null) {
				return CNull.NULL;
			} else if (c.getID() == null) {
				return new CInt(c.parent.getID().intValue(), tar);
			} else {
				return new CInt(c.getID().intValue(), tar);
			}
		}

		public String getName() {
			return "get_claim_id";
		}

		public Integer[] numArgs() {
			return new Integer[]{1};
		}

		public String docs() {
			return "int {location} Gets the id of a claim at given location.";
		}

		public MSVersion since() {
			return MSVersion.V3_3_1;
		}
	}

	@api
	public static class get_claim_info extends AbstractFunction {

		public Class<? extends CREThrowable>[] thrown() {
			return new Class[]{ CREInvalidPluginException.class, CRECastException.class };
		}

		public boolean isRestricted() {
			return true;
		}

		public Boolean runAsync() {
			return false;
		}

		public Mixed exec(Target tar, Environment environment, Mixed... args) throws ConfigRuntimeException {
			MCLocation l;
			Claim c;

			if (args[0] instanceof CArray) {
				l = ObjectGenerator.GetGenerator().location(args[0], null, tar);
				c = GriefPrevention.instance.dataStore.getClaimAt(Util.Location(l), true, null);
			} else {
				throw new CRECastException("Expected argument 1 of get_claim_info to be a location array.", tar);
			}

			if (c == null) {
				return CNull.NULL;
			}

			CArray data = new CArray(tar);
			CArray corners = new CArray(tar);

			MCLocation corner1 = Util.Location(c.getLesserBoundaryCorner());
			MCLocation corner2 = Util.Location(c.getGreaterBoundaryCorner());

			CArray Ccorner1 = ObjectGenerator.GetGenerator().location(corner1);
			CArray Ccorner2 = ObjectGenerator.GetGenerator().location(corner2);

			for (int i = 0; i <= 5; i++) {
				Ccorner1.remove(new CInt(i, tar));
			}

			for (int i = 0; i <= 5; i++) {
				Ccorner2.remove(new CInt(i, tar));
			}

			Ccorner1.remove(new CString("pitch", tar));
			Ccorner1.remove(new CString("yaw", tar));
			Ccorner2.remove(new CString("pitch", tar));
			Ccorner2.remove(new CString("yaw", tar));

			corners.push(Ccorner1, tar);
			corners.push(Ccorner2, tar);

			data.set("corners", corners, tar);
			data.set("owner", new CString(c.getOwnerName(), tar), tar);
			data.set("isadmin", CBoolean.get(c.isAdminClaim()), tar);
			data.set("Height", new CInt(c.getHeight(), tar), tar);

			if (c.getID() == null) {
				ArrayList<String> builders = new ArrayList<String>(), containers = new ArrayList<String>(), accesors = new ArrayList<String>(), managers = new ArrayList<String>();
				data.set("parentId", new CInt(c.parent.getID().intValue(), tar), tar);
				c.getPermissions(builders, containers, accesors, managers);
				CArray perms = Util.formPermissions(builders, containers, accesors, managers, tar);
				data.set("permissions", perms, tar);
			} else {
				data.set("id", new CInt(c.getID().intValue(), tar), tar);
				CArray children = new CArray(tar);
				for (int i = 0; i < c.children.size(); i++) {
					CArray childData = new CArray(tar);

					childData.set("Owner", new CString(c.children.get(i).getOwnerName(), tar), tar);
					children.push(childData, tar);
				}
				data.set("subclaims", children, tar);

				ArrayList<String> builders = new ArrayList<String>(), containers = new ArrayList<String>(), accesors = new ArrayList<String>(), managers = new ArrayList<String>();

				c.getPermissions(builders, containers, accesors, managers);
				CArray perms = Util.formPermissions(builders, containers, accesors, managers, tar);
				data.set("permissions", perms, tar);
			}

			return data;

		}

		public String getName() {
			return "get_claim_info";
		}

		public Integer[] numArgs() {
			return new Integer[]{1};
		}

		public String docs() {
			return "array {location} Returns various data about a claim.";
		}

		public MSVersion since() {
			return MSVersion.V3_3_1;
		}
	}

	@api(environments = {CommandHelperEnvironment.class})
	public static class has_gp_buildperm extends AbstractFunction {

		public Class<? extends CREThrowable>[] thrown() {
			return null;
		}

		public boolean isRestricted() {
			return true;
		}

		public Boolean runAsync() {
			return false;
		}

		public Mixed exec(Target t, Environment environment, Mixed... args) throws ConfigRuntimeException {
			Player player;
			CArray array;

			if (args.length == 0) {
				throw new CREFormatException("Invalid arguments. Use [player,] location", t);
			} else if (args.length == 1) {
				if (args[0] instanceof CArray) {
					array = (CArray) args[0];
					MCPlayer p = environment.getEnv(CommandHelperEnvironment.class).GetPlayer();
					player = ((BukkitMCPlayer) p)._Player();
				} else {
					throw new CREFormatException("Invalid arguments. Use [player,] location", t);
				}
			} else if (args.length == 2 && (args[0] instanceof CString) && (args[1] instanceof CArray)) {
				player = Bukkit.getPlayer(args[0].val());
				array = (CArray) args[1];
			} else {
				throw new CREFormatException("Invalid arguments. Use [player,] location", t);
			}

			MCLocation loc = ObjectGenerator.GetGenerator().location(array, null, t);
			Location location = ((BukkitMCLocation) loc).asLocation();

			Claim claim = GriefPrevention.instance.dataStore.getClaimAt(location, false, null);
			if (claim == null) {
				return CBoolean.TRUE;
			}
			String errorMessage = claim.allowBuild(player, Material.AIR);

			if (errorMessage == null) {
				return CBoolean.TRUE;
			} else {
				return CBoolean.FALSE;
			}
		}

		public String getName() {
			return getClass().getSimpleName();
		}

		public Integer[] numArgs() {
			return new Integer[]{1, 2};
		}

		public String docs() {
			return "boolean {[player,] location} See if a player can build at a given location.";
		}

		public MSVersion since() {
			return MSVersion.V3_3_1;
		}
	}
}
