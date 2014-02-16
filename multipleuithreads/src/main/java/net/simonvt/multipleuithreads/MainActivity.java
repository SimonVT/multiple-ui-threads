/*
 * Copyright (C) 2014 Simon Vig Therkildsen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.simonvt.multipleuithreads;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.View;
import android.widget.TextView;
import java.util.Random;

public class MainActivity extends Activity {

  private static final HandlerThread UI_THREAD = new HandlerThread("MyUiThread");
  private static final Handler UI_THREAD_HANDLER;

  static {
    UI_THREAD.start();
    UI_THREAD_HANDLER = new Handler(UI_THREAD.getLooper());
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        showDialog();
      }
    });
  }

  private void showDialog() {
    UI_THREAD_HANDLER.post(new Runnable() {
      @Override
      public void run() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        final TextView text = new TextView(builder.getContext());

        final Runnable updateRunnable = new Runnable() {
          @Override public void run() {
            Random random = new Random();
            int nextInt = random.nextInt();
            text.setText("Random int: " + nextInt);
            UI_THREAD_HANDLER.postDelayed(this, 500);
          }
        };

        builder.setView(text) //
            .setOnDismissListener(new DialogInterface.OnDismissListener() {
              @Override public void onDismiss(DialogInterface dialog) {
                UI_THREAD_HANDLER.removeCallbacks(updateRunnable);
              }
            }) //
            .create() //
            .show();

        UI_THREAD_HANDLER.post(updateRunnable);
      }
    });

    try {
      Thread.sleep(10000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
