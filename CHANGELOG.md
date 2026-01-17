# Changelog v0.3.3 to v0.3.4

## Features
- Started work on making W axis indistinguishable from X or Z axes
  - All entities have non-integer W positions and velocity.
  - Removed "stepping" mechanic. Walking in W is the same as in any horizontal direction - less friction while in air, more when in water or while shifting, etc.
  - Hitboxes have been extended to W. Players (and other entities) are officially not hyper-flat-landers. Fun fact: did you know hitbox size is the same in all horizontal directions? 

## Bug Fixes
- Fixed entities that shoot (like skeletons) crashing the game when they had no target