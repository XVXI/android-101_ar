package com.transcode.smartcity101p2.api

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
import retrofit2.Callback
import retrofit2.Response

class ApiRequest {
    private object Holder {
        val INSTANCE = ApiRequest()
    }

    companion object {
        val INSTANCE: ApiRequest by lazy {
            Holder.INSTANCE
        }
        const val applicationJson = "application/json"
    }

    fun requestEditChooseDatetime(callback: Callback<EditChooseDatetimeResponse>, request: EditChooseDatetimeRequest, bearer: String) {
        val request = RequestClient.INSTANCE.getLoginService().editChooseDatetime(applicationJson, bearer, request)
        request.enqueue(object : Callback<EditChooseDatetimeResponse> {
            override fun onFailure(call: Call<EditChooseDatetimeResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<EditChooseDatetimeResponse>?, response: Response<EditChooseDatetimeResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestCancelQueueCheckinRequest(callback: Callback<CancelQueueCheckinResponse>, request: CancelQueueCheckinRequest, bearer: String) {
        val request = RequestClient.INSTANCE.getLoginService().cancelQueueCheckin(applicationJson, bearer, request)
        request.enqueue(object : Callback<CancelQueueCheckinResponse> {
            override fun onFailure(call: Call<CancelQueueCheckinResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<CancelQueueCheckinResponse>?, response: Response<CancelQueueCheckinResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestYoutubePlayList(callback: Callback<YoutubePlayListResponse>, part: String, maxResults: String, playlistId: String, key: String) {
        val request = RequestClient.INSTANCE.getYoutubeService().getYoutubePlaylist(part, maxResults, playlistId, key)
        request.enqueue(object : Callback<YoutubePlayListResponse> {
            override fun onFailure(call: Call<YoutubePlayListResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<YoutubePlayListResponse>?, response: Response<YoutubePlayListResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestGetCctv(callback: Callback<CctvResponse>, request: CctvRequest, bearer: String) {
        val request = RequestClient.INSTANCE.getLoginService().getCctv(applicationJson, bearer, request)
        request.enqueue(object : Callback<CctvResponse> {
            override fun onFailure(call: Call<CctvResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<CctvResponse>?, response: Response<CctvResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestGetCall(callback: Callback<CallPhoneResponse>, request: CallPhoneRequest, bearer: String) {
        val request = RequestClient.INSTANCE.getLoginService().getCall(applicationJson, bearer, request)
        request.enqueue(object : Callback<CallPhoneResponse> {
            override fun onFailure(call: Call<CallPhoneResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<CallPhoneResponse>?, response: Response<CallPhoneResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestEditQueueCheckin(callback: Callback<EditQueueCheckinResponse>, request: EditQueueCheckinRequest, bearer: String) {
        val request = RequestClient.INSTANCE.getLoginService().editQueueCheckin(applicationJson, bearer, request)
        request.enqueue(object : Callback<EditQueueCheckinResponse> {
            override fun onFailure(call: Call<EditQueueCheckinResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<EditQueueCheckinResponse>?, response: Response<EditQueueCheckinResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestResetPassword(callback: Callback<ChangePasswordResponse>, request: ChangePasswordRequest, bearer: String) {
        val request = RequestClient.INSTANCE.getLoginService().resetPassword(applicationJson, bearer, request)
        request.enqueue(object : Callback<ChangePasswordResponse> {
            override fun onFailure(call: Call<ChangePasswordResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<ChangePasswordResponse>?, response: Response<ChangePasswordResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestProvince(callback: Callback<ProvinceResponse>, request: ProvinceRequest, bearer: String) {
        val request = RequestClient.INSTANCE.getLoginService().getProvince(applicationJson, bearer, request)
        request.enqueue(object : Callback<ProvinceResponse> {
            override fun onFailure(call: Call<ProvinceResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<ProvinceResponse>?, response: Response<ProvinceResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestAmphue(callback: Callback<AmphueResponse>, request: AmphueRequest, bearer: String) {
        val request = RequestClient.INSTANCE.getLoginService().getAmphue(applicationJson, bearer, request)
        request.enqueue(object : Callback<AmphueResponse> {
            override fun onFailure(call: Call<AmphueResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<AmphueResponse>?, response: Response<AmphueResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestTambon(callback: Callback<TambonResponse>, request: TambonRequest, bearer: String) {
        val request = RequestClient.INSTANCE.getLoginService().getTambon(applicationJson, bearer, request)
        request.enqueue(object : Callback<TambonResponse> {
            override fun onFailure(call: Call<TambonResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<TambonResponse>?, response: Response<TambonResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestEditCitizenInfo(callback: Callback<EditCitizenInfoResponse>, request: EditCitizenInfoRequest, bearer: String) {
        val request = RequestClient.INSTANCE.getLoginService().editCitizenInfo(applicationJson, bearer, request)
        request.enqueue(object : Callback<EditCitizenInfoResponse> {
            override fun onFailure(call: Call<EditCitizenInfoResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<EditCitizenInfoResponse>?, response: Response<EditCitizenInfoResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestEditCitizenAccount(callback: Callback<EditcitizenAccountRespons>, request: EditcitizenAccountRequest, bearer: String) {
        val request = RequestClient.INSTANCE.getLoginService().editCitizenAccount(applicationJson, bearer, request)
        request.enqueue(object : Callback<EditcitizenAccountRespons> {
            override fun onFailure(call: Call<EditcitizenAccountRespons>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<EditcitizenAccountRespons>?, response: Response<EditcitizenAccountRespons>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestCitizenInfo(callback: Callback<CitizenInfoResponse>, request: CitizenInfoRequest, bearer: String) {
        val request = RequestClient.INSTANCE.getLoginService().getCitizenInfo(applicationJson, bearer, request)
        request.enqueue(object : Callback<CitizenInfoResponse> {
            override fun onFailure(call: Call<CitizenInfoResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<CitizenInfoResponse>?, response: Response<CitizenInfoResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestLogin(callback: Callback<LoginResponse>, username: String, password: String, city_id: String, firebase_client_id: String) {
        val request = RequestClient.INSTANCE.getLoginService().postLogin(LoginRequest(username, password, city_id, firebase_client_id))
        request.enqueue(object : Callback<LoginResponse> {
            override fun onFailure(call: Call<LoginResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<LoginResponse>?, response: Response<LoginResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestAddComment(callback: Callback<AddCommentResponse>, request: AddCommentRequest, bearer: String) {
        val request = RequestClient.INSTANCE.getLoginService().addNewComment(applicationJson, bearer, request)
        request.enqueue(object : Callback<AddCommentResponse> {
            override fun onFailure(call: Call<AddCommentResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<AddCommentResponse>?, response: Response<AddCommentResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestGetComment(callback: Callback<AllNewsCommentResponse>, request: GetNewsCommentRequest, bearer: String) {
        val request = RequestClient.INSTANCE.getLoginService().getNewsComment(applicationJson, bearer, request)
        request.enqueue(object : Callback<AllNewsCommentResponse> {
            override fun onFailure(call: Call<AllNewsCommentResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<AllNewsCommentResponse>?, response: Response<AllNewsCommentResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestLoginGuest(callback: Callback<LoginResponse>, fb_id: String, is_fb: String, firebase_client_id: String, email: String, fname: String, lname: String) {
        val request = RequestClient.INSTANCE.getLoginService().postLoginGuest(LoginRequestGuest(fb_id, is_fb, firebase_client_id, email, fname, lname))
        request.enqueue(object : Callback<LoginResponse> {
            override fun onFailure(call: Call<LoginResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<LoginResponse>?, response: Response<LoginResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestLoginGoogle(callback: Callback<LoginResponse>, gg_id: String, is_fb: String, firebase_client_id: String, email: String, fname: String, lname: String) {
        val request = RequestClient.INSTANCE.getLoginService().postLoginGoogle(LoginRequestGoogle(gg_id, is_fb, firebase_client_id, email, fname, lname))
        request.enqueue(object : Callback<LoginResponse> {
            override fun onFailure(call: Call<LoginResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<LoginResponse>?, response: Response<LoginResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestAllCity(callback: Callback<AllCityResponse>, city_id: String, page: String, limit: String) {
        val request = RequestClient.INSTANCE.getLoginService().getCity(CityRequest(city_id, page, limit))
        request.enqueue(object : Callback<AllCityResponse> {
            override fun onFailure(call: Call<AllCityResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<AllCityResponse>?, response: Response<AllCityResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestRegister(callback: Callback<RegisterResponse>, request: RegisterRequest) {
        val request = RequestClient.INSTANCE.getLoginService().registerUser(request)
        request.enqueue(object : Callback<RegisterResponse> {
            override fun onFailure(call: Call<RegisterResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<RegisterResponse>?, response: Response<RegisterResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestPolicy(callback: Callback<PolicyResponse>) {
        val request = RequestClient.INSTANCE.getLoginService().getPolicy()
        request.enqueue(object : Callback<PolicyResponse> {
            override fun onFailure(call: Call<PolicyResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<PolicyResponse>?, response: Response<PolicyResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestForgotPassword(callback: Callback<ResponseBody>, request: ForgotPasswordRequest) {
        val request = RequestClient.INSTANCE.getLoginService().forgotPassword(request)
        request.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestCityFunction(callback: Callback<AllCityFunctionResponse>, request: CityFunctionRequest, bearer: String) {
        val request = RequestClient.INSTANCE.getLoginService().getCityFunction(applicationJson, bearer, request)
        request.enqueue(object : Callback<AllCityFunctionResponse> {
            override fun onFailure(call: Call<AllCityFunctionResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<AllCityFunctionResponse>?, response: Response<AllCityFunctionResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestQueueList(callback: Callback<ArrayList<QueueListResponse>>, city_id: String) {
        val request = RequestClient.INSTANCE.getLoginService().getQueueListByCity(city_id)
        request.enqueue(object : Callback<ArrayList<QueueListResponse>> {
            override fun onFailure(call: Call<ArrayList<QueueListResponse>>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<ArrayList<QueueListResponse>>?, response: Response<ArrayList<QueueListResponse>>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestMyQueueList(callback: Callback<ArrayList<MyQueueResponse>>, cus_id: String) {
        val request = RequestClient.INSTANCE.getLoginService().getMyQueueList(cus_id)
        request.enqueue(object : Callback<ArrayList<MyQueueResponse>> {
            override fun onFailure(call: Call<ArrayList<MyQueueResponse>>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<ArrayList<MyQueueResponse>>?, response: Response<ArrayList<MyQueueResponse>>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestBookNow(callback: Callback<GetBookQueueNowResponse>, queue_id: String, citizen_id: String, form_id: String) {
        val request = RequestClient.INSTANCE.getLoginService().getBookQueueNow(queue_id, citizen_id, form_id)
        request.enqueue(object : Callback<GetBookQueueNowResponse> {
            override fun onFailure(call: Call<GetBookQueueNowResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<GetBookQueueNowResponse>?, response: Response<GetBookQueueNowResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestBook(callback: Callback<GetBookQueueNowResponse>, queue_id: String, choose_datatime: String, citizen_id: String, queue_slot_id: String, form_id: String) {
        val request = RequestClient.INSTANCE.getLoginService().getBookQueue(queue_id, choose_datatime, citizen_id, queue_slot_id, form_id)
        request.enqueue(object : Callback<GetBookQueueNowResponse> {
            override fun onFailure(call: Call<GetBookQueueNowResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<GetBookQueueNowResponse>?, response: Response<GetBookQueueNowResponse>?) {
                callback.onResponse(call, response)
            }
        })
    }

    fun requestBookWithLatLng(callback: Callback<GetBookQueueNowResponse>, queue_id: String, choose_datatime: String, citizen_id: String, serve_lat: String, serve_lng: String, queue_slot_id: String, form_id: String) {
        val request = RequestClient.INSTANCE.getLoginService().getBookQueueWithLatLng(queue_id, choose_datatime, citizen_id, serve_lat, serve_lng, queue_slot_id, form_id)
        request.enqueue(object : Callback<GetBookQueueNowResponse> {
            override fun onFailure(call: Call<GetBookQueueNowResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<GetBookQueueNowResponse>?, response: Response<GetBookQueueNowResponse>?) {
                callback.onResponse(call, response)
            }
        })
    }

    fun requestNews(callback: Callback<AllNewsResponse>, request: NewsRequest, bearer: String) {
        val request = RequestClient.INSTANCE.getLoginService().getNews(applicationJson, bearer, request)
        request.enqueue(object : Callback<AllNewsResponse> {
            override fun onFailure(call: Call<AllNewsResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<AllNewsResponse>?, response: Response<AllNewsResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestNewsImage(callback: Callback<NewsImgResponse>, request: NewsImgRequest, bearer: String) {
        val request = RequestClient.INSTANCE.getLoginService().getNewsImg(applicationJson, bearer, request)
        request.enqueue(object : Callback<NewsImgResponse> {
            override fun onFailure(call: Call<NewsImgResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<NewsImgResponse>?, response: Response<NewsImgResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestNewsFile(callback: Callback<NewsFileResponse>, request: NewsFileRequest, bearer: String) {
        val request = RequestClient.INSTANCE.getLoginService().getNewsFile(applicationJson, bearer, request)
        request.enqueue(object : Callback<NewsFileResponse> {
            override fun onFailure(call: Call<NewsFileResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<NewsFileResponse>?, response: Response<NewsFileResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestEditCityAccount(callback: Callback<EditCityAccountResponse>, request: EditCityAccountRequest, bearer: String) {
        val request = RequestClient.INSTANCE.getLoginService().editCityAccount(applicationJson, bearer, request)
        request.enqueue(object : Callback<EditCityAccountResponse> {
            override fun onFailure(call: Call<EditCityAccountResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<EditCityAccountResponse>?, response: Response<EditCityAccountResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestQueueDetail(callback: Callback<ArrayList<QueueDetailResponse>>, queue_id: String) {
        val request = RequestClient.INSTANCE.getLoginService().getQueueDetail(queue_id)
        request.enqueue(object : Callback<ArrayList<QueueDetailResponse>> {
            override fun onFailure(call: Call<ArrayList<QueueDetailResponse>>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<ArrayList<QueueDetailResponse>>?, response: Response<ArrayList<QueueDetailResponse>>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestQueueSlot(callback: Callback<QueueSlotResponse>, queue_id: String, choose_datetime: String) {
        val request = RequestClient.INSTANCE.getLoginService().getQueueSlot(queue_id, choose_datetime)
        request.enqueue(object : Callback<QueueSlotResponse> {
            override fun onFailure(call: Call<QueueSlotResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<QueueSlotResponse>?, response: Response<QueueSlotResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestEmergencyList(callback: Callback<EmergencyListResponse>, citizen_id: String) {
        val request = RequestClient.INSTANCE.getLoginService().getEmergencyList(citizen_id)
        request.enqueue(object : Callback<EmergencyListResponse> {
            override fun onFailure(call: Call<EmergencyListResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<EmergencyListResponse>?, response: Response<EmergencyListResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestEmergencyType(callback: Callback<EmergencyTypeResponse>) {
        val request = RequestClient.INSTANCE.getLoginService().getEmergencyType()
        request.enqueue(object : Callback<EmergencyTypeResponse> {
            override fun onFailure(call: Call<EmergencyTypeResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<EmergencyTypeResponse>?, response: Response<EmergencyTypeResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestCreateEmergency(callback: Callback<CreateEmergencyResponse>, request: CreateEmergencyRequest) {
        val request = RequestClient.INSTANCE.getLoginService().createEmergency(request)
        request.enqueue(object : Callback<CreateEmergencyResponse> {
            override fun onFailure(call: Call<CreateEmergencyResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<CreateEmergencyResponse>?, response: Response<CreateEmergencyResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestEmergencyByID(callback: Callback<EmergencyByIDResponse>, emer_id: String) {
        val request = RequestClient.INSTANCE.getLoginService().getEmergencyByID(emer_id)
        request.enqueue(object : Callback<EmergencyByIDResponse> {
            override fun onFailure(call: Call<EmergencyByIDResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<EmergencyByIDResponse>?, response: Response<EmergencyByIDResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestComplainList(callback: Callback<ComplainListResponse>, citizen_id: String) {
        val request = RequestClient.INSTANCE.getLoginService().getComplainList(citizen_id)
        request.enqueue(object : Callback<ComplainListResponse> {
            override fun onFailure(call: Call<ComplainListResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<ComplainListResponse>?, response: Response<ComplainListResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestComplainType(callback: Callback<ComplainTypeResponse>) {
        val request = RequestClient.INSTANCE.getLoginService().getComplainType()
        request.enqueue(object : Callback<ComplainTypeResponse> {
            override fun onFailure(call: Call<ComplainTypeResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<ComplainTypeResponse>?, response: Response<ComplainTypeResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestCreateComplain(callback: Callback<CreateComplainResponse>, request: CreateComplainRequest) {
        val request = RequestClient.INSTANCE.getLoginService().createComplain(request)
        request.enqueue(object : Callback<CreateComplainResponse> {
            override fun onFailure(call: Call<CreateComplainResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<CreateComplainResponse>?, response: Response<CreateComplainResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestComplainByID(callback: Callback<ComplainByIDResponse>, complain_id: String) {
        val request = RequestClient.INSTANCE.getLoginService().getComplainByID(complain_id)
        request.enqueue(object : Callback<ComplainByIDResponse> {
            override fun onFailure(call: Call<ComplainByIDResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<ComplainByIDResponse>?, response: Response<ComplainByIDResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestCreateEmergencyDialog(callback: Callback<CreateEmergencyDialogResponse>, request: CreateEmergencyDialogRequest) {
        val request = RequestClient.INSTANCE.getLoginService().createEmergencyDialog(request)
        request.enqueue(object : Callback<CreateEmergencyDialogResponse> {
            override fun onFailure(call: Call<CreateEmergencyDialogResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<CreateEmergencyDialogResponse>?, response: Response<CreateEmergencyDialogResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestCreateComplainDialog(callback: Callback<CreateComplainDialogResponse>, request: CreateComplainDialogRequest) {
        val request = RequestClient.INSTANCE.getLoginService().createComplainDialog(request)
        request.enqueue(object : Callback<CreateComplainDialogResponse> {
            override fun onFailure(call: Call<CreateComplainDialogResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<CreateComplainDialogResponse>?, response: Response<CreateComplainDialogResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestPlanList(callback: Callback<PlanListResponse>, day: String, city_id: String) {
        val request = RequestClient.INSTANCE.getLoginService().getPlanList(day, city_id)
        request.enqueue(object : Callback<PlanListResponse> {
            override fun onFailure(call: Call<PlanListResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<PlanListResponse>?, response: Response<PlanListResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestPlanDetail(callback: Callback<PlanDetailResponse>, plan_id: String) {
        val request = RequestClient.INSTANCE.getLoginService().getPlanDetail(plan_id)
        request.enqueue(object : Callback<PlanDetailResponse> {
            override fun onFailure(call: Call<PlanDetailResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<PlanDetailResponse>?, response: Response<PlanDetailResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestPlaceListByType(callback: Callback<PlaceListByTypeResponse>, type: String, city_id: String) {
        val request = RequestClient.INSTANCE.getLoginService().getPlacemakerByType(type, city_id)
        request.enqueue(object : Callback<PlaceListByTypeResponse> {
            override fun onFailure(call: Call<PlaceListByTypeResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<PlaceListByTypeResponse>?, response: Response<PlaceListByTypeResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestPlaceListByText(callback: Callback<PlaceListByTypeResponse>, text: String, city_id: String) {
        val request = RequestClient.INSTANCE.getLoginService().getPlacemakerByText(text, city_id)
        request.enqueue(object : Callback<PlaceListByTypeResponse> {
            override fun onFailure(call: Call<PlaceListByTypeResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<PlaceListByTypeResponse>?, response: Response<PlaceListByTypeResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestPlaceType(callback: Callback<PlaceTypeResponse>, city_id: String) {
        val request = RequestClient.INSTANCE.getLoginService().getPlacetype(city_id)
        request.enqueue(object : Callback<PlaceTypeResponse> {
            override fun onFailure(call: Call<PlaceTypeResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<PlaceTypeResponse>?, response: Response<PlaceTypeResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestPlaceByID(callback: Callback<PlaceDetailsResponse>, place_id: String) {
        val request = RequestClient.INSTANCE.getLoginService().getPlaceByID(place_id)
        request.enqueue(object : Callback<PlaceDetailsResponse> {
            override fun onFailure(call: Call<PlaceDetailsResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<PlaceDetailsResponse>?, response: Response<PlaceDetailsResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestAddPlaceComment(callback: Callback<AddPlaceCommentResponse>, citizen_id: String, place_id: String, comment: String, rating: String) {
        val request = RequestClient.INSTANCE.getLoginService().addPlaceComment(citizen_id, place_id, comment, rating)
        request.enqueue(object : Callback<AddPlaceCommentResponse> {
            override fun onFailure(call: Call<AddPlaceCommentResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<AddPlaceCommentResponse>?, response: Response<AddPlaceCommentResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestGetMyStamp(callback: Callback<MyStampResponse>, citizen_id: String) {
        val request = RequestClient.INSTANCE.getLoginService().getMyStamp(citizen_id)
        request.enqueue(object : Callback<MyStampResponse> {
            override fun onFailure(call: Call<MyStampResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<MyStampResponse>?, response: Response<MyStampResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestGetPlaceStamp(callback: Callback<StampResponse>, province_id: String, citizen_id: String) {
        val request = RequestClient.INSTANCE.getLoginService().getPlaceStamp(province_id, citizen_id)
        request.enqueue(object : Callback<StampResponse> {
            override fun onFailure(call: Call<StampResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<StampResponse>?, response: Response<StampResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun addCheckinPlace(callback: Callback<AddStampResponse>, place_id: String, citizen_id: String) {
        val request = RequestClient.INSTANCE.getLoginService().addCheckin(place_id, citizen_id)
        request.enqueue(object : Callback<AddStampResponse> {
            override fun onFailure(call: Call<AddStampResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<AddStampResponse>?, response: Response<AddStampResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestPlaceSuggestion(callback: Callback<PlaceSuggestionResponse>, city_id: String) {
        val request = RequestClient.INSTANCE.getLoginService().getPlaceSugguest(city_id)
        request.enqueue(object : Callback<PlaceSuggestionResponse> {
            override fun onFailure(call: Call<PlaceSuggestionResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<PlaceSuggestionResponse>?, response: Response<PlaceSuggestionResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    //buy api

    fun requestShopList(callback: Callback<ShopListResponse>, requestBody: ShopListRequest, bearer: String) {
        val request = RequestClient.INSTANCE.getBuyService().getShopList(applicationJson, bearer, requestBody)
        request.enqueue(object : Callback<ShopListResponse> {
            override fun onFailure(call: Call<ShopListResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<ShopListResponse>?, response: Response<ShopListResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestAddWishList(callback: Callback<AddWishListResponse>, requestBody: AddWishListRequest, bearer: String) {
        val request = RequestClient.INSTANCE.getBuyService().addWishlist(applicationJson, bearer, requestBody)
        request.enqueue(object : Callback<AddWishListResponse> {
            override fun onFailure(call: Call<AddWishListResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<AddWishListResponse>?, response: Response<AddWishListResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestProductDetail(callback: Callback<GetProductResponse>, requestBody: GetProductRequest, bearer: String) {
        val request = RequestClient.INSTANCE.getBuyService().getProduct(applicationJson, bearer, requestBody)
        request.enqueue(object : Callback<GetProductResponse> {
            override fun onFailure(call: Call<GetProductResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<GetProductResponse>?, response: Response<GetProductResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestAllOrder(callback: Callback<GetAllOrderResponse>, requestBody: GetAllOrderRequest, bearer: String) {
        val request = RequestClient.INSTANCE.getBuyService().getAllOrders(applicationJson, bearer, requestBody)
        request.enqueue(object : Callback<GetAllOrderResponse> {
            override fun onFailure(call: Call<GetAllOrderResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<GetAllOrderResponse>?, response: Response<GetAllOrderResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestOrderDetail(callback: Callback<GetOrderResponse>, requestBody: GetOrderRequest, bearer: String) {
        val request = RequestClient.INSTANCE.getBuyService().getOrders(applicationJson, bearer, requestBody)
        request.enqueue(object : Callback<GetOrderResponse> {
            override fun onFailure(call: Call<GetOrderResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<GetOrderResponse>?, response: Response<GetOrderResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestAddOrder(callback: Callback<AddOrderResponse>, requestBody: AddOrderRequest, bearer: String) {
        val request = RequestClient.INSTANCE.getBuyService().addOrder(applicationJson, bearer, requestBody)
        request.enqueue(object : Callback<AddOrderResponse> {
            override fun onFailure(call: Call<AddOrderResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<AddOrderResponse>?, response: Response<AddOrderResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestConfirmReceiveOrder(callback: Callback<ConfirmReceiveOrderResponse>, requestBody: ConfirmReceiveOrderRequest, bearer: String) {
        val request = RequestClient.INSTANCE.getBuyService().confirmReceiveOrder(applicationJson, bearer, requestBody)
        request.enqueue(object : Callback<ConfirmReceiveOrderResponse> {
            override fun onFailure(call: Call<ConfirmReceiveOrderResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<ConfirmReceiveOrderResponse>?, response: Response<ConfirmReceiveOrderResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestShopDetail(callback: Callback<GetShopResponse>, requestBody: GetShopRequest, bearer: String) {
        val request = RequestClient.INSTANCE.getBuyService().getShop(applicationJson, bearer, requestBody)
        request.enqueue(object : Callback<GetShopResponse> {
            override fun onFailure(call: Call<GetShopResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<GetShopResponse>?, response: Response<GetShopResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestShopProducts(callback: Callback<GetShopProductsResponse>, requestBody: GetShopProductsRequest, bearer: String) {
        val request = RequestClient.INSTANCE.getBuyService().getShopProducts(applicationJson, bearer, requestBody)
        request.enqueue(object : Callback<GetShopProductsResponse> {
            override fun onFailure(call: Call<GetShopProductsResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<GetShopProductsResponse>?, response: Response<GetShopProductsResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestAddress(callback: Callback<AddressResponse>, requestBody: AddressRequest, bearer: String) {
        val request = RequestClient.INSTANCE.getBuyService().getCitizenAddress(applicationJson, bearer, requestBody)
        request.enqueue(object : Callback<AddressResponse> {
            override fun onFailure(call: Call<AddressResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<AddressResponse>?, response: Response<AddressResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestAddAddress(callback: Callback<AddAddressResponse>, requestBody: AddAddressRequest, bearer: String) {
        val request = RequestClient.INSTANCE.getBuyService().addCitizenAddress(applicationJson, bearer, requestBody)
        request.enqueue(object : Callback<AddAddressResponse> {
            override fun onFailure(call: Call<AddAddressResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<AddAddressResponse>?, response: Response<AddAddressResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestRemoveAddress(callback: Callback<RemoveAddressResponse>, requestBody: RemoveAddressRequest, bearer: String) {
        val request = RequestClient.INSTANCE.getBuyService().removeCitizenAddress(applicationJson, bearer, requestBody)
        request.enqueue(object : Callback<RemoveAddressResponse> {
            override fun onFailure(call: Call<RemoveAddressResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<RemoveAddressResponse>?, response: Response<RemoveAddressResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestWeeklyBestSellerProduct(callback: Callback<GetWeeklyBestSellerResponse>, requestBody: GetWeeklyBestSellerRequest, bearer: String) {
        val request = RequestClient.INSTANCE.getBuyService().getWeeklyBestSellerProduct(applicationJson, bearer, requestBody)
        request.enqueue(object : Callback<GetWeeklyBestSellerResponse> {
            override fun onFailure(call: Call<GetWeeklyBestSellerResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<GetWeeklyBestSellerResponse>?, response: Response<GetWeeklyBestSellerResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestWeeklyPopularProduct(callback: Callback<GetWeeklyPopularResponse>, requestBody: GetWeeklyPopularRequest, bearer: String) {
        val request = RequestClient.INSTANCE.getBuyService().getWeeklyPopularProduct(applicationJson, bearer, requestBody)
        request.enqueue(object : Callback<GetWeeklyPopularResponse> {
            override fun onFailure(call: Call<GetWeeklyPopularResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<GetWeeklyPopularResponse>?, response: Response<GetWeeklyPopularResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestShopByKeyWord(callback: Callback<ShopListResponse>, requestBody: SearchShopByKeywordRequest, bearer: String) {
        val request = RequestClient.INSTANCE.getBuyService().getShopByKeyword(applicationJson, bearer, requestBody)
        request.enqueue(object : Callback<ShopListResponse> {
            override fun onFailure(call: Call<ShopListResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<ShopListResponse>?, response: Response<ShopListResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestNewProduct(callback: Callback<GetNewProductResponse>, requestBody: GetNewProductRequest, bearer: String) {
        val request = RequestClient.INSTANCE.getBuyService().getNewProduct(applicationJson, bearer, requestBody)
        request.enqueue(object : Callback<GetNewProductResponse> {
            override fun onFailure(call: Call<GetNewProductResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<GetNewProductResponse>?, response: Response<GetNewProductResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestGetCategory(callback: Callback<CateResponse>) {
        val request = RequestClient.INSTANCE.getCateService().getCategory("1")
        request.enqueue(object : Callback<CateResponse> {
            override fun onFailure(call: Call<CateResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<CateResponse>?, response: Response<CateResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestUpdateAddress(callback: Callback<UpdateAddressResponse>, requestBody: UpdateAddressRequest, bearer: String) {
        val request = RequestClient.INSTANCE.getBuyService().updateCitizenAddress(applicationJson, bearer, requestBody)
        request.enqueue(object : Callback<UpdateAddressResponse> {
            override fun onFailure(call: Call<UpdateAddressResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<UpdateAddressResponse>?, response: Response<UpdateAddressResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestInVisit(callback: Callback<CommonResponse>, requestBody: InVisitRequest) {
        val request = RequestClient.INSTANCE.getLoginService().inVisit(applicationJson, requestBody)
        request.enqueue(object : Callback<CommonResponse> {
            override fun onFailure(call: Call<CommonResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<CommonResponse>?, response: Response<CommonResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestScanner(callback: Callback<CommonResponse>, requestBody: SCannerRequest) {
        val request = RequestClient.INSTANCE.getLoginService().sCanner(applicationJson, requestBody)
        request.enqueue(object : Callback<CommonResponse> {
            override fun onFailure(call: Call<CommonResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<CommonResponse>?, response: Response<CommonResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestSendView(callback: Callback<CommonResponse>, requestBody: SendViewRequest) {
        val request = RequestClient.INSTANCE.getLoginService().sendView(applicationJson, requestBody)
        request.enqueue(object : Callback<CommonResponse> {
            override fun onFailure(call: Call<CommonResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<CommonResponse>?, response: Response<CommonResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestSendOrder(callback: Callback<CommonResponse>, requestBody: SendOrderRequest) {
        val request = RequestClient.INSTANCE.getLoginService().sendOrder(applicationJson, requestBody)
        request.enqueue(object : Callback<CommonResponse> {
            override fun onFailure(call: Call<CommonResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<CommonResponse>?, response: Response<CommonResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestPlanView(callback: Callback<CommonResponse>, requestBody: PlanViewRequest) {
        val request = RequestClient.INSTANCE.getLoginService().planView(applicationJson, requestBody)
        request.enqueue(object : Callback<CommonResponse> {
            override fun onFailure(call: Call<CommonResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<CommonResponse>?, response: Response<CommonResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestGetUnreadEmer(callback: Callback<UnreadEmerResponse>, requestBody: UnreadEmerRequest) {
        val request = RequestClient.INSTANCE.getLoginService().getUnreadEmer(applicationJson, requestBody)
        request.enqueue(object : Callback<UnreadEmerResponse> {
            override fun onFailure(call: Call<UnreadEmerResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<UnreadEmerResponse>?, response: Response<UnreadEmerResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestGetUnreadComplain(callback: Callback<UnreadComplainResponse>, requestBody: UnreadComplainRequest) {
        val request = RequestClient.INSTANCE.getLoginService().getUnreadComplain(applicationJson, requestBody)
        request.enqueue(object : Callback<UnreadComplainResponse> {
            override fun onFailure(call: Call<UnreadComplainResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<UnreadComplainResponse>?, response: Response<UnreadComplainResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestUpdateEmerdialog(callback: Callback<CommonResponse>, requestBody: UpdateEmerDialogRequest) {
        val request = RequestClient.INSTANCE.getLoginService().updateEmerdialog(applicationJson, requestBody)
        request.enqueue(object : Callback<CommonResponse> {
            override fun onFailure(call: Call<CommonResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<CommonResponse>?, response: Response<CommonResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestUpdateComplaindialog(callback: Callback<CommonResponse>, requestBody: UpdateComplainDialogRequest) {
        val request = RequestClient.INSTANCE.getLoginService().updateComplaindialog(applicationJson, requestBody)
        request.enqueue(object : Callback<CommonResponse> {
            override fun onFailure(call: Call<CommonResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<CommonResponse>?, response: Response<CommonResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestPlanFav(callback: Callback<CommonResponse>, requestBody: PlanFavRequest) {
        val request = RequestClient.INSTANCE.getLoginService().planFav(applicationJson, requestBody)
        request.enqueue(object : Callback<CommonResponse> {
            override fun onFailure(call: Call<CommonResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<CommonResponse>?, response: Response<CommonResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestPlaceFav(callback: Callback<CommonResponse>, requestBody: PlaceFavRequest) {
        val request = RequestClient.INSTANCE.getLoginService().placeFav(applicationJson, requestBody)
        request.enqueue(object : Callback<CommonResponse> {
            override fun onFailure(call: Call<CommonResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<CommonResponse>?, response: Response<CommonResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestFavPlanList(callback: Callback<FavPlanListResponse>, user_id: String) {
        val request = RequestClient.INSTANCE.getLoginService().getFavPlanList(user_id)
        request.enqueue(object : Callback<FavPlanListResponse> {
            override fun onFailure(call: Call<FavPlanListResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<FavPlanListResponse>?, response: Response<FavPlanListResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestAddFavPlan(callback: Callback<CommonResponse>, request: AddFavPlanRequest) {
        val request = RequestClient.INSTANCE.getLoginService().addFavPlan(request)
        request.enqueue(object : Callback<CommonResponse> {
            override fun onFailure(call: Call<CommonResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<CommonResponse>?, response: Response<CommonResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestDeleteFavPlan(callback: Callback<CommonResponse>, user_id: Int, request: DeleteFavPlanRequest) {
        val request = RequestClient.INSTANCE.getLoginService().deleteFavPlan(user_id, request)
        request.enqueue(object : Callback<CommonResponse> {
            override fun onFailure(call: Call<CommonResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<CommonResponse>?, response: Response<CommonResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestFavPlaceList(callback: Callback<FavPlaceListResponse>, user_id: String) {
        val request = RequestClient.INSTANCE.getLoginService().getFavPlaceList(user_id)
        request.enqueue(object : Callback<FavPlaceListResponse> {
            override fun onFailure(call: Call<FavPlaceListResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<FavPlaceListResponse>?, response: Response<FavPlaceListResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestAddFavPlace(callback: Callback<CommonResponse>, request: AddFavPlaceRequest) {
        val request = RequestClient.INSTANCE.getLoginService().addFavPlace(request)
        request.enqueue(object : Callback<CommonResponse> {
            override fun onFailure(call: Call<CommonResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<CommonResponse>?, response: Response<CommonResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }

    fun requestDeleteFavPlace(callback: Callback<CommonResponse>, user_id: Int, request: DeleteFavPlaceRequest) {
        val request = RequestClient.INSTANCE.getLoginService().deleteFavPlace(user_id, request)
        request.enqueue(object : Callback<CommonResponse> {
            override fun onFailure(call: Call<CommonResponse>?, t: Throwable?) {
                callback.onFailure(call, t)
            }

            override fun onResponse(call: Call<CommonResponse>?, response: Response<CommonResponse>?) {
                callback.onResponse(call, response)
            }

        })
    }
}