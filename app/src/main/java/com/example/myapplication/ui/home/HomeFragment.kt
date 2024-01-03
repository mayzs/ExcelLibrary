package com.example.myapplication.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.AssetManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.TestBean
import com.example.myapplication.databinding.FragmentHomeBinding
import com.example.myapplication.utils.AppUtils
import com.example.myapplication.utils.ParamUtils
import com.mayz.excellibrary.ExcelManager
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.*
import java.lang.ref.WeakReference

class HomeFragment : Fragment() {
    //private var printManager: PrintManager? = null
//    private var myHandler: MyHandler? = null
    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

//        var printManager = PrintManager(activity, myHandler)
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        activity?.let { copyAssets(it) }
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        val textView: TextView = binding.textHome
//        homeViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })
        val fromExcel = ExcelManager.fromExcel(
            File(activity?.filesDir?.absolutePath + File.separator + "12345.xlsx"),
            TestBean::class.java
        )

        binding.textHome.setOnClickListener {
            GlobalScope.launch {
                if (fromExcel != null) {
                    //val testBean = fromExcel[0]
                    for (testBean in fromExcel) {
                        Log.i("tag", "===123==" + testBean.address)

                        //PrintService.disConnect()
                    }
                }
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun copyAssets(context: Context) {
        val assetManager: AssetManager = context.assets
        val files: Array<String>?
        try {
            files = assetManager.list("")

            if (files != null) for (filename in files) {
                var inputStream: InputStream? = null
                var out: OutputStream? = null
                try {
                    inputStream = assetManager.open(filename)
                    val outFile = File(context.filesDir.absolutePath, filename)
                    out = FileOutputStream(outFile)
                    copyFile(inputStream, out)
                } catch (e: IOException) {
                    Log.e("tag", "Failed to copy asset file: $filename", e)
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                    if (out != null) {
                        try {
                            out.close()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
            }

        } catch (e: IOException) {
            Log.e("tag", "Failed to get asset file list.", e)
        }

    }

    @Throws(IOException::class)
    private fun copyFile(inputStream: InputStream, out: OutputStream) {
        val buffer = ByteArray(1024)
        var read: Int?
        while (inputStream.read(buffer).also { read = it } != -1) {
            read?.let { out.write(buffer, 0, it) }
        }
    }


    private class MyHandler(activity: HomeFragment) : Handler(Looper.myLooper()!!) {
        private var mActivity: WeakReference<HomeFragment> = WeakReference(activity)

        @SuppressLint("NewApi")
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val homeFragment = mActivity.get()
            when (msg.what) {
                ParamUtils.LINK_SUCCESS -> {

                    Toast.makeText(homeFragment?.context, "打印机连接成功", Toast.LENGTH_SHORT)
                        .show()
                }

                ParamUtils.LINK_FAILURE -> {
                    Toast.makeText(
                        homeFragment?.context,
                        "打印机连接失败，请配置打印机IP并打开无线网选项连接同一网络",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                ParamUtils.PRINT_FAILURE -> {
                    Toast.makeText(homeFragment?.context, "打印失败", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @SuppressLint("HardwareIds")
    fun getAndroidID(): String {
        val id = Settings.Secure.getString(
            AppUtils.getContext().contentResolver,
            Settings.Secure.ANDROID_ID
        )
        return if ("9774d56d682e549c" == id) "" else id ?: ""
    }
}


