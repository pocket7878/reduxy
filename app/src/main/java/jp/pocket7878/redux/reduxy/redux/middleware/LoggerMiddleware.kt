package jp.pocket7878.redux.reduxy.redux.middleware

import jp.pocket7878.redux.libs.reducer.Dispatcher
import jp.pocket7878.redux.libs.reducer.Middleware
import jp.pocket7878.redux.reduxy.redux.ApplicationState
import jp.pocket7878.redux.reduxy.redux.ErrorTag
import jp.pocket7878.redux.reduxy.redux.Nav
import timber.log.Timber

class LoggerMiddleware : Middleware<ApplicationState, Nav, ErrorTag> {
    override fun call(s: ApplicationState): (Dispatcher<ApplicationState, Nav, ErrorTag>) -> Dispatcher<ApplicationState, Nav, ErrorTag> {
        return { dispatcher ->
            { action ->
                Timber.d(action.toString())
                dispatcher(action)
            }
        }
    }
}