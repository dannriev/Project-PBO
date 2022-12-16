package throughthewall;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class Game {
    public static final int WALL_DELAY = 100;

    private Boolean paused;

    private int pauseDelay;
    private int restartDelay;
    private int wallDelay;

    private Object object;
    private ArrayList<Wall> walls;
    private Keyboard keyboard;

    public int score;
    public Boolean gameover;
    public Boolean started;

    public Game() {
        keyboard = Keyboard.getInstance();
        restart();
    }

    public void restart() {
        paused = false;
        started = false;
        gameover = false;

        score = 0;
        pauseDelay = 0;
        restartDelay = 0;
        wallDelay = 0;

        object = new Object();
        walls = new ArrayList<Wall>();
    }

    public void update() {
        watchForStart();

        if (!started)
            return;

        watchForPause();
        watchForReset();

        if (paused)
            return;

        object.update();

        if (gameover)
            return;

        moveWalls();
        checkForCollisions();
    }

    public ArrayList<Render> getRenders() {
        ArrayList<Render> renders = new ArrayList<Render>();
        renders.add(new Render(0, 0, "lib/argen.png"));
        for (Wall wall : walls)
            renders.add(wall.getRender());
        renders.add(new Render(0, 0, "lib/foreground.png"));
        renders.add(object.getRender());
        return renders;
    }

    private void watchForStart() {
        if (!started && keyboard.isDown(KeyEvent.VK_SPACE)) {
            started = true;
        }
    }

    private void watchForPause() {
        if (pauseDelay > 0)
            pauseDelay--;

        if (keyboard.isDown(KeyEvent.VK_P) && pauseDelay <= 0) {
            paused = !paused;
            pauseDelay = 10;
        }
    }

    private void watchForReset() {
        if (restartDelay > 0)
            restartDelay--;

        if (keyboard.isDown(KeyEvent.VK_R) && restartDelay <= 0) {
            restart();
            restartDelay = 10;
            return;
        }
    }

    private void moveWalls() {
        wallDelay--;

        if (wallDelay < 0) {
            wallDelay = WALL_DELAY;
            Wall northWall = null;
            Wall southWall = null;

            // Look for pipes off the screen
            for (Wall wall : walls) {
                if (wall.x - wall.width < 0) {
                    if (northWall == null) {
                        northWall = wall;
                    } else if (southWall == null) {
                        southWall = wall;
                        break;
                    }
                }
            }

            if (northWall == null) {
                Wall wall = new Wall("north");
                walls.add(wall);
                northWall = wall;
            } else {
                northWall.reset();
            }

            if (southWall == null) {
                Wall wall = new Wall("south");
                walls.add(wall);
                southWall = wall;
            } else {
                southWall.reset();
            }

            northWall.y = southWall.y + southWall.height + 175;
        }

        for (Wall wall : walls) {
            wall.update();
        }
    }

    private void checkForCollisions() {

        for (Wall wall : walls) {
            if (wall.collides(object.x, object.y, object.width, object.height)) {
                gameover = true;
                object.dead = true;
            } else if (wall.x == wall.x && wall.orientation.equalsIgnoreCase("south")) {
                score++;
            }
        }

        // Ground + object collision
        if (object.y + object.height > ThroughTheWall.HEIGHT - 80) {
            gameover = true;
            object.y = ThroughTheWall.HEIGHT - 80 - object.height;
        }
    }
}
