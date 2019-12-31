package jp.pocket7878.redux.libs

interface Action<T : StateType, N : Navigation<T, E>, E : ErrorTag>

data class AsyncAction<T : StateType, N : Navigation<T, E>, E : ErrorTag>(val block: (state: T) -> Unit) :
    Action<T, N, E>