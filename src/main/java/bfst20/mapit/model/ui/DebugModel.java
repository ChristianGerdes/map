package bfst20.mapit.model.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bfst20.mapit.Model;

/**
 * DebugModel
 */
public class DebugModel {

  public int searchedTrees = 0;

  private boolean debug = false;
  private boolean limitedRange = false;
  private boolean routeDebug = false;
  private HashMap<String, String> debugMap = new HashMap<String, String>();
  private List<Runnable> debugObservers = new ArrayList<>();

  private Model model;

  public DebugModel(Model model) {
    this.model = model;
  }

  public void setDebugInfo(String key, String value) {
    debugMap.put(key, value);
    notifyDebugObservers();
  }

  public HashMap<String, String> getDebugInfo() {
    return this.debugMap;
  }

  public void toggleDebug() {
    this.debug = !this.debug;

    notifyDebugObservers();
  }

  public void toggleLimitedRange() {
    this.limitedRange = !this.limitedRange;

    model.notifyObservers();
  }

  public void setSearchedTrees(int trees) {
    this.searchedTrees = trees;
  }

  public int getSearchedTrees() {
    return searchedTrees;
  }

  public void incrementSearchedTrees() {
    searchedTrees++;
  }

  public void toggleRouteDebug() {
    routeDebug = !routeDebug;
    model.notifyObservers();
  }

  public boolean isRouteDebugging() {
    return routeDebug;
  }

  public boolean isDebugging() {
    return this.debug;
  }

  public boolean isLimitingRange() {
    return this.limitedRange;
  }

  public void addDebugObserver(Runnable observer) {
    if (this.debugObservers != null) {
      this.debugObservers.add(observer);
    }
  }

  public void notifyDebugObservers() {
    for (Runnable observer : this.debugObservers) {
      observer.run();
    }
  }

}