package com.me.hatem.a03_kt_note.Controller

import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.SearchView
import com.me.hatem.a03_kt_note.Adapter.NoteAdapter
import com.me.hatem.a03_kt_note.Model.Note
import com.me.hatem.a03_kt_note.R
import com.me.hatem.a03_kt_note.Services.DBManager
import com.me.hatem.a03_kt_note.Utilises.NOTE_MODE
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), FloatingActionMenu.MenuStateChangeListener {


    lateinit var noteAdapter: NoteAdapter
    var notes = ArrayList<Note>()
    lateinit var dbManager: DBManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ////////////////////////////////////
        dbManager = DBManager(this)
        notes = dbManager.getNotes() //to select all
        noteAdapter = NoteAdapter(this, notes)
        noteListView.adapter = noteAdapter

        fabCreation()
        searchViewSetup()

    }
//splashActivity
    private fun searchViewSetup() {
        //noteSearchView.setOnClickListener { actionMenu.close(true) }
        noteSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String): Boolean {
                notes.clear()
                if (query.isEmpty())  notes.addAll(dbManager.getNotes("%"))
                else notes.addAll(dbManager.getNotes("%$query%")) //search
                noteAdapter.notifyDataSetChanged()
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                notes.clear()
                if (newText.isEmpty())  notes.addAll(dbManager.getNotes("%"))
                else notes.addAll(dbManager.getNotes("%$newText%")) //search
                noteAdapter.notifyDataSetChanged()
                return true
            }

        })
    }

    override fun onResume() {
        notes.clear()
        notes.addAll(dbManager.getNotes()) //to select all
        println(notes)
        noteAdapter.notifyDataSetChanged()

        super.onResume()
    }

    fun fabCreation() {
        // add menu
        // search action
        val itemBuilder: SubActionButton.Builder = SubActionButton.Builder(this)
        val params: FrameLayout.LayoutParams // set the size of each sub action button

        val screenDensity = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            resources.configuration.densityDpi
        else 320
        val subMenuRadius: Int
        if (screenDensity < 420) {
            params = FrameLayout.LayoutParams(110, 110)
            subMenuRadius = 180
        } else {
            params = FrameLayout.LayoutParams(150, 150)
            subMenuRadius = 250
        }
        itemBuilder.setBackgroundDrawable(resources.getDrawable(R.drawable.button_action_blue_selector))
        itemBuilder.setLayoutParams(params) //set the layout change

        //setup the icon for search
        val searchIcon  = ImageView(this)
        searchIcon.setImageResource(R.drawable.ic_search_white_24dp) // Create an icon
        //setup the icon for add
        val addIcon     = ImageView(this)
        addIcon.setImageResource(R.drawable.ic_note_add_white_24dp) // Create an icon
        //setup the icon for setting
        val settingIcon = ImageView(this)
        settingIcon.setImageResource(R.drawable.ic_settings_white_24dp) // Create an icon

        //create each individual sub action button
        val searchAction    = itemBuilder.setContentView(searchIcon).build()
        val addAction       = itemBuilder.setContentView(addIcon).build()
        val settingAction   = itemBuilder.setContentView(settingIcon).build()
        //now marge it all
        val actionMenu    = FloatingActionMenu.Builder(this)
                .setRadius(subMenuRadius)
                .addSubActionView(settingAction)
                .addSubActionView(searchAction)
                .addSubActionView(addAction)
                .setStateChangeListener(this)
                .attachTo(fab).build()

        addAction.setOnClickListener {
            val toAddNote = Intent(this, NoteActivity::class.java)
            toAddNote.putExtra(NOTE_MODE, "new")
            startActivity(toAddNote)
        }
        searchAction.setOnClickListener {
            // search
            actionMenu.close(true)
            if (noteSearchView.visibility == View.VISIBLE) noteSearchView.visibility = View.GONE
            else noteSearchView.visibility = View.VISIBLE
        }
        settingAction.setOnClickListener {
            // settings
        }


    }


    override fun onMenuOpened(p0: FloatingActionMenu?) {
        fab.setImageResource(R.drawable.ic_clear_white_24dp)
    }

    override fun onMenuClosed(p0: FloatingActionMenu?) {
        fab.setImageResource(R.drawable.ic_add_white_24dp)
    }


}
