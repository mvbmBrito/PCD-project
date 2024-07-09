package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;

import environment.Board;
import environment.LocalBoard;

/**
 *  Class to create and configure GUI.
 *  Only the listener to the button should be edited, see TODO below.
 * 
 * @author luismota
 */
public class SnakeGui implements Observer {
    private static final int CELL_SIZE = 30; 
    private JFrame frame;
    private BoardComponent boardGui;
    private Board board;

    public SnakeGui(Board board, int x, int y) {
        super();
        this.board = board;
        frame = new JFrame("The Snake Game: " + (board instanceof LocalBoard ? "Local" : "Remote"));
        frame.setLocation(x, y);
        buildGui();
    }

    private void buildGui() {
        frame.setLayout(new BorderLayout());
        
        int width = LocalBoard.NUM_COLUMNS * CELL_SIZE;
        int height = LocalBoard.NUM_ROWS * CELL_SIZE;

        boardGui = new BoardComponent(board);
        boardGui.setPreferredSize(new Dimension(width, height));
        frame.add(boardGui, BorderLayout.CENTER);

        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void init() {
        frame.setVisible(true);
        board.addObserver(this);
        board.init();
    }

    @Override
    public void update(Observable o, Object arg) {
        boardGui.repaint();
    }
}
