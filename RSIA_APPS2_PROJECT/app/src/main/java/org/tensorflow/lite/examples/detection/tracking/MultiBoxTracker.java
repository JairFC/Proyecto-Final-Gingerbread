/* Copyright 2019 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================*/

package org.tensorflow.lite.examples.detection.tracking;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;


import androidx.annotation.Nullable;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.tensorflow.lite.examples.detection.R;
import org.tensorflow.lite.examples.detection.env.BorderedText;
import org.tensorflow.lite.examples.detection.env.ImageUtils;
import org.tensorflow.lite.examples.detection.env.Logger;
import org.tensorflow.lite.examples.detection.tflite.Detector.Recognition;


import static android.content.Context.MODE_PRIVATE;
import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;
import static org.tensorflow.lite.examples.detection.LoginActivity.PREFERENCE_FILENAME;

/** A tracker that handles non-max suppression and matches existing objects to new detections. */
public class MultiBoxTracker {
  private static final float TEXT_SIZE_DIP = 18;
  private static final float MIN_SIZE = 16.0f;

  HashMap map = new HashMap();

  private static final int[] COLORS = {
    Color.BLUE,
    Color.RED,
    Color.GREEN,
    Color.YELLOW,
    Color.CYAN,
    Color.MAGENTA,
    Color.WHITE,
    Color.parseColor("#55FF55"),
    Color.parseColor("#FFA500"),
    Color.parseColor("#FF8888"),
    Color.parseColor("#AAAAFF"),
    Color.parseColor("#FFFFAA"),
    Color.parseColor("#55AAAA"),
    Color.parseColor("#AA33AA"),
    Color.parseColor("#0D0068")
  };
  final List<Pair<Float, RectF>> screenRects = new LinkedList<Pair<Float, RectF>>();
  private final Logger logger = new Logger();
  private final Queue<Integer> availableColors = new LinkedList<Integer>();
  private final List<TrackedRecognition> trackedObjects = new LinkedList<TrackedRecognition>();
  private final Paint boxPaint = new Paint();
  private final float textSizePx;
  private final BorderedText borderedText;
  private Matrix frameToCanvasMatrix;
  private int frameWidth;
  private int frameHeight;
  private int sensorOrientation;

  public MultiBoxTracker(final Context context) {
    for (final int color : COLORS) {
      availableColors.add(color);
    }

    boxPaint.setColor(Color.RED);
    boxPaint.setStyle(Style.STROKE);
    boxPaint.setStrokeWidth(10.0f);
    boxPaint.setStrokeCap(Cap.ROUND);
    boxPaint.setStrokeJoin(Join.ROUND);
    boxPaint.setStrokeMiter(100);

    textSizePx =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_DIP, context.getResources().getDisplayMetrics());
    borderedText = new BorderedText(textSizePx);
  }

  public synchronized void setFrameConfiguration(
      final int width, final int height, final int sensorOrientation) {
    frameWidth = width;
    frameHeight = height;
    this.sensorOrientation = sensorOrientation;
  }

  public synchronized void drawDebug(final Canvas canvas) {
    final Paint textPaint = new Paint();
    textPaint.setColor(Color.WHITE);
    textPaint.setTextSize(60.0f);

    final Paint boxPaint = new Paint();
    boxPaint.setColor(Color.RED);
    boxPaint.setAlpha(200);
    boxPaint.setStyle(Style.STROKE);

    for (final Pair<Float, RectF> detection : screenRects) {
      final RectF rect = detection.second;
      canvas.drawRect(rect, boxPaint);
      canvas.drawText("" + detection.first, rect.left, rect.top, textPaint);
      borderedText.drawText(canvas, rect.centerX(), rect.centerY(), "" + detection.first);
    }
  }

  public synchronized void trackResults(final List<Recognition> results, final long timestamp) {
    logger.i("Processing %d results from %d", results.size(), timestamp);
    processResults(results);
  }

  private Matrix getFrameToCanvasMatrix() {
    return frameToCanvasMatrix;
  }

  public synchronized void draw(final Canvas canvas) {
    final boolean rotated = sensorOrientation % 180 == 90;
    final float multiplier =
        Math.min(
            canvas.getHeight() / (float) (rotated ? frameWidth : frameHeight),
            canvas.getWidth() / (float) (rotated ? frameHeight : frameWidth));
    frameToCanvasMatrix =
        ImageUtils.getTransformationMatrix(
            frameWidth,
            frameHeight,
            (int) (multiplier * (rotated ? frameHeight : frameWidth)),
            (int) (multiplier * (rotated ? frameWidth : frameHeight)),
            sensorOrientation,
            false);
    for (final TrackedRecognition recognition : trackedObjects) {
      final RectF trackedPos = new RectF(recognition.location);



      getFrameToCanvasMatrix().mapRect(trackedPos);
      boxPaint.setColor(recognition.color);

      float cornerSize = Math.min(trackedPos.width(), trackedPos.height()) / 8.0f;
      canvas.drawRoundRect(trackedPos, cornerSize, cornerSize, boxPaint);


      String SpanishObjName;

      switch (recognition.title) {
        case "person":
          SpanishObjName = "Persona";
          break;
        case "cell phone":
          SpanishObjName = "Celular";
          break;
        case "horse":
          SpanishObjName = "Caballo";
          break;
        case "cat":
          SpanishObjName = "Gato";
          break;
        case "dog":
          SpanishObjName = "Perro";
          break;
        case "car":
          SpanishObjName = "Automovil";
          break;
        case "stop sign":
          SpanishObjName = "ALTO";
          break;
        case "motorcycle":
          SpanishObjName = "Motocicleta";
          break;
        case "boat":
          SpanishObjName = "Barco";
          break;
        case "apple":
          SpanishObjName = "Manzana";
          break;
        case "orange":
          SpanishObjName = "Naranja";
          break;
        case "banana":
          SpanishObjName = "Platano";
          break;
        case "carrot":
          SpanishObjName = "Zanahoria";
          break;
        case "tv":
          SpanishObjName = "Television";
          break;
        case "remote":
          SpanishObjName = "Control Remoto";
          break;
        case "book":
          SpanishObjName = "Libro";
          break;
        case "refrigerator":
          SpanishObjName = "Refrigerador";
          break;
        case "sandwich":
          SpanishObjName = "Sandwich";
          break;
        case "donut":
          SpanishObjName = "Dona";
          break;
        case "broccoli":
          SpanishObjName = "brocoli";
          break;
        case "toilet":
          SpanishObjName = "Baño";
          break;
        case "bottle":
          SpanishObjName = "Botella";
          break;
        case "bicycle" :
          SpanishObjName = "Bicicleta";
          break;
        case "bed":
          SpanishObjName = "Cama";
          break;
        case "scissors":
          SpanishObjName="Tijeras";
          break;
        case "pizza":
          SpanishObjName="Pizza";
          break;




        default:
          SpanishObjName = "???";
          break;
      }


      final String labelString =
              !TextUtils.isEmpty(recognition.title)
                      ? String.format("%s %.2f", SpanishObjName, (100 * recognition.detectionConfidence))
                      : String.format("%.2f", (100 * recognition.detectionConfidence));
      //            borderedText.drawText(canvas, trackedPos.left + cornerSize, trackedPos.top,
      // labelString);

      String porcentaje = String.valueOf(Math.round((100 * recognition.detectionConfidence)));
      borderedText.drawText(
              canvas, trackedPos.left + cornerSize, trackedPos.top, labelString + "%", boxPaint);


      //AL MISMO TIEMPO QUE SE DIBUJA SE SUBE A FIREBASE


      SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("USERID", Context.MODE_PRIVATE);

      String UID = sharedPreferences.getString("key", null);


  /*    DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(UID).child("objetos");




      if (newPost.isEmpty()){
        newPost.put(recognition.title,recognition.title);
      }else{
        for (int i=0; i<newPost.size(); i++){
          if (newPost.get(recognition.title)!=null){
            String acomparar =  newPost.get(recognition.title).toString();
            if (recognition.title.equals(acomparar)){
              System.out.println("NO HAY OBJETOS NUEVOS");
            }else {
              newPost.put(recognition.title,recognition.title);
            }
          }else {
            newPost.put(recognition.title,recognition.title);
          }
        }
      }

      current_user_db.setValue(newPost);



*/

      DatabaseReference current_user_db2 = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(UID).child("objetos").child(SpanishObjName);



      map.put("name", SpanishObjName);
      map.put("fidelidad", recognition.detectionConfidence);
      map.put("imgClima",R.drawable.portada);






      if (map.isEmpty()){
        map.put("name", SpanishObjName);
        map.put("fidelidad", porcentaje);
        map.put("imgClima",32423423);
      }else{
        for (int i=0; i<map.size(); i++){
          if (map.get("name")!=null){
            String acomparar =  map.get("name").toString();
            if (recognition.title.equals(acomparar)){
              System.out.println("NO HAY OBJETOS NUEVOS");
            }else {
              map.put("name", SpanishObjName);
              map.put("fidelidad", porcentaje);
              map.put("imgClima",32423423);
            }

          }else {
            map.put("name", SpanishObjName);
            map.put("fidelidad", porcentaje);
            map.put("imgClima",32423423);
          }

        }
      }

      current_user_db2.setValue(map);

    }
  }


  private void processResults(final List<Recognition> results) {
    final List<Pair<Float, Recognition>> rectsToTrack = new LinkedList<Pair<Float, Recognition>>();

    screenRects.clear();
    final Matrix rgbFrameToScreen = new Matrix(getFrameToCanvasMatrix());

    for (final Recognition result : results) {
      if (result.getLocation() == null) {
        continue;
      }
      final RectF detectionFrameRect = new RectF(result.getLocation());

      final RectF detectionScreenRect = new RectF();
      rgbFrameToScreen.mapRect(detectionScreenRect, detectionFrameRect);

      logger.v(
          "Result! Frame: " + result.getLocation() + " mapped to screen:" + detectionScreenRect);

      screenRects.add(new Pair<Float, RectF>(result.getConfidence(), detectionScreenRect));

      if (detectionFrameRect.width() < MIN_SIZE || detectionFrameRect.height() < MIN_SIZE) {
        logger.w("Degenerate rectangle! " + detectionFrameRect);
        continue;
      }

      rectsToTrack.add(new Pair<Float, Recognition>(result.getConfidence(), result));
    }

    trackedObjects.clear();
    if (rectsToTrack.isEmpty()) {
      logger.v("Nothing to track, aborting.");
      return;
    }

    for (final Pair<Float, Recognition> potential : rectsToTrack) {
      final TrackedRecognition trackedRecognition = new TrackedRecognition();
      trackedRecognition.detectionConfidence = potential.first;
      trackedRecognition.location = new RectF(potential.second.getLocation());
      trackedRecognition.title = potential.second.getTitle();
      trackedRecognition.color = COLORS[trackedObjects.size()];
      trackedObjects.add(trackedRecognition);

      if (trackedObjects.size() >= COLORS.length) {
        break;
      }
    }
  }

  private static class TrackedRecognition {
    RectF location;
    float detectionConfidence;
    int color;
    String title;
  }
}
