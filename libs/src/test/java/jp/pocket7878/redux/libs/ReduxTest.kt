package jp.pocket7878.redux.libs

import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import jp.pocket7878.redux.libs.reducer.compose
import org.junit.Before
import org.junit.Test

class ReduxTest {
    lateinit var reducer: TestReducer
    lateinit var middleware: TestMiddleware
    lateinit var store: Store<TestState, TestNavigation, TestErrorTag>

    @Before
    fun setUp() {
        //Setup Schedulers
        RxAndroidPlugins.reset()
        RxJavaPlugins.reset()
        RxJavaPlugins.setIoSchedulerHandler {
            Schedulers.trampoline()
        }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler {
            Schedulers.trampoline()
        }

        //Reinitialize store
        reducer = TestReducer()
        middleware = TestMiddleware()
        store = Store(
            TestStateFactory(),
            listOf(reducer).compose(),
            listOf(middleware)
        )
    }

    //region middleware
    @Test
    fun middlewareDispatcherNotCalledWithoutDispatch() {
        assert(!middleware.dispatcherCalled)
    }

    @Test
    fun dispatchCallsMiddleware() {
        store.dispatch(TestAction.Increment())
        assert(middleware.dispatcherCalled)
    }

    @Test
    fun navigateCallsMiddleware() {
        store.navigate(TestNavigation.Nav())
        assert(middleware.dispatcherCalled)
    }

    @Test
    fun onErrorCallsMiddleware() {
        store.onError(TestErrorTag.ERROR1, null)
        assert(middleware.dispatcherCalled)
    }
    //endregion

    //region reducer
    @Test
    fun reducerNotCalledWithoutAnyEvent() {
        assert(!reducer.runCalled)
    }

    @Test
    fun dispatchCallsReducer() {
        store.dispatch(TestAction.Increment())
        assert(reducer.runCalled)
    }

    @Test
    fun navigateNotCallsReducer() {
        store.navigate(TestNavigation.Nav())
        assert(!reducer.runCalled)
    }

    @Test
    fun onErrorNotCallsReducer() {
        store.onError(TestErrorTag.ERROR1, null)
        assert(!reducer.runCalled)
    }
    //endregion

    //region Action
    @Test
    fun initialStateCounterIsZero() {
        val testObserver = store.state().test()

        testObserver.assertValue {
            it.counter == 0
        }

        testObserver.dispose()
    }

    @Test
    fun dispatchIncrementActionIncrementCounter() {
        val testObserver = store.state().skip(1).test()

        store.dispatch(TestAction.Increment())
        testObserver.assertValue {
            it.counter == 1
        }

        testObserver.dispose()
    }

    @Test
    fun dispatchNotAffectToNavigation() {
        val testObserver = store.navigation().test()
        store.dispatch(TestAction.Increment())
        testObserver.assertEmpty()
        testObserver.dispose()
    }

    @Test
    fun dispatchNotAffectToErrors() {
        val testObserver = store.errors().test()
        store.dispatch(TestAction.Increment())
        testObserver.assertEmpty()
        testObserver.dispose()
    }

    @Test
    fun dispatchInvokeAsyncAction() {
        store.dispatch(AsyncAction {
            store.dispatch(TestAction.Increment())
            store.dispatch(TestAction.Increment())
        })

        val testObserver = store.state().test()
        testObserver.assertValue {
            it.counter == 2
        }

        testObserver.dispose()
    }
    //endregion

    //region navigation
    @Test
    fun navigateObservableFromNavigation() {
        val testObserver = store.navigation().test()

        val dummyNav = TestNavigation.Nav()

        store.navigate(dummyNav)
        testObserver.assertValue(dummyNav)

        testObserver.dispose()
    }

    @Test
    fun navigateNotPublishEventWhichDispatchedBeforeSubscribe() {
        val dummyNav = TestNavigation.Nav()

        store.navigate(dummyNav)
        val testObserver = store.navigation().test()
        testObserver.assertEmpty()

        testObserver.dispose()
    }

    @Test
    fun navigateNotAffectToState() {
        val testObserver = store.state().skip(1).test()

        val dummyNav = TestNavigation.Nav()

        store.navigate(dummyNav)
        testObserver.assertEmpty()

        testObserver.dispose()
    }

    @Test
    fun navigateNotAffectToErrors() {
        val testObserver = store.state().skip(1).test()

        store.onError(TestErrorTag.ERROR1, null)
        testObserver.assertEmpty()

        testObserver.dispose()
    }
    //endregion

    //region error
    @Test
    fun onErrorTagObservableFromErrors() {
        val testObserver = store.errors().test()

        store.onError(TestErrorTag.ERROR1, null)
        testObserver.assertValue {
            it.tag == TestErrorTag.ERROR1
        }

        testObserver.dispose()
    }

    @Test
    fun onErrorNotPublishEventBeforeSubscription() {
        store.onError(TestErrorTag.ERROR1, null)
        val testObserver = store.errors().test()
        testObserver.assertEmpty()

        testObserver.dispose()
    }

    @Test
    fun onErrorCauseObservableFromErrors() {
        val testObserver = store.errors().test()

        store.onError(TestErrorTag.ERROR1, RuntimeException("Happy Hacking"))
        testObserver.assertValue {
            it.cause is RuntimeException && it.cause?.message == "Happy Hacking"
        }

        testObserver.dispose()
    }

    @Test
    fun onErrorExtrasObservableFromErrors() {
        val testObserver = store.errors().test()

        store.onError(TestErrorTag.ERROR1, null, extras = 10)
        testObserver.assertValue {
            it.extras == 10
        }

        testObserver.dispose()
    }

    @Test
    fun errorsSelectTag() {
        var testObserver = store.errors(TestErrorTag.ERROR1).test()

        store.onError(TestErrorTag.ERROR1, null)
        testObserver.assertValue {
            it.tag == TestErrorTag.ERROR1
        }

        testObserver.dispose()

        testObserver = store.errors(TestErrorTag.ERROR2).test()

        store.onError(TestErrorTag.ERROR1, null)
        testObserver.assertEmpty()

        testObserver.dispose()
    }

    @Test
    fun errorsSelectTagList() {
        var testObserver = store.errors(arrayOf<ErrorTag>(TestErrorTag.ERROR1)).test()

        store.onError(TestErrorTag.ERROR1, null)
        testObserver.assertValue {
            it.tag == TestErrorTag.ERROR1
        }

        testObserver.dispose()

        testObserver = store.errors(arrayOf<ErrorTag>(TestErrorTag.ERROR2)).test()

        store.onError(TestErrorTag.ERROR1, null)
        testObserver.assertEmpty()

        testObserver.dispose()
    }

    @Test
    fun onErrorNotAffectToState() {
        var testObserver = store.state().skip(1).test()

        store.onError(TestErrorTag.ERROR1, null)
        testObserver.assertEmpty()
        testObserver.dispose()
    }

    @Test
    fun onErrorNotAffectToNavigate() {
        var testObserver = store.navigation().test()

        store.onError(TestErrorTag.ERROR1, null)
        testObserver.assertEmpty()
        testObserver.dispose()
    }
    //endregion
}