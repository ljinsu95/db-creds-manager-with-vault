package src;
import javax.swing.SwingUtilities;

import src.ui.LoginForm;

public class Main {
    public static void main(String[] args) {

        // UI 생성
        System.out.println("start");
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginForm();
            }
        });
    }
}
