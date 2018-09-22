package com.me.hatem.a03_kt_note.Controller

import android.content.DialogInterface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import com.me.hatem.a03_kt_note.Model.Note
import com.me.hatem.a03_kt_note.Model.Tag
import com.me.hatem.a03_kt_note.R
import com.me.hatem.a03_kt_note.Services.DBManager
import com.me.hatem.a03_kt_note.Utilises.*
import kotlinx.android.synthetic.main.activity_note.*
import kotlinx.android.synthetic.main.note_view.*
import kotlinx.android.synthetic.main.tags_chooser_model.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class NoteActivity : AppCompatActivity() {

    lateinit var noteMode: String
    var _id: Int            = 0
    val selectedTags        = ArrayList<String>()
    var selectedTagsText    = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)
        ///////////////////////////////////
        noteMode = intent.getStringExtra(NOTE_MODE)
        if (noteMode == MODE_NEW) {
            addNoteBtn.text = "Add"
        } else {
            val note = intent.getParcelableExtra<Note>(NOTE_EDIT)
            noteTitleText.setText(note.title)
            noteBodyText.setText(note.content)
            selectedTagsLabel.text  = note.tags
            addNoteBtn.text         = "Edit"
            _id                     = note._id
        }

        tagsBtn.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val view     = layoutInflater.inflate(R.layout.tags_chooser_model, null)
            builder.setView(view)
                    .setNegativeButton("Cancel"){dialog, which ->  }
                    .setPositiveButton("Add Tag") { dialog, which -> addTags(view) }
                    .show()

            //fill list of tags
            val dbManager   = DBManager(this, TAGS_TABLE)
            val tags        = dbManager.getAllTags()
            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, tags)
            val tags_list = view.findViewById<ListView>(R.id.exist_tags_list)
            tags_list.adapter = adapter
            //Put the list of selected tags in array
            tags_list.setOnItemClickListener { parent, view, position, id ->
                if (view.background == null) {
                    /*if (!(tags[position].title in selectedTags))*/
                    selectedTags.add(tags[position].title)
                    view.setBackgroundColor(resources.getColor(R.color.colorAccent))
                }
                else {
                    selectedTags.remove(tags[position].title)
                    view.setBackgroundDrawable(null)
                }
                println(selectedTags)
            }
        }



    }

    fun addNoteClicked(view: View) {
        val title   = noteTitleText.text.toString()
        val content = noteBodyText.text.toString()
        val date    = returnDate()
        val newNote        = Note(_id, title, content, date, selectedTagsText)
        val dbManager      = DBManager(this)
        if (noteMode == MODE_NEW) dbManager.insertNote(newNote)
         else {
            dbManager.updateNote(newNote)
        }
        finish()
    }

    fun returnDate(): String {
        val currentTime = Calendar.getInstance().time as Date
        val form        = "EEE, d MMM yyyy"
        //val form        = "E, h:mm a"
        val dateFormat  = SimpleDateFormat(form, Locale.getDefault())
        return dateFormat.format(currentTime)
    }

    fun addTags(view: View) {
        val title = view.findViewById<EditText>(R.id.tagsNewOneText).text.toString()
        val desc         = ""
        val dbManager    = DBManager(this, TAGS_TABLE)
        if (title.isNotEmpty()) {
                val tag = Tag(0, title, desc)
                selectedTags.add(tag.title)
                dbManager.insertTag(tag)
        }
        // show the selected tags
        selectedTagsText = ""
        selectedTags.forEach {s: String ->  selectedTagsText += "${s}, "  }
        selectedTags.clear()
        selectedTagsLabel.text = selectedTagsText
        println(selectedTagsText)
    }
}
