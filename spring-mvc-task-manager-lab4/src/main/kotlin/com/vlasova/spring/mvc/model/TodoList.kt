package com.vlasova.spring.mvc.model

data class TodoList(var id: Long = -1, val description: String = "") {
    val items = mutableListOf<TodoListItem>()
    fun add(item: TodoListItem) {
        items.add(item)
    }
}