package View;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class ConfettiParticle extends Rectangle {
    private double dx, dy;
    private static final double GRAVITY = 0.2;

    /**
     * יוצר חלקיק קונפטי קטן עם מהירות התחלית אקראית
     * @param startX מיקום התחלתי על ציר ה-X
     * @param startY מיקום התחלתי על ציר ה-Y
     */
    public ConfettiParticle(double startX, double startY) {
        // מלבן בגודל 5x10 עם צבע HSB אקראי (גוונים שונים)
        super(5, 10, Color.hsb(Math.random() * 360, 0.8, 1.0));
        setX(startX);
        setY(startY);
        // מהירות אקראית אופקית
        this.dx = (Math.random() - 0.5) * 4;
        // מהירות אנכית כלפי מעלה
        this.dy = - (Math.random() * 5 + 2);
        // סיבוב התחלתי אקראי
        setRotate(Math.random() * 360);
    }

    /**
     * מתעדכן בכל פריים על ידי ה־AnimationTimer:
     * מוסיף כוח כבידה, מעדכן מיקום וסיבוב
     */
    public void update() {
        // כוח משיכה שמוסיף דיפאולטית
        dy += GRAVITY;
        // עדכון מיקום בהתאם למהירות
        setX(getX() + dx);
        setY(getY() + dy);
        // סיבוב קל לפי תנועת dx
        setRotate(getRotate() + dx * 2);
    }
}
