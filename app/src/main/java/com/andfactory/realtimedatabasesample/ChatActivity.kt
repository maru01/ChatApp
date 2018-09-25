package com.andfactory.realtimedatabasesample

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.andfactory.realtimedatabasesample.databinding.ActivityChatBinding
import com.andfactory.realtimedatabasesample.databinding.ItemChatListBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatActivity : AppCompatActivity() {

    lateinit var chatListAdapter: ChatListAdapter

    companion object {
        private const val KEY_USER_NAME = "key_user_name"
        fun start(context: Context, userName: String) {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra(KEY_USER_NAME, userName)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<ActivityChatBinding>(this, R.layout.activity_chat)

        val userName = intent.getStringExtra(KEY_USER_NAME)

        binding.userName.text = getString(R.string.user_name, userName)

        chatListAdapter = ChatListAdapter(this@ChatActivity, userName)
        binding.recyclerView.apply {
            adapter = chatListAdapter
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        }

        val fdb = FirebaseDatabase.getInstance()
        val ref = fdb.getReference("mychat")
        ref.removeValue()
        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                val chatData = p0.getValue<ChatData>(ChatData::class.java)
                chatData?: return
                chatListAdapter.apply {
                    chatList.add(chatData)
                    notifyItemInserted(chatList.size - 1)
                }
            }
        })
        binding.sendButton.setOnClickListener {
            val chatData = ChatData(
                    chatListAdapter.chatList.size.toLong(),
                    userName,
                    binding.sendWordEdit.text.toString())
            ref.setValue(chatData)
            binding.sendWordEdit.text.clear()
        }
    }

    class ChatListAdapter(private val context: Context,
                          private val user: String)
        : RecyclerView.Adapter<ChatListAdapter.ViewHolder>() {

        val chatList = ArrayList<ChatData?>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                    DataBindingUtil.inflate(
                            LayoutInflater.from(context),
                            R.layout.item_chat_list,
                            parent,
                            false)
            )
        }

        override fun getItemCount() = chatList.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val chat = chatList[position]
            chat?: return
            holder.binding.apply {
                chatText.apply {
                    if (chat.isMyChat(user)) {
                        setTextColor(Color.BLACK)
                        gravity = Gravity.END
                        userName.visibility = View.GONE
                    } else {
                        setTextColor(Color.RED)
                        gravity = Gravity.START
                        userName.apply {
                            text = chat.userName
                            setTextColor(Color.RED)
                            gravity = Gravity.START
                        }
                    }
                    text = chat.text
                }
            }
        }

        class ViewHolder(val binding: ItemChatListBinding)
            : RecyclerView.ViewHolder(binding.root)
    }
}