package com.sid1723431.happytimes.ui.home

import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.sid1723431.happytimes.R
import com.sid1723431.happytimes.SortOrder
import com.sid1723431.happytimes.data.Habit
import com.sid1723431.happytimes.databinding.FragmentHomeBinding
import com.sid1723431.happytimes.ui.ItemAdapter
import com.sid1723431.happytimes.util.exhaustive
import com.sid1723431.happytimes.util.onQueryTextChanged
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home),
    ItemAdapter.OnItemClickListener {

    private val viewModel : HomeViewModel by viewModels()
    private lateinit var searchView: SearchView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val itemAdapter = ItemAdapter(this)
        val binding = FragmentHomeBinding.bind(view)

        binding.apply {
            recyclerViewTasks.apply {
                adapter = itemAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }
            ItemTouchHelper(object: ItemTouchHelper.SimpleCallback(0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
                override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                   //drag and drop/ up and down
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val task = itemAdapter.currentList[viewHolder.adapterPosition]
                    viewModel.onTaskSwiped(task)
                }
            }).attachToRecyclerView(recyclerViewTasks)

            addHabitFab.setOnClickListener {
                viewModel.onAddNewHabitClick()
            }
        }

        setFragmentResultListener("add_edit_request"){_, bundle ->
            val result = bundle.getInt("add_edit_result")
            viewModel.onAddEditResult(result)
        }

        viewModel.habits.observe(viewLifecycleOwner, Observer {
            itemAdapter.submitList(it)
        })
        //once fragment is put in the background, no need to listen for events

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.taskEvent.collect{ event ->
                when(event){
                    is HomeViewModel.TaskEvent.ShowUndoDeleteTaskManager -> {
                        Snackbar.make(requireView(), "Habit deleted", Snackbar.LENGTH_LONG)
                                .setAction("UNDO"){
                                    viewModel.onUndoDeleteClick(event.habit)
                                }.show()
                    }
                    is HomeViewModel.TaskEvent.NavigateToAddHabitScreen -> {
                        val action =
                            HomeFragmentDirections.actionHomeFragmentToAddEditFragment(
                                null,
                                "New Habit"
                            )
                        findNavController().navigate(action)
                    }
                    is HomeViewModel.TaskEvent.NavigateToEditHabitScreen -> {
                        val action =
                            HomeFragmentDirections.actionHomeFragmentToAddEditFragment(
                                event.habit,
                                "Edit Habit"
                            )
                        findNavController().navigate(action)
                    }
                    is HomeViewModel.TaskEvent.ShowTaskSavedConfirmation -> {
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_SHORT).show()
                    }
                    HomeViewModel.TaskEvent.NavigateToDeleteAllCompletedScreen -> {
                        val action =
                            HomeFragmentDirections.actionGlobalDeleteAllCompletedDialogFragment()
                        findNavController().navigate(action)
                    }

                }.exhaustive
            }
        }

        setHasOptionsMenu(true)
    }


    override fun onItemClick(habit: Habit) {
        viewModel.onHabitSelected(habit)
    }

    override fun onCheckBoxClick(habit: Habit, isChecked: Boolean) {
        viewModel.onHabitCheckedChanged(habit, isChecked)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.tasks, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as androidx.appcompat.widget.SearchView

        val pendingQuery = viewModel.searchQuery.value
        if (pendingQuery != null && pendingQuery.isNotEmpty()) {
            searchItem.expandActionView()
            searchView.setQuery(pendingQuery, false)
        }

        searchView.onQueryTextChanged {
            viewModel.searchQuery.value = it
        }

        viewLifecycleOwner.lifecycleScope.launch {
            menu.findItem(R.id.action_hide_completed_tasks).isChecked =
                    viewModel.preferencesFlow.first().hideCompleted
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
       return when(item.itemId){
            R.id.action_sort_by_name -> {
                viewModel.onSortOrderSelected(SortOrder.BY_NAME)
                true
            }
            R.id.action_sort_by_date_created ->{
                viewModel.onSortOrderSelected(SortOrder.BY_DATE)
                true
            }
           R.id.action_hide_completed_tasks ->{
               item.isChecked = !item.isChecked
               viewModel.onHideCompletedClick(item.isChecked)
               true
           }
           R.id.action_delete_all_completed_tasks ->{
                viewModel.onDeleteAllCompletedClick()
               true
           }
           R.id.action_logout ->{
               viewModel.logoutClick()
               true
           }
           else -> super.onOptionsItemSelected(item)
        }
    }






}


