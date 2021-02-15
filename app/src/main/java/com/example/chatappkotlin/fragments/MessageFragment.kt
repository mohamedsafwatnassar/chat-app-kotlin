package com.example.chatappkotlin.fragments

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AlertDialogLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.chatappkotlin.Adapter.MessageAdapter
import com.example.chatappkotlin.R
import com.example.chatappkotlin.database.model.MessageModel
import com.example.chatappkotlin.database.model.UserModel
import com.example.chatappkotlin.viewModel.ChatViewModel
import com.fxn.pix.Options
import com.fxn.pix.Pix
import com.fxn.utility.PermUtil
import com.google.android.gms.tasks.Task
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.iceteck.silicompressorr.SiliCompressor
import com.orhanobut.hawk.Hawk
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_dash_board.*
import kotlinx.android.synthetic.main.fragment_message.*
import kotlinx.android.synthetic.main.fragment_message.view.*
import kotlinx.android.synthetic.main.progress_dialog.view.*
import java.io.File

class MessageFragment : Fragment() {

    private var clickedUser: UserModel? = Hawk.get("ClickedUser")
    private var currentUser: UserModel? = Hawk.get("CurrentUser")

    var chatViewModel: ChatViewModel? = null

    private var chatId: String? = null

    var bundle = Bundle()
    var fragment: Fragment? = null

    private lateinit var messageAdapter: MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // initialize ViewModel
        //chatViewModel = ViewModelProvider(this).get(ChatViewModel::class.java)

        // initialize ViewModel
        chatViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(activity!!.application)
            .create(ChatViewModel::class.java)

        // get bundle (chatId) from chatAdapter when click item of chatAdapter
        if (arguments != null)
            chatId = arguments!!.getString("chatId")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_message, container, false)

        activity!!.bottomChip.visibility = View.GONE

        // get user status in case online and typing
        chatViewModel!!.getUserStatus()

        // handle chatId
        if (chatId != null) {

            // read messages when click of item in chat fragment and can make send message
            chatViewModel!!.readMessages(chatId!!)
        }

        // get data from view model
        subscribeToLiveData(view)

        // set Data in XML of fragment
        setData(view)

        return view
    }

    private fun subscribeToLiveData(view: View?) {

        // send message and get this messages
        chatViewModel!!.messsageMutable.observe(viewLifecycleOwner, Observer {
            view!!.messageRecyclerView?.apply {
                if (it != null) {
                    Log.d("MessageAdapter", "" + it.size)

                    layoutManager = LinearLayoutManager(context)
                    (layoutManager as LinearLayoutManager).stackFromEnd = true
                    setHasFixedSize(true)
                    messageAdapter = MessageAdapter(it)
                    adapter = messageAdapter

                }
            }
        })

        // handle online status of user
        chatViewModel!!.onlineStatus.observe(viewLifecycleOwner, {
            if (it != null) {
                if (it.equals("online")) {
                    view!!.imgOnlineStatus.setImageResource(R.drawable.ic_online)
                } else {
                    view!!.imgOnlineStatus.setImageResource(R.drawable.ic_offline)
                }
            }
        })

        // handle typing status of user
        chatViewModel!!.typingStatus.observe(viewLifecycleOwner, {
            if (it != null) {
                if (it == currentUser!!.uID) {
                    view!!.lottieAnimation.visibility = View.VISIBLE
                    lottieAnimation.playAnimation()
                    view.lottieAnimation2.visibility = View.VISIBLE
                    lottieAnimation.playAnimation()
                } else {
                    view!!.lottieAnimation.visibility = View.GONE
                    lottieAnimation.cancelAnimation()
                    view.lottieAnimation2.visibility = View.GONE
                    lottieAnimation.cancelAnimation()

                }
            }
        })
    }

    private fun setData(view: View?) {
        clickedUser = Hawk.get("ClickedUser")

        val image: Uri = Uri.parse(clickedUser!!.image)
        Glide.with(view!!.context).load(image).into(view.msgImage)
        view.msgUserName.text = clickedUser!!.name

        // click of send to send message
        view.btnSend.setOnClickListener {
            val message = view.msgText.text.toString()
            if (message.isEmpty()) {
                Toast.makeText(context, "Enter Message", Toast.LENGTH_SHORT).show()

            } else {
                chatViewModel!!.sendMessage(message, "text")
                chatViewModel!!.getToken(message, "text")
                view.msgText.text = null
            }
        }

        view.msgBack.setOnClickListener {
            activity!!.onBackPressed()
        }

        view.msgInfo.setOnClickListener {
            val activity = it.context as AppCompatActivity

            val bundle = Bundle()
            bundle.putString("userId", clickedUser!!.uID)

            val toFragment: Fragment = UserInfoFragment()
            toFragment.arguments = bundle
            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.dashboardContainer, toFragment)
                .addToBackStack(null)
                .commit()
        }

        view.msgImage.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.dashboardContainer, ImageViewFragment())
                .addToBackStack("image")
                .commit()
        }

        view.msgText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().isEmpty()) {
                    chatViewModel!!.typingStatus("false")
                } else {
                    chatViewModel!!.typingStatus(clickedUser!!.uID)
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        view.btnDataSend.setOnClickListener {
            pickImage()
        }

    }

    private fun pickImage() {
        val options: Options = Options.init()
            .setRequestCode(100)
            .setCount(5)
            .setFrontfacing(false)
            .setSpanCount(4)
            .setExcludeVideos(true)
            .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)
            .setPath("/Chat Me/Media/Sent")

        Pix.start(this, options)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {

        when (requestCode) {
            PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS -> {

                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickImage()
                } else {
                    Toast.makeText(
                        context, "Approve permissions to open Pix ImagePicker",
                        Toast.LENGTH_LONG
                    ).show()
                }

                return
            }
        }
    }

    private fun uploadImage(fileName: String) {
        val pd = ProgressDialog(context)
        pd.setMessage("Sending")
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        pd.show()

        val storageReference = FirebaseStorage.getInstance()
            .getReference(chatId + "/Media/Images/" + currentUser!!.uID + "/" + System.currentTimeMillis())

        val uri = Uri.fromFile(File(fileName))

        storageReference.putFile(uri).addOnSuccessListener { taskSnapshot ->
            val task = taskSnapshot.storage.downloadUrl
            task.addOnCompleteListener { uri: Task<Uri> ->
                if (uri.isSuccessful) {
                    val path = uri.result.toString()

                    chatViewModel!!.sendMessage(path, "image")
                    chatViewModel!!.getToken(path, "image")
                    pd.dismiss()

                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == 100) {

            val returnValue = data?.getStringArrayListExtra(Pix.IMAGE_RESULTS)

            for (a in returnValue!!.indices) {
                uploadImage(returnValue[a])
            }

            //val hisId = clickedUser!!.uID

            /*val intent = Intent(activity, SendMediaService::class.java)
            intent.putExtra("hisID", hisId)
            intent.putExtra("chatID", chatId)
            intent.putStringArrayListExtra("media", returnValue)

            /*if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
                activity!!.startForegroundService(intent)
            else
                activity!!.startService(intent)*/*/
        }

    }


}