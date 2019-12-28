package com.weehoo.geenotes.adapters;

import android.content.ClipData;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.weehoo.geenotes.R;
import com.weehoo.geenotes.note.NoteBook;

import java.util.ArrayList;

public class NoteBookAdapter extends ArrayAdapter<NoteBook> {
    private Context mContext;
    private ArrayList<NoteBook> mNoteBooks;
    private ArrayList<ItemStatus> mNoteBookItemStatus;

    public NoteBookAdapter(Context context, ArrayList<NoteBook> noteBooks) {
        super(context, 0, noteBooks);

        mContext = context;
        mNoteBooks = noteBooks;
        mNoteBookItemStatus = new ArrayList<>();

        for (int i = 0; i < mNoteBooks.size(); i++) {
            mNoteBookItemStatus.add(ItemStatus.NORMAL);
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        NoteBook noteBook = mNoteBooks.get(position);
        ItemStatus itemStatus = mNoteBookItemStatus.get(position);

        View listItem = convertView;

        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.notebook_list_item, parent,false);

        // Name view.
        TextView nameView = listItem.findViewById(R.id.notebook_name);
        nameView.setText(noteBook.name);
        nameView.setVisibility(itemStatus == ItemStatus.NORMAL ? View.VISIBLE : View.GONE);

        // Rename (name edit) view.
        TextView renameView = listItem.findViewById(R.id.notebook_rename);
        renameView.setText(noteBook.name);
        renameView.setVisibility(itemStatus == ItemStatus.EDIT ? View.VISIBLE : View.GONE);

        // ID (hidden) view
        TextView idView = listItem.findViewById(R.id.notebook_id);
        idView.setText(noteBook.getID());

        return listItem;
    }

    public void setItemStatus(int index, ItemStatus itemStatus) {
        mNoteBookItemStatus.set(index, itemStatus);
    }

    /**
     * Note book item status.
     * This status is used by the adapter to determine view visibility.
     */
    public enum ItemStatus {
        NORMAL,
        EDIT
    }
}
