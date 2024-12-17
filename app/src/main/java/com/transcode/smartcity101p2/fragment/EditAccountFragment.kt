package com.transcode.smartcity101p2.fragment

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.Toast
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.R
import com.transcode.smartcity101p2.adapter.AmphueArrayAdapter
import com.transcode.smartcity101p2.adapter.ProvinceArrayAdapter
import com.transcode.smartcity101p2.adapter.TambonArrayAdapter
import com.transcode.smartcity101p2.contract.EditAccountFragmentContract
import com.transcode.smartcity101p2.dialog.LoadingDialog
import com.transcode.smartcity101p2.dialog.SelectImageDialog
import com.transcode.smartcity101p2.extension.load
import com.transcode.smartcity101p2.extension.loadCircle
import com.transcode.smartcity101p2.model.*
import com.transcode.smartcity101p2.presenter.EditAccountFragmentPresenter
import com.transcode.smartcity101p2.utils.AppUtils
import com.transcode.smartcity101p2.view.CustomAppBarLayout2
import kotlinx.android.synthetic.main.appbar_main2.view.*
import kotlinx.android.synthetic.main.fragment_edit_account.*
import java.io.ByteArrayOutputStream
import java.io.IOException

class EditAccountFragment : CoreFragment(), EditAccountFragmentContract.View {
    override fun editAccountInfoSuccess() {
        info?.let {
            val loginResponse = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
            presenter.editAccountImage(it.Email ?: "",
                    loginResponse.authority_info?.getAllToken() ?: "",
                    loginResponse.authority_info?.CityId ?: "",
                    it.CitizenId ?: "",
                    selectedImgBase64)
        }
    }

    override fun editAccountImageSuccess() {
        loadingDialog?.dismiss()
        showError(Const.MESSAGE_SUCCESS)
//        fragmentManager?.popBackStack()
//        FragmentHelper.remove(fragmentManager, this)
        activity?.apply {
            finish()
        }
    }

    override fun showError(message: String) {
        loadingDialog?.dismiss()
        context?.let {
            Toast.makeText(it, message, Toast.LENGTH_LONG).show()
        }
    }

    override fun updateCitizenInfo(data: CitizenInfoResponse.CitizenInfoResponseData) {
        loadingDialog?.dismiss()
        info = data
        val url = AppUtils.getImageUrl(data.CitizenImg)
        avarta.loadCircle(url, R.mipmap.icon_account)
        nametext.setText(data.FName)
        lastnametext.setText(data.LName)
        if (!data.Email.isNullOrEmpty()) {
            emailtext.setText(data.Email)
        }
        teltext.setText(data.Tel)
        addresstext.setText(data.Address)
        ziptext.setText(data.ZipCode)
        id_card.setText(data.IdCard)

        clear_image.setOnClickListener {
            selectedImgBase64 = null
            avarta.loadCircle(url, R.mipmap.icon_account)
            clear_image.visibility = View.GONE
        }

        val user = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        when {
            user.authority_info?.IsFb == "1" -> {
                clear_image.visibility = View.GONE
                edit_image.visibility = View.GONE
                avarta.setOnClickListener { }
                val accessToken = AccessToken.getCurrentAccessToken()
                val request = GraphRequest.newMeRequest(accessToken) { json, _ ->
                    // Application code
                    try {
                        val picture = json.getJSONObject("picture").getJSONObject("data").getString("url")
                        avarta.loadCircle(picture, R.mipmap.icon_account)
                    } catch (exception: Exception) {
                    }
                }
                val parameters = Bundle()
                parameters.putString("fields", "id,name,email,gender,birthday,picture.type(large)")
                request.parameters = parameters
                request.executeAsync()
            }
            user.authority_info?.IsFb == "3" -> {
                avarta.setOnClickListener { }
                clear_image.visibility = View.GONE
                edit_image.visibility = View.GONE
                context?.let {
                    val acct = Hawk.get<GAccount>("G_ACCOUNT")
                    avarta.loadCircle(acct?.photoUrl.toString(), R.mipmap.icon_account)
                }
            }
            else -> {
                avarta.setOnClickListener { edit_image.performClick() }
                avarta.loadCircle(url, R.mipmap.icon_account)
            }
        }

        presenter.getProvince()
    }

    override fun updateTambon(list: ArrayList<TambonResponse.TambonResponseData>) {

        if (list_tambon == null) {
            return
        } else if (list.size <= 0) {
            val header = TambonResponse.TambonResponseData()
            header.TambonName = getString(R.string.edit_acc_tambon)
            tambonList = list
            tambonList.add(0, header)
            context?.let {
                val adapter = TambonArrayAdapter(it, R.layout.simple_list_item_1, tambonList)
                list_tambon.adapter = adapter
                adapter.notifyDataSetChanged()
            }
            return
        }

        val header = TambonResponse.TambonResponseData()
        header.TambonName = getString(R.string.edit_acc_tambon)
        tambonList = list
        tambonList.add(0, header)
        list_tambon.onItemSelectedListener = null

        if (autoTambon) {
            autoTambon = false
            for (i in 0 until tambonList.size) {
                val tambon = tambonList[i]
                if (info?.TambonId == tambon.TambonId) {
                    list_tambon.post { list_tambon.setSelection(i, false) }
                    break
                }
            }
        }

        context?.let {
            val adapter = TambonArrayAdapter(it, R.layout.simple_list_item_1, tambonList)
            list_tambon.adapter = adapter
            adapter.notifyDataSetChanged()

            val listener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, id: Long) {

                }
            }

            list_tambon.onItemSelectedListener = listener
        }
    }

    override fun updateAmphue(list: ArrayList<AmphueResponse.AmphueResponseData>) {

        if (list_amphue == null) {
            return
        } else if (list.size <= 0) {
            val header = AmphueResponse.AmphueResponseData()
            header.AmphueName = getString(R.string.edit_acc_amper)
            amphueList = list
            amphueList.add(0, header)
            context?.let {
                val adapter = AmphueArrayAdapter(it, R.layout.simple_list_item_1, amphueList)
                list_amphue.adapter = adapter
                adapter.notifyDataSetChanged()
            }
            updateTambon(arrayListOf())
            return
        }

        val header = AmphueResponse.AmphueResponseData()
        header.AmphueName = getString(R.string.edit_acc_amper)
        amphueList = list
        amphueList.add(0, header)
        list_amphue.onItemSelectedListener = null

        if (autoAmphue) {
            autoAmphue = false
            for (i in 0 until amphueList.size) {
                val amphue = amphueList[i]
                if (info?.AmphueId == amphue.AmphueId) {
                    list_amphue.post { list_amphue.setSelection(i, false) }
                    break
                }
            }
        }

        context?.let {
            val adapter = AmphueArrayAdapter(it, R.layout.simple_list_item_1, amphueList)
            list_amphue.adapter = adapter
            adapter.notifyDataSetChanged()

            val listener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, id: Long) {
                    if (position == 0) {
                        updateTambon(arrayListOf())
                    } else {
                        presenter.getTambon(amphueList[position].ProvinceId
                                ?: "", amphueList[position].AmphueId ?: "")
                    }
                }
            }

            list_amphue.onItemSelectedListener = listener
        }
    }

    override fun updateProvince(list: ArrayList<ProvinceResponse.ProvinceResponseData>) {

        if (list_province == null) {
            return
        } else if (list.size <= 0) {
            val header = ProvinceResponse.ProvinceResponseData()
            header.province_name = getString(R.string.edit_acc_province)
            provinceList = list
            provinceList.add(0, header)
            context?.let {
                val adapter = ProvinceArrayAdapter(it, R.layout.simple_list_item_1, provinceList)
                list_province.adapter = adapter
                adapter.notifyDataSetChanged()
            }
            return
        }

        val header = ProvinceResponse.ProvinceResponseData()
        header.province_name = getString(R.string.edit_acc_province)
        provinceList = list
        provinceList.add(0, header)
        list_province.onItemSelectedListener = null

        if (autoProvince) {
            autoProvince = false
            for (i in 0 until provinceList.size) {
                val province = provinceList[i]
                if (info?.ProvinceId == province.province_id) {
                    list_province.post { list_province.setSelection(i, false) }
                    break
                }
            }
        }

        context?.let {
            val adapter = ProvinceArrayAdapter(it, R.layout.simple_list_item_1, provinceList)
            list_province.adapter = adapter
            adapter.notifyDataSetChanged()

            val listener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, id: Long) {
                    if (position == 0) {
                        updateAmphue(arrayListOf())
                    } else {
                        presenter.getAmphue(provinceList[position].province_id ?: "")
                    }
                }
            }

            list_province.onItemSelectedListener = listener
        }
    }

    companion object {
        fun newInstance(): EditAccountFragment {
            return EditAccountFragment().apply {
                val bundle = Bundle()
                arguments = bundle
            }
        }
    }

    lateinit var presenter: EditAccountFragmentPresenter
    private val CODE_GET_IMAGE = 500
    private val CODE_IMAGE_CAPTURE = 499
    private var selectedImgBase64: String? = null
    private var info: CitizenInfoResponse.CitizenInfoResponseData? = null
    private var autoProvince = true
    private var autoAmphue = true
    private var autoTambon = true
    var loadingDialog: LoadingDialog? = null

    var provinceList = arrayListOf<ProvinceResponse.ProvinceResponseData>()
    var amphueList = arrayListOf<AmphueResponse.AmphueResponseData>()
    var tambonList = arrayListOf<TambonResponse.TambonResponseData>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_edit_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
    }

    private fun initView() {
        val appBar = appbar as CustomAppBarLayout2
        appBar.setTitle("")
        appBar.setHeaderBackground(R.drawable.header_bg_purple)
        appBar.leftBt.setOnClickListener {
            //            fragmentManager?.popBackStack()
//            FragmentHelper.remove(fragmentManager, this)
            activity?.apply {
                finish()
            }
        }

        cancel.setOnClickListener {
            activity?.apply {
                finish()
            }
        }

        context?.let {
            loadingDialog = LoadingDialog(it)
            loadingDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            loadingDialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
            loadingDialog?.setCancelable(false)
        }

        presenter = EditAccountFragmentPresenter(this)
        loadingDialog?.show()

        val loginResponse = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)

        submit.setOnClickListener {
            val province = if (provinceList.isNotEmpty()) {
                provinceList[list_province.selectedItemPosition]
            } else {
                ProvinceResponse.ProvinceResponseData()
            }
            val amphue = if (amphueList.isNotEmpty()) {
                amphueList[list_amphue.selectedItemPosition]
            } else {
                AmphueResponse.AmphueResponseData()
            }
            val tambon = if (tambonList.isNotEmpty()) {
                tambonList[list_tambon.selectedItemPosition]
            } else {
                TambonResponse.TambonResponseData()
            }

            if (id_card.text.toString().length < 13) {
                showError(getString(R.string.edit_acc_error1))
                return@setOnClickListener
            }

            if (province.province_id == null) {
                showError(getString(R.string.edit_acc_error2))
                return@setOnClickListener
            }

            if (amphue.AmphueId == null) {
                showError(getString(R.string.edit_acc_error3))
                return@setOnClickListener
            }

            if (tambon.TambonId == null) {
                showError(getString(R.string.edit_acc_error4))
                return@setOnClickListener
            }

            if (nametext.text.toString().isNullOrEmpty()) {
                showError(getString(R.string.edit_acc_error5))
                return@setOnClickListener
            }

            if (lastnametext.text.toString().isNullOrEmpty()) {
                showError(getString(R.string.edit_acc_error6))
                return@setOnClickListener
            }

            if (id_card.text.isNullOrEmpty()) {
                showError(getString(R.string.edit_acc_error7))
                return@setOnClickListener
            }

            if (teltext.text.isNullOrEmpty()) {
                showError(getString(R.string.edit_acc_error8))
                return@setOnClickListener
            } else if (teltext.text.toString().length < 10) {
                showError(getString(R.string.edit_acc_error8))
                return@setOnClickListener
            }

            if (addresstext.text.toString().isNullOrEmpty()) {
                showError(getString(R.string.edit_acc_error9))
                return@setOnClickListener
            }

            if (ziptext.text.toString().isNullOrEmpty()) {
                showError(getString(R.string.edit_acc_error10))
                return@setOnClickListener
            }

            loadingDialog?.show()
            presenter.editAccountInfo(id_card.text.toString(),
                    info?.CitizenId ?: "",
                    nametext.text.toString(),
                    lastnametext.text.toString(),
                    teltext.text.toString(),
                    province.province_id ?: "",
                    amphue.AmphueId ?: "",
                    tambon.TambonId ?: "",
                    addresstext.text.toString(),
                    ziptext.text.toString(),
                    loginResponse.authority_info?.getAllToken() ?: "")
        }

        if (loginResponse?.authority_info?.IsFb == "1") {
            val accessToken = AccessToken.getCurrentAccessToken()
            val request = GraphRequest.newMeRequest(accessToken) { json, _ ->
                // Application code
                val email = json.getString("email")
                emailtext.setText(email.toString())
                nametext.isEnabled = false
                lastnametext.isEnabled = false
            }
            val parameters = Bundle()
            parameters.putString("fields", "id,name,email,gender,birthday")
            request.parameters = parameters
            request.executeAsync()
        } else if (loginResponse?.authority_info?.IsFb == "3") {
            context?.let {
                val acct = Hawk.get<GAccount>("G_ACCOUNT")
                emailtext.setText(acct?.email ?: "")
                nametext.isEnabled = false
                lastnametext.isEnabled = false
            }
        }

        edit_image.setOnClickListener {
            showSelectImageDialog()
        }

        Handler().postDelayed({
            presenter.getCitizenInfo()
        }, 1000)
    }

    private fun showSelectImageDialog() {
        context?.let {
            val selectImageDialog = SelectImageDialog(it)
            selectImageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            selectImageDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            selectImageDialog.show()
            selectImageDialog.setOnClickGalleryListener(View.OnClickListener {
                dispatchGetPictureIntent()
                selectImageDialog.dismiss()
            })
            selectImageDialog.setOnClickCaptureListener(View.OnClickListener {
                dispatchTakePictureIntent()
                selectImageDialog.dismiss()
            })
        }
    }

    private fun dispatchGetPictureIntent() {
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, CODE_GET_IMAGE)
    }

    private fun dispatchTakePictureIntent() {
        context?.let {
            val co = it
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                takePictureIntent.resolveActivity(co.packageManager)?.also {
                    startActivityForResult(takePictureIntent, CODE_IMAGE_CAPTURE)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode === Activity.RESULT_OK)
            when (requestCode) {
                CODE_GET_IMAGE -> {
                    val selectedImage = data?.data
                    selectedImgBase64 = try {
                        clear_image.visibility = View.VISIBLE
                        val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, selectedImage)
//                        avarta.setImageBitmap(bitmap)

                        avarta.loadCircle(bitmap)

                        val baos = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                        val b = baos.toByteArray()
                        Base64.encodeToString(b, Base64.DEFAULT)
                    } catch (e: IOException) {
                        clear_image.visibility = View.GONE
                        null
                    }
                }
                CODE_IMAGE_CAPTURE -> {
                    selectedImgBase64 = try {
                        clear_image.visibility = View.VISIBLE
                        val bitmap = data?.extras?.get("data") as Bitmap
//                        avarta.setImageBitmap(bitmap)

                        avarta.loadCircle(bitmap)

                        val baos = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                        val b = baos.toByteArray()
                        Base64.encodeToString(b, Base64.DEFAULT)
                    } catch (e: IOException) {
                        clear_image.visibility = View.GONE
                        null
                    }
                }
            }
    }

}