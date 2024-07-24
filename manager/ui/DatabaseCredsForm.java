package manager.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class DatabaseCredsForm extends JDialog {
    private UserMainForm owner;

    private String dbConnection;
    private String dbUsername;
    private String dbPassword;

    private JLabel lblDBConnection;
    private JLabel lblDBUserName;
    private JLabel lblDBPassword;

    private JTextField txtDBConnection;
    private JTextField txtDBUserName;
    private JTextField txtDBPassword;
    private JTextField[] txtArr;

    private JButton btnDBConnectionCopy;
    private JButton btnDBUserNameCopy;
    private JButton btnDBPasswordCopy;
    private JButton[] btnCopyArr;

    private JButton btnOK;

	private JPanel pnlCenter;

	private LayoutManager flowLeft;

    public DatabaseCredsForm(UserMainForm owner, String dbConnection, String dbUsername, String dbPassword) {
        this.owner = owner;
        this.dbConnection = dbConnection;
        this.dbUsername = dbUsername;
        this.dbPassword =dbPassword;

        init();
		setDisplay();
		addListners();
		showFrame();
    }

    private void init() {
        Dimension labelSize = new Dimension(180, 30);

        lblDBConnection = new JLabel("Connection : ");
        lblDBConnection.setPreferredSize(labelSize);
        lblDBUserName = new JLabel("Username : ");
        lblDBUserName.setPreferredSize(labelSize);
        lblDBPassword = new JLabel("Password : ");
        lblDBPassword.setPreferredSize(labelSize);

        int txtSize = 40;

        txtDBConnection = new JTextField(txtSize);
        txtDBConnection.setText(dbConnection);
        txtDBUserName = new JTextField(txtSize);
        txtDBUserName.setText(dbUsername);
        txtDBPassword = new JTextField(txtSize);
        txtDBPassword.setText(dbPassword);

        txtArr = new JTextField[3];
        txtArr[0] = txtDBConnection;
        txtArr[1] = txtDBUserName;
        txtArr[2] = txtDBPassword;

		Dimension btnSize = new Dimension(100, 25);

		btnDBConnectionCopy = new JButton("복사");
		btnDBConnectionCopy.setPreferredSize(btnSize);
		btnDBUserNameCopy = new JButton("복사");
		btnDBUserNameCopy.setPreferredSize(btnSize);
		btnDBPasswordCopy = new JButton("복사");
		btnDBPasswordCopy.setPreferredSize(btnSize);
        
        btnCopyArr = new JButton[3];
        btnCopyArr[0] = btnDBConnectionCopy;
        btnCopyArr[1] = btnDBUserNameCopy;
        btnCopyArr[2] = btnDBPasswordCopy;

		btnOK = new JButton("확인");
		btnOK.setPreferredSize(btnSize);
    }

    private void setDisplay() {
		// 컴포넌트를 왼쪽 정렬로 배치
		flowLeft = new FlowLayout(FlowLayout.LEFT);
		pnlCenter = new JPanel(new GridLayout(0, 1));

        JPanel pnlConnection = new JPanel(flowLeft);
        pnlConnection.add(lblDBConnection);
        pnlConnection.add(txtDBConnection);
        pnlConnection.add(btnDBConnectionCopy);

        JPanel pnlUsername = new JPanel(flowLeft);
        pnlUsername.add(lblDBUserName);
        pnlUsername.add(txtDBUserName);
        pnlUsername.add(btnDBUserNameCopy);

        JPanel pnlPassword = new JPanel(flowLeft);
        pnlPassword.add(lblDBPassword);
        pnlPassword.add(txtDBPassword);
        pnlPassword.add(btnDBPasswordCopy);

        pnlCenter.add(pnlConnection);
        pnlCenter.add(pnlUsername);
        pnlCenter.add(pnlPassword);

        JPanel southPanel = new JPanel();
		southPanel.add(btnOK);

        add(pnlCenter, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);
    }

    private void addListners() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent we) {
				dispose();
				owner.setVisible(true);
			}
		});

        for (int i = 0; i < btnCopyArr.length; i++) {
            int index = i;
            btnCopyArr[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    ae.getActionCommand();
                    String text = txtArr[index].getText();
                    StringSelection stringSelection = new StringSelection(text);
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clipboard.setContents(stringSelection, null);
                    JOptionPane.showMessageDialog(DatabaseCredsForm.this, "Text copied to clipboard!");
    
                }
            });
        }


        btnOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                ae.getActionCommand();
				dispose();
				owner.setVisible(true);
            }
        });
	}

    private void showFrame() {
		pack();
		setLocationRelativeTo(owner); // loginForm이 있는 위치를 기준으로 위치를 조정함
		setDefaultCloseOperation(DISPOSE_ON_CLOSE); // 다이얼로그를 닫을 때 해당 다이얼로그만 닫히고 프로그램이 종료되지는 않음
		setResizable(false); // 창 크기를 조절할 수 없도록 설정함
	}
}
