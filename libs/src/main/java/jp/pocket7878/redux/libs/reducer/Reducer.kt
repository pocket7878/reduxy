package jp.pocket7878.redux.libs.reducer

import jp.pocket7878.redux.libs.Action
import jp.pocket7878.redux.libs.ErrorTag
import jp.pocket7878.redux.libs.Navigation
import jp.pocket7878.redux.libs.StateType

interface Reducer<T : StateType, N : Navigation<T, E>, E : ErrorTag> {
    fun run(state: T, action: Action<T, N, E>): T
}

fun <T : StateType, N : Navigation<T, E>, E : ErrorTag> Reducer<T, N, E>.compose(other: Reducer<T, N, E>): Reducer<T, N, E> {
    return object : Reducer<T, N, E> {
        override fun run(state: T, action: Action<T, N, E>): T {
            val tmpState = this@compose.run(state, action)
            return other.run(tmpState, action)
        }
    }
}

fun <T : StateType, N : Navigation<T, E>, E : ErrorTag> Collection<Reducer<T, N, E>>.compose(): Reducer<T, N, E> {
    return this.reduce { acc, other ->
        acc.compose(other)
    }
}