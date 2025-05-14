package notecom.itaem.ai.note.ui.activity.main

import android.os.Bundle
import android.view.KeyEvent
import android.view.Window
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import notecom.itaem.ai.note.R
import notecom.itaem.ai.note.common.DefaultUI
import notecom.itaem.ai.note.databinding.ActivityMainBinding
import notecom.itaem.ai.note.util.Edge2EdgeUtil.enablePadding

class MainActivity : AppCompatActivity(),DefaultUI {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val navController by lazy {
        Navigation.findNavController(binding.fragmentContainer)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        window.sharedElementsUseOverlay = false
        setExitSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        initView()
    }

    override fun initView() {
        binding.fragmentContainer.enablePadding(left = 0, right = 0, bottom = 0)
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode== KeyEvent.KEYCODE_BACK) {
            when (navController.currentDestination?.id) {
                R.id.addNoteFragment -> {
                    navController.popBackStack()
                    return false
                }
                R.id.aiFragment->{
                    navController.popBackStack()
                    return false
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}