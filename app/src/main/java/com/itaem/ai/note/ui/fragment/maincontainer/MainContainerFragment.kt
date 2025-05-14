package notecom.itaem.ai.note.ui.fragment.maincontainer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import notecom.itaem.ai.note.R
import notecom.itaem.ai.note.common.DefaultUI
import notecom.itaem.ai.note.databinding.FragmentMainContainerBinding
import notecom.itaem.ai.note.ui.fragment.editnote.EditNoteFragment
import notecom.itaem.ai.note.util.Edge2EdgeUtil.enablePadding

class MainContainerFragment : Fragment(),DefaultUI {
    private val binding by lazy {
        FragmentMainContainerBinding.inflate(layoutInflater)
    }
    private val navController by lazy {
        Navigation.findNavController(binding.fragmentContainer)
    }
    private val navOptions by lazy {
        NavOptions.Builder()
            .setLaunchSingleTop(true)
            .setPopUpTo(
                navController.graph.startDestinationId,
                inclusive = false,
                saveState = true
            )
            .build()
    }
    val parentNavCtrl by lazy{
        requireActivity().findNavController(R.id.fragment_container)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        initView()

        return binding.root
    }

    override fun initView() {

        binding.bottomNav.enablePadding(0,0,0)
        binding.bottomNav.viewTreeObserver.addOnGlobalLayoutListener {
            binding.fragmentContainer.setPadding(0,0,0,binding.bottomNav.height)
        }
        binding.fabCenter.setOnClickListener {
            binding.fabCenter.transitionName = EditNoteFragment::class.java.name
            val direction = MainContainerFragmentDirections.actionMainContainerFragmentToAddNoteFragment(
                EditNoteFragment::class.java.name
            )
            parentNavCtrl.navigate(
                direction,
                FragmentNavigatorExtras(binding.fabCenter to EditNoteFragment::class.java.name)
            )
        }

        binding.bottomNav.setOnItemSelectedListener {
            val currentDestination = navController.currentDestination
            when(it.itemId){
                R.id.nav_note->{
                    if (currentDestination?.id!=R.id.noteListFragment)
                        navController.navigate(R.id.noteListFragment,null,navOptions)
                }
                R.id.nav_favor->{
                    if (currentDestination?.id!=R.id.favorFolderFragment)
                        navController.navigate(R.id.favorFolderFragment,null,navOptions)
                }
                R.id.nav_setting->{
                    if (currentDestination?.id!=R.id.settingFragment)
                        navController.navigate(R.id.settingFragment)
                }
                R.id.nav_my->{
                    if (currentDestination?.id!=R.id.myFragment)
                        navController.navigate(R.id.myFragment)
                }
            }
            true
        }
    }

}