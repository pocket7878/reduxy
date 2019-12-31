package jp.pocket7878.redux.reduxy.redux.action

import jp.pocket7878.redux.libs.Action
import jp.pocket7878.redux.reduxy.redux.ApplicationState
import jp.pocket7878.redux.reduxy.redux.ErrorTag
import jp.pocket7878.redux.reduxy.redux.Nav

interface CounterAction : Action<ApplicationState, Nav, ErrorTag> {
    class CountUp : CounterAction
    class Reset : CounterAction
}