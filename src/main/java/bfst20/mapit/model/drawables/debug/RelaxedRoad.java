package bfst20.mapit.model.drawables.debug;

import bfst20.mapit.model.drawables.roads.Road;
import bfst20.mapit.model.Type;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * RelaxedRoad
 */
public class RelaxedRoad extends Road {

  /**
   *
   */
  private static final long serialVersionUID = -405883776043271263L;

  public RelaxedRoad(Road road) {
    super(road);
  }

  @Override
  public void draw(GraphicsContext gc) {
    if (super.oneway) {
      gc.setStroke(Color.rgb(253, 110, 254, 0.5));
    } else {
      gc.setStroke(Color.rgb(255, 50, 50, 0.5));
    }
    gc.setLineWidth(1.6875405741623497E-5 * 5);
    super.draw(gc);
  }

  @Override
  public Type getType() {
    return Type.RELAXEDROAD;
  }
}