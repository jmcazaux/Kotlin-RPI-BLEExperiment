## Experimenting with Bluetooth Low Energy libraries in a Kotlin / Raspeberry Pi environment

This is a little experiment project to test the ability to connect to an indoor rowing machine ([WaterRower](https://www.waterrower.com/)).  
The WaterRower is equipped with a Bluetooth Low Energy [communication module](https://www.waterrower.com/us/products/waterrower-commodule).

Eventually, I'd like to use the Raspberry to broadcast my WaterRower workouts to either Garmin Connect, or to my Garmin watch (via an ANT+ dongle).

And to make things more fun, the goal is to use Kotlin!


### [Blue-Falcon](https://github.com/Reedyuk/blue-falcon)

Tested on branch `bluefalcon`:
- ✅ Was able to detect devices, though it was a bit strange initially (it did not show any device name).
- ✅ Can connect to the WaterRower
- ✅ Can discover services
- ❌ Did not succeed reading characteristics / value
- ❌ Do not get notification when paired device disconnects.

### [Blessed for Bluez](https://github.com/weliem/blessed-bluez)

This is the library that actually backs Blue Falcon

Tested on branch `blessed-bluez`:
- ✅ Was able to detect devices, though it was a bit strange initially (it did not show any device name).
- ✅ Can connect to the WaterRower
- ✅ Can discover services
- ✅ Succeeded reading characteristics value (tested with string values only, but seems to work in principle)
- ✅ Succeeded getting notifications on subscribed characteristics.
- ✅ Succeeded to extract rower data from the characteristic value.