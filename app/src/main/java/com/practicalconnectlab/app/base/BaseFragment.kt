package com.practicalconnectlab.app.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar


abstract class BaseFragment : Fragment() {

    protected abstract fun getContentResource(): Int

    private lateinit var baseViewModel: BaseVM
    private var errorSnackBar: Snackbar? = null
    private lateinit var rootView: View

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rootView = view
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(getContentResource(), container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initViews()
    }

    @CallSuper
    protected open fun initViews() {
    }

    fun setBaseViewModel(viewModel: BaseVM) {
        this.baseViewModel = viewModel
        baseViewModel.errorMessage.observe(viewLifecycleOwner, Observer { errorMessage ->
            if (errorMessage != null) showError(errorMessage) else hideError()
        })
    }

    fun showError(errorMessage: String) {
        if (errorMessage.isNotEmpty()) {
            errorSnackBar = Snackbar.make(rootView, errorMessage, Snackbar.LENGTH_SHORT)
            errorSnackBar?.show()
            baseViewModel.errorMessage.value = ""
        }
    }

    private fun hideError() {
        errorSnackBar?.dismiss()
    }

    fun hideKeyboardInstantly(view: View?) {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }
}