package notecom.itaem.ai.note.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class CommonViewModelFactory<V:ViewModel,P>(
    private val param:P,
    private val creator:(P)->V
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return creator(param) as T
    }
}