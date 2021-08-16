package com.example.tripreminder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import java.util.List;

public class NoteAdapter extends ArrayAdapter<String> {
    private List<String> _notes;
    private Context _context;

    public NoteAdapter(Context context, List<String> notes) {
        super(context, R.layout.custom_row, R.id.item, notes);
        _context = context;
        _notes = notes;
    }

    @Override
    public View getView(int position , View convertView , ViewGroup parent){
        View view = convertView;
        ViewHolder viewHolder;
        if(view == null) {
            LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.custom_row, parent, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }
        else
            viewHolder = (ViewHolder) view.getTag();

        viewHolder.getItem().setText(_notes.get(position));
        return view;
    }

    private class ViewHolder{
        View view;
        CheckBox item;
        public ViewHolder(View convertView){
            view = convertView;
        }

        public CheckBox getItem(){
            if(item == null)
                item = view.findViewById(R.id.item);
            return item;
        }
    }



}
