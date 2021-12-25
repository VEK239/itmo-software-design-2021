package com.vlasova.spring.mvc.dao

import org.springframework.stereotype.Component
import com.vlasova.spring.mvc.model.TodoListItem
import com.vlasova.spring.mvc.model.TodoList

@Component
class ItemInMemoryDao : ItemDao {
    private var currentListItemMaxId = 0L
    private var currentListMaxId = 0L
    private val lists = mutableListOf(TodoList(0, "Default list"))

    override fun addTodoListItem(item: TodoListItem): Long {
        item.id = currentListItemMaxId++
        lists.find { it.id == item.listId }!!.add(item)
        return currentListItemMaxId
    }

    override fun getLists(): List<TodoList> {
        return lists
    }

    override fun markItemDone(listId: Long, id: Long) {
        lists.find { it.id == listId }!!.items.find { it.id == id }!!.isDone = true
    }

    override fun addList(list: TodoList): Long {
        list.id = ++currentListMaxId
        lists.add(list)
        return list.id
    }

    override fun deleteList(listId: Long) {
        lists.removeIf { it.id == listId }
    }
}