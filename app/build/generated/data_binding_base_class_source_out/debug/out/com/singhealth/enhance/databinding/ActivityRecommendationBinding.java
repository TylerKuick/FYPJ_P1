// Generated by view binder compiler. Do not edit!
package com.singhealth.enhance.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.singhealth.enhance.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityRecommendationBinding implements ViewBinding {
  @NonNull
  private final RelativeLayout rootView;

  @NonNull
  public final TextView avgHomeDiaBPTV;

  @NonNull
  public final TextView avgHomeSysBPTV;

  @NonNull
  public final BottomNavigationView bottomNavigationView;

  @NonNull
  public final TextView bpStage;

  @NonNull
  public final TextView controlStatusTV;

  @NonNull
  public final TextView recommendationTV;

  private ActivityRecommendationBinding(@NonNull RelativeLayout rootView,
      @NonNull TextView avgHomeDiaBPTV, @NonNull TextView avgHomeSysBPTV,
      @NonNull BottomNavigationView bottomNavigationView, @NonNull TextView bpStage,
      @NonNull TextView controlStatusTV, @NonNull TextView recommendationTV) {
    this.rootView = rootView;
    this.avgHomeDiaBPTV = avgHomeDiaBPTV;
    this.avgHomeSysBPTV = avgHomeSysBPTV;
    this.bottomNavigationView = bottomNavigationView;
    this.bpStage = bpStage;
    this.controlStatusTV = controlStatusTV;
    this.recommendationTV = recommendationTV;
  }

  @Override
  @NonNull
  public RelativeLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityRecommendationBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityRecommendationBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_recommendation, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityRecommendationBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.avgHomeDiaBPTV;
      TextView avgHomeDiaBPTV = ViewBindings.findChildViewById(rootView, id);
      if (avgHomeDiaBPTV == null) {
        break missingId;
      }

      id = R.id.avgHomeSysBPTV;
      TextView avgHomeSysBPTV = ViewBindings.findChildViewById(rootView, id);
      if (avgHomeSysBPTV == null) {
        break missingId;
      }

      id = R.id.bottomNavigationView;
      BottomNavigationView bottomNavigationView = ViewBindings.findChildViewById(rootView, id);
      if (bottomNavigationView == null) {
        break missingId;
      }

      id = R.id.bpStage;
      TextView bpStage = ViewBindings.findChildViewById(rootView, id);
      if (bpStage == null) {
        break missingId;
      }

      id = R.id.controlStatusTV;
      TextView controlStatusTV = ViewBindings.findChildViewById(rootView, id);
      if (controlStatusTV == null) {
        break missingId;
      }

      id = R.id.recommendationTV;
      TextView recommendationTV = ViewBindings.findChildViewById(rootView, id);
      if (recommendationTV == null) {
        break missingId;
      }

      return new ActivityRecommendationBinding((RelativeLayout) rootView, avgHomeDiaBPTV,
          avgHomeSysBPTV, bottomNavigationView, bpStage, controlStatusTV, recommendationTV);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
