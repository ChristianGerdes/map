package bfst20.mapit.model.ui;

import java.util.HashMap;

import bfst20.mapit.model.TreeType;
import bfst20.mapit.model.Type;
import javafx.scene.paint.Color;

public class TypeSettings {
    private HashMap<Type, Color> changedColor = new HashMap<>();

    public HashMap<Integer, Boolean> drawables;

    public HashMap<TreeType, Boolean> trees = new HashMap<>();

    public TypeSettings() {
        this.changedColor = new HashMap<>();

        for (TreeType treeType : TreeType.values()) {
            this.trees.put(treeType, true);
        }
    }

    public boolean isVisible(TreeType treeType) {
        return this.trees.get(treeType);
    }

    public void changeVisibility(TreeType treeType, boolean visible) {
        this.trees.put(treeType, visible);
    }

    public void resetVisibility() {
        for (TreeType treeType : TreeType.values()) {
            this.trees.put(treeType, true);
        }
    }

    public Color getChangedColor(Type type) {
        return this.changedColor.get(type);
    }

    public void putChangedColor(Type type, Color color) {
        this.changedColor.put(type, color);
    }

    public void clearChangedColor() {
        this.changedColor.clear();
    }
}