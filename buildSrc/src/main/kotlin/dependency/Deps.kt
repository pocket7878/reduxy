package dependency

object Deps {
    object AutoDispose {
        const val version = "1.2.0"
        const val core = "com.uber.autodispose:autodispose-ktx:$version"
        const val android = "com.uber.autodispose:autodispose-android-ktx:$version"
        const val aac = "com.uber.autodispose:autodispose-android-archcomponents-ktx:$version"
    }

    object Utility {
        const val timber = "com.jakewharton.timber:timber:4.7.1"
    }

    object AndroidX {
        object Navigation {
            const val version = "2.1.0"
            const val fragment = "androidx.navigation:navigation-fragment:$version"
            const val fragmentKtx = "androidx.navigation:navigation-fragment-ktx:$version"
            const val ui = "androidx.navigation:navigation-ui:$version"
            const val uiKtx = "androidx.navigation:navigation-ui-ktx:$version"
            const val gradlePlugin =
                "androidx.navigation:navigation-safe-args-gradle-plugin:$version"
        }
    }
}