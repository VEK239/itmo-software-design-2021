package com.vlasova.spring.mvc.dao

import com.vlasova.spring.mvc.model.TodoList
import com.vlasova.spring.mvc.model.TodoListItem

interface ItemDao {
  fun addTodoListItem(item: TodoListItem): Long
  fun getLists(): List<TodoList>
  fun markItemDone(listId: Long, id: Long)
  fun addList(list: TodoList): Long
  fun deleteList(listId: Long)
}