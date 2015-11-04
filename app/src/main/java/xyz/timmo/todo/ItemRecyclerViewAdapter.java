package xyz.timmo.todo;

import android.content.Context;
import android.graphics.Paint;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

class ItemRecyclerViewAdapter extends RecyclerView.Adapter<ItemRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Integer> arrayListID;
    private ArrayList<Integer> arrayListChecked;
    private ArrayList<String> arrayListItems;
    private ItemDatabaseHandler db;

    public ItemRecyclerViewAdapter
            (Context c, ArrayList<Integer> id, ArrayList<Integer> checked, ArrayList<String> items) {
        context = c;
        arrayListID = id;
        arrayListChecked = checked;
        arrayListItems = items;
    }

    @Override
    public ItemRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        v.setTag(viewHolder);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        db = new ItemDatabaseHandler(context);

        holder.editTextItem.setText(arrayListItems.get(position));

        if (arrayListChecked.get(position) == 1) {
            Check(true, holder);
        } else {
            Check(false, holder);
        }

        holder.checkBoxItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Check(true, holder);
                    UpdateChecklist(arrayListID.get(position), holder.editTextItem.getText().toString(), 1);
                } else {
                    Check(false, holder);
                    UpdateChecklist(arrayListID.get(position), holder.editTextItem.getText().toString(), 0);
                }
            }
        });

        holder.editTextItem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                UpdateChecklist(arrayListID.get(position), s.toString(), 0);
                //Snackbar.make(holder.itemView, "Saved.", Snackbar.LENGTH_LONG)
                //       .setAction("Action", null).show();
            }
        });

        holder.editTextItem.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    holder.imageButtonDelete.setVisibility(View.VISIBLE);
                } else {
                    holder.imageButtonDelete.setVisibility(View.INVISIBLE);
                }
            }
        });

        holder.imageButtonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HideKeyboard(holder);

                List<Item> items = db.getAllItems();

                int id = arrayListID.get(position) - 1;
                if (id >= arrayListID.size()) {
                    id = arrayListID.size() - 1;
                }
                db.deleteItem(items.remove(id));
                Snackbar.make(holder.itemView, "Removed.", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
                notifyDataSetChanged();
                //notifyItemRemoved(id);
            }
        });
    }

    private void UpdateChecklist(int id, String item, int checked) {
        db.updateItem(new Item(id, item, checked));
    }

//    private void ShowIDToast(int position) {
//        Toast.makeText(context, "position: " + position
//                + "\narrayListID: " + arrayListID.get(position)
//                + "\narrayListItems: " + arrayListItems.get(position)
//                + "\narrayListChecked: " + arrayListChecked.get(position)
//                , Toast.LENGTH_LONG).show();
//    }

    private void Check(boolean isChecked, ViewHolder holder) {
        if (isChecked) {
            holder.editTextItem.setPaintFlags(holder.editTextItem.getPaintFlags()
                    | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.editTextItem.setEnabled(false);
            holder.imageButtonDelete.setVisibility(View.VISIBLE);
            holder.checkBoxItem.setChecked(true);
        } else {
            holder.editTextItem.setPaintFlags(holder.editTextItem.getPaintFlags()
                    & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.editTextItem.setEnabled(true);
            holder.imageButtonDelete.setVisibility(View.INVISIBLE);
            holder.checkBoxItem.setChecked(false);
        }
        HideKeyboard(holder);
    }

    private void HideKeyboard(ViewHolder holder) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(holder.itemView.getWindowToken(), 0);
    }

    @Override
    public int getItemCount() {
        return arrayListID.size();
    }

    // Create the ViewHolder class to keep references to your views
    public static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linearLayoutItem;
        CheckBox checkBoxItem;
        EditText editTextItem;
        ImageButton imageButtonDelete;

        public ViewHolder(View v) {
            super(v);
            linearLayoutItem = (LinearLayout) v.findViewById(R.id.linearLayoutItem);
            checkBoxItem = (CheckBox) v.findViewById(R.id.checkBoxItem);
            editTextItem = (EditText) v.findViewById(R.id.editTextItem);
            imageButtonDelete = (ImageButton) v.findViewById(R.id.imageButtonDelete);
        }
    }
}