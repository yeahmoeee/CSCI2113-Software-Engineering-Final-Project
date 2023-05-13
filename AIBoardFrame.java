import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;


public class AIBoardFrame extends JFrame {
    public GridLayout grid_layout;
    public JScrollPane scrollPane_members_practice;
    public JButton practice_room_disconnect;
    public JButton spectate;
    public JButton back;
    public JButton [] ai_boards;
    public int [] cells;
    public int player;
    public int [][] winning_position = {{0,1,2},{3,4,5},{6,7,8},{0,3,6},{1,4,7},{2,5,8},{0,4,8},{2,4,6}};
    public boolean start_player;
    public JPanel board_panel;
    public JPanel waitlist_disconnect_spectate_back;
    public boolean player_turn;

    public JFrame frame_link = this;


    public AIBoardFrame(JScrollPane waitlist, JButton disconnect, JButton spectate_from_waitlist, JFrame waiting_room){
        super();
        GUI_initial_settings(waitlist, disconnect, spectate_from_waitlist, waiting_room); 
        MyThread mythread = new MyThread();
        Thread run = new Thread(mythread);
        run.start();
    }
    public void GUI_initial_settings(JScrollPane waitlist, JButton disconnect, JButton spectate_from_waitlist, JFrame waiting_room){ 
        this.setTitle("3 x 3 Tic-Tac-Toe - Player vs AI");
        this.setSize(1500,1500);
        this.grid_layout = new GridLayout(3,3);
        this.scrollPane_members_practice = waitlist;
        this.practice_room_disconnect = new JButton("Disconnect");
        this.spectate = new JButton("Spectate");
        this.back = new JButton("Back");
        this.ai_boards = new JButton[9]; 

        
        
        this.board_panel = new JPanel();
        this.board_panel.setPreferredSize(new Dimension(600,600));
        this.board_panel.setLayout(grid_layout);
        for (int i = 0; i < 9; ++i){
            ai_boards[i] = new JButton("-");
            final int index = i;
            ai_boards[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (!player_turn){
                        cells[index] = 1;
                        ai_boards[index].setText("X");
                        ai_boards[index].setEnabled(false);  
                        player_turn = true;
                    }
                    else {
                        cells[index] = -1;
                        ai_boards[index].setText("O");
                        ai_boards[index].setEnabled(false);
                        player_turn = false;
                    }

                }
                
            });
            this.board_panel.add(ai_boards[i]);
        }
        this.add(this.board_panel, BorderLayout.WEST);

        this.cells = new int[9];
        for (int i = 0; i < 9; i++) {
            this.cells[i] = 0;
        }
        this.waitlist_disconnect_spectate_back= new JPanel();
        this.waitlist_disconnect_spectate_back.setLayout(new BorderLayout());
        //this.waitlist_disconnect_spectate_back.setPreferredSize(new Dimension(140,400));

        JPanel waitlist_disconnect_spectate_back_top = new JPanel(new FlowLayout());
        waitlist_disconnect_spectate_back_top.add(new JLabel("\uD83C\uDF10 Players on Server"));
        
        this.practice_room_disconnect.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                dispose_frame();
                disconnect.doClick();
            }

        });
        waitlist_disconnect_spectate_back_top.add(practice_room_disconnect);

        this.waitlist_disconnect_spectate_back.add(waitlist_disconnect_spectate_back_top, BorderLayout.NORTH);

        JPanel waitlist_disconnect_spectate_back_mid = new JPanel();
        waitlist_disconnect_spectate_back_mid.add(scrollPane_members_practice);
        this.waitlist_disconnect_spectate_back.add(waitlist_disconnect_spectate_back_mid, BorderLayout.CENTER);

        JPanel waitlist_disconnect_spectate_back_bot = new JPanel(new FlowLayout());
        this.spectate.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                dispose();
                spectate_from_waitlist.doClick();

            }

        });
        waitlist_disconnect_spectate_back_bot.add(this.spectate);
        this.back.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                waiting_room.setVisible(true);
                dispose_frame();
                setVisible(false);
            }

        });
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                practice_room_disconnect.doClick();
                System.exit(0);
            }
         });

        waitlist_disconnect_spectate_back_bot.add(this.back);
        this.waitlist_disconnect_spectate_back.add(waitlist_disconnect_spectate_back_bot,BorderLayout.SOUTH);
        this.add(this.waitlist_disconnect_spectate_back, BorderLayout.EAST);
        this.setVisible(true);
        this.pack();

    }
    public int check_game(){
        for (int i = 0; i < 8; ++i){
            if (this.cells[winning_position[i][0]] != 0 && this.cells[winning_position[i][0]] == this.cells[winning_position[i][1]] && this.cells[winning_position[i][0]] == this.cells[winning_position[i][2]]){
                return this.cells[winning_position[i][2]];
            }
        }
        for (int i = 0; i < 9; ++i){
            if (this.cells[i] == 0){
                return 2;
            }
        }
        return 0;
    }

    public int minimax_search(int player){
        int res = check_game();
        if (res != 2){
            return res;
        }
        ArrayList<Integer> scores = new ArrayList<Integer>();
        for (int i = 0; i < 9; ++i) {
            if (this.cells[i] == 0) {
                this.cells[i] = player;
                scores.add(this.minimax_search(player * -1));
                this.cells[i] = 0;
            }
        }
        if (player == 1) {
            return max(scores);
        }
        else {
            return min(scores);
        }

    }

    public boolean isFull(){
        for (int i = 0; i < 9; i++){
            if (this.cells[i] == 0){
                return false;
            }   
        }
        return true;
    }
    public int max(ArrayList<Integer> scores) {
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < scores.size(); ++i){
            int num = scores.get(i);
            if (num > max){
                max = num;
            }
        }
        return max;
    }
    public int min(ArrayList<Integer> scores) {
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < scores.size(); ++i){
            int num = scores.get(i);
            if (num < min){
                min = num;
            }
        }
        return min;
    }
    public void ai_turn() {
        int pos = -1;
        int max_val = -2;
        for (int i = 0; i < 9; i++){
            if (this.cells[i] == 0) {
                this.cells[i] = 1;
                int score = minimax_search(-1);
                this.cells[i] = 0; 
                if (score > max_val) {
                    max_val = score;
                    pos = i;
                }
            }
        }
        this.ai_boards[pos].doClick();

    }
    public void human_turn() {
        Thread t = new Thread(new MyThread());
        t.start();
    }

    public void dispose_frame() {
        this.dispose();
    }

    public void reset() {
        for (int i = 0; i < 9; ++i){
            this.cells[i] = 0;
            this.ai_boards[i].setText("-");
            this.ai_boards[i].setEnabled(true);
        }
    }
    private class MyThread implements Runnable {
        private volatile boolean isRunning = true;
        public void run() {
            // do something repeatedly
            while (isRunning) {
                int result = JOptionPane.showConfirmDialog(frame_link, "Do you want to start first?", "Confirmation", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    start_player = true;
                    player_turn = true;
                }
                else if (result == JOptionPane.NO_OPTION){
                    start_player = false;
                    player_turn = false;
                }
                else {
                    start_player = true;
                    player_turn = true;
                }

                if (start_player) {
                    player = 1;
                }
                else {
                    player = 0;
                }
                
                for (int i = 0; i < 9; ++i){
                    if (check_game() != 2){
                        break;
                    }
                    if ((i+player)%2 == 0){
                        ai_turn();
                    }
                    else {
                        while (player_turn){
                            System.out.println();
                        }
                    }
                }
                int game_result = check_game();
                if (game_result == 0){
                    int res = JOptionPane.showConfirmDialog(frame_link, "Draw! Do you want to continue playing againt AI?", "Confirmation", JOptionPane.YES_NO_OPTION);
                    if (res == JOptionPane.YES_OPTION){
                        reset();
                        continue;
                    }
                    else if (res == JOptionPane.NO_OPTION){
                        reset();
                        back.doClick();
                        isRunning = false;
                        break;
                    }
                    else {
                        reset();
                        back.doClick();
                        isRunning = false;
                        break;
                    }
                }
                else if (game_result == 1) {
                    int res = JOptionPane.showConfirmDialog(frame_link, "AI win! Do you want to continue playing againt AI?", "Confirmation", JOptionPane.YES_NO_OPTION);
                    if (res == JOptionPane.YES_OPTION){
                        reset();
                        continue;
                    }
                    else if (res == JOptionPane.NO_OPTION){
                        reset();
                        back.doClick();
                        isRunning = false;
                        break;
                    }
                    else {
                        reset();
                        back.doClick();
                        isRunning = false;
                        break;
                    }
                }
                else if (game_result == -1) {
                    int res = JOptionPane.showConfirmDialog(frame_link, "Human win! Do you want to continue playing againt AI?", "Confirmation", JOptionPane.YES_NO_OPTION);
                    if (res == JOptionPane.YES_OPTION){
                        reset();
                        continue;
                    }
                    else if (res == JOptionPane.NO_OPTION){
                        reset();
                        back.doClick();
                        isRunning = false;
                        break;
                    }
                    else {
                        reset();
                        back.doClick();
                        isRunning = false;
                        break;
                    }
                } 

            } 
        }
    }

}


