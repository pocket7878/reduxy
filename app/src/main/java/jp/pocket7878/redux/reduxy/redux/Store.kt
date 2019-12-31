package jp.pocket7878.redux.reduxy.redux

import jp.pocket7878.redux.libs.Store
import jp.pocket7878.redux.libs.reducer.compose
import jp.pocket7878.redux.reduxy.redux.middleware.LoggerMiddleware
import jp.pocket7878.redux.reduxy.redux.reducer.CounterReducer
import jp.pocket7878.redux.reduxy.redux.reducer.SeekbarReducer

object Store {
    private val _instance by lazy {
        Store(
            ApplicationStateFactory(),
            listOf(CounterReducer(), SeekbarReducer()).compose(),
            listOf(LoggerMiddleware())
        )
    }

    fun getInstance(): Store<ApplicationState, Nav, ErrorTag> {
        return _instance
    }
}