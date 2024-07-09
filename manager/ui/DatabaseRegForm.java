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
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import common.VaultException;
import common.model.Vault;
import common.service.VaultDatabaseEngine;
import common.service.VaultUserpassAuth;

public class DatabaseRegForm extends JDialog {
	private Vault vault;

	private MainForm owner;

	// private JButton dbRegBtn;

	private JLabel lblDBType;
	private JLabel lblDBHostname;
	private JLabel lblDBPort;
	private JLabel lblDBUsername;
	private JLabel lblDBPassword;
	private JLabel lblCreationStatements;

	private ButtonGroup btnGroup;
	private JRadioButton rbtnAuth[];

	private JTextField txtDBType;
	private JTextField txtDBHostname;
	private JTextField txtDBPort;
	private JTextField txtDBUsername;
	private JTextField txtDBPassword;

	private JScrollPane spCreationStatements;
	private JTextArea taCreationStatements;

	private JPanel northPanel;
	private JPanel pnlCenter;

	private JButton btnDBReg;
	private JButton btnCancel;
	private LayoutManager flowLeft;

	public DatabaseRegForm(MainForm owner) {
		this.owner = owner;

		init();
		setDisplay();
		addListners();
		showFrame();
	}

	private void init() {
        vault = Vault.getInstance();
		// vault database engine check
		String dbEnable = new VaultDatabaseEngine(vault).engineCheck();
		if (dbEnable == null) {
			System.out.println("Vault Database Engine 없음.");
			try {
				new VaultDatabaseEngine(vault).engineEnable();
			} catch (VaultException e) {
				e.getStackTrace();
			}	
		} else if (dbEnable.equals("database")) {
			System.out.println("Database Engine 활성화 상태");
		} else {
			System.out.println("Vault 내부에서 db-manager를 이미 사용중입니다.");
		}

		// 사이즈 통일
		Dimension labelSize = new Dimension(180, 30);
		int txtSize = 40;
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
		lblCreationStatements = new JLabel("Creation Statements : ");
		lblCreationStatements.setPreferredSize(labelSize);

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
		taCreationStatements = new JTextArea(5, txtSize);
		taCreationStatements.setText("CREATE USER '{{name}}'@'%' IDENTIFIED BY '{{password}}';\nGRANT SELECT ON *.* TO '{{name}}'@'%';");
		taCreationStatements.setLineWrap(false); // 줄 바꿈 비활성화

		spCreationStatements = new JScrollPane(taCreationStatements);
		spCreationStatements.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        spCreationStatements.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);


		btnDBReg = new JButton("DB 등록");
		btnDBReg.setPreferredSize(btnSize);

		btnCancel = new JButton("취소");
		btnCancel.setPreferredSize(btnSize);

	}

	private void setDisplay() {

		// 컴포넌트를 왼쪽 정렬로 배치
		flowLeft = new FlowLayout(FlowLayout.LEFT);

		northPanel = new JPanel(new GridLayout(0, 1));
		pnlCenter = new JPanel(new GridLayout(0, 1));

		JPanel pnlDbType = new JPanel(flowLeft);
		pnlDbType.add(lblDBType);
		pnlDbType.add(txtDBType);

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
		
		northPanel.add(pnlDbType);
		northPanel.add(pnlHostname);
		northPanel.add(pnlPort);
		northPanel.add(pnlUsername);
		northPanel.add(pnlpassword);

		JPanel pnlStatements = new JPanel(flowLeft);
		pnlStatements.add(lblCreationStatements);
		pnlStatements.add(spCreationStatements);
		
		pnlCenter.add(pnlStatements);
		// northPanel.add(tokenPnl);

		JPanel southPanel = new JPanel();
		southPanel.add(btnDBReg);
		southPanel.add(btnCancel);

		northPanel.setBorder(new EmptyBorder(0, 20, 0, 20));
		pnlCenter.setBorder(new EmptyBorder(0, 20, 0, 20));
		southPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

		add(northPanel, BorderLayout.NORTH);
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

		btnDBReg.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				ae.getActionCommand();
				try {
					new VaultDatabaseEngine(vault).configCreate(txtDBType.getText(), txtDBHostname.getText(), txtDBUsername.getText(), txtDBPassword.getText());
					new VaultUserpassAuth(vault).createPolicy(txtDBType.getText());
					
					new VaultDatabaseEngine(vault).createRole(txtDBType.getText(), taCreationStatements.getText());
					// 사용자 전용 role 생성
					for (String user : vault.getUserList()) {
						new VaultDatabaseEngine(vault).createRole(user, txtDBType.getText(), taCreationStatements.getText());
						new VaultUserpassAuth(vault).updateUserPolicy(user, new VaultUserpassAuth(vault).getUserPolicy(user)+", creds-"+txtDBType.getText());
					}
					
				} catch (VaultException e) {
					e.getStackTrace();
				}

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
				owner.setBtnDBReg(true);
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