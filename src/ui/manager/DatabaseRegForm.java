package src.ui.manager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import src.common.VaultException;
import src.model.Vault;
import src.model.VaultDatabasePlugin;
import src.service.VaultDatabaseEngine;
import src.service.VaultUserpassAuth;

public class DatabaseRegForm extends JDialog {
	private Vault vault;
	private VaultDatabaseEngine dbEngine;
	private VaultUserpassAuth userpassAuth;

	private ManagerMainForm owner;

	// private JButton dbRegBtn;

	private JLabel lblDBConnName;
	private JLabel lblDBHostname;
	private JLabel lblDBPort;
	private JLabel lblDBUsername;
	private JLabel lblDBPassword;
	private JLabel lblCreationStatements;

	private JComboBox<String> cbxDBType;
	private JTextField txtConnectionName;
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

	public DatabaseRegForm(ManagerMainForm owner) {
		this.owner = owner;

		init();
		setDisplay();
		addListners();
		showFrame();
	}

	private void init() {
        vault = Vault.getInstance();
		dbEngine = new VaultDatabaseEngine(vault);
		userpassAuth = new VaultUserpassAuth(vault);

		// vault database engine 유무 확인
		String dbEnable = dbEngine.engineCheck();
		if (dbEnable == null) {
			System.out.println("Vault Database Engine 없음.");
			try {
				// Database Engine 활성화
				dbEngine.engineEnable();
			} catch (VaultException e) {
				e.printStackTrace();
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
		lblDBConnName = new JLabel("DB Connection Name : ");
		lblDBConnName.setPreferredSize(labelSize);
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
		
		// 콤보박스 생성 및 옵션 추가
		cbxDBType = new JComboBox<>(VaultDatabasePlugin.getPluginNames());
		// cbxDBType = new JComboBox<>(Vault.ARR_DB_TYPE);
		cbxDBType.setPreferredSize(new Dimension(130, 30));

		txtConnectionName = new JTextField(txtSize-13);
		txtConnectionName.setText("connection");

		txtDBHostname = new JTextField(txtSize);
		txtDBHostname.setText("localhost");
		txtDBPort = new JTextField(txtSize);
		txtDBPort.setText("3306");

		txtDBUsername = new JTextField(txtSize);
		txtDBUsername.setText("admin");
		txtDBPassword = new JTextField(txtSize);
		taCreationStatements = new JTextArea(5, txtSize);
		taCreationStatements.setText(VaultDatabasePlugin.valueOf(cbxDBType.getSelectedItem().toString()).getStatements());
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
		pnlDbType.add(lblDBConnName);
		pnlDbType.add(cbxDBType);
		pnlDbType.add(new JLabel("-"));
		pnlDbType.add(txtConnectionName);

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

		/* 
		 * DB Connection 등록
		 */
		btnDBReg.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				ae.getActionCommand();
				String dbType = cbxDBType.getSelectedItem().toString();
				String connectionName = dbType + "-" + txtConnectionName.getText();
				connectionName = connectionName.toLowerCase();	// policy는 자동으로 lowcase로 생성되므로 connection name도 lowcase로 설정 (사용자는 policy 목록 기반으로 connection list를 출력)
				String dbHostname = txtDBHostname.getText();
				String dbUsername = txtDBUsername.getText();
				String dbPassword = txtDBPassword.getText();
				try {
					// Vault DB Config 생성
					dbEngine.configCreate(connectionName, dbType, dbHostname, dbUsername, dbPassword);

					// Vault DB Config와 연결된 Role을 사용하여 DB Creds/Revoke 할 수 있는 권한 생성
					userpassAuth.createPolicy(connectionName);
					
					// Vault DB Config와 연결된 Role 생성
					dbEngine.createRole(connectionName, taCreationStatements.getText());

					// 사용자 전용 role 생성
					for (String user : vault.getUserList()) {
						// Role 생성
						dbEngine.createRole(user, connectionName, taCreationStatements.getText());

						// 사용자가 보유한 Policies 조회
						String userPolicies = userpassAuth.getUserPolicy(user);

						// 사용자의 Policies에 DB Creds Policy 추가
						userpassAuth.updateUserPolicy(user, userPolicies + ", creds-" + connectionName);
					}
					
				} catch (NullPointerException npe) {
					System.out.println("user 없음.");
				} catch (VaultException e) {
					e.printStackTrace();
				}

				dispose();
				owner.updatePnlDB();
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
		setTitle("DB Connection Registry");
		pack();
		setLocationRelativeTo(owner); // loginForm이 있는 위치를 기준으로 위치를 조정함
		setDefaultCloseOperation(DISPOSE_ON_CLOSE); // 다이얼로그를 닫을 때 해당 다이얼로그만 닫히고 프로그램이 종료되지는 않음
		setResizable(false); // 창 크기를 조절할 수 없도록 설정함
	}
}