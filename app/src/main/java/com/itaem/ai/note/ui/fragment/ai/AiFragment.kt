package notecom.itaem.ai.note.ui.fragment.ai

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.itaem.markdown.data.api.params.Message
import kotlinx.coroutines.launch
import notecom.itaem.ai.note.R
import notecom.itaem.ai.note.common.CommonRVAdapter
import notecom.itaem.ai.note.common.DefaultUI
import notecom.itaem.ai.note.databinding.FragmentAiBinding
import notecom.itaem.ai.note.model.entity.Msg
import notecom.itaem.ai.note.util.ContainerTransform.getFragmentContainerTransform
import notecom.itaem.ai.note.util.Edge2EdgeUtil.addNavigationBarPadding
import notecom.itaem.ai.note.util.Edge2EdgeUtil.softInputAdjustResize
import notecom.itaem.ai.note.viewmodel.factory.AiFragmentViewModel

class AiFragment : Fragment(),DefaultUI {

    private val binding by lazy {
        FragmentAiBinding.inflate(layoutInflater)
    }
    private val args:AiFragmentArgs by navArgs()
    private val msgS:MutableList<Msg> = mutableListOf()
    private val viewModel:AiFragmentViewModel by viewModels()
    private val handler = Handler(Looper.getMainLooper())
    var typingRunnable:Runnable? = null
    private val chatRVAdapter by lazy {
        CommonRVAdapter(
            items = msgS,
            layoutId = R.layout.chat_item){msg ->
            val leftLayout = itemView.findViewById<LinearLayout>(R.id.receive_msg_layout)
            val leftText = itemView.findViewById<TextView>(R.id.receive_text)
            val rightLayout = itemView.findViewById<LinearLayout>(R.id.send_msg_layout)
            val rightText = itemView.findViewById<TextView>(R.id.send_text)
            when(msg.type){
                Msg.TYPE_RECEIVED ->{
                    leftLayout.visibility = View.VISIBLE
                    msg.apply {
                        if (msg.isAnimated){
                            leftText.text = content
                        }else{
                            val delay = 50L
                            handler.removeCallbacksAndMessages(null)
                            for(i in 1 .. content.length){
                                typingRunnable = Runnable {
                                    leftText.setText(content.substring(0, i))
                                }
                                handler.postDelayed(typingRunnable!!, (delay*i))
                            }
                            isAnimated = true
                        }
                    }
                    rightLayout.visibility = View.GONE
                    //这里判断
                }
                Msg.TYPE_SENT->{
                    rightText.visibility = View.VISIBLE
                    rightText.text = msg.content
                    leftText.visibility = View.GONE
                }
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.chatRV.apply {
            adapter = chatRVAdapter
            layoutManager = LinearLayoutManager(requireActivity()).apply {
                orientation = LinearLayoutManager.VERTICAL
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initView()
        observeVM()
        return binding.root
    }

    override fun initView() {
        binding.root.transitionName = args.transitionName
        val containerTransform = getFragmentContainerTransform(R.id.fragment_container,350)
        sharedElementEnterTransition = containerTransform
        sharedElementReturnTransition = containerTransform
        binding.aiBottomLayout.addNavigationBarPadding(false)
        binding.root.softInputAdjustResize()

        binding.imgBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.sendToAiButton.setOnClickListener {
            val content = binding.aiTextField.text.toString()
            msgS.add(Msg(content,Msg.TYPE_SENT))
            chatRVAdapter.apply {
                notifyItemInserted(itemCount-1)
            }
            typingRunnable?.let { handler.removeCallbacks(it) }
            lifecycleScope.launch {
                viewModel.apply {
                    askAI(Message("user",content))
                }
            }
            binding.aiTextField.setText("")
        }

    }

    private fun EditText.clearEditFocus(){
        if (this.isFocused){
            this.clearFocus()
            val imm = ContextCompat.getSystemService(requireActivity(), InputMethodManager::class.java)
            imm?.hideSoftInputFromWindow(this.windowToken, InputMethodManager.RESULT_UNCHANGED_SHOWN)
            return
        }
    }

    private fun observeVM(){
        viewModel.chatMessages.observe(viewLifecycleOwner
        ) {
            it.apply {
                if (it.size>0)
                    msgS.add(Msg(it[it.size-1].content,Msg.TYPE_RECEIVED))
            }
            chatRVAdapter.apply {
                notifyItemInserted(itemCount-1)
            }
        }
    }
}