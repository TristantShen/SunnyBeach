package cn.cqautotest.sunnybeach.ui.fragment.user.media

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.app.PagingFragment
import cn.cqautotest.sunnybeach.databinding.UserFishListFragmentBinding
import cn.cqautotest.sunnybeach.ktx.dp
import cn.cqautotest.sunnybeach.ktx.snapshotList
import cn.cqautotest.sunnybeach.manager.UserManager
import cn.cqautotest.sunnybeach.model.Fish
import cn.cqautotest.sunnybeach.other.IntentKey
import cn.cqautotest.sunnybeach.ui.activity.FishPondDetailActivity
import cn.cqautotest.sunnybeach.ui.activity.ImagePreviewActivity
import cn.cqautotest.sunnybeach.ui.adapter.FishListAdapter
import cn.cqautotest.sunnybeach.ui.adapter.delegate.AdapterDelegate
import cn.cqautotest.sunnybeach.ui.dialog.ShareDialog
import cn.cqautotest.sunnybeach.util.SUNNY_BEACH_FISH_URL_PRE
import cn.cqautotest.sunnybeach.util.SimpleLinearSpaceItemDecoration
import cn.cqautotest.sunnybeach.viewmodel.fishpond.FishPondViewModel
import com.blankj.utilcode.util.VibrateUtils
import com.hjq.umeng.Platform
import com.hjq.umeng.UmengShare
import com.umeng.socialize.media.UMImage
import com.umeng.socialize.media.UMWeb
import kotlinx.coroutines.flow.collectLatest

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/31
 * desc   : 用户摸鱼列表 Fragment
 */
class UserFishListFragment : PagingFragment<AppActivity>() {

    private val mBinding by viewBinding<UserFishListFragmentBinding>()
    private val mFishPondViewModel by activityViewModels<FishPondViewModel>()
    private val mAdapterDelegate = AdapterDelegate()
    private val mFishListAdapter = FishListAdapter(mAdapterDelegate)

    override fun getPagingAdapter() = mFishListAdapter

    override fun getLayoutId(): Int = R.layout.user_fish_list_fragment

    override fun initView() {
        super.initView()
        mBinding.pagingRecyclerView.addItemDecoration(SimpleLinearSpaceItemDecoration(4.dp))
    }

    override suspend fun loadListData() {
        val userId = arguments?.getString(IntentKey.ID, "").orEmpty()
        mFishPondViewModel.getUserFishList(userId).collectLatest { mFishListAdapter.submitData(it) }
    }

    override fun initEvent() {
        super.initEvent()
        mAdapterDelegate.setOnItemClickListener { _, position ->
            mFishListAdapter.snapshotList[position]?.let { FishPondDetailActivity.start(requireContext(), it.id) }
        }
        mFishListAdapter.setOnNineGridClickListener { sources, index ->
            ImagePreviewActivity.start(requireContext(), sources.toMutableList(), index)
        }
        mFishListAdapter.setOnMenuItemClickListener { view, item, position ->
            when (view.id) {
                R.id.ll_share -> shareFish(item)
                R.id.ll_great -> dynamicLikes(item, position)
            }
        }
    }

    private fun dynamicLikes(item: Fish.FishItem, position: Int) {
        val thumbUpList = item.thumbUpList
        val currUserId = UserManager.loadCurrUserId()
        takeIf { thumbUpList.contains(currUserId) }?.let { toast("请不要重复点赞") }?.also { return }
        thumbUpList.add(currUserId)
        mFishListAdapter.notifyItemChanged(position)
        VibrateUtils.vibrate(80)
        mFishPondViewModel.dynamicLikes(item.id).observe(viewLifecycleOwner) {}
    }

    private fun shareFish(item: Fish.FishItem) {
        val momentId = item.id
        val content = UMWeb(SUNNY_BEACH_FISH_URL_PRE + momentId)
        content.title = "我分享了一条摸鱼动态，快来看看吧~"
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

    companion object {
        @JvmStatic
        fun newInstance(userId: String): UserFishListFragment {
            val fragment = UserFishListFragment()
            val args = Bundle().apply {
                putString(IntentKey.ID, userId)
            }
            fragment.arguments = args
            return fragment
        }
    }
}