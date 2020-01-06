package jp.pocket7878.redux.libs

import jp.pocket7878.redux.libs.reducer.compose
import org.junit.Before
import org.junit.Test
import kotlin.RuntimeException

class ReduxTest {
    lateinit var reducer: TestReducer
    lateinit var middleware: TestMiddleware
    lateinit var store: Store<TestState, TestNavigation, TestErrorTag>

    @Before
    fun setUp() {
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
        store.onError(TestErrorTag.ERROR, null)
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
        store.onError(TestErrorTag.ERROR, null)
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
        store.dispatch(TestAction.Increment())

        val testObserver = store.state().test()
        testObserver.assertValue {
            it.counter == 1
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
    //endregion

    //region error
    @Test
    fun onErrorTagObservableFromErrors() {
        val testObserver = store.errors().test()

        store.onError(TestErrorTag.ERROR, null)
        testObserver.assertValue {
            it.tag == TestErrorTag.ERROR
        }

        testObserver.dispose()
    }

    @Test
    fun onErrorCauseObservableFromErrors() {
        val testObserver = store.errors().test()

        store.onError(TestErrorTag.ERROR, RuntimeException("Happy Hacking"))
        testObserver.assertValue {
            it.cause is RuntimeException && it.cause?.message == "Happy Hacking"
        }

        testObserver.dispose()
    }

    @Test
    fun onErrorExtrasObservableFromErrors() {
        val testObserver = store.errors().test()

        store.onError(TestErrorTag.ERROR, null, extras = 10)
        testObserver.assertValue {
            it.extras == 10
        }

        testObserver.dispose()
    }
    //endregion
}