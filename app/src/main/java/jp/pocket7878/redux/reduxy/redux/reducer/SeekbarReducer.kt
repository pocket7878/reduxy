package jp.pocket7878.redux.reduxy.redux.reducer

import jp.pocket7878.redux.libs.Action
import jp.pocket7878.redux.libs.reducer.Reducer
import jp.pocket7878.redux.reduxy.redux.ApplicationState
import jp.pocket7878.redux.reduxy.redux.ErrorTag
import jp.pocket7878.redux.reduxy.redux.Nav
import jp.pocket7878.redux.reduxy.redux.action.SeekbarAction

class SeekbarReducer : Reducer<ApplicationState, Nav, ErrorTag> {
    override fun run(
        state: ApplicationState,
        action: Action<ApplicationState, Nav, ErrorTag>
    ): ApplicationState {
        return when (action) {
            is SeekbarAction.SetProgress -> {
                state.copy(
                    progress = action.progress
                )
            }
            else -> state
        }
    }
}