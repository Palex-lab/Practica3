import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Client {

    String appName = "Xat en Swing";
    Client mainGUI;
    JFrame newFrame = new JFrame(appName);
    JFrame preFrame;
    JButton btEnviar;
    JList lista;
    DefaultListModel model;
    private boolean conectado = false;
    private MySocket mysocket;
    JTextArea missatgesXat;
    String nickname;

    Toolkit pantalla = Toolkit.getDefaultToolkit();
    Dimension tamanyPantalla = pantalla.getScreenSize();
    int alturaPantalla = tamanyPantalla.height;
    int amplePantalla = tamanyPantalla.width;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | IllegalAccessException | InstantiationException
                        | UnsupportedLookAndFeelException e) {
                }
                Client mainGUI = new Client();
                mainGUI.preDisplay();
            }
        });
    }

    public Client() {
        super();
        this.mysocket = new MySocket("localhost", 8001);
        // System.out.println("Conectado: " + socket);
    }

    public void preDisplay() {

        LaminaPreDisplay laminapre = new LaminaPreDisplay();

        preFrame = new JFrame("Entrada d'usuari");
        preFrame.setSize(amplePantalla / 2, alturaPantalla / 2);
        preFrame.setLocation(amplePantalla / 4, alturaPantalla / 4);
        preFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        preFrame.setVisible(true);
        preFrame.add(laminapre);

        newFrame.setVisible(false);

    }

    public void display() {

        LaminaDisplay lamina = new LaminaDisplay();


        newFrame.setSize(amplePantalla / 2, alturaPantalla / 2);
        newFrame.setLocation(amplePantalla / 4, alturaPantalla / 4);
        newFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        newFrame.setVisible(true);
        newFrame.add(lamina); 

        newFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        newFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                mysocket.println("Adeu");
                mysocket.close();
                newFrame.dispose();
                System.exit(0);
            }
        });


    }

    
  
    class OutputThread extends Thread {
        public void run() {
            while (conectado) {
                String msg;
                msg = mysocket.readLine();
                switch (msg) {
                    case "Adeu":
                        conectado = false;
                        break;
                    case ".updateList":
                        updateUsers();
                        break;
                    default:
                        missatgesXat.append(msg + "\n");
                        break;
                }
            }
        }
    }

    public int close() {
        mysocket.close();
        newFrame.dispose();
        return 0;
    }

    public void updateUsers() {
        String u;
        u = mysocket.readLine();
        String[] l = u.split(" ");
        model.removeAllElements();
        for (String l1 : l) {
            model.addElement(l1);
        }
    }

    class LaminaPreDisplay extends JPanel {
        private JTextField campnick;

        private JLabel resultat;

        public LaminaPreDisplay() {
            setLayout(new BorderLayout());

            JPanel milamina2 = new JPanel();
            milamina2.setLayout(new FlowLayout());

            resultat = new JLabel("", JLabel.CENTER);

            JLabel text1 = new JLabel("Nom d'usuari: ");
            milamina2.add(text1);

            campnick = new JTextField(20);
            milamina2.add(campnick);

            add(resultat, BorderLayout.CENTER);

            JButton entrarServidor = new JButton("Entrar al xat");

            campnick.addActionListener(new butoEntrarServidor());
            entrarServidor.addActionListener(new butoEntrarServidor());

            milamina2.add(entrarServidor);

            add(milamina2, BorderLayout.CENTER);
        }

        private class butoEntrarServidor implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent e) {
                nickname = campnick.getText();
                if (nickname.length() < 1) {
                } else {
                    mysocket.println(nickname);
                    String condicion = mysocket.readLine();
                    if (condicion.equals("DNC")) {
                        JOptionPane.showMessageDialog(null, "Aquest usuari ja existeix. Prova-ho de nou!", "Error!",
                                JOptionPane.INFORMATION_MESSAGE);
                        campnick.setText("");
                    } else if (condicion.equals("DC")) {
                        preFrame.setVisible(false);

                        conectado = true;
                        display();
                        OutputThread outThread = new OutputThread();
                        outThread.start();
                    }
                }

            }

        }

    }

    class LaminaDisplay extends JPanel {

        //private JTextArea missatgesXat;
        private JTextField campmissatge;

        public LaminaDisplay() {
            missatgesXat = new JTextArea();
            missatgesXat.setEnabled(false);
            missatgesXat.setLineWrap(true);
            missatgesXat.setWrapStyleWord(true);
            JScrollPane laminaText = new JScrollPane(missatgesXat);
            add(laminaText);

            campmissatge = new JTextField("");
            campmissatge.requestFocusInWindow();
            campmissatge.addActionListener(new butoEnviarMissatge());

            btEnviar = new JButton("Enviar");
            btEnviar.setBackground(Color.BLUE);
            btEnviar.addActionListener(new butoEnviarMissatge());
            model = new DefaultListModel();
            lista = new JList(model);

            Container c = newFrame.getContentPane();
            c.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();

            // Quadre dels missatges
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 2;
            gbc.weightx = 1;
            gbc.weighty = 1;
            gbc.fill = GridBagConstraints.BOTH;
            c.add(laminaText, gbc);

            // Llista usuaris
            gbc.gridx = 2;
            gbc.gridy = 0;
            gbc.gridwidth = 2;
            gbc.weightx = 1;
            gbc.weighty = 1;
            gbc.fill = GridBagConstraints.BOTH;
            c.add(lista, gbc);

            // Text (missatges)
            gbc.gridwidth = 1;
            gbc.weighty = 0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(0, 10, 10, 10);
            gbc.gridx = 0;
            gbc.gridy = 1;
            c.add(campmissatge, gbc);

            // Botó enviar
            gbc.weightx = 0;
            gbc.gridx = 2;
            gbc.gridy = 1;
            gbc.anchor = GridBagConstraints.LINE_END;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            c.add(btEnviar, gbc);


        }

        private class butoEnviarMissatge implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (campmissatge.getText().length() < 1) {
                    
                } else {
                    missatgesXat.append("Tú: " + campmissatge.getText() + "\n");
                    mysocket.println(campmissatge.getText());
                    campmissatge.setText("");
                }
                campmissatge.requestFocusInWindow();

            }

        }
    } 
}