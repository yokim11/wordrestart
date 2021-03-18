package com.sufamily.wordrestart.wordrestart.ActivityAdapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.sufamily.wordrestart.wordrestart.R;
import com.sufamily.wordrestart.wordrestart.db.repository.DictionaryRepository;
import com.sufamily.wordrestart.wordrestart.domain.model.JoinEntity;

import java.util.ArrayList;

public class DictionaryAdapter extends ArrayAdapter<JoinEntity> {

    private ArrayList<JoinEntity> joinEntityList;
    private Context context;
    private DictionaryRepository dictionaryRepository;

    public DictionaryAdapter(Context context, int textViewResourceId, ArrayList<JoinEntity> joinEntityList) {
        super(context, textViewResourceId, joinEntityList);
        this.context = context;
        this.joinEntityList = new ArrayList<>();
        this.joinEntityList.addAll(joinEntityList);
    }

    private class ViewHolder {
        TextView word;
        CheckBox meaning;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        Log.v("ConvertView", String.valueOf(position));

        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater)context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.word_info, null);

            holder = new ViewHolder();
            holder.word = (TextView) convertView.findViewById(R.id.code);
            holder.meaning = (CheckBox) convertView.findViewById(R.id.checkBox1);
            convertView.setTag(holder);

            dictionaryRepository = new DictionaryRepository(context.getApplicationContext());

            holder.meaning.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v;
                    JoinEntity joinEntity = (JoinEntity) cb.getTag();
                    if (cb.isChecked()) {
                        dictionaryRepository.updateMyDictionaryToggle(joinEntity, cb.isChecked());
                    } else {
                        dictionaryRepository.updateMyDictionaryToggle(joinEntity, cb.isChecked());
                    }
                }
            });
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        JoinEntity joinEntity = joinEntityList.get(position);
        holder.word.setText(" " + joinEntity.getMeaning().replace("\n", " "));
        holder.meaning.setText(joinEntity.getWord());
        holder.meaning.setTag(joinEntity);
        if (joinEntity.getMy_seq() > 0) {
            holder.meaning.setChecked(true);
        } else {
            holder.meaning.setChecked(false);
        }
        return convertView;

    }
}