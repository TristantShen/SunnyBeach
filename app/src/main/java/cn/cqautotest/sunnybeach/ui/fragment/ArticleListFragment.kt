package cn.cqautotest.sunnybeach.ui.fragment

import android.annotation.SuppressLint
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.StatusAction
import cn.cqautotest.sunnybeach.app.TitleBarFragment
import cn.cqautotest.sunnybeach.databinding.ArticleListFragmentBinding
import cn.cqautotest.sunnybeach.ui.activity.HomeActivity
import cn.cqautotest.sunnybeach.ui.adapter.AdapterDelegate
import cn.cqautotest.sunnybeach.ui.adapter.ArticleAdapter
import cn.cqautotest.sunnybeach.util.SimpleLinearSpaceItemDecoration
import cn.cqautotest.sunnybeach.util.dp
import cn.cqautotest.sunnybeach.viewmodel.ArticleViewModel
import cn.cqautotest.sunnybeach.widget.StatusLayout
import kotlinx.coroutines.flow.collectLatest

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/6/20
 * desc   : 首页 Fragment
 */
class ArticleListFragment : TitleBarFragment<HomeActivity>(), StatusAction {

    private var _binding: ArticleListFragmentBinding? = null
    private val mBinding get() = _binding!!
    private val mArticleViewModel by viewModels<ArticleViewModel>()
    private val mArticleAdapter = ArticleAdapter(AdapterDelegate())
    private val loadStateListener = { cls: CombinedLoadStates ->
        if (cls.refresh is LoadState.NotLoading) {
            showComplete()
            mBinding.refreshLayout.finishRefresh()
        }
        if (cls.refresh is LoadState.Loading) {
            showLoading()
        }
        if (cls.refresh is LoadState.Error) {
            showError {
                mArticleAdapter.refresh()
            }
        }
    }

    override fun getLayoutId(): Int = R.layout.article_list_fragment

    override fun onBindingView() {
        _binding = ArticleListFragmentBinding.bind(view)
    }

    override fun initObserver() {

    }

    @SuppressLint("InflateParams")
    override fun initEvent() {
        mBinding.topLayout.setOnClickListener {

        }
        mBinding.refreshLayout.setOnRefreshListener {
            mArticleAdapter.refresh()
        }
        // 需要在 View 销毁的时候移除 listener
        mArticleAdapter.addLoadStateListener(loadStateListener)
    }

    override fun initData() {
        loadArticleList()
    }

    private fun loadArticleList() {
        lifecycleScope.launchWhenCreated {
            mArticleViewModel.getArticleListByCategoryId("recommend").collectLatest {
                mArticleAdapter.submitData(it)
            }
        }
    }

    override fun initView() {
        mBinding.rvArticleList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mArticleAdapter
            addItemDecoration(SimpleLinearSpaceItemDecoration(4.dp))
        }
    }

    override fun getStatusLayout(): StatusLayout = mBinding.slArticleLayout

    override fun onDestroyView() {
        super.onDestroyView()
        mArticleAdapter.removeLoadStateListener(loadStateListener)
        _binding = null
    }

    override fun isStatusBarEnabled(): Boolean {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled()
    }

    companion object {

        @JvmStatic
        fun newInstance(): ArticleListFragment {
            return ArticleListFragment()
        }
    }
}