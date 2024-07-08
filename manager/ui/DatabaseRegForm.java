package manager.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import common.model.Vault;
import common.service.VaultDatabaseEngine;

// 로그인 성공 시 개인 정보가 출력되는 GUI 화면 
// LOGOUT 과 WITHDRAW 기능
// LOGOUT 클릭 시 LOGINFORM 화면으로 이동
// WITHDRAW 클릭 시 탈퇴 및 정보 삭제

public class DatabaseRegForm extends JDialog {
	private Vault vault;

	private MainForm owner;

	// private JButton dbRegBtn;

	private JLabel lblDBType;
	private JLabel lblDBHostname;
	private JLabel lblDBPort;
	private JLabel lblDBUsername;
	private JLabel lblDBPassword;

	private ButtonGroup btnGroup;
	private JRadioButton rbtnAuth[];

	private JTextField txtDBType;
	private JTextField txtDBHostname;
	private JTextField txtDBPort;
	private JTextField txtDBUsername;
	private JTextField txtDBPassword;

	private JPanel northPanel;

	private JPanel usernamePnl;
	private JPanel passwordPnl;
	private JPanel tokenPnl;
	private JButton btnDBReg;
	private JButton btnCancel;
	private LayoutManager flowLeft;

	public DatabaseRegForm(MainForm owner, Vault vault) {
		this.owner = owner;
		this.vault = vault;

		init();
		setDisplay();
		addListners();
		showFrame();
	}

	private void init() {
		// vault database engine check
		if (new VaultDatabaseEngine(vault).engineCheck() == null) {
			System.out.println("Vault Database Engine 없음.");
			new VaultDatabaseEngine(vault).engineEnable();
		}

		// 사이즈 통일
		Dimension labelSize = new Dimension(120, 30);
		int txtSize = 20;
		Dimension btnSize = new Dimension(100, 25);

		// 레이블 설정
		lblDBType = new JLabel("DB Type : ");
		lblDBType.setPreferredSize(labelSize);
		lblDBHostname = new JLabel("DB Hostname : ");
		lblDBHostname.setPreferredSize(labelSize);
		lblDBPort = new JLabel("DB Port : ");
		lblDBPort.setPreferredSize(labelSize);
		lblDBUsername = new JLabel("DB Username : ");
		lblDBUsername.setPreferredSize(labelSize);
		lblDBPassword = new JLabel("DB Password : ");
		lblDBPassword.setPreferredSize(labelSize);

		// 필드 설정
		txtDBType = new JTextField(txtSize);
		txtDBType.setText("mysql");

		// btnGroup = new ButtonGroup();
		// rbtnAuth = new JRadioButton[vaultAuthList.length];
		// for (int i = 0; i < vaultAuthList.length; i++) {
		// rbtnAuth[i] = new JRadioButton(vaultAuthList[i]);
		// if (vaultAuthList[i].equals(vaultAuthType)) {
		// rbtnAuth[i].setSelected(true);
		// }
		// btnGroup.add(rbtnAuth[i]);
		// }

		txtDBHostname = new JTextField(txtSize);
		txtDBHostname.setText("localhost");
		txtDBPort = new JTextField(txtSize);
		txtDBPort.setText("3306");

		txtDBUsername = new JTextField(txtSize);
		txtDBUsername.setText("admin");
		txtDBPassword = new JTextField(txtSize);

		btnDBReg = new JButton("DB 등록");
		btnDBReg.setPreferredSize(btnSize);

		btnCancel = new JButton("취소");
		btnCancel.setPreferredSize(btnSize);

	}

	private void setDisplay() {

		// 컴포넌트를 왼쪽 정렬로 배치
		flowLeft = new FlowLayout(FlowLayout.LEFT);

		northPanel = new JPanel(new GridLayout(0, 1));

		JPanel pnlHostname = new JPanel(flowLeft);
		pnlHostname.add(lblDBHostname);
		pnlHostname.add(txtDBHostname);

		JPanel pnlPort = new JPanel(flowLeft);
		pnlPort.add(lblDBPort);
		pnlPort.add(txtDBPort);

		JPanel pnlUsername = new JPanel(flowLeft);
		pnlUsername.add(lblDBUsername);
		pnlUsername.add(txtDBUsername);

		JPanel pnlpassword = new JPanel(flowLeft);
		pnlpassword.add(lblDBPassword);
		pnlpassword.add(txtDBPassword);

		northPanel.add(pnlHostname);
		northPanel.add(pnlPort);
		northPanel.add(pnlUsername);
		northPanel.add(pnlpassword);
		// northPanel.add(tokenPnl);

		JPanel southPanel = new JPanel();
		southPanel.add(btnDBReg);
		southPanel.add(btnCancel);

		northPanel.setBorder(new EmptyBorder(0, 20, 0, 20));
		southPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

		add(northPanel, BorderLayout.NORTH);
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

		btnDBReg.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				ae.getActionCommand();
				new VaultDatabaseEngine(vault).configCreate(txtDBType.getText(), txtDBHostname.getText(), txtDBUsername.getText(), txtDBPassword.getText());
				
				// MainForm mainForm = new MainForm(MainForm.this, vault);

				// InformationForm informationForm = new InformationForm(LoginForm.this, title);
				// mainForm.setcheck(users.getUser(idTxt.getText()).toString());
				dispose();
				owner.setVisible(true);
				owner.setBtnDBReg(true);
				// mainForm.setVisible(true);
			}
		});


        btnCancel.addActionListener(new ActionListener() {
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