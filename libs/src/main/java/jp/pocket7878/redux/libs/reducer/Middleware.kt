package jp.pocket7878.redux.libs.reducer

import jp.pocket7878.redux.libs.Action
import jp.pocket7878.redux.libs.ErrorTag
import jp.pocket7878.redux.libs.Navigation
import jp.pocket7878.redux.libs.StateType

typealias Dispatcher<T, N, E> = (Action<T, N, E>) -> Unit

interface Middleware<T : StateType, N : Navigation<T, E>, E : ErrorTag> {
    fun call(s: T): (Dispatcher<T, N, E>) -> Dispatcher<T, N, E>
}
