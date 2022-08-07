package com.practicalconnectlab.app.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper

abstract class BaseViewModelFragment<T : BaseVM> : BaseFragment() {

    protected val viewModel by lazy { buildViewModel() }

    protected abstract fun buildViewModel(): T

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initLiveDataObservers()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        setBaseViewModel(viewModel)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    @CallSuper
    protected open fun initLiveDataObservers() {
    }

}
