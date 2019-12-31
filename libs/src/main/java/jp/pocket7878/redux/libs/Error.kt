package jp.pocket7878.redux.libs

interface ErrorTag

data class ErrorEntry<T : StateType, N : Navigation<T, E>, E : ErrorTag>(
    val tag: E,
    val cause: Throwable?,
    val extras: Any? = null,
    val recoverAction: (() -> Unit)? = null
) : Action<T, N, E>