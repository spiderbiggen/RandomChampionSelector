# Random Champion Selector

An Android app that lets you browse the full League of Legends champion roster — splash art and
lore — or jump straight to a randomly picked champion. Champion data and images come
from Riot's public [Data Dragon](https://developer.riotgames.com/docs/lol#data-dragon) static API
and are cached on-device, so the app keeps working offline after the first sync.

## Features

- Alphabetical overview of the full champion roster
- A button that picks a random champion and opens its details screen
- Details screen with splash art and champion lore
- Background download of champion data and images, with progress on the splash screen
- Local cache so data is only fetched when a new game version appears

## Project structure

Multi-module, layered by responsibility:

| Module          | Contents                                                              |
|-----------------|-----------------------------------------------------------------------|
| `:app`          | Application class, manifest, build/signing config                     |
| `:domain`       | Models, repository interfaces, use cases — no Android UI or IO detail |
| `:data`         | Data Dragon API client, Room database, file and preference storage    |
| `:presentation` | Activity, fragments, adapters, view models, resources                 |
| `build-logic`   | Shared Gradle convention plugins (`randomchampion.*`)                 |

## Disclaimer

Random Champion Selector is not endorsed by Riot Games and does not reflect the views or opinions of
Riot Games or anyone officially involved in producing or managing League of Legends. League of
Legends and Riot Games are trademarks or registered trademarks of Riot Games, Inc.
