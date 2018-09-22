package com.me.hatem.a03_kt_note.Model


class Tag(val _id: Int, val title: String, val description: String) {
    override fun toString(): String {
        return title
    }
}