package jp.pocket7878.redux.reduxy.redux

import jp.pocket7878.redux.libs.StateFactory
import jp.pocket7878.redux.libs.StateType

data class ApplicationState(
    val counter: Int,
    val progress: Int
) : StateType

class ApplicationStateFactory : StateFactory<ApplicationState> {
    override fun create(): ApplicationState {
        return ApplicationState(0, 0)
    }
}