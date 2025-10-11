package src.core;

public class GameLoop implements Runnable {
    
    private final GamePanel gamePanel;
    private Thread thread;
    private final int FPS = 60; //Targeted frames per scond



    // CONSTRUCTOR
    public GameLoop(GamePanel gamePanel){
        this.gamePanel = gamePanel;
    }



    // STRATS GAMELOOP THREAD
    public void start(){
        thread = new Thread(this);
        thread.start();
    }



    @Override
    public void run(){

        double drawInterval = 1000000000 / FPS; // Time per frame in nanoseconds
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;


        // WHILE LOOP: RUNS AS LONG AS THE GAME IS ACTIVE
        while (thread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                
                gamePanel.update(); //Update Logic
                gamePanel.repaint(); // Draw everything
                delta --;
            }


        }

    }


}
