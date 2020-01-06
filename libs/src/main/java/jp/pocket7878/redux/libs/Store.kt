package jp.pocket7878.redux.libs

import android.os.Handler
import android.os.Looper
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import jp.pocket7878.redux.libs.reducer.Dispatcher
import jp.pocket7878.redux.libs.reducer.Middleware
import jp.pocket7878.redux.libs.reducer.Reducer
import java.util.concurrent.locks.ReentrantLock

interface StateFactory<T : StateType> {
    fun create(): T
}

class Store<T : StateType, N : Navigation<T, E>, E : ErrorTag>(
    private val initialStateFactory: StateFactory<T>,
    private val reducer: Reducer<T, N, E>,
    middlewares: List<Middleware<T, N, E>>
) {
    //region Lock
    private val lock = ReentrantLock()

    private val withLock: Middleware<T, N, E> = object : Middleware<T, N, E> {
        override fun call(s: T): (Dispatcher<T, N, E>) -> Dispatcher<T, N, E> {
            return { dispatcher ->
                { action ->
                    if (lock.tryLock()) {
                        try {
                            dispatcher(action)
                        } finally {
                            lock.unlock()
                        }
                    } else {
                        throw IllegalAccessError("Store is not allowed multithreaded dispatching")
                    }
                }
            }
        }
    }
    //endregion

    //region State
    private val actions: PublishSubject<Action<T, N, E>> = PublishSubject.create()
    private val state: BehaviorSubject<T> =
        BehaviorSubject.createDefault(initialStateFactory.create())
    private val observable: Observable<T> = state.hide()
    fun state(): Observable<T> = observable
    fun getState(): T = state.value ?: initialStateFactory.create()
    //endregion

    //region Action
    private val _dispatch: (Action<T, N, E>) -> Unit =
        (middlewares + withLock).fold(
            { action: Action<T, N, E> ->
                when (action) {
                    is AsyncAction<T, N, E> -> {
                        action.block(this.state.value!!)
                    }
                    is ErrorEntry<T, N, E> -> {
                        this.errorsSubject.onNext(action)
                    }
                    is Navigation<T, E> -> {
                        this.navigationSubject.onNext(action as N)
                    }
                    else -> this.state.onNext(
                        reducer.run(this.state.value!!, action)
                    )
                }
            }
        ) { acc, middleWare ->
            middleWare.call(this.state.value!!)(acc)
        }

    fun dispatch(action: Action<T, N, E>) {
        this.actions.onNext(action)
    }
    //endregion

    //region Navigation
    private val navigationSubject: PublishSubject<N> = PublishSubject.create()
    private val _navigation: Observable<N> = navigationSubject.hide()

    fun navigation(): Observable<N> = _navigation
    fun navigate(navigation: N) {
        this.dispatch(navigation as Action<T, N, E>)
    }
    //endregion

    //region Errors
    private val errorsSubject: PublishSubject<ErrorEntry<T, N, E>> = PublishSubject.create()
    private val _errors = errorsSubject.hide()

    fun errors(): Observable<ErrorEntry<T, N, E>> = _errors
    fun errors(tag: ErrorTag): Observable<ErrorEntry<T, N, E>> = _errors.filter { it.tag == tag }
    fun errors(tags: Array<ErrorTag>): Observable<ErrorEntry<T, N, E>> =
        _errors.filter { tags.contains(it.tag) }

    fun onError(
        tag: E,
        cause: Throwable?,
        extras: Any? = null,
        recoverAction: (() -> Unit)? = null
    ) {
        this.dispatch(ErrorEntry(tag, cause, extras, recoverAction))
    }
    //endregion

    init {
        actions.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { this._dispatch(it) }
    }
}