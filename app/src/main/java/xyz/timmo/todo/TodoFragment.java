package xyz.timmo.todo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.List;

public class TodoFragment extends Fragment implements View.OnClickListener {

    ArrayList<Integer> arrayListID;
    ArrayList<Integer> arrayListChecked;
    ArrayList<String> arrayListItems;
    private FloatingActionButton fab;
    private CardView cardViewAdd;
    private EditText editTextAddItem;
    private RecyclerView recyclerViewTodo;
    private RecyclerView.Adapter recyclerViewAdapter;

    private boolean isInView = false;

    private SharedPreferences sharedPreferences;
    private Resources resources;
    private ItemDatabaseHandler db;

    public TodoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_todo, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        resources = getResources();
        db = new ItemDatabaseHandler(getActivity());

        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        cardViewAdd = (CardView) view.findViewById(R.id.cardViewAdd);
        editTextAddItem = (EditText) view.findViewById(R.id.editTextAddItem);
        ImageButton imageButtonAdd = (ImageButton) view.findViewById(R.id.imageButtonAdd);
        recyclerViewTodo = (RecyclerView) view.findViewById(R.id.recyclerViewTodo);

        arrayListID = new ArrayList<>();
        arrayListChecked = new ArrayList<>();
        arrayListItems = new ArrayList<>();

        LoadItems();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), 1, false);
        recyclerViewTodo.setLayoutManager(linearLayoutManager);
        recyclerViewAdapter = new ItemRecyclerViewAdapter
                (getActivity(), arrayListID, arrayListChecked, arrayListItems);
        recyclerViewTodo.setAdapter(recyclerViewAdapter);

        imageButtonAdd.setOnClickListener(this);
        fab.setOnClickListener(this);

        FirstLaunch();

    }

    private void FirstLaunch() {
        final SharedPreferences sharedPreferencesFirst = getActivity().getSharedPreferences("PREFS_FIRST_LAUNCH", 0);
        if (sharedPreferencesFirst.getBoolean("first_launch", true)) {
            AddItem(resources.getString(R.string.welcome_item), 0);
            //db.addItem(new Item(, resources.getString(R.string.welcome_item), 0));
            sharedPreferencesFirst.edit().putBoolean("first_launch", false).apply();
        }
    }

    private void ShowAdd() {
        cardViewAdd.animate().translationY(0)
                .setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(500);
        recyclerViewTodo.animate().translationY(200)
                .setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(500);
        fab.animate().rotation(135)
                .setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(500);
        new CountDownTimer(500, 500) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                editTextAddItem.setEnabled(true);
                editTextAddItem.requestFocus();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                }
                isInView = true;
            }
        }.start();
    }

    private void HideAdd() {
        cardViewAdd.animate().translationY(-200)
                .setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(500);
        recyclerViewTodo.animate().translationY(0)
                .setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(500);
        fab.animate().rotation(0)
                .setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(500);

        new CountDownTimer(500, 500) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                View view = getActivity().getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                editTextAddItem.clearFocus();
                editTextAddItem.setText("");
                editTextAddItem.setEnabled(false);
                isInView = false;
            }
        }.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageButtonAdd:
                AddItem(editTextAddItem.getText().toString(), 0);
                HideAdd();
                break;
            case R.id.fab:
                // Snackbar.make(view, "Added.", Snackbar.LENGTH_LONG)
                // .setAction("Action", null).show();
                if (!isInView) {
                    // Show
                    ShowAdd();
                } else {
                    // Hide
                    if (editTextAddItem.getText().toString().equals("")) {
                        HideAdd();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Cancel")
                                .setMessage("Are you sure?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        HideAdd();
                                        dialog.dismiss();
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                });
                        builder.create();
                        builder.show();
                    }
                }
        }
    }

    private int getItemCount() {
        if (db.getItemCount() <= 0) {
            return 0;
        } else {
            return db.getItemCount();
        }
    }

    private void AddItem(String newItem, int checked) {
        int newPermID = getItemCount() + 1;
        db.addItem(new Item(newPermID, newItem, checked));
        LoadItems();
        recyclerViewAdapter.notifyItemInserted(arrayListID.indexOf(newPermID));
        recyclerViewTodo.smoothScrollToPosition(0);
    }

    public void LoadItems() {
        // Clear existing ArrayList's
        arrayListID.clear();
        arrayListItems.clear();
        arrayListChecked.clear();

        List<Item> items = db.getAllItems();
        for (Item cn : items) {
            arrayListID.add(cn.getID());
            arrayListItems.add(cn.getItem());
            arrayListChecked.add(cn.getChecked());
        }
    }

}