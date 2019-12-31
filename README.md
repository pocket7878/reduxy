# reduxy

Reduxy like architecture for Android.

# Architecture

![architecture](https://raw.githubusercontent.com/pocket7878/reduxy/master/docs/crew_android_redux_flow.png)

## StateType

Application state data type tag.

### Example

```kotlin
data class ApplicationState(
    val counter: Int,
    val progress: Int
) : StateType
```

## StateFactory<T: StateType>

Create state instance.

```kotlin
interface StateFactory<T : StateType> {
    fun create(): T
}
```

### Example

```kotlin
class ApplicationStateFactory : StateFactory<ApplicationState> {
    override fun create(): ApplicationState {
        return ApplicationState(0, 0)
    }
}
```
## Navigation

Nagitation type tag to represent screen transition.

```kotlin
interface Navigation<T : StateType, E : ErrorTag> : Action<T, Navigation<T, E>, E>
```

### Example

Can be combine with Navigation component.

```kotlin
interface Nav : Navigation<ApplicationState, ErrorTag> {
    data class Direction(val direction: NavDirections) : Nav
}
```

## ErrorTag, ErrorEntry

Error type tag to represent application error.

```kotlin
interface ErrorTag

data class ErrorEntry<T : StateType, N : Navigation<T, E>, E : ErrorTag>(
    val tag: E,
    val cause: Throwable?,
    val extras: Any? = null,
    val recoverAction: (() -> Unit)? = null
) : Action<T, N, E>
```

## Action

```kotlin
interface Action<T : StateType, N : Navigation<T, E>, E : ErrorTag>
```

Action type tag to grouping StateType, Navigation, ErrorTag.

### Example

```kotlin
interface CounterAction : Action<ApplicationState, Nav, ErrorTag> {
    class CountUp : CounterAction
    class Reset : CounterAction
}
```

## Reducer

Reducer to generate new state from current state and dispatched action.

```kotlin
interface Reducer<T : StateType, N : Navigation<T, E>, E : ErrorTag> {
    fun run(state: T, action: Action<T, N, E>): T
}
```

### Example

```kotlin
class CounterReducer : Reducer<ApplicationState, Nav, ErrorTag> {
    override fun run(
        state: ApplicationState,
        action: Action<ApplicationState, Nav, ErrorTag>
    ): ApplicationState {
        return when (action) {
            is CounterAction.CountUp -> {
                state.copy(
                    counter = state.counter + 1
                )
            }
            is CounterAction.Reset -> {
                state.copy(
                    counter = 0
                )
            }
            else -> state
        }
    }
}
```

### Utility

Also defined some utility functions:

- `fun <T : StateType, N : Navigation<T, E>, E : ErrorTag> Reducer<T, N, E>.compose(other: Reducer<T, N, E>): Reducer<T, N, E>`
- `fun <T : StateType, N : Navigation<T, E>, E : ErrorTag> Collection<Reducer<T, N, E>>.compose(): Reducer<T, N, E>`

## Middleware

Middleware to filter, modify, or submit action before reducer.
```kotlin
typealias Dispatcher<T, N, E> = (Action<T, N, E>) -> Unit

interface Middleware<T : StateType, N : Navigation<T, E>, E : ErrorTag> {
    fun call(s: T): (Dispatcher<T, N, E>) -> Dispatcher<T, N, E>
}
```

### Example

```kotlin
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
```

## Store

Manage all redux component.

```kotlin
object Store {
    private val _instance by lazy {
        Store(
            ApplicationStateFactory(),
            listOf(CounterReducer(), SeekbarReducer()).compose(),
            listOf(LoggerMiddleware())
        )
    }

    fun getInstance(): Store<ApplicationState, Nav, ErrorTag> {
        return _instance
    }
}
```

## Example App

Example application is stored in `app/` directory

```kotlin
class FirstFragment : Fragment() {
    ...
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        ...
        binding.plusOneButton.setOnClickListener {
            Store.getInstance()
                .dispatch(CounterAction.CountUp())
        }

        binding.toSecondButton.setOnClickListener {
            Store.getInstance().navigate(
                Nav.Direction(
                    FirstFragmentDirections.actionFirstFragmentToSecondFragment()
                )
            )
        }

        Store.getInstance()
            .state()
            .map { it.counter.toString() }
            .distinctUntilChanged()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .autoDispose(this)
            .subscribe {
                binding.textView.text = it
            }
        ...
    }
    ...
}
```