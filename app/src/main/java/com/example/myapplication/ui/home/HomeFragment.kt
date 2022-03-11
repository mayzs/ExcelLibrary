package com.example.myapplication.ui.home

import android.content.Context
import android.content.res.AssetManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.TestBean
import com.example.myapplication.databinding.FragmentHomeBinding
import com.mayz.excellibrary.ExcelManager
import java.io.*

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        activity?.let { copyAssets(it) }
        textView.setOnClickListener {
            //showAgreementDialog()
            //startActivity(Intent(context, Test2Activity::class.java))

//            val fromExcel = ExcelManager2.getInstance().fromExcel(
//                File(activity?.filesDir?.absolutePath + File.separator + "1234.xls"),
//                TestBean::class.java
//            )
//            if (fromExcel!=null)
//            for (testBean in fromExcel) {
//                Log.i("tag","====123==="+testBean.name+"===="+testBean.department+"===="+testBean.macid)
//            }
            var list= ArrayList<TestBean>()
            var testBean=TestBean()
            testBean.name="马仨"
            testBean.department="哈哈哈"
            testBean.macid="3424234234324"
            for (i in 1..100){
                list.add(testBean)
            }
            //var sss= ExcelManager.getInstance().toExcel(activity?.filesDir?.absolutePath + File.separator + "1111.xlsx",list)
            //Log.i("TAG", "onCreateView: ===="+sss)
            val fromExcel = ExcelManager.fromExcel(
                File(activity?.filesDir?.absolutePath + File.separator + "1234.xls"),
                TestBean::class.java
            )
            if (fromExcel != null) {
                for (testBean in fromExcel){
                    Log.i("tag","====123==="+testBean.name+"===="+testBean.department+"===="+testBean.macid)
                }
            }
        }
        return root
    }
    private fun showAgreementDialog() {
        context?.let {

        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    fun copyAssets(context: Context) {
        val assetManager: AssetManager = context.assets
        var files: Array<String>? = null
        try {
            files = assetManager.list("")

            if (files != null) for (filename in files) {
                var `in`: InputStream? = null
                var out: OutputStream? = null
                try {
                    `in` = assetManager.open(filename)
                    val outFile = File(context.filesDir.absolutePath, filename)
                    out = FileOutputStream(outFile)
                    copyFile(`in`, out)
                } catch (e: IOException) {
                    Log.e("tag", "Failed to copy asset file: $filename", e)
                } finally {
                    if (`in` != null) {
                        try {
                            `in`.close()
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
    private fun copyFile(`in`: InputStream?, out: OutputStream) {
        val buffer = ByteArray(1024)
        var read: Int? = null
        while (`in`?.read(buffer).also({ read = it!! }) != -1) {
            read?.let { out.write(buffer, 0, it) }
        }
    }
}


