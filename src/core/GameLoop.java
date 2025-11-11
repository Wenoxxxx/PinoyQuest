package src.core;


public class GameLoop implements Runnable {
    
    // Reference to the GamePanel (the main drawing surface)
    private final GamePanel gamePanel;
    
    // The thread that runs the game loop
    private Thread thread;
    
    // Flag that indicates whether the game loop is running
    private volatile boolean running = false;
    
    // Desired frames per second (FPS)
    private final int FPS = 60; // Targeted frames per second



    // CONSTRUCTOR
    // Initializes the GameLoop with a specific GamePanel
    public GameLoop(GamePanel gamePanel){
        this.gamePanel = gamePanel;
    }



    // STARTS THE GAME LOOP THREAD
    // Creates and starts a new thread if one isn't already running
    public void start(){
        if (thread == null) {
            running = true;
            thread = new Thread(this);
            thread.start(); // Calls the run() method in a separate thread
        }
    }

    // STOPS THE GAME LOOP
    // Gracefully stops the loop and releases the thread
    public void stop() {
        running = false;
        thread = null;
    }



    // MAIN GAME LOOP
    @Override
    public void run(){

        // Calculates the time per frame in nanoseconds (1 second = 1,000,000,000 ns)
        double drawInterval = 1000000000.0 / FPS;
        double delta = 0;

        // Keeps track of time between frames
        long lastTime = System.nanoTime();
        long currentTime;

        // DELTA METHOD: ensures consistent frame rate even if system lags
        while (running && thread != null) {
            currentTime = System.nanoTime();

            // Calculates how much time has passed since last frame
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            // If enough time has passed for one frame, update and repaint
            if (delta >= 1) {
                gamePanel.update();   // Update game logic (movement, collisions, etc.)
                gamePanel.repaint();  // Redraw graphics
                delta--;              // Decrease delta for next frame
            }
            
            // Small delay to prevent CPU from running at 100%
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                running = false;
                break;
            }
        }
    }
}
