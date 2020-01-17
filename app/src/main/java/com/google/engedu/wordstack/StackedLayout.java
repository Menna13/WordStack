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
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.Stack;

public class StackedLayout extends LinearLayout {

    private Stack<View> tiles = new Stack();

    public StackedLayout(Context context) {
        super(context);
    }

    /*
    Implement push to:

- call removeView with the tile on top of the stack
(if there is one) to hide that tile
- push the specified tile onto the tiles stack
- call addView with the tile that was just pushed
     */
    public void push(View tile) {
        if (!tiles.empty()) {
            removeView(tiles.peek());
        }
        tiles.push(tile);
        addView(tiles.peek());
    }

    /*
    pop should:

   1- pop a tile from tiles
   2- call removeView with it
   3- call addView with the tile that is now on top of the stack
    return the popped tile
     */
    public View pop() {
        View popped = tiles.pop();
        this.removeView(popped); // ?? is that what 1, 2 are asking for?
        if (!tiles.empty()) {
            addView(tiles.peek());
        }
        return popped;
    }

    public View peek() {
        return tiles.peek();
    }

    public boolean empty() {
        return tiles.empty();
    }

    //remove
    public void clear() {
        removeAllViews();
        tiles.removeAllElements();
    }
}