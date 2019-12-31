package jp.pocket7878.redux.reduxy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import jp.pocket7878.redux.reduxy.ext.autoDispose
import jp.pocket7878.redux.reduxy.redux.Nav
import jp.pocket7878.redux.reduxy.redux.Store

class MainActivity : AppCompatActivity() {

    private val navController by lazy {
        findNavController(R.id.main_content)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Store.getInstance()
            .navigation()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .autoDispose(this)
            .subscribe {
                when (it) {
                    is Nav.Direction -> {
                        navController.navigate(it.direction)
                    }
                }
            }
    }
}
