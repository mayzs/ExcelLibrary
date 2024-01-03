package com.example.myapplication.ui.notifications

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.database.DbManager
import com.example.myapplication.database.dao.UserDao
import com.example.myapplication.database.entity.User
import com.example.myapplication.databinding.FragmentNotificationsBinding
import com.google.gson.Gson

class NotificationsFragment : Fragment() {

    private lateinit var notificationsViewModel: NotificationsViewModel
    private var _binding: FragmentNotificationsBinding? = null
    var userDao: UserDao? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        userDao= activity?.let { DbManager.initDB(it).userDao() }
        notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        val textView: TextView = binding.textNotifications
//        notificationsViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })
        binding.text.setContent("打哈哈断电水电费哈达哈发的说法大红色花多少按时哈市按时暗红色哈发的说法大红色花多少按时哈市按时暗红色哈发的说法大红色花多少按时哈市按时暗红色的哈市后发回复哈哈发发哈返回发发发发发")
        binding.inset.setOnClickListener {
            var user=User(aliasName = "张三")
            userDao?.addUser(user)
        }
        binding.select.setOnClickListener {
            val queryAllUser = userDao?.queryAllUser()
            Log.i("tag","===123=="+Gson().toJson(queryAllUser))
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}