import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.regex.Pattern;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Board_PVP_GUI extends JFrame {
    public GridLayout grid_layout;
    public JPanel chatbox;
    public JTextArea messages;
    public JPanel send_area;
    public JTextArea compose;
    public JScrollPane scrollPane_messages;
    public JScrollPane scrollPane_members;
    public DefaultListModel<String> waitlistModel;
    public JList<String> waitlist;
    public JButton send;
    public JButton disconnect;
    public JPanel waitlist_disconnect;
    public JPanel board;
    public JFrame waiting_room;

    public DefaultListModel<String> waiting_room_waitlist_model;
    public JList<String> waiting_room_waitlist;
    public JScrollPane scrollPane_members_waitlist;

    public DefaultListModel<String> practice_room_waitlist_model;
    public JList<String> practice_room_waitlist;
    public JScrollPane scrollPane_members_practice;

    public Socket server; 
    public char [][][] cells;
    public char [] board_status;
    public int [][] winning_position = {{0,1,2},{3,4,5},{6,7,8},{0,3,6},{1,4,7},{2,5,8},{0,4,8},{2,4,6}};
    public BufferedReader bufferedReader;
    public PrintWriter bufferedWriter;

    public JPanel sub_panel0;
    public JPanel sub_panel1;
    public JPanel sub_panel2;
    public JPanel sub_panel3;
    public JPanel sub_panel4;
    public JPanel sub_panel5;
    public JPanel sub_panel6;
    public JPanel sub_panel7;
    public JPanel sub_panel8;


    boolean isPlayer = false;

    public String username;
    public String representation = "-";
    public JButton [][] buttons;
    public JPanel [] panels;

    public boolean dummy = true;
    public JFrame login_frame;

    public Board_PVP_GUI(String username, JFrame login_frame) throws UnknownHostException, IOException {
        super();
        GUI_initial_settings(); 
        this.login_frame = login_frame;
        this.username = username;  
        try {
            this.server = new Socket("localhost", 8080); 
            this.bufferedWriter = new PrintWriter(this.server.getOutputStream(), true);
            this.bufferedReader = new BufferedReader(new InputStreamReader(this.server.getInputStream()));
        } catch (Exception d) {
            JFrame error_frame = new JFrame();
            JOptionPane.showMessageDialog(error_frame,
            "Invalid IP Address/Port.",
            "Error Message",
            JOptionPane.ERROR_MESSAGE);
            error_frame.setVisible(true);
            error_frame.dispose();
            System.exit(0);
            return;
        } 
        
        try {
            bufferedWriter.write("SECRET\n" +"3c3c4ac618656ae32b7f3431e75f7b26b1a14a87\n" + "NAME\n" + username + "\n");
            bufferedWriter.flush();
        } catch (Exception e) {
            closeEverything(this.server, this.bufferedReader, this.bufferedWriter);
        }
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9;j++) {
                buttons[i][j].setEnabled(false);
            }
        }
        listenForMessage();
    
        this.disconnect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    if (isPlayer) {
                        bufferedWriter.println("RESET_GAME");
                        reset();
                        bufferedWriter.println("FNAJDSIFNIJNSDIFNISDNIFGUBIFDAISFIOT");
                        bufferedWriter.flush();
                        bufferedWriter.println(username);
                        bufferedWriter.flush();
                    }
                    else {
                        bufferedWriter.println("FNAJDSIFNIJNSDIFNISDNIFGUBIFDAISFIOTE");
                        bufferedWriter.flush();
                        bufferedWriter.println(username);
                        bufferedWriter.flush();
                    }
                } catch (Exception f) {
                    closeEverything(server, bufferedReader, bufferedWriter);
                }
                login_frame.setVisible(true);
                compose.setEditable(false);
                messages.setText("");
                waiting_room_waitlist_model.clear();
                waitlistModel.clear();
                practice_room_waitlist_model.clear();
                dispose();

            }
        });

        this.send.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                bufferedWriter.println("[" + username + "]: " + compose.getText());
                messages.append("[" + username + "]: " + compose.getText() + "\n");
                bufferedWriter.flush();
                compose.setText("");
                }
        });

        this.compose.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if(e.getKeyChar() == '\n') { 
                    bufferedWriter.print("[" + username + "]: " + compose.getText());
                    messages.append("[" + username + "]: " + compose.getText());
                    bufferedWriter.flush();
                    compose.setText("");
                    }
                }
            @Override
            public void keyPressed(KeyEvent e) {}
            @Override
            public void keyReleased(KeyEvent e) {}
        });

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    if (isPlayer) {
                        bufferedWriter.println("RESET_GAME");
                        reset();
                        bufferedWriter.println("FNAJDSIFNIJNSDIFNISDNIFGUBIFDAISFIOT");
                        bufferedWriter.flush();
                        bufferedWriter.println(username);
                        bufferedWriter.flush();
                    }
                    else {
                        bufferedWriter.println("FNAJDSIFNIJNSDIFNISDNIFGUBIFDAISFIOTE");
                        bufferedWriter.flush();
                        bufferedWriter.println(username);
                        bufferedWriter.flush();
                    }
                } catch (Exception f) {
                    closeEverything(server, bufferedReader, bufferedWriter);
                }
                compose.setEditable(false);
                messages.setText("");
                waiting_room_waitlist_model.clear();
                waitlistModel.clear();
                practice_room_waitlist_model.clear();
                dispose();
                //disconnect.doClick();
                System.out.println("Server closed successfully."); 
                System.exit(0);
            }
         });
    }
    

    public void listenForMessage() {
        new Thread(new Runnable() {
            @Override 
            public void run() {
                try {
                    while (server.isConnected()) {
                        String message = bufferedReader.readLine();
                        if (message == null) {
                            break;
                        }
                        else if (message.equals("START_CLIENT_LIST")) {
                            for (int j = 0; j < 9; j++) {
                                for (int k = 0; k < 9; k++) {
                                    buttons[j][k].setEnabled(false);
                                }
                            } 
                            waiting_room_waitlist_model.clear();
                            practice_room_waitlist_model.clear();
                            waitlistModel.clear();
                            int i = 0;
                            while (true) {
                                String clientName = bufferedReader.readLine();
    
                                if (clientName.contains(username)) { 
                                    if (clientName.contains("(Player)")) {
                                        isPlayer = true;
                                        if (i == 0) {
                                            representation = "O";
                                            for (int j = 0; j < 9; j++) {
                                                for (int k = 0; k < 9; k++) {
                                                    buttons[j][k].setEnabled(true);
                                                }
                                            }
                                        }
                                        else {
                                            representation = "X";
                                            for (int j = 0; j < 9; j++) {
                                                for (int k = 0; k < 9; k++) {
                                                    buttons[j][k].setEnabled(true);
                                                }
                                            }
                                        }
                                    }
                                    else {  
                                        for (int j = 0; j < 9; j++) {
                                            for (int k = 0; k < 9; k++) {
                                                buttons[j][k].setEnabled(false);
                                            }
                                        }
                                        isPlayer = false;
                                    }
                                }
                
                                i += 1;
                                if (clientName.equals("END_CLIENT_LIST")) {
                                    if (!isPlayer) {
                                        setVisible(false);
                                        waiting_room.setLayout(new BorderLayout());
                                        waiting_room.setVisible(true);
                                        waiting_room.setSize(450,550);
                                        waiting_room.setTitle("Ultimate Tic-Tac-Toe - Waiting Room");   
                                        JPanel waiting_room_top = new JPanel(new FlowLayout());
                                        waiting_room_top.add(new JLabel("\uD83C\uDF10 Players on Server"));

                                        JButton waiting_room_disconnect = new JButton("Disconnect");
                                        waiting_room_disconnect.addActionListener(new ActionListener() {
                                            public void actionPerformed(ActionEvent e) {
                                                try {
                                                    bufferedWriter.println("FNAJDSIFNIJNSDIFNISDNIFGUBIFDAISFIOTE");
                                                    bufferedWriter.flush();
                                                    bufferedWriter.println(username);
                                                    bufferedWriter.flush();
                                                } catch (Exception f) {
                                                    closeEverything(server, bufferedReader, bufferedWriter);
                                                }
                                                compose.setEditable(false);
                                                messages.setText("");
                                                waiting_room_waitlist_model.clear();
                                                practice_room_waitlist_model.clear();
                                                waitlistModel.clear();
                                                System.exit(0);
                                                waiting_room.dispose();
                                                dispose();
                                                //should implement the back that jump back to the login frame
                                
                                            }
                                        });
                                        waiting_room.addWindowListener(new WindowAdapter() {
                                            @Override
                                            public void windowClosing(WindowEvent e) {
                                                waiting_room_disconnect.doClick();
                                                System.exit(0);
                                            }
                                         });
                                        waiting_room_top.add(waiting_room_disconnect);
                                        waiting_room.add(waiting_room_top, BorderLayout.NORTH);

                                        JPanel waiting_room_mid = new JPanel();
                                        waiting_room_mid.add(scrollPane_members_waitlist, BorderLayout.CENTER);
                                        waiting_room.add(waiting_room_mid);

                                        JPanel waiting_room_bot = new JPanel(new FlowLayout());
                                        JButton spectate = new JButton("Spectate");
                                        JButton practice = new JButton("Practice");
                                        waiting_room_bot.add(practice);
                                        waiting_room_bot.add(spectate);
                                        waiting_room.add(waiting_room_bot, BorderLayout.SOUTH);
                                        waiting_room.pack();
                                        spectate.addActionListener(new ActionListener() {
                                            @Override
                                            public void actionPerformed(ActionEvent e) {
                                                //bufferedWriter.println("UPDATE_LIST");
                                                setVisible(true);
                                                waiting_room.setVisible(false);
                                            }
                                            
                                        });
                                        practice.addActionListener(new ActionListener() {

                                            @Override
                                            public void actionPerformed(ActionEvent e) {
                                                waiting_room.setVisible(false);
                                                AIBoardFrame frame = new AIBoardFrame(scrollPane_members_practice, waiting_room_disconnect, spectate, waiting_room);
                                                frame.setVisible(true);
                                            }
                                            
                                        });

                                    }
                                    else {
                                        waiting_room.setVisible(false);
                                        setVisible(true);
                                    }
                                    break;
                                }
                                waiting_room_waitlist_model.addElement(clientName);
                                practice_room_waitlist_model.addElement(clientName);
                                waitlistModel.addElement(clientName);
                            }
                                
                        } 
                        else if (Board_PVP_GUI.matchServerEntrance(message)){
                            String name = bufferedReader.readLine();
                            if (waitlistModel.getSize() >= 2) {
                                waiting_room_waitlist_model.addElement(name);
                                practice_room_waitlist_model.addElement(name);
                                waitlistModel.addElement(name);
                            }
                            else {
                                waiting_room_waitlist_model.addElement(name + " (Player)");
                                practice_room_waitlist_model.addElement(name + " (Player)");
                                waitlistModel.addElement(name + " (Player)");
                            }
                        }
                        else if (Board_PVP_GUI.matchServerExit(message)){
                            String name = bufferedReader.readLine();
                            for (int i = 0; i < waitlistModel.getSize(); i++) {
                                if (waitlistModel.get(i).contains(name)) {
                                    waiting_room_waitlist_model.remove(i);
                                    practice_room_waitlist_model.remove(i);
                                    waitlistModel.remove(i);
                                    break;
                                }
                            }

                            if (waitlistModel.getSize() == 0) {
                                continue;
                            }
                            if (!waitlistModel.get(0).contains("(Player)")){
                                waitlistModel.set(0, waitlistModel.get(0) + " (Player)");
                                waiting_room_waitlist_model.set(0, waiting_room_waitlist_model.get(0) + " (Player)");
                                practice_room_waitlist_model.set(0, practice_room_waitlist_model.get(0) + " (Player)");
                            }

                            if (waitlistModel.getSize() == 1) {
                                continue;
                            }

                            if (!waitlistModel.get(1).contains("(Player)")){
                                waitlistModel.set(1, waitlistModel.get(1) + " (Player)");
                                waiting_room_waitlist_model.set(1, waiting_room_waitlist_model.get(1) + " (Player)");
                                practice_room_waitlist_model.set(1, practice_room_waitlist_model.get(1) + " (Player)");
                            }
                            
                        } 
                        
                        else if (message.equals("SENDBACK_UPDATE_COLOR")){
                            if (!isPlayer) {
                                for (int i = 0; i < 9; i++) {
                                    String i_subboard_string = bufferedReader.readLine();
                                    String [] i_subboard = i_subboard_string.split(" ");
                                    for (int j = 0; j < 9; j++) {
                                        buttons[i][j].setText(i_subboard[j]);
                                    }
                                    if (i_subboard[9].equals("COLOR_UPDATE_RED")){
                                        panels[i].setBackground(Color.RED);
                                    }
                                    else if (i_subboard[9].equals("COLOR_UPDATE_BLUE")) {
                                        panels[i].setBackground(Color.BLUE);
                                    }
                                    else if (i_subboard[9].equals("COLOR_UPDATE_GREEN")) {
                                        panels[i].setBackground(Color.GREEN);
                                    }
                                    else {
                                        panels[i].setBackground(Color.LIGHT_GRAY);
                                    }
                                }
                            }
                            else if (message.equals("SENDBACK_UPDATE")) {
                                if (!isPlayer) {
                                    for (int i = 0; i < 9; i++) {
                                        String [] i_subboard = bufferedReader.readLine().split(" ");
                                        for (int j = 0; j < 9; j++) {
                                            buttons[i][j].setText(i_subboard[j]);
                                        }
                                    }
                                }
                                else {
                                    for (int i = 0; i < 9; i++) {
                                        bufferedReader.readLine();
                                    }
                                }
                            }
                            else {

                                for (int i = 0; i < 9; i++) {
                                    String i_subboard_string = bufferedReader.readLine();
                                    String [] i_subboard = i_subboard_string.split(" ");
                                    if (i_subboard[9].equals("COLOR_UPDATE_RED")){
                                        panels[i].setBackground(Color.RED);
                                    }
                                    else if (i_subboard[9].equals("COLOR_UPDATE_BLUE")) {
                                        panels[i].setBackground(Color.BLUE);
                                    }
                                    else if (i_subboard[9].equals("COLOR_UPDATE_GREEN")) {
                                        panels[i].setBackground(Color.GREEN);
                                    }
                                    else {
                                        panels[i].setBackground(Color.LIGHT_GRAY);
                                    }
                                }
                            }
                        }
                        else if (message.equals("SENDBACK_KEY_COLOR")) {
                            if (isPlayer) {
                                String i_row_col_color_str = bufferedReader.readLine();
                                String [] i_row_col_color = i_row_col_color_str.split(" ");
                                int i = Integer.parseInt(i_row_col_color[0]);
                                int row = Integer.parseInt(i_row_col_color[1]);
                                int col = Integer.parseInt(i_row_col_color[2]);
                                String color = i_row_col_color[3];
                                if (color.equals("COLOR_UPDATE_RED")){
                                    panels[i].setBackground(Color.RED);
                                }                               
                                else if (color.equals("COLOR_UPDATE_BLUE")){
                                    panels[i].setBackground(Color.BLUE);
                                }
                                else if (color.equals("COLOR_UPDATE_GREEN")){
                                    panels[i].setBackground(Color.GREEN);
                                }
                                else {
                                    panels[i].setBackground(Color.LIGHT_GRAY);
                                }
                                if (representation.equals("O")){
                                    buttons[i][3 * row + col].setEnabled(true);
                                    buttons[i][3 * row + col].setText("X");
                                    cells[i][row][col] = 'X';
                                    StringBuilder str = new StringBuilder();
                                    str.append("UPDATE_SPECTATE_COLOR\n");
                                    for (int a = 0; a < 9; ++a) {
                                        for (int b = 0; b < 9; ++b) {
                                            if (b != 8) {
                                                str.append(buttons[a][b].getText() + " ");
                                            }
                                            else {
                                                if (panels[a].getBackground().equals(Color.RED)){
                                                    str.append(buttons[a][b].getText() + " " + "COLOR_UPDATE_RED");
                                                }
                                                else if (panels[a].getBackground().equals(Color.BLUE)){
                                                    str.append(buttons[a][b].getText() + " " + "COLOR_UPDATE_BLUE");
                                                }
                                                else if (panels[a].getBackground().equals(Color.GREEN)){
                                                    str.append(buttons[a][b].getText() + " " + "COLOR_UPDATE_GREEN");
                                                }
                                                else {
                                                    str.append(buttons[a][b].getText() + " " + "COLOR_UPDATE_NO");
                                                }
                                            }
                                        }
                                        str.append("\n");
                                    }
                                    bufferedWriter.print(str);
                                    bufferedWriter.flush();
                                    if (is_winner_subboard(i,'X') && board_status[i] == '?'){
                                        board_status[i] = 'X';
                                        panels[i].setBackground(Color.BLUE); 
                                        if (is_winner('X')) {
                                            bufferedWriter.print("WIN_MESSAGE\n" + waitlistModel.get(1).split(" ")[0] + " has won the game.\n");
                                            bufferedWriter.flush();
                                            bufferedWriter.print("REORDER_CLIENT\n" + waitlistModel.get(0).split(" ")[0] + "\n");    
                                            bufferedWriter.flush();
                                            bufferedWriter.print("RESET_GAME\n");
                                            bufferedWriter.flush();
                                            reset();
                                            //implement the part to add the loser to the end of the server, for now loser just disconnect
                                            continue;
                                        }
                                    }
                                }
                                else {
                                    buttons[i][3 * row + col].setEnabled(true);
                                    buttons[i][3 * row + col].setText("O");
                                    cells[i][row][col] = 'O';
                                    StringBuilder str = new StringBuilder();
                                    str.append("UPDATE_SPECTATE_COLOR\n");
                                    for (int a = 0; a < 9; ++a) {
                                        for (int b = 0; b < 9; ++b) {
                                            if (b != 8) {
                                                str.append(buttons[a][b].getText() + " ");
                                            }
                                            else {
                                                if (panels[a].getBackground().equals(Color.RED)){
                                                    str.append(buttons[a][b].getText() + " " + "COLOR_UPDATE_RED");
                                                }
                                                else if (panels[a].getBackground().equals(Color.BLUE)){
                                                    str.append(buttons[a][b].getText() + " " + "COLOR_UPDATE_BLUE");
                                                }
                                                else if (panels[a].getBackground().equals(Color.GREEN)){
                                                    str.append(buttons[a][b].getText() + " " + "COLOR_UPDATE_GREEN");
                                                }
                                                else {
                                                    str.append(buttons[a][b].getText() + " " + "COLOR_UPDATE_NO");
                                                }
                                            }
                                        }
                                        str.append("\n");
                                    }
                                    bufferedWriter.print(str);
                                    bufferedWriter.flush();
                                    if (is_winner_subboard(i,'O') && board_status[i] == '?'){
                                        board_status[i] = 'O';
                                        panels[i].setBackground(Color.RED); 
                                        if (is_winner('O')) {
                                            bufferedWriter.print("WIN_MESSAGE\n" + waitlistModel.get(0).split(" ")[0] + " has won the game.\n");
                                            bufferedWriter.flush();
                                            bufferedWriter.print("REORDER_CLIENT\n" + waitlistModel.get(1).split(" ")[0] + "\n");             
                                            bufferedWriter.flush();
                                            bufferedWriter.print("RESET_GAME\n");
                                            bufferedWriter.flush();
                                            reset();
                                            continue;
                                        }
                                    }
                                }
                                buttons[i][3 * row + col].setEnabled(false);

                    
                                if (is_full_subboard(3 * row + col) && !is_winner_subboard(3 * row + col, 'X') && !is_winner_subboard(3 * row + col, 'O') && board_status[3 * row + col] == '?'){
                                    panels[3 * row + col].setBackground(Color.GREEN); 
                                    board_status[3 * row + col] = 'D';
                                }

                                if (is_full()) {
                                    Random random = new Random();
                                    int randomNumber = random.nextInt(2);
                                    bufferedWriter.print("WIN_MESSAGE\n" + "The game is draw. One of the players is randomly drawn to play next round.\n");
                                    bufferedWriter.flush();
                                    bufferedWriter.print("REORDER_CLIENT\n" + waitlistModel.get(randomNumber).split(" ")[0]+ "\n");             
                                    bufferedWriter.flush();
                                    bufferedWriter.print("RESET_GAME\n");
                                    bufferedWriter.flush();
                                    reset();
                                }


                                if (is_full_subboard(3 * row + col)) {
                                    for (int x = 0; x < 9; ++x) {
                                        for (int y = 0; y < 9; ++y ){
                                            if (!buttons[x][y].getText().equals("-")) {
                                                buttons[x][y].setEnabled(false);
                                            }
                                            else {
                                                buttons[x][y].setEnabled(true);
                                            }
                                        }
                                    }
                                }
                                else {
                                    for (int j = 0; j < 9; ++j) {
                                        for (int k = 0; k < 9; ++k) {
                                            if (j != 3 * row + col) {
                                                buttons[j][k].setEnabled(false);
                                            } else {
                                                if (buttons[j][k].getText().equals("-")){
                                                    buttons[j][k].setEnabled(true);
                                                }
                                                else {
                                                    buttons[j][k].setEnabled(false);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            else {
                                bufferedReader.readLine();
                            }

                        }
                        else if (message.equals("SENDBACK_RESET")) {
                            reset();
                        }
                        else if (message.equals("")){
                            continue;
                        }
    
                        else {
                            messages.append(message + "\n");
                        }


                }
            } catch (IOException e) {
                closeEverything(server, bufferedReader, bufferedWriter);
            }
        } }).start();
    }

    public static boolean matchServerEntrance(String sentence) {
        String regex = "^SERVER: (.+?) has entered the chat!$";
        Pattern pattern = Pattern.compile(regex);
        java.util.regex.Matcher matcher = pattern.matcher(sentence);
        return matcher.matches();
    }
    public static boolean matchServerExit(String sentence) {
        String regex = "^SERVER: (.+?) has left the chat!$";
        Pattern pattern = Pattern.compile(regex);
        java.util.regex.Matcher matcher = pattern.matcher(sentence);
        return matcher.matches();
    }
    

    public void GUI_initial_settings() {
        this.setTitle("Ultimate Tic-Tac-Toe - Player vs Player"); 
        this.setSize(1500,1500);
        this.grid_layout = new GridLayout(3,3);
        this.chatbox = new JPanel();
        this.chatbox.setLayout(new BoxLayout(this.chatbox, BoxLayout.Y_AXIS)); 
        this.waiting_room = new JFrame();
        this.waiting_room.setVisible(false);
        
        this.messages = new JTextArea();
        this.messages.setLineWrap(true);
        this.messages.setWrapStyleWord(true);
        this.messages.setEditable(false);

        this.send_area = new JPanel();
        this.compose = new JTextArea();
        this.compose.setPreferredSize(new Dimension(200, 25));
        this.compose.setLineWrap(true); 
        this.compose.setWrapStyleWord(true);
        this.compose.setEditable(true);

        this.scrollPane_messages = new JScrollPane(messages);
        this.scrollPane_messages.setPreferredSize(new Dimension(100,800));
        this.scrollPane_messages.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        DefaultCaret caret = (DefaultCaret)this.messages.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);


        this.send = new JButton("Send"); 
        this.send_area.add(this.compose);
        this.send_area.add(this.send); 

      


        this.chatbox.add(new JLabel("\ud83d\udde3 Chat Room"));
        this.chatbox.add(this.scrollPane_messages);
        this.chatbox.add(this.send_area);
        this.add(chatbox, BorderLayout.WEST);

        this.waitlistModel = new DefaultListModel<String>();
        this.waitlist = new JList<String>(this.waitlistModel);

        this.waiting_room_waitlist_model = new DefaultListModel<>();
        this.waiting_room_waitlist = new JList<String>(this.waiting_room_waitlist_model);

        this.practice_room_waitlist_model = new DefaultListModel<>();
        this.practice_room_waitlist = new JList<String>(this.practice_room_waitlist_model);

        this.scrollPane_members_practice = new JScrollPane(this.practice_room_waitlist);
        this.scrollPane_members_practice.setPreferredSize(new Dimension(200, 900));
        this.scrollPane_members_practice.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        this.scrollPane_members_practice.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        this.scrollPane_members_waitlist = new JScrollPane(this.waiting_room_waitlist);
        this.scrollPane_members_waitlist.setPreferredSize(new Dimension(300,400));
        this.scrollPane_members_waitlist.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        this.scrollPane_members_waitlist.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);


        this.scrollPane_members = new JScrollPane(this.waitlist);
        this.scrollPane_members.setPreferredSize(new Dimension(200, 900));
        this.scrollPane_members.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        this.scrollPane_members.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);


        this.waitlist_disconnect = new JPanel();
        this.waitlist_disconnect.setLayout(new BoxLayout(this.waitlist_disconnect, BoxLayout.Y_AXIS));
        this.disconnect = new JButton("Disconnect"); 
        this.waitlist_disconnect.add(this.disconnect);
        this.waitlist_disconnect.add(new JLabel("\uD83C\uDF10 Players on Server"));
        this.waitlist_disconnect.add(this.scrollPane_members);

        this.board = new JPanel();
        this.board.setPreferredSize(new Dimension(800,800));
        this.board.setLayout(grid_layout);  

        this.cells = new char [9][3][3];
        this.board_status = new char [9];

        for (int i = 0; i < 9; ++i) {
            this.board_status[i] = '?';
            for (int row = 0; row < 3; ++row) {
                for (int col = 0; col < 3; ++col) {
                    this.cells[i][row][col] = '?';
                }
            }
        }
        
        buttons = new JButton[9][];
        panels = new JPanel[9];

        this.sub_panel0 = new JPanel();
        this.sub_panel0.setBackground(Color.LIGHT_GRAY);
        sub_panel0.setLayout(grid_layout);
        buttons[0] =  new JButton[9];
        for (int i = 0; i < 9; ++i) {
            buttons[0][i] = new JButton("-");
            final int index = i;
            buttons[0][i].addActionListener(new ActionListener() { 
                public void actionPerformed(ActionEvent e) {
                    String str = "INPUT_MOVE\n" + 0 + " " + (index/3) + " " + (index%3);
                    buttons[0][index].setText(representation);          
                    cells[0][index/3][index%3] = representation.charAt(0);  
                    if (is_winner_subboard(0,representation.charAt(0)) && board_status[0] == '?'){
                        board_status[0] = representation.charAt(0);
                        if (is_winner(representation.charAt(0))) {
                            reset();
                        }
                    } 
                    if (is_full_subboard(0) && !is_winner_subboard(0, 'X') && !is_winner_subboard(0, 'O') && board_status[0] == '?'){
                        board_status[0] = 'D';
                    }

                    if (board_status[0] == 'O') {
                        str += "\nCOLOR_UPDATE_RED";
                        sub_panel0.setBackground(Color.RED); 
                    }
                    else if (board_status[0] == 'X') { 
                        str += "\nCOLOR_UPDATE_BLUE";
                        sub_panel0.setBackground(Color.BLUE);
                    }
                    else if (board_status[0] == '?'){
                        str += "\nCOLOR_UPDATE_NO";
                        sub_panel0.setBackground(Color.LIGHT_GRAY);
                    }
                    else if (board_status[0] == 'D') {
                        str += "\nCOLOR_UPDATE_GREEN";
                        sub_panel0.setBackground(Color.GREEN);
                    }


                    if (is_full()) {
                        reset();
                    }
                    for (int j = 0; j < 9; j++) {
                        for (int k = 0; k < 9; k++){
                            buttons[j][k].setEnabled(false);
                        }
                    }
                    bufferedWriter.println(str);
                    bufferedWriter.flush();
                 }
            });
            sub_panel0.add(buttons[0][i]);
        }
        panels[0] = sub_panel0;
        board.add(sub_panel0);
 
        this.sub_panel1 = new JPanel();
        this.sub_panel1.setBackground(Color.LIGHT_GRAY);
        sub_panel1.setLayout(grid_layout);
        buttons[1] =  new JButton[9];
        for (int i = 0; i < 9; ++i) {
            buttons[1][i] = new JButton("-");
            final int index = i;
            buttons[1][i].addActionListener(new ActionListener() { 
                public void actionPerformed(ActionEvent e) {
                    String str = "INPUT_MOVE\n" + 1 + " " + (index/3) + " " + (index%3);
                    cells[1][index/3][index%3] = representation.charAt(0);   
                    buttons[1][index].setText(representation);
                    if (is_winner_subboard(1,representation.charAt(0)) && board_status[1] == '?'){
                        board_status[1] = representation.charAt(0);
                        if (is_winner(representation.charAt(0))) {
                            reset();
                        }
                    }

                    if (is_full_subboard(1) && !is_winner_subboard(1, 'X') && !is_winner_subboard(1, 'O')  && board_status[1] == '?'){
                        board_status[1] = 'D';
                    }
                    if (board_status[1] == 'O') {
                        str += "\nCOLOR_UPDATE_RED";
                        sub_panel1.setBackground(Color.RED); 
                    }
                    else if (board_status[1] == 'X') {
                        str += "\nCOLOR_UPDATE_BLUE";
                        sub_panel1.setBackground(Color.BLUE);
                    }
                    else if (board_status[1] == '?'){
                        str += "\nCOLOR_UPDATE_NO";
                        sub_panel1.setBackground(Color.LIGHT_GRAY);
                    }
                    else if (board_status[1] == 'D'){
                        str += "\nCOLOR_UPDATE_GREEN";
                        sub_panel1.setBackground(Color.GREEN);
                    }

                    if (is_full()) {
                        reset();
                    }
                    
                    for (int j = 0; j < 9; j++) {
                        for (int k = 0; k < 9; k++){
                            buttons[j][k].setEnabled(false);
                        }
                    }
                    bufferedWriter.println(str);
                    bufferedWriter.flush();
                 }
            });
            sub_panel1.add(buttons[1][i]);
        }
        panels[1] = sub_panel1;
        board.add(sub_panel1);

        this.sub_panel2 = new JPanel();
        this.sub_panel2.setBackground(Color.LIGHT_GRAY);
        sub_panel2.setLayout(grid_layout);
        buttons[2] = new JButton[9];
        for (int i = 0; i < 9; ++i) {
            buttons[2][i] = new JButton("-");
            final int index = i;
            buttons[2][i].addActionListener(new ActionListener() { 
                public void actionPerformed(ActionEvent e) {
                    String str = "INPUT_MOVE\n" + 2 + " " + (index/3) + " " + (index%3);
                    cells[2][index/3][index%3] = representation.charAt(0);   
                    buttons[2][index].setText(representation);
                    if (is_winner_subboard(2,representation.charAt(0)) && board_status[2] == '?'){
                        board_status[2] = representation.charAt(0);
                        if (is_winner(representation.charAt(0))) {
                            reset();
                        }
                    }
        
                    if (is_full_subboard(2) && !is_winner_subboard(2, 'X') && !is_winner_subboard(2, 'O')  && board_status[2] == '?'){
                        board_status[2] = 'D';
                    }
                    if (board_status[2] == 'O') {
                        str += "\nCOLOR_UPDATE_RED";
                        sub_panel2.setBackground(Color.RED); 
                    }
                    else if (board_status[2] == 'X') { 
                        str += "\nCOLOR_UPDATE_BLUE";
                        sub_panel2.setBackground(Color.BLUE);
                    }
                    else if (board_status[2] == '?'){
                        str += "\nCOLOR_UPDATE_NO";
                        sub_panel2.setBackground(Color.LIGHT_GRAY);
                    }
                    else if (board_status[2] == 'D') {
                        sub_panel2.setBackground(Color.GREEN); 
                        str += "\nCOLOR_UPDATE_GREEN";
                    }

                    if (is_full()) {
                        reset();
                    } 
                    for (int j = 0; j < 9; j++) {
                        for (int k = 0; k < 9; k++){
                            buttons[j][k].setEnabled(false);
                        }
                    }
                    bufferedWriter.println(str);
                    bufferedWriter.flush();
                 }
            });
            sub_panel2.add(buttons[2][i]);
        }
        panels[2] = sub_panel2;
        this.board.add(sub_panel2);

        this.sub_panel3 = new JPanel();
        this.sub_panel3.setBackground(Color.LIGHT_GRAY);
        sub_panel3.setLayout(grid_layout);
        buttons[3] = new JButton[9];
        for (int i = 0; i < 9; ++i) {
            buttons[3][i] = new JButton("-");
            final int index = i;
            buttons[3][i].addActionListener(new ActionListener() { 
                public void actionPerformed(ActionEvent e) {
                    String str = "INPUT_MOVE\n" + 3 + " " + (index/3) + " " + (index%3);
                    cells[3][index/3][index%3] = representation.charAt(0);   
                    buttons[3][index].setText(representation);
                    if (is_winner_subboard(3,representation.charAt(0)) && board_status[3] == '?'){
                        board_status[3] = representation.charAt(0);
                        if (is_winner(representation.charAt(0))) {
                            reset();
                        }
                    }
                    if (is_full_subboard(3) && !is_winner_subboard(3, 'X') && !is_winner_subboard(3, 'O')  && board_status[3] == '?'){
                        board_status[3] = 'D';
                    }
                    if (board_status[3] == 'O') {
                        str += "\nCOLOR_UPDATE_RED";
                        sub_panel3.setBackground(Color.RED); 
                    }
                    else if (board_status[3] == 'X') { 
                        str += "\nCOLOR_UPDATE_BLUE";
                        sub_panel3.setBackground(Color.BLUE);
                    }
                    else if (board_status[3] == '?'){
                        str += "\nCOLOR_UPDATE_NO";
                        sub_panel3.setBackground(Color.LIGHT_GRAY);
                    }
                    else if (board_status[3] == 'D') {
                        sub_panel3.setBackground(Color.GREEN); 
                        str += "\nCOLOR_UPDATE_GREEN";
                    }

                    if (is_full()) {
                        reset();
                    } 
                    for (int j = 0; j < 9; j++) {
                        for (int k = 0; k < 9; k++){
                            buttons[j][k].setEnabled(false);
                        }
                    }
                    bufferedWriter.println(str);
                    bufferedWriter.flush();
                 }
            });
            panels[3] = sub_panel3;
            sub_panel3.add(buttons[3][i]);
        }
        this.board.add(sub_panel3);

        this.sub_panel4 = new JPanel();
        this.sub_panel4.setBackground(Color.LIGHT_GRAY);
        sub_panel4.setLayout(grid_layout);
        buttons[4] = new JButton[9];
        for (int i = 0; i < 9; ++i) {
            buttons[4][i] = new JButton("-");
            final int index = i;
            buttons[4][i].addActionListener(new ActionListener() { 
                public void actionPerformed(ActionEvent e) {
                    String str = "INPUT_MOVE\n" + 4 + " " + (index/3) + " " + (index%3);
                    cells[4][index/3][index%3] = representation.charAt(0);  
                    buttons[4][index].setText(representation);
                    if (is_winner_subboard(4,representation.charAt(0)) && board_status[4] == '?'){
                        board_status[4] = representation.charAt(0);
                        if (is_winner(representation.charAt(0))) {
                            reset();
                        }
                    }
                    if (is_full_subboard(4) && !is_winner_subboard(4, 'X') && !is_winner_subboard(4, 'O') && board_status[4] == '?'){
                        board_status[4] = 'D';
                    }

                    if (board_status[4] == 'O') {
                        str += "\nCOLOR_UPDATE_RED";
                        sub_panel4.setBackground(Color.RED); 
                    }
                    else if (board_status[4] == 'X') { 
                        str += "\nCOLOR_UPDATE_BLUE";
                        sub_panel4.setBackground(Color.BLUE);
                    }
                    else if (board_status[4] == '?'){
                        str += "\nCOLOR_UPDATE_NO";
                        sub_panel4.setBackground(Color.LIGHT_GRAY);
                    }
                    else if (board_status[4] == 'D') {
                        sub_panel4.setBackground(Color.GREEN); 
                        str += "\nCOLOR_UPDATE_GREEN";
                    }


                    if (is_full()) {
                        reset();
                    }  
                
                    for (int j = 0; j < 9; j++) {
                        for (int k = 0; k < 9; k++){
                            buttons[j][k].setEnabled(false);
                        }
                    }
                    bufferedWriter.println(str);
                    bufferedWriter.flush();
                 }
            });
            sub_panel4.add(buttons[4][i]);
        }
        panels[4] = sub_panel4;
        this.board.add(sub_panel4);

        this.sub_panel5 = new JPanel();
        this.sub_panel5.setBackground(Color.LIGHT_GRAY);
        sub_panel5.setLayout(grid_layout);
        buttons[5] = new JButton[9];
        for (int i = 0; i < 9; ++i) {
            buttons[5][i] = new JButton("-");
            final int index = i;
            buttons[5][i].addActionListener(new ActionListener() { 
                public void actionPerformed(ActionEvent e) {
                    String str = "INPUT_MOVE\n" + 5 + " " + (index/3) + " " + (index%3);
                    cells[5][index/3][index%3] = representation.charAt(0);   
                    buttons[5][index].setText(representation);
                    if (is_winner_subboard(5,representation.charAt(0)) && board_status[5] == '?'){
                        board_status[5] = representation.charAt(0);
                        if (is_winner(representation.charAt(0))) {
                            reset();
                        }
                    }

                    if (is_full_subboard(5) && !is_winner_subboard(5, 'X') && !is_winner_subboard(5, 'O')  && board_status[5] == '?'){
                        board_status[5] = 'D';
                    }
                    if (board_status[5] == 'O') {
                        str += "\nCOLOR_UPDATE_RED";
                        sub_panel5.setBackground(Color.RED); 
                    }
                    else if (board_status[5] == 'X') { 
                        str += "\nCOLOR_UPDATE_BLUE";
                        sub_panel5.setBackground(Color.BLUE);
                    }
                    else if (board_status[5] == '?'){
                        str += "\nCOLOR_UPDATE_NO";
                        sub_panel5.setBackground(Color.LIGHT_GRAY);
                    }
                    else if (board_status[5] == 'D') {
                        sub_panel5.setBackground(Color.GREEN); 
                        str += "\nCOLOR_UPDATE_GREEN";
                    }

                    if (is_full()) {
                        reset();
                    } 
                    for (int j = 0; j < 9; j++) {
                        for (int k = 0; k < 9; k++){
                            buttons[j][k].setEnabled(false);
                        }
                    }
                    bufferedWriter.println(str);
                    bufferedWriter.flush();
                 }
            });
            sub_panel5.add(buttons[5][i]);
        }
        panels[5] = sub_panel5;
        this.board.add(sub_panel5);

        this.sub_panel6 = new JPanel();
        this.sub_panel6.setBackground(Color.LIGHT_GRAY);
        sub_panel6.setLayout(grid_layout);
        buttons[6] = new JButton[9];
        for (int i = 0; i < 9; ++i) {
            buttons[6][i] = new JButton("-");
            final int index = i;
            buttons[6][i].addActionListener(new ActionListener() { 
                public void actionPerformed(ActionEvent e) {
                    String str = "INPUT_MOVE\n" + 6 + " " + (index/3) + " " + (index%3);
                    cells[6][index/3][index%3] = representation.charAt(0);   
                    buttons[6][index].setText(representation);
                    if (is_winner_subboard(6,representation.charAt(0)) && board_status[6] == '?'){
                        board_status[6] = representation.charAt(0);
                        if (is_winner(representation.charAt(0))) {
                            reset();
                        }
                    }
            
                    if (is_full_subboard(6) && !is_winner_subboard(6, 'X') && !is_winner_subboard(6, 'O')  && board_status[6] == '?'){ 
                        board_status[6] = 'D';
                    }
                    if (board_status[6] == 'O') {
                        str += "\nCOLOR_UPDATE_RED";
                        sub_panel6.setBackground(Color.RED); 
                    }
                    else if (board_status[6] == 'X') { 
                        str += "\nCOLOR_UPDATE_BLUE";
                        sub_panel6.setBackground(Color.BLUE);
                    }
                    else if (board_status[6] == '?'){
                        str += "\nCOLOR_UPDATE_NO";
                        sub_panel6.setBackground(Color.LIGHT_GRAY);
                    }
                    else if (board_status[6] == 'D') {
                        sub_panel6.setBackground(Color.GREEN); 
                        str += "\nCOLOR_UPDATE_GREEN";
                    }

                    if (is_full()) {
                        reset();
                    } 
                    for (int j = 0; j < 9; j++) {
                        for (int k = 0; k < 9; k++){
                            buttons[j][k].setEnabled(false);
                        }
                    }
                    bufferedWriter.println(str);
                    bufferedWriter.flush();
                 }
            });
            sub_panel6.add(buttons[6][i]);
        }
        panels[6] = sub_panel6;
        this.board.add(sub_panel6);

        this.sub_panel7 = new JPanel();
        this.sub_panel7.setBackground(Color.LIGHT_GRAY);
        sub_panel7.setLayout(grid_layout);
        buttons[7] = new JButton[9];
        for (int i = 0; i < 9; ++i) {
            buttons[7][i] = new JButton("-");
            final int index = i;
            buttons[7][i].addActionListener(new ActionListener() { 
                public void actionPerformed(ActionEvent e) {
                    String str = "INPUT_MOVE\n" + 7 + " " + (index/3) + " " + (index%3);
                    cells[7][index/3][index%3] = representation.charAt(0);   
                    buttons[7][index].setText(representation);
                    if (is_winner_subboard(7,representation.charAt(0)) && board_status[7] == '?'){
                        board_status[7] = representation.charAt(0);
                        if (is_winner(representation.charAt(0))) {
                            reset();
                        }
                    }
                    if (is_full_subboard(7) && !is_winner_subboard(7, 'X') && !is_winner_subboard(7, 'O')  && board_status[7] == '?'){
                        board_status[7] = 'D';
                    }
                    if (board_status[7] == 'O') {
                        str += "\nCOLOR_UPDATE_RED";
                        sub_panel7.setBackground(Color.RED); 
                    }
                    else if (board_status[7] == 'X') { 
                        str += "\nCOLOR_UPDATE_BLUE";
                        sub_panel7.setBackground(Color.BLUE);
                    }
                    else if (board_status[7] == '?'){
                        str += "\nCOLOR_UPDATE_NO";
                        sub_panel7.setBackground(Color.LIGHT_GRAY);
                    }
                    else if (board_status[7] == 'D') {
                        sub_panel7.setBackground(Color.GREEN); 
                        str += "\nCOLOR_UPDATE_GREEN";
                    }


                    if (is_full()) {
                        reset();
                    } 
                    for (int j = 0; j < 9; j++) {
                        for (int k = 0; k < 9; k++){
                            buttons[j][k].setEnabled(false);
                        }
                    }
                    bufferedWriter.println(str);
                    bufferedWriter.flush();
                 }
            });
            sub_panel7.add(buttons[7][i]);
        }
        panels[7] = sub_panel7;
        this.board.add(sub_panel7);

        this.sub_panel8 = new JPanel();
        this.sub_panel8.setBackground(Color.LIGHT_GRAY);
        sub_panel8.setLayout(grid_layout);
        buttons[8] = new JButton[9];
        for (int i = 0; i < 9; ++i) {
            buttons[8][i] = new JButton("-");
            final int index = i;
            buttons[8][i].addActionListener(new ActionListener() { 
                public void actionPerformed(ActionEvent e) {
                    String str = "INPUT_MOVE\n" + 8 + " " + (index/3) + " " + (index%3);
                    cells[8][index/3][index%3] = representation.charAt(0);   
                    buttons[8][index].setText(representation);
                    if (is_winner_subboard(8,representation.charAt(0)) && board_status[8] == '?'){
                        board_status[8] = representation.charAt(0);
                        if (is_winner(representation.charAt(0))) {
                            reset();
                        }
                    }
                    if (is_full_subboard(8) && !is_winner_subboard(8, 'X') && !is_winner_subboard(8, 'O')  && board_status[8] == '?'){
                        board_status[8] = 'D';
                    }
                    if (board_status[8] == 'O') {
                        str += "\nCOLOR_UPDATE_RED";
                        sub_panel8.setBackground(Color.RED); 
                    }
                    else if (board_status[8] == 'X') { 
                        str += "\nCOLOR_UPDATE_BLUE";
                        sub_panel8.setBackground(Color.BLUE);
                    }
                    else if (board_status[8] == '?'){
                        str += "\nCOLOR_UPDATE_NO";
                        sub_panel8.setBackground(Color.LIGHT_GRAY);
                    }
                    else if (board_status[8] == 'D') {
                        sub_panel8.setBackground(Color.GREEN); 
                        str += "\nCOLOR_UPDATE_GREEN";
                    }


                    if (is_full()) {
                        reset();
                    } 
                    for (int j = 0; j < 9; j++) {
                        for (int k = 0; k < 9; k++){
                            buttons[j][k].setEnabled(false);
                        }
                    }
                    bufferedWriter.println(str);
                    bufferedWriter.flush();
                 }
            });
            sub_panel8.add(buttons[8][i]);
        }
        panels[8] = sub_panel8;
        this.board.add(sub_panel8);

        this.add(this.board, BorderLayout.CENTER);
        this.add(this.waitlist_disconnect, BorderLayout.EAST);
        this.pack();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


    }
    public void closeEverything(Socket socket, BufferedReader bufferedReader, PrintWriter bufferedWriter) {
        try {
            if (bufferedReader != null)
            {
                bufferedReader.close();
            }
            if (bufferedWriter != null)
            {
                bufferedWriter.close();
            }
            if (socket != null)
            {
                socket.close();
            }          
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reset() {
        for (int i = 0; i < this.cells.length; ++ i)
        {
            this.board_status[i] = '?';

            for (int row = 0; row < 3; ++row) {
                for (int col = 0; col < 3; ++col) { 
                    this.cells[i][row][col] = '?';
                    this.buttons[i][3 * row + col].setText("-");
                    this.buttons[i][3 * row + col].setEnabled(true);
                    this.panels[i].setBackground(Color.LIGHT_GRAY);
                }
            }

        }
    }
    
    public boolean is_unused() {
        for (int i = 0; i < 9; i++) {
            for (int row = 0; row < 3; ++row){
                for (int col = 0; col < 3; ++col) {
                    if (this.cells[i][row][col] != '?') {
                        return false;
                    }
                }
            }
        }
        return true;

    }

    public boolean is_full_subboard(int i) {
        for (int row = 0; row < 3; ++row){
            for (int col = 0; col < 3; ++col) {
                if (this.cells[i][row][col] == '?') {
                    return false;
                }
            }
        }
        return true;
    }
    public boolean is_full() {
        for (int i = 0; i < 9; ++ i)
        {
            for (int row = 0; row < 3; ++row) {
                for (int col = 0; col < 3; ++col) {
                    if (this.cells[i][row][col] == '?') {
                    return false;
                    }
                }
            }
        }
        return true;
    }
    public boolean is_winner_subboard(int subboard, char symbol) {
        for (int i = 0; i < this.winning_position.length; ++i) {
            int winning_pos_0 = this.winning_position[i][0];
            int winning_pos_1 = this.winning_position[i][1];
            int winning_pos_2 = this.winning_position[i][2];
            if ((this.cells[subboard][winning_pos_0/3][winning_pos_0%3] == symbol) && (this.cells[subboard][winning_pos_1/3][winning_pos_1%3] == symbol) && (this.cells[subboard][winning_pos_2/3][winning_pos_2%3] == symbol)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean is_winner(char symbol) {
        for (int i = 0; i < this.winning_position.length; ++i) {
            if ((this.board_status[this.winning_position[i][0]] == symbol) && (this.board_status[this.winning_position[i][1]] == symbol)  && (this.board_status[this.winning_position[i][2]] == symbol)) {
                return true;
            }
        }
        return false;
    }
}

