package br.usp.guilherme_galdino_siqueira.e_eyes.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.view.View;
import android.app.ListActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


import java.io.File;

import br.usp.guilherme_galdino_siqueira.e_eyes.properties.Constants;

/**
 * Created by gsiqueira on 7/24/16.
 */
public class SelectFileActivity extends ListActivity {

    ArrayList<String> realListItems =new ArrayList<>();

    ArrayList<String> displayedListItems=new ArrayList<>();

    ArrayAdapter<String> adapter;

    File folder;

    File fileList[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_file_activity);

        adapter=new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                displayedListItems);

        setListAdapter(adapter);

        listFolders();
    }

    private void listFolders()
    {
        String path = Constants.DIRECTORY_PATH;
        Log.d("Files", "Path: " + path);

        folder = new File(path);
        fileList = folder.listFiles();

        Log.d("Files", "Size: " + fileList.length);

        for (File aFileList : fileList) {
            realListItems.add(aFileList.getName());
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
            try {


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
            catch (Exception e)
            {

            }



        }
    }

    @Override
    protected void onListItemClick (ListView l, View v, int position, long id) {

        Intent intent = new Intent(this, ViewFileActivity.class);
        intent.putExtra("FOLDER", realListItems.get(position));
        startActivity(intent);
    }
}