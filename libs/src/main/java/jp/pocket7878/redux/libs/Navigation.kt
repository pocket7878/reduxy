package jp.pocket7878.redux.libs

interface Navigation<T : StateType, E : ErrorTag> : Action<T, Navigation<T, E>, E>