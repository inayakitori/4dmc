# Changelog v0.3.2 to v0.3.3

## Features
- All land and air entities can use 4D pathfinding
- Villagers (and other entities that use their brains) can sense and remember Points of Interest (beds, workstations, etc.) across multiple slices
- Mobs will do the following goals across slices:
  - Target other mobs (e.g zombies attacking players)
  - Wander
  - Mate (e.g livestock)
  - Be tempted (e.g by a player holding wheat)
  - Follow parents
  - Pollinate flowers (bees)
  - Pick up items/food (Pandas/foxes)
  - Escape Sunlight
  - Share Anger (Zombie Piglines)
  - Place blocks (Enderman)
  - Look for bamboo (Pandas)
- Mobs, when trying to check line of sight, will do so across slices
  - This is currently done in a rudimentary way, the mob's eyes are projected into the other slices and then the raycast occurs 
  - Mobs that throw projectiles (skeletons, creepers, other stuff) will step into the player's slice before trying to use projectiles
  - Creepers will not increase fuse when out of the player slice
- Mobs are now rendered up to three slices away
- Holding shift while stepping will prevent the player from falling
- Added a new stat: W Step Count
- When holding PlaceW + (direction), an outline of the targeted block is shown
- Re-added ModMenu Config Menu

## Bug Fixes
- Stepping while sprinting or swimming no longer excessively consumes hunger
  - It now has a similar amount of consumption to sprint jumping
- Fixed an issue with distance calculation away from the origin
- Fixed crashes regarding intersections between 3D and 4D Boxes
- Fixed ability to step into solid blocks in spectator mode
- Nether portals can be created in all slices
- Fixed some bounding box intersection/containment checks

## Other
- Created a workflow for automatically creating releases