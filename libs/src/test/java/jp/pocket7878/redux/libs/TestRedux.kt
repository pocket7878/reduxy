package jp.pocket7878.redux.libs

import jp.pocket7878.redux.libs.reducer.Dispatcher
import jp.pocket7878.redux.libs.reducer.Middleware
import jp.pocket7878.redux.libs.reducer.Reducer
import jp.pocket7878.redux.libs.reducer.compose

data class TestState(val counter: Int = 0) : StateType

class TestStateFactory() : StateFactory<TestState> {
    override fun create(): TestState {
        return TestState()
    }
}

enum class TestErrorTag : ErrorTag {
    ERROR1,
    ERROR2
}

interface TestNavigation : Navigation<TestState, TestErrorTag> {
    class Nav : TestNavigation
}

interface TestAction : Action<TestState, TestNavigation, TestErrorTag> {
    class Increment : TestAction
}

class TestReducer : Reducer<TestState, TestNavigation, TestErrorTag> {
    var runCalled: Boolean = false
        private set

    override fun run(
        state: TestState,
        action: Action<TestState, TestNavigation, TestErrorTag>
    ): TestState {
        runCalled = true
        return when (action) {
            is TestAction.Increment -> {
                state.copy(
                    counter = state.counter + 1
                )
            }
            else -> state
        }
    }
}

class TestMiddleware : Middleware<TestState, TestNavigation, TestErrorTag> {

    var dispatcherCalled: Boolean = false
        private set

    override fun call(s: TestState): (Dispatcher<TestState, TestNavigation, TestErrorTag>) -> Dispatcher<TestState, TestNavigation, TestErrorTag> {
        return { dispatcher ->
            { action ->
                dispatcherCalled = true
                dispatcher(action)
            }
        }
    }
}

object TestStore {
    private val _instance by lazy {
        Store(
            TestStateFactory(),
            listOf(TestReducer()).compose(),
            listOf(TestMiddleware())
        )
    }

    fun getInstance(): Store<TestState, TestNavigation, TestErrorTag> {
        return _instance
    }
}

