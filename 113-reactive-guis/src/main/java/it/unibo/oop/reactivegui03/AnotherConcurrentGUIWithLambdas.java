package it.unibo.oop.reactivegui03;

import it.unibo.oop.JFrameUtil;

import java.io.Serial;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Third experiment with reactive gui, solution using lambdas.
 */
@SuppressWarnings("PMD.AvoidPrintStackTrace")
public final class AnotherConcurrentGUIWithLambdas extends JFrame {

    @Serial
    private static final long serialVersionUID = 1L;
    private static final double WIDTH_PERC = 0.2;
    private static final double HEIGHT_PERC = 0.1;
    private static final long WAITING_TIME = TimeUnit.SECONDS.toMillis(10);

    private final JLabel display = new JLabel();
    private final JButton stop = new JButton("stop");
    private final JButton up = new JButton("up");
    private final JButton down = new JButton("down");

    private final CounterAgent counterAgent = new CounterAgent();

    /**
     * Builds a C3GUI.
     */
    @SuppressWarnings("CPD-START")
    public AnotherConcurrentGUIWithLambdas() {
        JFrameUtil.dimensionJFrame(this);
        final JPanel panel = new JPanel();
        panel.add(display);
        panel.add(up);
        panel.add(down);
        panel.add(stop);
        this.getContentPane().add(panel);
        this.setVisible(true);
        up.addActionListener(e -> counterAgent.upCounting());
        down.addActionListener(e -> counterAgent.downCounting());
        stop.addActionListener(e -> this.stopCounting());
        new Thread(counterAgent).start();
        new Thread(() -> {
            try {
                Thread.sleep(WAITING_TIME);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            this.stopCounting();
        }).start();
    }

    private void stopCounting() {
        counterAgent.stopCounting();
        SwingUtilities.invokeLater(() -> {
            stop.setEnabled(false);
            up.setEnabled(false);
            down.setEnabled(false);
        });
    }

    private final class CounterAgent implements Runnable, Serializable {

        @Serial
        private static final long serialVersionUID = 1L;
        private volatile boolean stop;
        private volatile boolean up = true;
        private int counter;

        @Override
        public void run() {
            while (!stop) {
                try {
                    final var nextText = Integer.toString(counter);
                    SwingUtilities.invokeAndWait(() -> display.setText(nextText));
                    counter += up ? 1 : -1;
                    Thread.sleep(100);
                } catch (InterruptedException | InvocationTargetException ex) {
                    ex.printStackTrace();
                }
            }
        }
        public void stopCounting() {
            this.stop = true;
        }
        public void upCounting() {
            this.up = true;
        }
        public void downCounting() {
            this.up = false;
        }
    }
}
