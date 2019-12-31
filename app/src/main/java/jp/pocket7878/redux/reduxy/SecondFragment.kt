package jp.pocket7878.redux.reduxy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import jp.pocket7878.redux.reduxy.databinding.FragmentSecondBinding
import jp.pocket7878.redux.reduxy.ext.autoDispose
import jp.pocket7878.redux.reduxy.redux.Nav
import jp.pocket7878.redux.reduxy.redux.Store
import jp.pocket7878.redux.reduxy.redux.action.SeekbarAction

class SecondFragment : Fragment() {

    private lateinit var binding: FragmentSecondBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(requireContext()),
            R.layout.fragment_second,
            null,
            false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.seekBar.progress = Store.getInstance().getState().progress

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(bar: SeekBar, progress: Int, p2: Boolean) {
                postProgress(progress)
            }

            override fun onStartTrackingTouch(p0: SeekBar) {
            }

            override fun onStopTrackingTouch(p0: SeekBar) {
                postProgress(p0.progress)
            }
        })

        binding.toFirstButton.setOnClickListener {
            Store.getInstance().navigate(
                Nav.Direction(
                    SecondFragmentDirections.actionSecondFragmentToFirstFragment()
                )
            )
        }

        Store.getInstance()
            .state()
            .map { it.progress.toString() }
            .distinctUntilChanged()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .autoDispose(this)
            .subscribe {
                showProgress(it)
            }
    }

    private fun showProgress(it: String?) {
        binding.progressText.text = it
    }

    private fun postProgress(progress: Int) {
        Store.getInstance().dispatch(SeekbarAction.SetProgress(progress))
    }
}