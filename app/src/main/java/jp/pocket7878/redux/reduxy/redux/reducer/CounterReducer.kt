package jp.pocket7878.redux.reduxy.redux.reducer

import jp.pocket7878.redux.libs.Action
import jp.pocket7878.redux.libs.reducer.Reducer
import jp.pocket7878.redux.reduxy.redux.ApplicationState
import jp.pocket7878.redux.reduxy.redux.action.CounterAction
import jp.pocket7878.redux.reduxy.redux.ErrorTag
import jp.pocket7878.redux.reduxy.redux.Nav

class CounterReducer : Reducer<ApplicationState, Nav, ErrorTag> {
    override fun run(
        state: ApplicationState,
        action: Action<ApplicationState, Nav, ErrorTag>
    ): ApplicationState {
        return when (action) {
            is CounterAction.CountUp -> {
                state.copy(
                    counter = state.counter + 1
                )
            }
            is CounterAction.Reset -> {
                state.copy(
                    counter = 0
                )
            }
            else -> state
        }
    }
}