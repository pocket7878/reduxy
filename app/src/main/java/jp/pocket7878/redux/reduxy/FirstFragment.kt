package jp.pocket7878.redux.reduxy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.ui.NavigationUI
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import jp.pocket7878.redux.reduxy.databinding.FragmentFirstBinding
import jp.pocket7878.redux.reduxy.ext.autoDispose
import jp.pocket7878.redux.reduxy.redux.Nav
import jp.pocket7878.redux.reduxy.redux.action.CounterAction
import jp.pocket7878.redux.reduxy.redux.Store

class FirstFragment : Fragment() {

    private lateinit var binding: FragmentFirstBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(requireContext()),
            R.layout.fragment_first,
            null,
            false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.plusOneButton.setOnClickListener {
            Store.getInstance()
                .dispatch(CounterAction.CountUp())
        }

        binding.resetButton.setOnClickListener {
            Store.getInstance()
                .dispatch(CounterAction.Reset())
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
    }
}