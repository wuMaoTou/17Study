package com.mt.mvvmkotlindemo.tasks

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.os.PersistableBundle
import android.support.design.widget.NavigationView
import android.support.v4.widget.DrawerLayout
import com.mt.mvvmkotlindemo.LifecycleAppCompatActivity
import com.mt.mvvmkotlindemo.R
import com.mt.mvvmkotlindemo.util.obtainViewModel
import com.mt.mvvmkotlindemo.util.setupActionBar
import kotlinx.android.synthetic.main.act_tasks.*

/**
 * Created by wuchundu on 18-2-6.
 */
class TasksActivity : LifecycleAppCompatActivity(), TaskItemNavigator, TasksNavigator {

    private lateinit var drawerLayout: DrawerLayout

    private lateinit var viewModel: TasksViewModel

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.act_tasks)

        setupActionBar(R.id.toolbar){
            setHomeAsUpIndicator(R.drawable.ic_menu)
            setDisplayHomeAsUpEnabled(true)
        }

        setupNavigationDrawer()

        findOrCreateViewFragment()

        viewModel = obtainViewModel().apply {
            openTaskEvent.observe(this@TasksActivity, Observer<String>{ taskId ->
                if (taskId != null){
                    openTaskDetails(taskId)
                }
            })
            newTaskEvent.observe(this@TasksActivity, Observer<Void> {
                this@TasksActivity.addNewTask()
            })
        }
    }

    private fun findOrCreateViewFragment() =
        supportFragmentManager.findFragmentById(R.id.contentFrame) ?: null
//                StatisticsFragment.newInstance().also{
//                    replaceFragmentInActivity(it,R.id.contentFrame)
//                }


    private fun setupNavigationDrawer() {
        drawer_layout.apply {
            setStatusBarBackground(R.color.colorPrimaryDark)
        }
        setupDrawerContent(nav_view)
    }

    private fun setupDrawerContent(navView: NavigationView) {
        navView.setNavigationItemSelectedListener { item ->
            when(item.itemId){
                R.id.list_navigation_menu_item -> {

                }

                R.id.statistics_navigation_menu_item -> {
//                    val intent = Intent(this@TasksActivity)
                }
            }

            item.isChecked = true
            drawerLayout.closeDrawers()

            true
        }

    }

    override fun addNewTask() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun openTaskDetails(taskId: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun obtainViewModel() : TasksViewModel = obtainViewModel(TasksViewModel::class.java)

}