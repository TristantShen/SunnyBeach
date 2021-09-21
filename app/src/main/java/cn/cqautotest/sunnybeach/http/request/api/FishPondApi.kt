package cn.cqautotest.sunnybeach.http.request.api

import cn.cqautotest.sunnybeach.model.*
import cn.cqautotest.sunnybeach.util.SUNNY_BEACH_BASE_URL
import okhttp3.MultipartBody
import retrofit2.http.*

interface FishPondApi {

    /**
     * 上传图片（摸鱼动态）
     */
    @POST("${SUNNY_BEACH_BASE_URL}ct/image/mo_yu")
    suspend fun uploadFishImage(@Part body: MultipartBody.Part): BaseResponse<String>

    /**
     * 发布动态
     */
    @POST("${SUNNY_BEACH_BASE_URL}ct/moyu")
    suspend fun putFish(@Body moment: @JvmSuppressWildcards Map<String, Any?>): BaseResponse<Any>

    /**
     * 获取动态评论
     */
    @GET("${SUNNY_BEACH_BASE_URL}ct/moyu/comment/{momentId}/{page}?sort=1")
    suspend fun getFishCommendListById(
        @Path("momentId") momentId: String,
        @Path("page") page: Int
    ): BaseResponse<FishPondComment>

    /**
     * 获取动态详情
     */
    @GET("${SUNNY_BEACH_BASE_URL}ct/moyu/{momentId}")
    suspend fun loadFishDetailById(@Path("momentId") momentId: String): BaseResponse<Fish.FishItem>

    /**
     * 获取动态列表
     */
    @GET("${SUNNY_BEACH_BASE_URL}ct/moyu/list/{topicId}/{page}")
    suspend fun loadFishListById(
        @Path("topicId") topicId: String,
        @Path("page") page: Int
    ): BaseResponse<Fish>

    /**
     * 获取首页话题（类似于摸鱼首页侧栏）
     */
    @GET("${SUNNY_BEACH_BASE_URL}ct/moyu/topic/index")
    suspend fun loadTopicListByIndex(): BaseResponse<FishPondTopicIndex>

    /**
     * 获取热门动态列表
     */
    @GET("${SUNNY_BEACH_BASE_URL}ct/moyu/hot/{size}")
    suspend fun loadHotFish(@Path("size") size: Int): BaseResponse<Fish>

    /**
     * 获取话题列表
     */
    @GET("${SUNNY_BEACH_BASE_URL}ct/moyu/topic")
    suspend fun loadTopicList(): BaseResponse<FishPondTopicList>

    /**
     * 发表评论(评论动态)
     */
    @POST("${SUNNY_BEACH_BASE_URL}ct/moyu/comment")
    suspend fun postComment(@Body momentComment: @JvmSuppressWildcards Map<String, Any?>): BaseResponse<String>
}