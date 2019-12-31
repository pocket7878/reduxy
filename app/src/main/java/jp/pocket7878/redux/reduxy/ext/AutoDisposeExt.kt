package jp.pocket7878.redux.reduxy.ext

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.uber.autodispose.ScopeProvider
import com.uber.autodispose.android.lifecycle.scope
import com.uber.autodispose.autoDisposable
import io.reactivex.*
import io.reactivex.android.MainThreadDisposable

fun <T> Observable<T>.autoDispose(fragment: Fragment) =
    autoDisposable(fragment.viewLifecycleOwner.scope())

fun <T> Observable<T>.autoDispose(activity: AppCompatActivity) = autoDisposable(activity.scope())
fun <T> Observable<T>.autoDispose(view: View) = autoDisposable(ViewScopeProvider2(view))

fun <T> Flowable<T>.autoDispose(fragment: Fragment) =
    autoDisposable(fragment.viewLifecycleOwner.scope())

fun <T> Flowable<T>.autoDispose(activity: AppCompatActivity) = autoDisposable(activity.scope())
fun <T> Flowable<T>.autoDispose(view: View) = autoDisposable(ViewScopeProvider2(view))

fun <T> Single<T>.autoDispose(fragment: Fragment) =
    autoDisposable(fragment.viewLifecycleOwner.scope())

fun <T> Single<T>.autoDispose(activity: AppCompatActivity) = autoDisposable(activity.scope())
fun <T> Single<T>.autoDispose(view: View) = autoDisposable(ViewScopeProvider2(view))

fun <T> Maybe<T>.autoDispose(fragment: Fragment) =
    autoDisposable(fragment.viewLifecycleOwner.scope())

fun <T> Maybe<T>.autoDispose(activity: AppCompatActivity) = autoDisposable(activity.scope())
fun <T> Maybe<T>.autoDispose(view: View) = autoDisposable(ViewScopeProvider2(view))

fun Completable.autoDispose(fragment: Fragment) =
    autoDisposable(fragment.viewLifecycleOwner.scope())

fun Completable.autoDispose(activity: AppCompatActivity) = autoDisposable(activity.scope())
fun Completable.autoDispose(view: View) = autoDisposable(ViewScopeProvider2(view))

/**
 * autoDispose備え付けのViewScopeProviderはviewがattachされる前に使用すると自動でdisposeしてしまう。
 * RecyclerView内で使う場合、attachされる直前にsubscribeすることになるので全部即disposeになってしまうので、
 * attachされているかどうかはみない
 */
private class ViewScopeProvider2(private val view: View) : ScopeProvider {
    override fun requestScope(): CompletableSource = DetachEventCompletable(view)
}

private class DetachEventCompletable(private val view: View) : CompletableSource {
    override fun subscribe(observer: CompletableObserver) {
        val listener = Listener(view, observer)
        observer.onSubscribe(listener)
        view.addOnAttachStateChangeListener(listener)
        if (listener.isDisposed) view.removeOnAttachStateChangeListener(listener)
    }

    private class Listener(private val view: View, private val observer: CompletableObserver) :
        MainThreadDisposable(),
        View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(v: View) = Unit
        override fun onViewDetachedFromWindow(v: View) {
            if (!isDisposed) observer.onComplete()
        }

        override fun onDispose() {
            view.removeOnAttachStateChangeListener(this)
        }
    }
}