package com.burakocak.todolist.view.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;

import com.burakocak.todolist.R;
import com.burakocak.todolist.databinding.ActivityTodoListItemBinding;
import com.burakocak.todolist.model.EventbusObject;
import com.burakocak.todolist.model.TodoItem;
import com.burakocak.todolist.view.adapter.TodoListItemAdapter;
import com.burakocak.todolist.view.base.BaseActivity;
import com.burakocak.todolist.view.callback.OnRecyclerItemClickListener;
import com.burakocak.todolist.viewmodel.TodoListItemViewModel;

import static com.burakocak.todolist.utils.Constants.ADD_TODO_REQUEST;
import static com.burakocak.todolist.utils.Constants.TODO_ITEM_DATE;
import static com.burakocak.todolist.utils.Constants.TODO_ITEM_DESC;
import static com.burakocak.todolist.utils.Constants.TODO_ITEM_TITLE;


public class TodoListItemActivity extends BaseActivity implements OnRecyclerItemClickListener {

    private ActivityTodoListItemBinding binding;
    private TodoListItemViewModel viewModel;
    private TodoListItemAdapter todoListItemAdapter;
    private int todoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(TodoListItemActivity.this, R.layout.activity_todo_list_item);

        String todoName = getIntent().getStringExtra("todoName");
        todoId = getIntent().getIntExtra("todoId", 10);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("TODO: " + todoName);

        viewModel = ViewModelProviders.of(TodoListItemActivity.this).get(TodoListItemViewModel.class);
        viewModel.setAllTodoItems(todoId);
        viewModel.getAllTodoItems().observe(this, todoItems -> todoListItemAdapter.setTodoItem(todoItems));

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                todoListItemAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                todoListItemAdapter.getFilter().filter(newText);
                return false;
            }
        });
        init();
    }

    private void init() {
        binding.btnAddTodoItem.setOnClickListener(view ->
                startActivityForResult(new Intent(TodoListItemActivity.this, AddTodoItemActivity.class), ADD_TODO_REQUEST));
        binding.rvTodoItem.setLayoutManager(new LinearLayoutManager(this));
        binding.rvTodoItem.setHasFixedSize(true);
        todoListItemAdapter = new TodoListItemAdapter(this, this);
        binding.rvTodoItem.setAdapter(todoListItemAdapter);
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onCustomEvent(EventbusObject eventbusObject) {

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == ADD_TODO_REQUEST && resultCode == RESULT_OK && data != null) {
            String itemTitle = data.getStringExtra(TODO_ITEM_TITLE);
            String itemDesc = data.getStringExtra(TODO_ITEM_DESC);
            String itemDate = data.getStringExtra(TODO_ITEM_DATE);
            TodoItem todoItem = new TodoItem();
            todoItem.setTitle(itemTitle);
            todoItem.setDesc(itemDesc);
            todoItem.setExpiredDate(itemDate);
            todoItem.setTodoId(todoId);
            todoItem.setCompleted(false);
            viewModel.addTodoItem(todoItem);
            showSuccessSneaker("Save!", "New todo is add to do list");
        } else {
            showErrorSneaker("Not Save!!", "New todo is not saved!!");
        }
    }


    @Override
    public void onDeleteClick(Object object) {
        viewModel.deleteTodoItem((TodoItem) object);
    }

    @Override
    public void onCompleteClick(Object object) {
        TodoItem todoItem = (TodoItem) object;
        if (todoItem.isCompleted()) {
            todoItem.setCompleted(false);
            showErrorSneaker("Not Completed!!", "Todo item is not completed");
        } else {
            todoItem.setCompleted(true);
            showSuccessSneaker("Completed!!", "Todo item is completed");
        }
        viewModel.updateTodoItem(todoItem);
    }
}
