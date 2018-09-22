package com.me.hatem.a03_kt_note.Adapter

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import com.me.hatem.a03_kt_note.Controller.NoteActivity
import com.me.hatem.a03_kt_note.Model.Note
import com.me.hatem.a03_kt_note.R
import com.me.hatem.a03_kt_note.Services.DBManager
import com.me.hatem.a03_kt_note.Utilises.MODE_EDIT
import com.me.hatem.a03_kt_note.Utilises.NOTE_EDIT
import com.me.hatem.a03_kt_note.Utilises.NOTE_MODE

class NoteAdapter(val contxt: Context, val notes: ArrayList<Note>) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val noteView: View
        val holder: ViewHolder
        if (convertView == null) {
            noteView = LayoutInflater.from(contxt).inflate(R.layout.note_view, null)
            holder = ViewHolder()
            holder.titleLabel       = noteView.findViewById(R.id.noteTitleLabel)
            holder.contentLabel     = noteView.findViewById(R.id.noteBodyLable)
            holder.dateLabel        = noteView.findViewById(R.id.noteDateLable)
            holder.tagsLabel        = noteView.findViewById(R.id.noteTagsLabel)
            holder.noteDeleteBtn    = noteView.findViewById(R.id.noteDeleteBtn)
            holder.noteEditBtn      = noteView.findViewById(R.id.noteEditBtn)
            noteView.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
            noteView = convertView
        }
        holder.titleLabel?.text     = notes[position].title
        holder.contentLabel?.text   = notes[position].content
        holder.dateLabel?.text      = notes[position].date
        holder.tagsLabel?.text      = notes[position].tags
        holder.noteDeleteBtn?.setOnClickListener {
            val dbManager = DBManager(contxt)
            dbManager.deleteNote(notes[position]._id.toString())
            notes.remove(notes[position])
            notifyDataSetChanged()
        }
        holder.noteEditBtn?.setOnClickListener {
            val toEditNote = Intent(contxt, NoteActivity::class.java)
            toEditNote.putExtra(NOTE_MODE, MODE_EDIT)
            toEditNote.putExtra(NOTE_EDIT, notes[position])
            startActivity(contxt, toEditNote, null)
        }

        return noteView
    }

    override fun getItem(position: Int): Any {
        return notes[position]
    }

    override fun getItemId(position: Int): Long {
       return 0
    }

    override fun getCount(): Int {
        return notes.count()
    }
    private class ViewHolder {
        var titleLabel: TextView ?          = null
        var contentLabel: TextView ?        = null
        var dateLabel: TextView ?           = null
        var tagsLabel: TextView ?           = null
        var noteDeleteBtn: ImageButton ?    = null
        var noteEditBtn: ImageButton ?      = null
    }
}