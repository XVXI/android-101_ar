package com.transcode.smartcity101p2.api

import com.transcode.smartcity101p2.BuildConfig
import com.transcode.smartcity101p2.model.*
import com.transcode.smartcity101p2.model.buyapi.request.*
import com.transcode.smartcity101p2.model.buyapi.response.*
import com.transcode.smartcity101p2.model.complain.*
import com.transcode.smartcity101p2.model.emergency.*
import com.transcode.smartcity101p2.model.notification.*
import com.transcode.smartcity101p2.model.travel.request.AddFavPlaceRequest
import com.transcode.smartcity101p2.model.travel.request.AddFavPlanRequest
import com.transcode.smartcity101p2.model.travel.request.DeleteFavPlaceRequest
import com.transcode.smartcity101p2.model.travel.request.DeleteFavPlanRequest
import com.transcode.smartcity101p2.model.travel.response.FavPlaceListResponse
import com.transcode.smartcity101p2.model.travel.response.FavPlanListResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface Services {
    companion object {
        val BASE_URL = BuildConfig.BASE_URL
        val YOUTUBE_BASE_URL = "https://www.googleapis.com/youtube/v3/"
        val BUY_BASE_URL = BuildConfig.BASE_URL
        val CATE_BASE_URL = BuildConfig.BASE_URL
    }

    @POST("editchoosedatetime")
    fun editChooseDatetime(@Header("Content-Type") content_type: String,
                           @Header("Authorization") authorization: String,
                           @Body request: EditChooseDatetimeRequest): Call<EditChooseDatetimeResponse>

    @POST("cancelqueuecheckin")
    fun cancelQueueCheckin(@Header("Content-Type") content_type: String,
                           @Header("Authorization") authorization: String,
                           @Body request: CancelQueueCheckinRequest): Call<CancelQueueCheckinResponse>

    @GET("playlistItems")
    fun getYoutubePlaylist(@Query("part") part: String,
                           @Query("maxResults") maxResults: String,
                           @Query("playlistId") playlistId: String,
                           @Query("key") key: String): Call<YoutubePlayListResponse>

    @POST("getcctv")
    fun getCctv(@Header("Content-Type") content_type: String,
                @Header("Authorization") authorization: String,
                @Body request: CctvRequest): Call<CctvResponse>

    @POST("getcall")
    fun getCall(@Header("Content-Type") content_type: String,
                @Header("Authorization") authorization: String,
                @Body request: CallPhoneRequest): Call<CallPhoneResponse>

    @POST("editqueuecheckin")
    fun editQueueCheckin(@Header("Content-Type") content_type: String,
                         @Header("Authorization") authorization: String,
                         @Body request: EditQueueCheckinRequest): Call<EditQueueCheckinResponse>

    @POST("resetpassword")
    fun resetPassword(@Header("Content-Type") content_type: String,
                      @Header("Authorization") authorization: String,
                      @Body request: ChangePasswordRequest): Call<ChangePasswordResponse>

    @POST("getprovince")
    fun getProvince(@Header("Content-Type") content_type: String,
                    @Header("Authorization") authorization: String,
                    @Body request: ProvinceRequest): Call<ProvinceResponse>

    @POST("getamphue")
    fun getAmphue(@Header("Content-Type") content_type: String,
                  @Header("Authorization") authorization: String,
                  @Body request: AmphueRequest): Call<AmphueResponse>

    @POST("gettambon")
    fun getTambon(@Header("Content-Type") content_type: String,
                  @Header("Authorization") authorization: String,
                  @Body request: TambonRequest): Call<TambonResponse>

    @POST("editcitizeninfo")
    fun editCitizenInfo(@Header("Content-Type") content_type: String,
                        @Header("Authorization") authorization: String,
                        @Body request: EditCitizenInfoRequest): Call<EditCitizenInfoResponse>


    @POST("editcitizenaccount")
    fun editCitizenAccount(@Header("Content-Type") content_type: String,
                           @Header("Authorization") authorization: String,
                           @Body request: EditcitizenAccountRequest): Call<EditcitizenAccountRespons>

    @POST("getcitizeninfo")
    fun getCitizenInfo(@Header("Content-Type") content_type: String,
                       @Header("Authorization") authorization: String,
                       @Body request: CitizenInfoRequest): Call<CitizenInfoResponse>

    @POST("addnewcomment")
    fun addNewComment(@Header("Content-Type") content_type: String, @Header("Authorization") authorization: String, @Body request: AddCommentRequest): Call<AddCommentResponse>

    @POST("getnewcomment")
    fun getNewsComment(@Header("Content-Type") content_type: String, @Header("Authorization") authorization: String, @Body request: GetNewsCommentRequest): Call<AllNewsCommentResponse>

    @POST("editcityaccount")
    fun editCityAccount(@Header("Content-Type") content_type: String, @Header("Authorization") authorization: String, @Body request: EditCityAccountRequest): Call<EditCityAccountResponse>

    @POST("getnews")
    fun getNews(@Header("Content-Type") content_type: String, @Header("Authorization") authorization: String, @Body request: NewsRequest): Call<AllNewsResponse>

    @POST("getnewsimg")
    fun getNewsImg(@Header("Content-Type") content_type: String,
                   @Header("Authorization") authorization: String,
                   @Body request: NewsImgRequest): Call<NewsImgResponse>

    @POST("getnewsfile")
    fun getNewsFile(@Header("Content-Type") content_type: String,
                    @Header("Authorization") authorization: String,
                    @Body request: NewsFileRequest): Call<NewsFileResponse>

    @Headers("Content-Type: application/json")
    @POST("login")
    fun postLogin(@Body request: LoginRequest): Call<LoginResponse>

    @Headers("Content-Type: application/json")
    @POST("loginguest")
    fun postLoginGuest(@Body request: LoginRequestGuest): Call<LoginResponse>

    @Headers("Content-Type: application/json")
    @POST("logingoogle")
    fun postLoginGoogle(@Body request: LoginRequestGoogle): Call<LoginResponse>

    @Headers("Content-Type: application/json")
    @POST("getcity")
    fun getCity(@Body request: CityRequest): Call<AllCityResponse>

    @Headers("Content-Type: application/json")
    @POST("addcitizenaccount")
    fun registerUser(@Body request: RegisterRequest): Call<RegisterResponse>

    @Headers("Content-Type: application/json")
    @POST("searchmbpolicy")
    fun getPolicy(): Call<PolicyResponse>

    @Headers("Content-Type: application/json")
    @POST("forgetpassword")
    fun forgotPassword(@Body request: ForgotPasswordRequest): Call<ResponseBody>

    @POST("searchcityfunction")
    fun getCityFunction(@Header("Content-Type") content_type: String, @Header("Authorization") authorization: String, @Body request: CityFunctionRequest): Call<AllCityFunctionResponse>

    //queue
    @FormUrlEncoded
    @POST("getqueuelistbycity")
    fun getQueueListByCity(@Field("city_id") city_id: String): Call<ArrayList<QueueListResponse>>

    @FormUrlEncoded
    @POST("getmyqueue")
    fun getMyQueueList(@Field("cus_id") cus_id: String): Call<ArrayList<MyQueueResponse>>

    @FormUrlEncoded
    @POST("getbookqueuenow")
    fun getBookQueueNow(@Field("queue_id") queue_id: String, @Field("citizen_id") citizen_id: String, @Field("form_id") form_id: String): Call<GetBookQueueNowResponse>

    @FormUrlEncoded
    @POST("getbookqueue")
    fun getBookQueue(@Field("queue_id") queue_id: String, @Field("choose_datatime") choose_datatime: String, @Field("citizen_id") citizen_id: String, @Field("queue_slot_id") queue_slot_id: String, @Field("form_id") form_id: String): Call<GetBookQueueNowResponse>

    @FormUrlEncoded
    @POST("getbookqueue")
    fun getBookQueueWithLatLng(@Field("queue_id") queue_id: String, @Field("choose_datatime") choose_datatime: String, @Field("citizen_id") citizen_id: String, @Field("serve_lat") serve_lat: String, @Field("serve_lng") serve_lng: String, @Field("queue_slot_id") queue_slot_id: String, @Field("form_id") form_id: String): Call<GetBookQueueNowResponse>

    @FormUrlEncoded
    @POST("getqueuedetail")
    fun getQueueDetail(@Field("queue_id") queue_id: String): Call<ArrayList<QueueDetailResponse>>

    @FormUrlEncoded
    @POST("getqueueslot")
    fun getQueueSlot(@Field("queue_id") queue_id: String, @Field("choose_datatime") choose_datetime: String): Call<QueueSlotResponse>

    //complain and emergency

    //complain
    @GET("complain/type")
    fun getComplainType(): Call<ComplainTypeResponse>

    @GET("complain/list")
    fun getComplainList(@Query("citizen_id") citizen_id: String): Call<ComplainListResponse>

    @GET("complain/info")
    fun getComplainByID(@Query("complain_id") complain_id: String): Call<ComplainByIDResponse>

    @Headers("Content-Type: application/json")
    @POST("complain/create")
    fun createComplain(@Body request: CreateComplainRequest): Call<CreateComplainResponse>

    @Headers("Content-Type: application/json")
    @POST("complain/dialog")
    fun createComplainDialog(@Body request: CreateComplainDialogRequest): Call<CreateComplainDialogResponse>

    @Headers("Content-Type: application/json")
    @POST("complain/rate")
    fun createComplainRate(@Body request: CreateComplainRateRequest): Call<CreateComplainRateResponse>

    //emergency
    @GET("emergency/type")
    fun getEmergencyType(): Call<EmergencyTypeResponse>

    @GET("emergency/list")
    fun getEmergencyList(@Query("citizen_id") citizen_id: String): Call<EmergencyListResponse>

    @GET("emergency/info")
    fun getEmergencyByID(@Query("emer_id") emer_id: String): Call<EmergencyByIDResponse>

    @Headers("Content-Type: application/json")
    @POST("emergency/create")
    fun createEmergency(@Body request: CreateEmergencyRequest): Call<CreateEmergencyResponse>

    @Headers("Content-Type: application/json")
    @POST("emergency/dialog")
    fun createEmergencyDialog(@Body request: CreateEmergencyDialogRequest): Call<CreateEmergencyDialogResponse>

    @Headers("Content-Type: application/json")
    @POST("emergency/rate")
    fun createEmergencyRate(@Body request: CreateEmergencyRateRequest): Call<CreateEmergencyRateResponse>

    //travel
    //plan list
    @FormUrlEncoded
    @POST("travels/planlist")
    fun getPlanList(@Field("day") day: String, @Field("city_id") city_id: String): Call<PlanListResponse>

    //plan detail
    @FormUrlEncoded
    @POST("travels/plandetail")
    fun getPlanDetail(@Field("plan_id") plan_id: String): Call<PlanDetailResponse>

    //get maker by type
    @FormUrlEncoded
    @POST("travels/placemaker")
    fun getPlacemakerByType(@Field("type") type: String, @Field("city_id") city_id: String): Call<PlaceListByTypeResponse>

    //get maker by text
    @FormUrlEncoded
    @POST("travels/placesearch")
    fun getPlacemakerByText(@Field("text") type: String, @Field("city_id") city_id: String): Call<PlaceListByTypeResponse>

    //get place type
    @FormUrlEncoded
    @POST("travels/getplacetype")
    fun getPlacetype(@Field("city_id") city_id: String): Call<PlaceTypeResponse>

    //get place by id
    @FormUrlEncoded
    @POST("travels/getplacebyid")
    fun getPlaceByID(@Field("place_id") place_id: String): Call<PlaceDetailsResponse>

    //get place sugguest
    @FormUrlEncoded
    @POST("travels/getplacesugguest")
    fun getPlaceSugguest(@Field("city_id") city_id: String): Call<PlaceSuggestionResponse>

    //get place comment
    @FormUrlEncoded
    @POST("travels/addcomment")
    fun addPlaceComment(@Field("citizen_id") citizen_id: String,
                        @Field("place_id") place_id: String,
                        @Field("comment") comment: String,
                        @Field("rating") rating: String): Call<AddPlaceCommentResponse>

    //get my stamp
    @FormUrlEncoded
    @POST("travels/getcheckinbyctid")
    fun getMyStamp(@Field("citizen_id") citizen_id: String): Call<MyStampResponse>

    //get place stamp
    @FormUrlEncoded
    @POST("travels/getplacesuggestinprovince")
    fun getPlaceStamp(@Field("province_id") province_id: String, @Field("citizen_id") citizen_id: String): Call<StampResponse>

    //add checkin
    @FormUrlEncoded
    @POST("travels/checkinplace")
    fun addCheckin(@Field("place_id") place_id: String, @Field("citizen_id") citizen_id: String): Call<AddStampResponse>

    //Buy-Sale
    //getshoplist
    @POST("getshoplist")
    fun getShopList(@Header("Content-Type") content_type: String,
                    @Header("Authorization") authorization: String,
                    @Body request: ShopListRequest): Call<ShopListResponse>

    //addwishlist
    @POST("addwishlist")
    fun addWishlist(@Header("Content-Type") content_type: String,
                    @Header("Authorization") authorization: String,
                    @Body request: AddWishListRequest): Call<AddWishListResponse>

    //Getproduct
    @POST("Getproduct")
    fun getProduct(@Header("Content-Type") content_type: String,
                   @Header("Authorization") authorization: String,
                   @Body request: GetProductRequest): Call<GetProductResponse>

    //getallorders
    @POST("getallorders")
    fun getAllOrders(@Header("Content-Type") content_type: String,
                     @Header("Authorization") authorization: String,
                     @Body request: GetAllOrderRequest): Call<GetAllOrderResponse>

    //getorder
    @POST("getorder")
    fun getOrders(@Header("Content-Type") content_type: String,
                  @Header("Authorization") authorization: String,
                  @Body request: GetOrderRequest): Call<GetOrderResponse>

    //AddOrder
    @POST("AddOrder")
    fun addOrder(@Header("Content-Type") content_type: String,
                 @Header("Authorization") authorization: String,
                 @Body request: AddOrderRequest): Call<AddOrderResponse>

    //ConfirmReceiveOrder
    @POST("ConfirmReceiveOrder")
    fun confirmReceiveOrder(@Header("Content-Type") content_type: String,
                            @Header("Authorization") authorization: String,
                            @Body request: ConfirmReceiveOrderRequest): Call<ConfirmReceiveOrderResponse>

    //getshop
    @POST("getshop")
    fun getShop(@Header("Content-Type") content_type: String,
                @Header("Authorization") authorization: String,
                @Body request: GetShopRequest): Call<GetShopResponse>

    //getshopproducts
    @POST("getshopproducts")
    fun getShopProducts(@Header("Content-Type") content_type: String,
                        @Header("Authorization") authorization: String,
                        @Body request: GetShopProductsRequest): Call<GetShopProductsResponse>

    //address
    @POST("Getcitizenaddress")
    fun getCitizenAddress(@Header("Content-Type") content_type: String,
                          @Header("Authorization") authorization: String,
                          @Body request: AddressRequest): Call<AddressResponse>

    @POST("addcitizenaddress")
    fun addCitizenAddress(@Header("Content-Type") content_type: String,
                          @Header("Authorization") authorization: String,
                          @Body request: AddAddressRequest): Call<AddAddressResponse>

    @POST("removecitizenaddress")
    fun removeCitizenAddress(@Header("Content-Type") content_type: String,
                             @Header("Authorization") authorization: String,
                             @Body request: RemoveAddressRequest): Call<RemoveAddressResponse>

    //GetWeeklyPopularProduct
    @POST("GetWeeklyPopularProduct")
    fun getWeeklyPopularProduct(@Header("Content-Type") content_type: String,
                                @Header("Authorization") authorization: String,
                                @Body request: GetWeeklyPopularRequest): Call<GetWeeklyPopularResponse>

    //    GetWeeklyBestSellerProduct
    @POST("GetWeeklyBestSellerProduct")
    fun getWeeklyBestSellerProduct(@Header("Content-Type") content_type: String,
                                   @Header("Authorization") authorization: String,
                                   @Body request: GetWeeklyBestSellerRequest): Call<GetWeeklyBestSellerResponse>

    //    GetLastestProduct
    @POST("GetLastestProduct")
    fun getNewProduct(@Header("Content-Type") content_type: String,
                      @Header("Authorization") authorization: String,
                      @Body request: GetNewProductRequest): Call<GetNewProductResponse>

    //    SearchShopByKeyword
    @POST("SearchShopByKeyword")
    fun getShopByKeyword(@Header("Content-Type") content_type: String,
                         @Header("Authorization") authorization: String,
                         @Body request: SearchShopByKeywordRequest): Call<ShopListResponse>

    //    https://market.aniccom.com/api/product-online-category?where[is_active]=1
    //    product-online-category
    @GET("GetProductCategory")
    fun getCategory(@Query("where%5Bis_active%5D") isactive: String): Call<CateResponse>

    //    updatecitizenaddress
    @POST("updatecitizenaddress")
    fun updateCitizenAddress(@Header("Content-Type") content_type: String,
                             @Header("Authorization") authorization: String,
                             @Body request: UpdateAddressRequest): Call<UpdateAddressResponse>

    //    invisit
    @POST("r1/invisit")
    fun inVisit(@Header("Content-Type") content_type: String,
                @Body request: InVisitRequest): Call<CommonResponse>

    //    scanar
    @POST("r1/scanar")
    fun sCanner(@Header("Content-Type") content_type: String,
                @Body request: SCannerRequest): Call<CommonResponse>

    //    sendview
    @POST("r1/sendview")
    fun sendView(@Header("Content-Type") content_type: String,
                 @Body request: SendViewRequest): Call<CommonResponse>

    //    sendorder
    @POST("r3/sendorder")
    fun sendOrder(@Header("Content-Type") content_type: String,
                  @Body request: SendOrderRequest): Call<CommonResponse>

    //    planview
    @POST("r6/planview")
    fun planView(@Header("Content-Type") content_type: String,
                 @Body request: PlanViewRequest): Call<CommonResponse>

    //    getUnreadEmer
    @POST("getUnreadEmer")
    fun getUnreadEmer(@Header("Content-Type") content_type: String,
                      @Body request: UnreadEmerRequest): Call<UnreadEmerResponse>

    //    getUnreadEmer
    @POST("getUnreadComplain")
    fun getUnreadComplain(@Header("Content-Type") content_type: String,
                          @Body request: UnreadComplainRequest): Call<UnreadComplainResponse>

    //    updateEmerdialog
    @POST("updateEmerdialog")
    fun updateEmerdialog(@Header("Content-Type") content_type: String,
                         @Body request: UpdateEmerDialogRequest): Call<CommonResponse>

    //    updateComplaindialog
    @POST("updateComplaindialog")
    fun updateComplaindialog(@Header("Content-Type") content_type: String,
                             @Body request: UpdateComplainDialogRequest): Call<CommonResponse>


    //    updateComplaindialog
    @POST("planfav")
    fun planFav(@Header("Content-Type") content_type: String,
                @Body request: PlanFavRequest): Call<CommonResponse>


    //    updateComplaindialog
    @POST("placefav")
    fun placeFav(@Header("Content-Type") content_type: String,
                 @Body request: PlaceFavRequest): Call<CommonResponse>

    //favplan

    @GET("travels/planfav/{user_id}")
    fun getFavPlanList(@Path(value = "user_id", encoded = true) user_id: String): Call<FavPlanListResponse>

    @POST("travels/planfav")
    fun addFavPlan(@Body request: AddFavPlanRequest): Call<CommonResponse>

    //    @DELETE("travels/planfav/{user_id}")
    @HTTP(method = "DELETE", path = "travels/planfav/{user_id}", hasBody = true)
    fun deleteFavPlan(@Path(value = "user_id", encoded = true) user_id: Int, @Body request: DeleteFavPlanRequest): Call<CommonResponse>

    //fav place

    @GET("travels/placefav/{user_id}")
    fun getFavPlaceList(@Path(value = "user_id", encoded = true) user_id: String): Call<FavPlaceListResponse>

    @POST("travels/placefav")
    fun addFavPlace(@Body request: AddFavPlaceRequest): Call<CommonResponse>

    //    @DELETE("travels/placefav/{user_id}")
    @HTTP(method = "DELETE", path = "travels/placefav/{user_id}", hasBody = true)
    fun deleteFavPlace(@Path(value = "user_id", encoded = true) user_id: Int, @Body request: DeleteFavPlaceRequest): Call<CommonResponse>


}