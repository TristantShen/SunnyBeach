package cn.cqautotest.sunnybeach.ui.fragment.user.media

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.StatusAction
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.app.AppFragment
import cn.cqautotest.sunnybeach.databinding.UserArticleListFragmentBinding
import cn.cqautotest.sunnybeach.ktx.dp
import cn.cqautotest.sunnybeach.ktx.loadStateListener
import cn.cqautotest.sunnybeach.model.UserArticle
import cn.cqautotest.sunnybeach.other.IntentKey
import cn.cqautotest.sunnybeach.ui.activity.BrowserActivity
import cn.cqautotest.sunnybeach.ui.activity.ImagePreviewActivity
import cn.cqautotest.sunnybeach.ui.adapter.AdapterDelegate
import cn.cqautotest.sunnybeach.ui.adapter.UserArticleAdapter
import cn.cqautotest.sunnybeach.ui.dialog.ShareDialog
import cn.cqautotest.sunnybeach.util.SUNNY_BEACH_ARTICLE_URL_PRE
import cn.cqautotest.sunnybeach.util.SimpleLinearSpaceItemDecoration
import cn.cqautotest.sunnybeach.viewmodel.ArticleViewModel
import cn.cqautotest.sunnybeach.widget.StatusLayout
import com.hjq.umeng.Platform
import com.hjq.umeng.UmengShare
import com.umeng.socialize.media.UMImage
import com.umeng.socialize.media.UMWeb
import kotlinx.coroutines.flow.collectLatest

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/31
 * desc   : 用户文章列表 Fragment
 */
class UserArticleListFragment : AppFragment<AppActivity>(), StatusAction {

    private val mBinding by viewBinding<UserArticleListFragmentBinding>()
    private val mAdapterDelegate = AdapterDelegate()
    private val mUserArticleAdapter = UserArticleAdapter(mAdapterDelegate)
    private val mArticleViewModel by activityViewModels<ArticleViewModel>()
    private val loadStateListener = loadStateListener(mUserArticleAdapter) {
        mBinding.refreshLayout.finishRefresh()
    }

    override fun getLayoutId(): Int = R.layout.user_article_list_fragment

    override fun initView() {
        mBinding.rvArticleList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mUserArticleAdapter
            addItemDecoration(SimpleLinearSpaceItemDecoration(4.dp))
        }
    }

    override fun initData() {
        val userId = arguments?.getString(IntentKey.ID, "") ?: ""
        loadUserArticleList(userId)
    }

    private fun loadUserArticleList(userId: String) {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            mArticleViewModel.getUserArticleList(userId).collectLatest {
                mUserArticleAdapter.submitData(it)
            }
        }
    }

    override fun initEvent() {
        mBinding.refreshLayout.setOnRefreshListener {
            mUserArticleAdapter.refresh()
        }
        // 需要在 View 销毁的时候移除 listener
        mUserArticleAdapter.addLoadStateListener(loadStateListener)
        mAdapterDelegate.setOnItemClickListener { _, position ->
            // 跳转到文章详情界面
            val item = mUserArticleAdapter.snapshot()[position] ?: return@setOnItemClickListener
            val url = "$SUNNY_BEACH_ARTICLE_URL_PRE${item.id}"
            BrowserActivity.start(requireContext(), url)
        }
        mUserArticleAdapter.setOnMenuItemClickListener { view, item, _ ->
            when (view.id) {
                R.id.ll_share -> shareArticle(item)
            }
        }
        mUserArticleAdapter.setOnNineGridClickListener { sources, index ->
            ImagePreviewActivity.start(requireContext(), sources.toMutableList(), index)
        }
    }

    private fun shareArticle(item: UserArticle.UserArticleItem) {
        val articleId = item.id
        val content = UMWeb(SUNNY_BEACH_ARTICLE_URL_PRE + articleId)
        content.title = item.title
        content.setThumb(UMImage(requireContext(), R.mipmap.launcher_ic))
        content.description = getString(R.string.app_name)
        // 分享
        ShareDialog.Builder(requireActivity())
            .setShareLink(content)
            .setListener(object : UmengShare.OnShareListener {
                override fun onSucceed(platform: Platform?) {
                    toast("分享成功")
                }

                override fun onError(platform: Platform?, t: Throwable) {
                    toast(t.message)
                }

                override fun onCancel(platform: Platform?) {
                    toast("分享取消")
                }
            })
            .show()
    }

    override fun getStatusLayout(): StatusLayout = mBinding.hlArticleListHint

    override fun onDestroyView() {
        super.onDestroyView()
        mUserArticleAdapter.removeLoadStateListener(loadStateListener)
    }

    companion object {
        @JvmStatic
        fun newInstance(userId: String): UserArticleListFragment {
            val fragment = UserArticleListFragment()
            val args = Bundle().apply {
                putString(IntentKey.ID, userId)
            }
            fragment.arguments = args
            return fragment
        }
    }
}