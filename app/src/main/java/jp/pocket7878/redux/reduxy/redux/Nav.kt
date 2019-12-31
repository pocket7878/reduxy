package jp.pocket7878.redux.reduxy.redux

import androidx.navigation.NavDirections
import jp.pocket7878.redux.libs.Navigation

interface Nav : Navigation<ApplicationState, ErrorTag> {
    data class Direction(val direction: NavDirections) : Nav
}