package com.aczchef.chgp.functions;

import com.aczchef.chgp.util.Util;
import com.laytonsmith.abstraction.MCLocation;
import com.laytonsmith.abstraction.MCPlayer;
import com.laytonsmith.abstraction.bukkit.BukkitMCLocation;
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
import com.laytonsmith.core.exceptions.CRE.*;
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
				return new CInt(c.parent.getID(), tar);
			} else {
				return new CInt(c.getID(), tar);
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
			MCLocation l = ObjectGenerator.GetGenerator().location(args[0], null, tar);
			Claim c = GriefPrevention.instance.dataStore.getClaimAt(Util.Location(l), true, null);

			if (c == null) {
				return CNull.NULL;
			}

			CArray data = CArray.GetAssociativeArray(tar);
			CArray corners = new CArray(tar);

			MCLocation corner1 = Util.Location(c.getLesserBoundaryCorner());
			MCLocation corner2 = Util.Location(c.getGreaterBoundaryCorner());

			corners.push(ObjectGenerator.GetGenerator().location(corner1, false), tar);
			corners.push(ObjectGenerator.GetGenerator().location(corner2, false), tar);

			data.set("corners", corners, tar);
			data.set("owner", new CString(c.getOwnerName(), tar), tar);
			data.set("isadmin", CBoolean.get(c.isAdminClaim()), tar);

			if (c.getID() == null) {
				data.set("parentId", new CInt(c.parent.getID(), tar), tar);
			} else {
				data.set("id", new CInt(c.getID(), tar), tar);
				CArray children = new CArray(tar);
				for (int i = 0; i < c.children.size(); i++) {
					CArray childData = new CArray(tar);
					childData.set("owner", new CString(c.children.get(i).getOwnerName(), tar), tar);
					children.push(childData, tar);
				}
				data.set("subclaims", children, tar);
			}

			ArrayList<String> builders = new ArrayList<>();
			ArrayList<String> containers = new ArrayList<>();
			ArrayList<String> accessors = new ArrayList<>();
			ArrayList<String> managers = new ArrayList<>();
			c.getPermissions(builders, containers, accessors, managers);
			CArray perms = Util.formPermissions(builders, containers, accessors, managers, tar);
			data.set("permissions", perms, tar);

			return data;

		}

		public String getName() {
			return "get_claim_info";
		}

		public Integer[] numArgs() {
			return new Integer[]{1};
		}

		public String docs() {
			return "array {location} Returns an array of data about a claim."
					+ " The following keys are present in the array:"
					+ " 'corners': (array) An array of two location arrays for each corner of the claim."
					+ " 'owner': (string) The claim owner's name."
					+ " 'isadmin': (boolean) Whether or not this is an administrative claim."
					+ " 'permissions': (array) An associative array of arrays of permissions for 'builders',"
					+ " 'containers', 'accessors', and 'managers'."
					+ " 'id': (int) The id of the claim (doesn't exist for subclaims)."
					+ " 'parentId': (int) The id of the parent claim (exists for subclaims only)."
					+ " 'subclaims': (array) An array of subclaim arrays, which contain the key 'owner'.";
		}

		public MSVersion since() {
			return MSVersion.V3_3_1;
		}
	}

	@api(environments = {CommandHelperEnvironment.class})
	public static class has_gp_buildperm extends AbstractFunction {

		public Class<? extends CREThrowable>[] thrown() {
			return new Class[]{ CREPlayerOfflineException.class, CREFormatException.class };
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
					if(p == null) {
						throw new CREPlayerOfflineException("No player provided in this context.", t);
					}
					player = ((Player) p.getHandle());
				} else {
					throw new CREFormatException("Invalid arguments. Use [player,] location", t);
				}
			} else if (args.length == 2 && (args[0] instanceof CString) && (args[1] instanceof CArray)) {
				player = Bukkit.getPlayer(args[0].val());
				array = (CArray) args[1];
				if(player == null) {
					throw new CREPlayerOfflineException("Player is not online: " + args[0].val(), t);
				}
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
			return "has_gp_buildperm";
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
