package com.krishe.govern

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.krishe.govern.camera.MainViewModel
import com.krishe.govern.databinding.ActivityMainBinding
import com.krishe.govern.utils.BaseActivity
import com.krishe.govern.views.home.HomeFragment

class MainActivity : BaseActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var viewModel: MainViewModel
    lateinit var fragment: Fragment

    companion object {
        val fragmentSetter: MutableLiveData<Fragment> = MutableLiveData()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@MainActivity, R.layout.activity_main)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        fragment = HomeFragment()
        replaceFragment(fragment)

//        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
//        val navController = navHostFragment.navController
//        navController.

        fragmentSetter.observe(this, Observer {
            if (fragment != it) {
                replaceFragment(it)
            }
        })

    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 1) {
            supportFragmentManager.popBackStack()
            super.onBackPressed();
            //finish()
        } else if (supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.popBackStack()
        }
    }

    // Extension function to replace fragment
    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.host, fragment)
        transaction.addToBackStack(null)
        transaction.setCustomAnimations(
            R.anim.slide_in,
            R.anim.fade_out,
            R.anim.fade_in,
            R.anim.slide_out
        )
        transaction.commit()
    }

}