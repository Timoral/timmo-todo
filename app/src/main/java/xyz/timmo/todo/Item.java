package xyz.timmo.todo;

public class Item {
    private int _id;
    private String _item;
    private int _checked;

    public Item() {

    }

    public Item(int id, String item, int checked) {
        this._id = id;
        this._item = item;
        this._checked = checked;
    }

    public int getID() {
        return this._id;
    }

    public void setID(int id) {
        this._id = id;
    }

    public String getItem() {
        return this._item;
    }

    public void setItem(String item) {
        this._item = item;
    }

    public int getChecked() {
        return this._checked;
    }

    public void setChecked(int checked) {
        this._checked = checked;
    }

}
