package dm550.tictactoe;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;

@SuppressWarnings("serial")
public class GUI extends JFrame implements UserInterface, MouseListener {

    public static void main(String[] args) {
        GUI ui = new GUI();
        int numPlayers = ui.getParameter("number of players",2,6);
        Game game = new TTTGame(numPlayers);
        ui.startGame(game);
    }

    /** constant defining the (default) size of each cell of the board */
    public static final int UNIT = 100;

    private Map<Coordinate,JLabel> pos2label = new LinkedHashMap<Coordinate,JLabel>();
    private Map<JPanel,Coordinate> panel2pos = new LinkedHashMap<JPanel,Coordinate>();
    private Game game;

    @Override
    public void startGame(Game game) {
        game.setUserInterface(this);
        this.setTitle(game.getTitle());
        this.game = game;
        int xSize = this.game.getHorizontalSize();
        int ySize = this.game.getVerticalSize();
        this.setSize(xSize*UNIT, ySize*UNIT);
        JPanel content = new JPanel();
        content.setLayout(new GridLayout(ySize,xSize));
        for (int y = 0; y < ySize; y++) {
            for (int x = 0; x < xSize; x++) {
                Coordinate pos = new XYCoordinate(x,y);
                JLabel label = new JLabel(this.game.getContent(pos), JLabel.CENTER);
                label.setFont(new Font("Arial Bold", Font.BOLD, 42));
                JPanel panel = new JPanel(new BorderLayout());
                panel.add(label, BorderLayout.CENTER);
                panel.setBorder(new LineBorder(Color.BLACK));
                panel.setBackground(Color.WHITE);
                panel.addMouseListener(this);
                content.add(panel);
                this.pos2label.put(pos, label);
                this.panel2pos.put(panel, pos);
            }
        }
        this.setContentPane(content);
        this.setLocation(100,100);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    // helper
    private Coordinate getPosition(Component panel) {
        return this.panel2pos.get(panel);
    }

    // update all positions
    private void updateLabels() {
        for (Map.Entry<Coordinate,JLabel> entry : this.pos2label.entrySet()) {
            entry.getValue().setText(this.game.getContent(entry.getKey()));
        }
    }

    @Override
    public void mouseClicked(MouseEvent event) {
        Coordinate pos = getPosition(event.getComponent());
        if (this.game.isFree(pos)) {
            this.game.addMove(pos);
            this.updateLabels();
            this.game.checkResult();
        }
    }

    @Override
    public void mouseEntered(MouseEvent event) {
        Component panel = event.getComponent();
        if (this.game.isFree(getPosition(panel))) {
            panel.setBackground(Color.CYAN);
        }
    }

    @Override
    public void mouseExited(MouseEvent event) {
        event.getComponent().setBackground(Color.WHITE);
    }

    @Override
    public void mousePressed(MouseEvent event) {
        Component panel = event.getComponent();
        if (this.game.isFree(getPosition(panel))) {
            panel.setBackground(Color.BLUE);
        } else {
            panel.setBackground(Color.RED);
        }
    }

    @Override
    public void mouseReleased(MouseEvent event) {
        event.getComponent().setBackground(Color.WHITE);
    }

    @Override
    public void showResult(String message) {
        JOptionPane.showMessageDialog(this, message+"\n\nThanks for playing.");
        System.exit(0);
    }

    public int getParameter(String message, int min, int max) {
        Object[] options = new Object[max-min+1];
        for (int i = min; i <= max; i++) {
            options[i-min] = i;
        }
        return (Integer) JOptionPane.showInputDialog(null, "Select "+message, "Input", JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
    }

}
