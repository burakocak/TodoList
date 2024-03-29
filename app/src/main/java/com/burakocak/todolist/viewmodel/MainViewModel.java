package com.burakocak.todolist.viewmodel;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.burakocak.todolist.database.TodoDatabase;
import com.burakocak.todolist.database.dao.TodoDao;
import com.burakocak.todolist.model.TodoList;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainViewModel extends AndroidViewModel {
    private TodoDao todoDao;
    private LiveData<List<TodoList>> allTodo;
    private ExecutorService executorService;

    public MainViewModel(@NonNull Application application) {
        super(application);
        TodoDatabase todoDatabase = TodoDatabase.getDatabase(application);
        todoDao = todoDatabase.todoDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void setAllTodo(String username) {
        allTodo = todoDao.getTodo(username);
    }

    public LiveData<List<TodoList>> getAllTodo() {
        return allTodo;
    }



    public void addTodo(TodoList todoList) {
        new InsertTodoAsyncTask(todoDao).execute(todoList);
    }




     //Operation was performed using executor method.

    public void deleteTodo(TodoList list) {
        executorService.execute(() -> todoDao.delete(list));
    }


    /*
        Operation was performed using AsyncTask.
        Login results of transactions using EvenBus
    */
    private static class InsertTodoAsyncTask extends AsyncTask<TodoList, Void, Void> {
        TodoDao todoDao;

        private InsertTodoAsyncTask(TodoDao todoDao) {
            this.todoDao = todoDao;
        }

        @Override
        protected Void doInBackground(TodoList... todoLists) {
            todoDao.insertTodo(todoLists[0]);
            return null;
        }
    }
}
