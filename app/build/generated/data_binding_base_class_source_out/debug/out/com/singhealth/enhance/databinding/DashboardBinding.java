// Generated by view binder compiler. Do not edit!
package com.singhealth.enhance.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.singhealth.enhance.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class DashboardBinding implements ViewBinding {
  @NonNull
  private final DrawerLayout rootView;

  @NonNull
  public final WebView WB;

  @NonNull
  public final BottomNavigationView bottomNavigationView;

  @NonNull
  public final DrawerLayout drawerLayout;

  @NonNull
  public final NavigationView navigationView;

  private DashboardBinding(@NonNull DrawerLayout rootView, @NonNull WebView WB,
      @NonNull BottomNavigationView bottomNavigationView, @NonNull DrawerLayout drawerLayout,
      @NonNull NavigationView navigationView) {
    this.rootView = rootView;
    this.WB = WB;
    this.bottomNavigationView = bottomNavigationView;
    this.drawerLayout = drawerLayout;
    this.navigationView = navigationView;
  }

  @Override
  @NonNull
  public DrawerLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static DashboardBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static DashboardBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.dashboard, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static DashboardBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.WB;
      WebView WB = ViewBindings.findChildViewById(rootView, id);
      if (WB == null) {
        break missingId;
      }

      id = R.id.bottomNavigationView;
      BottomNavigationView bottomNavigationView = ViewBindings.findChildViewById(rootView, id);
      if (bottomNavigationView == null) {
        break missingId;
      }

      DrawerLayout drawerLayout = (DrawerLayout) rootView;

      id = R.id.navigationView;
      NavigationView navigationView = ViewBindings.findChildViewById(rootView, id);
      if (navigationView == null) {
        break missingId;
      }

      return new DashboardBinding((DrawerLayout) rootView, WB, bottomNavigationView, drawerLayout,
          navigationView);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
