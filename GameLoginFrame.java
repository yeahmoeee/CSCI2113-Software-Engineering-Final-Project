import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class GameLoginFrame extends JFrame{
    public JLabel logotitle;
    public JTextField username;
    public JButton login;
    public JPanel login_panel;
    public Socket server;
    public PrintWriter bufferedWriter;
    public BufferedReader bufferedReader;

    public JFrame frame_link = this;

    public static void main(String [] args){
        GameLoginFrame login_frame = new GameLoginFrame();
    }
    public GameLoginFrame() {
        super();
        GUI_initial_settings(); 
        this.login.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    server = new Socket("localhost", 8080);
                    bufferedWriter = new PrintWriter(server.getOutputStream(), true);
                    bufferedReader = new BufferedReader(new InputStreamReader(server.getInputStream()));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                if (username.getText().contains(" ")) {
                    JFrame error_frame = new JFrame();
                    JOptionPane.showMessageDialog(error_frame,
                    "Username cannot contain space.",
                    "Error Message",
                    JOptionPane.ERROR_MESSAGE);
                    error_frame.setVisible(true);
                    error_frame.dispose();
                    return;
                }
                bufferedWriter.write("LOGIN_VALIDATION\n" + username.getText() + "\n"); 
                bufferedWriter.flush();
                try {
                    String approval = bufferedReader.readLine();
                    if (approval.equals("APPROVED_VALIDATION")){
                        setVisible(false);
                        Board_PVP_GUI game_frame = new Board_PVP_GUI(username.getText(), frame_link);
                    }
                    else {
                        JFrame error_frame = new JFrame();
                        JOptionPane.showMessageDialog(error_frame,
                        "Username has already been taken.",
                        "Error Message",
                        JOptionPane.ERROR_MESSAGE);
                        error_frame.setVisible(true);
                        error_frame.dispose();
                        return;
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }


            }
            
        });

        this.username.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if(e.getKeyChar() == '\n') { 
                    login.doClick();
                }
            }
            @Override
            public void keyPressed(KeyEvent e) {}
            @Override
            public void keyReleased(KeyEvent e) {}
        });
    }
    public void GUI_initial_settings(){
        ImageIcon game_logo = new ImageIcon("logo.jpg");
        Image original_logo = game_logo.getImage();
        Image scaled_logo = original_logo.getScaledInstance(200, 200, Image.SCALE_DEFAULT);
        ImageIcon new_game_logo = new ImageIcon(scaled_logo);
        this.logotitle= new JLabel(new_game_logo);
        
        this.login_panel = new JPanel(new FlowLayout());
        this.username = new JTextField("",15);
        this.login = new JButton("Join");

        this.login_panel.add(new JLabel("Username"));
        this.login_panel.add(this.username);
        this.login_panel.add(this.login);
        this.add(this.logotitle, BorderLayout.NORTH);
        this.add(this.login_panel);
        this.setPreferredSize(new Dimension(400,300));
        this.setTitle("Ultimate Tic-Tac-Toe");
        this.setVisible(true);
        this.pack();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
}
