// Safe-args lives in its own convention plugin because only `presentation` hosts a
// navigation graph. It must be applied after one of the android convention plugins.
plugins {
    id("androidx.navigation.safeargs.kotlin")
}
