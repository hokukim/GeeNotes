package com.weehoo.geenotes.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.weehoo.geenotes.R;
import com.weehoo.geenotes.note.NoteBook;

import java.util.ArrayList;

public class NoteBookAdapter extends ArrayAdapter<NoteBook> {
    private Context mContext;
    private ArrayList<NoteBook> mNoteBooks;

    public NoteBookAdapter(Context context, ArrayList<NoteBook> noteBooks) {
        super(context, 0, noteBooks);

        mContext = context;
        mNoteBooks = noteBooks;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        NoteBook noteBook = mNoteBooks.get(position);

        View listItem = convertView;

        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.notebook_list_item, parent,false);

        TextView nameView = listItem.findViewById(R.id.notebook_name);
        nameView.setText(noteBook.name);
        nameView.setTag(noteBook.getID());

        return listItem;
    }
}
