package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.database.AsteroidsFilter
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import com.udacity.asteroidradar.repository.FeedStatus

class MainFragment : Fragment(), MenuProvider {

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner

        binding.viewModel = viewModel


        binding.asteroidRecycler.adapter = AsteroidListAdapter(AsteroidListAdapter.AsteroidClickListener {
            findNavController().navigate(MainFragmentDirections.actionShowDetail(it))
        })

        // observe to show spinner
        viewModel.feedStatus.observe(viewLifecycleOwner) { feedStatus ->
            when (feedStatus) {
                        FeedStatus.LOADING -> {
                    binding.statusLoadingWheel.visibility = View.VISIBLE
                }
                        FeedStatus.ERROR -> {
                    binding.statusLoadingWheel.visibility = View.GONE
                }
                        FeedStatus.LOADED -> {
                    binding.statusLoadingWheel.visibility = View.GONE
                }
            }
        }
        // setup adapter
        viewModel.asteroids.observe(viewLifecycleOwner) { data ->
            val adapter = binding.asteroidRecycler.adapter as AsteroidListAdapter
            adapter.submitList(data)
            if(data.isNotEmpty()) {
                viewModel.updateFeedStatus(FeedStatus.LOADED)
            }
        }


        // setup menu
        val menuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        return binding.root
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.main_overflow_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.show_week_asteroids -> viewModel.getAsteroids(AsteroidsFilter.SHOW_WEEK_ASTEROIDS)
            R.id.show_today_asteroids -> viewModel.getAsteroids(AsteroidsFilter.SHOW_TODAY_ASTEROIDS)
            R.id.show_saved_asteroids -> viewModel.getAsteroids(AsteroidsFilter.SHOW_SAVED_ASTEROIDS)
        }
        return true
    }
}
