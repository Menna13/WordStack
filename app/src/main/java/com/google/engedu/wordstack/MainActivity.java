/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.wordstack;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    private static final int WORD_LENGTH = 5;
    public static final int LIGHT_BLUE = Color.rgb(176, 200, 255);
    public static final int LIGHT_GREEN = Color.rgb(200, 255, 200);
    private ArrayList<String> words = new ArrayList<>();
    private Random random = new Random();
    private StackedLayout stackedLayout;
    private String word1, word2;
    private Stack<LetterTile> placedTiles = new Stack<>();
    private static int increseDif = 3;
    View word1LinearLayout;
    View word2LinearLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AssetManager assetManager = getAssets();
        try {
            InputStream inputStream = assetManager.open("words.txt");
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            while((line = in.readLine()) != null) {
                String word = line.trim();

//                if (word.length() == increseDif){
                    words.add(word);
//                }
            }
        } catch (IOException e) {
            Toast toast = Toast.makeText(this, "Could not load dictionary", Toast.LENGTH_LONG);
            toast.show();
        }
        LinearLayout verticalLayout = (LinearLayout) findViewById(R.id.vertical_layout);
        stackedLayout = new StackedLayout(this);
        verticalLayout.addView(stackedLayout, 3);

        word1LinearLayout = findViewById(R.id.word1);
        word2LinearLayout = findViewById(R.id.word2);
//        word1LinearLayout.setOnTouchListener(new TouchListener());
        word1LinearLayout.setOnDragListener(new DragListener());
//        word2LinearLayout.setOnTouchListener(new TouchListener());
        word2LinearLayout.setOnDragListener(new DragListener());
    }

    private class TouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN && !stackedLayout.empty()) {
                LetterTile tile = (LetterTile) stackedLayout.peek();
                tile.moveToViewGroup((ViewGroup) v);
                if (stackedLayout.empty()) {
                    TextView messageBox = (TextView) findViewById(R.id.message_box);
                    messageBox.setText(word1 + " " + word2);
                }
                placedTiles.push(tile);
                return true;
            }
            return false;
        }
    }

    private class DragListener implements View.OnDragListener {

        public boolean onDrag(View v, DragEvent event) {
            int action = event.getAction();
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    v.setBackgroundColor(LIGHT_BLUE);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DRAG_ENTERED:
                    v.setBackgroundColor(LIGHT_GREEN);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DRAG_EXITED:
                    v.setBackgroundColor(LIGHT_BLUE);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DRAG_ENDED:
                    v.setBackgroundColor(Color.WHITE);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DROP:
                    // Dropped, reassign Tile to the target Layout
                    LetterTile tile = (LetterTile) event.getLocalState();
                    System.out.println(tile);
                    tile.moveToViewGroup((ViewGroup) v);
                    if (stackedLayout.empty()) {
                        TextView messageBox = (TextView) findViewById(R.id.message_box);
                        messageBox.setText(word1 + " " + word2);
                    }
                    placedTiles.push(tile);
                    return true;
            }
            return false;
        }
    }
    /*
    So go back to onStartGame and add code to call removeAllViews on
    word1LinearLayout and word2LinearLayout as well as to call clear on
     the StackLayout.

     ?? it's only defined in onCreate ??
     the game closes once I hit start
     */
    public ArrayList<String> changeLength (int diffLength) {
        ArrayList<String> incrementArray= new ArrayList<String>();
        for (int i = 0; i < words.size(); i++){
            if (words.get(i).length() == diffLength){
                incrementArray.add(words.get(i));
            }
        }
        return incrementArray;
    }

    /*
    Allow dragging to undo and dragging tiles between word1 and word2.
    This requires not freezing the right-most tile of each LinearLayout so that it can be dragged elsewhere.
     */
    public boolean onStartGame(View view) {
        stackedLayout.clear();
        TextView messageBox = (TextView) findViewById(R.id.message_box);
        messageBox.setText("Game started");
        ArrayList<String> updatedArray = changeLength (increseDif);
        int word1Index = random.nextInt(updatedArray.size());
        int word2Index = random.nextInt(updatedArray.size());
        word1 = updatedArray.get(word1Index);
        word2 = updatedArray.get(word2Index);
        int word1Counter = 0;
        int word2Counter = 0;
        int wordLimit = word1.length(); //any of the two words length would work as they're the same length
        String scrambled = "";

        while (word1Counter < wordLimit && word2Counter < wordLimit){
            int randomIndex = random.nextInt(2);
            if (randomIndex == 0){
                scrambled = scrambled + word1.charAt(word1Counter);
                word1Counter++;
            }
            else{
                scrambled = scrambled + word2.charAt(word2Counter);
                word2Counter++;
            }
        }
        if (word1Counter==wordLimit){
            scrambled = scrambled + word2.substring(word2Counter, wordLimit);
        }
        else{
            scrambled = scrambled + word1.substring(word1Counter, wordLimit);
        }
        for (int i = scrambled.length()-1; i >= 0 ; i--){
            LetterTile oneLetter = new LetterTile(this, scrambled.charAt(i)); //what is context?
            stackedLayout.push(oneLetter);
        }
        messageBox.setText(scrambled + " " + word1 + " " + word2); //here?
        LinearLayout word1Lay = (LinearLayout) word1LinearLayout;
        LinearLayout word2Lay = (LinearLayout) word2LinearLayout;
        word1Lay.removeAllViews();
        word2Lay.removeAllViews();
        increseDif++;
        return true;
    }
    public boolean onUndo(View view) {
        if (!placedTiles.empty()) {
            LetterTile mostRecent = placedTiles.pop();
            mostRecent.moveToViewGroup(stackedLayout);
        }
        return true;
    }
}
