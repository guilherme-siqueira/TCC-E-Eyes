package br.usp.guilherme_galdino_siqueira.e_eyes.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import java.util.ArrayList;
import android.view.View;
import android.app.ListActivity;

import java.util.Collections;
import java.util.Comparator;
import android.widget.TextView;

import java.io.File;
/**
 * Created by gsiqueira on 7/24/16.
 */
public class SelectFileActivity extends ListActivity {

    ArrayList<String> realListItems =new ArrayList<String>();

    ArrayList<String> displayedListItems=new ArrayList<String>();

    ArrayAdapter<String> adapter;

    File folder;

    File fileList[];

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_file_activity);

        //listView = (ListView) findViewById(R.id.list);

        //listView.addHeaderView(textView);

        adapter=new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                displayedListItems)
        {
            /*@Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                if (position % 2 == 1) {
                    //view.setBackgroundColor(Color.BLUE);
                    view.setBackgroundResource(R.color.colorOddListItem);
                } else {
                    view.setBackgroundResource(R.color.colorEvenListItem);
                    //view.setBackgroundColor(Color.CYAN);
                }


                return view;
            }
            */
        };

        setListAdapter(adapter);

        listFolders();
    }

    //public void addItems(String fileName) {
       // realListItems.add(fileName);
        //adapter.notifyDataSetChanged();
    //}

    private void listFolders()
    {
        String path = Environment.getExternalStorageDirectory().toString()+"/E-EYES/";
        Log.d("Files", "Path: " + path);

        folder = new File(path);
        fileList = folder.listFiles();

        Log.d("Files", "Size: " + fileList.length);

        for (int i=0; i < fileList.length; i++)
        {
            realListItems.add(fileList[i].getName());
            //addItems(fileList[i].getName());
            //Log.d("Files", "FileName:" + fileList[i].getName());
        }

        // Sorting
        Collections.sort(realListItems, new Comparator<String>() {
            @Override
            public int compare(String fileName2, String fileName1) {

                return fileName1.compareTo(fileName2);
            }
        });

        for (int i=0; i < realListItems.size(); i++)
        {
            displayedListItems.add(realListItems.get(i));

            char a0 = displayedListItems.get(i).charAt(0);
            char a1 = displayedListItems.get(i).charAt(1);
            char a2 = displayedListItems.get(i).charAt(2);
            char a3 = displayedListItems.get(i).charAt(3);

            char m0 = displayedListItems.get(i).charAt(5);
            char m1 = displayedListItems.get(i).charAt(6);

            char d0 = displayedListItems.get(i).charAt(8);
            char d1 = displayedListItems.get(i).charAt(9);

            char h0 = displayedListItems.get(i).charAt(11);
            char h1 = displayedListItems.get(i).charAt(12);

            char mi0 = displayedListItems.get(i).charAt(14);
            char mi1 = displayedListItems.get(i).charAt(15);

            String newName;
            newName = "Descrição de " + d0 + d1 + "/" + m0 + m1 + "/" + a0 + a1 + a2 + a3 + " " + h0 + h1 + ":" + mi0 + mi1;

            displayedListItems.set(i,newName);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onListItemClick (ListView l, View v, int position, long id) {

        //Toast.makeText(this, "Clicked row " + position, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, ViewFileActivity.class);
        intent.putExtra("FOLDER", realListItems.get(position));
        startActivity(intent);
    }




}
