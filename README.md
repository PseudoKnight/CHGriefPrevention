A CommandHelper extension to access the GriefPrevention API on a Minecraft server.

## Downloads

[CHGriefPrevention 2.0.0](https://letsbuild.net/jenkins/job/CHGriefPrevention/lastSuccessfulBuild/) is compatible with GP 16.18.x and CH 3.3.5  
[CHGriefPrevention 1.2.2](https://letsbuild.net/jenkins/job/CHGriefPrevention/25/) is compatible with GP 16.17.x and CH 3.3.4 - 3.3.5

## Functions
### boolean has\_gp\_buildperm([player,] location):
See if a player can build at a given location.
### int get\_claim\_id(location):
Gets the id of a claim at given location.
### array get\_claim\_info(location):
Returns various data about a claim.

The following keys are present in the array:  
`corners`: (array) An array of two location arrays for each corner of the claim.  
`owner`: (string) The claim owner's name.  
`isadmin`: (boolean) Whether or not this is an administrative claim.  
`permissions`: (array) An associative array of arrays of permissions for 'builders', 'containers', 'accessors', and 'managers'.  
`id`: (int) The id of the claim (doesn't exist for subclaims).  
`parentId`: (int) The id of the parent claim (exists for subclaims only).  
`subclaims`: (array) An array of subclaim arrays, which contain the key 'owner'.