package com.transcode.smartcity101p2.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.transcode.smartcity101p2.R
import com.transcode.smartcity101p2.contract.PolicyFragmentContract
import com.transcode.smartcity101p2.model.PolicyResponse
import com.transcode.smartcity101p2.presenter.PolicyFragmentPresenter
import com.transcode.smartcity101p2.view.CustomAppBarLayout2
import kotlinx.android.synthetic.main.appbar_main2.view.*
import kotlinx.android.synthetic.main.fragment_policy.*


class PolicyFragment : CoreFragment(), PolicyFragmentContract.View {

    companion object {
        fun newInstance(): PolicyFragment {
            return PolicyFragment().apply {
                val bundle = Bundle()
                arguments = bundle
            }
        }
    }

    lateinit var presenter: PolicyFragmentPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_policy, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
    }

    private fun initView() {
        val appBar = appbar as CustomAppBarLayout2
        appBar.setTitle(getString(R.string.policy_policy_text))
        appBar.leftBt.setOnClickListener {
            fragmentManager?.popBackStack()
            FragmentHelper.remove(fragmentManager, this)
        }

        presenter = PolicyFragmentPresenter(this)

        presenter.getPolicy()
    }

    override fun updateView(res: PolicyResponse) {
        policy_text?.text = res.data?.PolicyDetail ?: ""
    }
}