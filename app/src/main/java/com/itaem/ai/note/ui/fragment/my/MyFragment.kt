package notecom.itaem.ai.note.ui.fragment.my

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.transition.MaterialSharedAxis
import notecom.itaem.ai.note.common.DefaultUI
import notecom.itaem.ai.note.databinding.FragmentMyBinding


class MyFragment : Fragment(),DefaultUI {
    private val binding by lazy {
        FragmentMyBinding.inflate(layoutInflater)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initView()
        return binding.root
    }

    override fun initView() {

        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z,false)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z,true)
    }

}