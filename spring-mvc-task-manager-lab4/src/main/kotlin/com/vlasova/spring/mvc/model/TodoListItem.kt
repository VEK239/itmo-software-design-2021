package com.vlasova.spring.mvc.model

data class TodoListItem(val description: String = "", val listId: Long = -1, var id: Long = -1, var isDone: Boolean = false)