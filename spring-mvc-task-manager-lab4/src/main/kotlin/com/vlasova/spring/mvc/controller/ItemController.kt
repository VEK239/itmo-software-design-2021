package com.vlasova.spring.mvc.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.*
import com.vlasova.spring.mvc.dao.ItemDao
import com.vlasova.spring.mvc.model.TodoList
import com.vlasova.spring.mvc.model.TodoListItem

@Controller
class ItemController(private val itemDao: ItemDao) {
  @PostMapping("/add-item")
  fun addItem(@ModelAttribute("itemDto") todoListItemDto: TodoListItem): String {
    itemDao.addTodoListItem(todoListItemDto)
    return "redirect:/"
  }

  @PostMapping("/add-list")
  fun addList(@ModelAttribute("listDto") todoListDto: TodoList): String {
    itemDao.addList(todoListDto)
    return "redirect:/"
  }

  @GetMapping("/done/{listId}/{id}")
  fun markTaskDone(@PathVariable("listId") listId: Long, @PathVariable("id") id: Long): String {
    itemDao.markItemDone(listId, id)
    return "redirect:/"
  }

  @GetMapping("/deleteList/{listId}")
  fun deleteList(@PathVariable("listId") listId: Long): String {
    itemDao.deleteList(listId)
    return "redirect:/"
  }

  @GetMapping("/")
  fun getIndex(map: ModelMap): String {
    map.addAttribute("itemDto", TodoListItem())
    map.addAttribute("lists", itemDao.getLists())
    map.addAttribute("listDto", TodoList())
    return "index"
  }

}
