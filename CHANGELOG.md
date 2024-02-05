# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).


## [v1.2.0] - 2024-02-05
### :sparkles: New Features
- [`62f1265`](https://github.com/WinDanesz/ArcaneApprentices/commit/62f126523ff469ecc54177fe93ed60d34ab49dad) - This mod now forces the 'Fix Effects When Changing Dimensions' setting of the PotionCore mod to be false, if PotionCore is installed (although this can also be disabled, but not recommended). This fixes the issue with respawning apprentices instantly dying. *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`321b34e`](https://github.com/WinDanesz/ArcaneApprentices/commit/321b34ebd805e768bf7da7fae8fbe5c70aec3714) - Buffed Explorer's Belt's journey time reduction 15%->25%. (Exposed this to configs as well) *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`68da088`](https://github.com/WinDanesz/ArcaneApprentices/commit/68da08831b6046657e29bc9d9863c98d1e1cfd23) - Apprentices can use swords for combat *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`f5a5d55`](https://github.com/WinDanesz/ArcaneApprentices/commit/f5a5d55d1f2a51e1c4ac17bef20b59911688e923) - Added /resetapprenticedata <player> command. This purges the apprentice data of a player (and resets their max cap). *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`fb8b5bf`](https://github.com/WinDanesz/ArcaneApprentices/commit/fb8b5bfb26e1cfd8461b7d2c611b5da6d05f8d70) - Apprentices can use bows (if you provide them arrows) *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`e3f614a`](https://github.com/WinDanesz/ArcaneApprentices/commit/e3f614a1e127791b6dabbd1b9b9002001532ac1f) - AI improvements and more speech lines *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`03e3837`](https://github.com/WinDanesz/ArcaneApprentices/commit/03e383745dfdb109fe201edd446a30daae325d75) - Added new artefacts *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`d52fc38`](https://github.com/WinDanesz/ArcaneApprentices/commit/d52fc38e8f1552ef4004a2a2bfe71ce156af6cda) - The Scroll of Amnesia item of Ancient Spellcraft can reset all spell slots of an apprentice *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`4776cea`](https://github.com/WinDanesz/ArcaneApprentices/commit/4776cea25f41e694a53c7b046b22fada34c3c40d) - Improved study progress persistence for apprentices. They can resume studying a spell. *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`0a03c53`](https://github.com/WinDanesz/ArcaneApprentices/commit/0a03c5371fadaf0c169baa0d3bbc40ab80b09d80) - Improved shift click interactions with the apprentice inventory *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`0de7f27`](https://github.com/WinDanesz/ArcaneApprentices/commit/0de7f277f4272dfc99e2c6da773850eb540af937) - Before updating, please make sure you remove items from your apprentice's artefact slot, otherwise you might not find it after the update. *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`b34b893`](https://github.com/WinDanesz/ArcaneApprentices/commit/b34b893c1a07109a697715ad5d15c477140069b7) - More artefacts *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`f829259`](https://github.com/WinDanesz/ArcaneApprentices/commit/f82925924c311078e7170aedebd6b3d3651d6933) - Implemented Talents! All apprentices now have a hidden talent that grants them a special ability. This is unlocked once the apprentice grows up. *(commit by [@WinDanesz](https://github.com/WinDanesz))*

### :bug: Bug Fixes
- [`8b60733`](https://github.com/WinDanesz/ArcaneApprentices/commit/8b60733ed6a6bce56cf3362f434e37c20863e30b) - Fixed apprentice natural health regen when health is not near full *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`5be500a`](https://github.com/WinDanesz/ArcaneApprentices/commit/5be500ac6f9b9c31b24bad2e0cb5e257eb1a08c5) - Lang key fixes *(commit by [@WinDanesz](https://github.com/WinDanesz))*

### :wrench: Chores
- [`6724eb0`](https://github.com/WinDanesz/ArcaneApprentices/commit/6724eb016f84c37c0f0a36dfe39676a651a88d02) - Apprentices can use tipped arrows *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`af50930`](https://github.com/WinDanesz/ArcaneApprentices/commit/af50930ee003ffca84e429dccdce17c06b9c0b88) - AI improvements *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`ebe6759`](https://github.com/WinDanesz/ArcaneApprentices/commit/ebe6759091a3ba716dc407e46639aa6f337b1a42) - artefact fixes *(commit by [@WinDanesz](https://github.com/WinDanesz))*


## [v1.1.1] - 2024-01-30
### :bug: Bug Fixes
- [`54466da`](https://github.com/WinDanesz/ArcaneApprentices/commit/54466da9a734f2e301c1dc70d26f8478456c3204) - Fixed occasional crash with Apprentices fighting mobs *(commit by [@WinDanesz](https://github.com/WinDanesz))*


## [v1.1.0] - 2024-01-28
### :sparkles: New Features
- [`2a79c5d`](https://github.com/WinDanesz/ArcaneApprentices/commit/2a79c5dcafd3a896421879774ea35cbc8d948ba6) - Spell identification grants XP for NPCs (configurable) *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`6e3a270`](https://github.com/WinDanesz/ArcaneApprentices/commit/6e3a270c817f328285802b1d78a805900d7b274d) - Some more speech-hints about how to hire an apprentice *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`8de95c9`](https://github.com/WinDanesz/ArcaneApprentices/commit/8de95c9b1e946bb1d7c96d0ad032d19cc6d832bf) - Lowered Enchanted Itinerary artefact tier to Rare *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`8a3b878`](https://github.com/WinDanesz/ArcaneApprentices/commit/8a3b878cb46751c98b26fda276a4d0df917820fb) - Apprentices learn from and gain some XP just by watching the player casting spells *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`6153d9d`](https://github.com/WinDanesz/ArcaneApprentices/commit/6153d9d2140cb1f5ef1b2be1651243ca77450bef) - Apprentices learn from and gain some XP just by watching the player casting spells *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`ff6c060`](https://github.com/WinDanesz/ArcaneApprentices/commit/ff6c060c09d63f6d93e9d69c6941e659122d6196) - Lowered Itinerary tier to Uncommon *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`99ec05e`](https://github.com/WinDanesz/ArcaneApprentices/commit/99ec05ee2ca6c4bc8581b4fb0f07d551ab84cd71) - Improved itinerary artefact with location *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`b75bf1c`](https://github.com/WinDanesz/ArcaneApprentices/commit/b75bf1cda4429b5209b2c3ba6a5bdae6b1878d67) - Apprentices can two artefacts, using the offhand and the artefact slot *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`b8b6015`](https://github.com/WinDanesz/ArcaneApprentices/commit/b8b6015e0f404c2831edd166738e86712ac7d2e4) - Added Recall Apprentices spell *(commit by [@WinDanesz](https://github.com/WinDanesz))*

### :bug: Bug Fixes
- [`c744203`](https://github.com/WinDanesz/ArcaneApprentices/commit/c744203f6a126d95c5ddb2a3b5dc6eb25000eb56) - Fixed long duration journeys having a broken timer *(commit by [@WinDanesz](https://github.com/WinDanesz))*
- [`0de8479`](https://github.com/WinDanesz/ArcaneApprentices/commit/0de847953fa398bd82f2a38b8be0999fa39ceb79) - Fixed medium and long duration journeys having much longer duration that set in the config *(commit by [@WinDanesz](https://github.com/WinDanesz))*


[v1.1.0]: https://github.com/WinDanesz/ArcaneApprentices/compare/v1.0.2...v1.1.0
[v1.1.1]: https://github.com/WinDanesz/ArcaneApprentices/compare/v1.1.0...v1.1.1
[v1.2.0]: https://github.com/WinDanesz/ArcaneApprentices/compare/v1.1.1...v1.2.0