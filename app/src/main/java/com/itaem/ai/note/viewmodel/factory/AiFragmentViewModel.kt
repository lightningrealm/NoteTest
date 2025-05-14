package notecom.itaem.ai.note.viewmodel.factory

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itaem.markdown.data.api.ApiRepo
import com.itaem.markdown.data.api.params.AIMessage
import com.itaem.markdown.data.api.params.Message
import kotlinx.coroutines.launch
import java.io.IOException

class AiFragmentViewModel: ViewModel() {
    val chatMessages = MutableLiveData<MutableList<Message>>(mutableListOf())

    fun add2chatMessages(message: Message){
        chatMessages.value?.let{
            it.add(message)
            chatMessages.value = it
        }
    }

    fun askAI(message: Message){
        //让ai结合上下文
        chatMessages.value?.let {
            viewModelScope.launch {
                try {
                    val msg = ApiRepo.getAIResponse(
                        AIMessage(messages = it.apply {
                            add(message)
                        })
                    ).choices[0].message
                    add2chatMessages(msg)
                }catch (e: IOException){
                    e.printStackTrace()
                    chatMessages.value = it.apply {
                        add(Message("system","网络或接口异常"))
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                    chatMessages.value = it.apply {
                        add(Message("system","未知异常，请查看日志"))
                    }
                }
            }

        }
    }
}