package manager.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import common.VaultException;
import common.model.Vault;
import common.service.VaultDatabaseEngine;
import common.service.VaultUserpassAuth;

public class ManagerMainForm extends JDialog {
	private Vault vault;

	private LoginForm owner;

	private JScrollPane spDb;
	private JTable tbDb;
	private JLabel lblDetail;

	private JTextArea check;

	private JButton btnDBReg;

	// user pnl
	private JScrollPane spUser;
	private JTable tbUser;
	private JLabel lblUserDetail;
	private JButton btnUserReg;
	private JPanel pnlUser;

	private JButton btnLogout;
	private JButton btnWithdraw;

	public ManagerMainForm(LoginForm owner) {
		this.owner = owner;
		
		init();
		setDisplay();
		addListners();
		setcheck("사용자 정보 텍스트 등"); // 수정된 부분
		showFrame();
	}

	private void init() {
		vault = Vault.getInstance();

		// vault userpass auth check
		try {
			if (new VaultUserpassAuth(vault).authCheck() == null) {
				System.out.println("Vault Userpass Auth 없음.");
				new VaultUserpassAuth(vault).authEnable();
			}
		} catch (VaultException ve) {
			ve.getStackTrace();
		}

		String[] configs = null;
		String[] users = null;
		try {
			/* DB config 목록 조회 */
			configs = new VaultDatabaseEngine(vault).configList();
			System.out.println("config : " + configs.toString());
			
			/* User 목록 조회 */
			users = new VaultUserpassAuth(vault).userList();
			System.out.println("config : " + users.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		

		/* 지워도 됩니다. Auth Check 동작 확인용 */
		System.out.println("auth check : " + new VaultUserpassAuth(vault).authCheck());

		/* 버튼 사이즈 통일 */
		Dimension btnSize = new Dimension(100, 25);

		/* DB List 출력 화면 Start */
		/* DB List 화면 */
        DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"DB Type"}, 0);
        for (int i = 0; i < configs.length; i++) {
			tableModel.addRow(new Object[]{configs[i]});
        }
        tbDb = new JTable(tableModel);
        tbDb.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// tbDb.setSize(btnSize);
		
		/* 스크롤 추가 */
        spDb = new JScrollPane(tbDb);
		spDb.setPreferredSize(new Dimension(200, 100));
		
		/* 상세 내용 출력 레이블 */
        lblDetail = new JLabel("상세 내용: 마우스를 항목 위에 올려보세요");
        lblDetail.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		/* DB 등록 버튼 */
		// TODO : 등록 버튼 색상 변경 필요
		btnDBReg = new JButton("DB 등록");
		btnDBReg.setPreferredSize(btnSize);
		/* DB List 출력 화면 End */
		
		
		/* User List 출력 화면 Start */
		// 테이블 모델 생성
		DefaultTableModel dtmUserList = new DefaultTableModel(new Object[]{"User List"}, 0);
		for (int i = 0; i < users.length; i++) {
			dtmUserList.addRow(new Object[]{users[i]});
		}
		
		// JTable 생성
		tbUser = new JTable(dtmUserList);
		tbUser.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// tbDb.setSize(btnSize);
		
		// JScrollPane 생성 및 JTable 추가
		spUser = new JScrollPane(tbUser);
		spUser.setPreferredSize(new Dimension(200, 100));
		
		// 상세 내용을 표시할 JLabel 생성
		lblUserDetail = new JLabel("상세 내용: 마우스를 항목 위에 올려보세요");
		lblUserDetail.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		btnUserReg = new JButton("User 등록");
		btnUserReg.setPreferredSize(btnSize);
		/* User List 출력 화면 End */
		
		
		check = new JTextArea(10, 30);
		check.setEditable(false);


		btnLogout = new JButton("로그아웃");
		btnLogout.setPreferredSize(btnSize);

		btnWithdraw = new JButton("탈퇴하기");
		btnWithdraw.setPreferredSize(btnSize);
	}

	private void setDisplay() {
		LineBorder lBorder = new LineBorder(Color.GRAY, 1);
		TitledBorder border = new TitledBorder(lBorder, "안녕하세요! 본인의 정보를 확인 할 수 있습니다");
		check.setBorder(border);

		JPanel pnlNorth = new JPanel(new GridLayout(0, 1));
		pnlNorth.add(spDb);
		// pnlNorth.add(lblDetail);

		JPanel southPanel = new JPanel();
		southPanel.add(btnLogout);
		southPanel.add(btnWithdraw);

		JPanel pnlCenter = new JPanel(new BorderLayout());

		// DB Pnl
		JPanel pnlCNorth = new JPanel(new BorderLayout());

		JPanel pnlCNEast = new JPanel();
		pnlCNEast.add(btnDBReg);

		pnlCNorth.add(spDb, BorderLayout.WEST);
		pnlCNorth.add(lblDetail, BorderLayout.CENTER);
		// pnlCenNorth.add(new JScrollPane(check), BorderLayout.NORTH);
		pnlCNorth.add(pnlCNEast, BorderLayout.EAST);

		// User Pnl
		pnlUser = new JPanel(new BorderLayout());

		JPanel pnlCCEast = new JPanel();
		pnlCCEast.add(btnUserReg);

		pnlUser.add(spUser, BorderLayout.WEST);
		pnlUser.add(lblUserDetail, BorderLayout.CENTER);
		pnlUser.add(pnlCCEast, BorderLayout.EAST);

		pnlCenter.add(pnlCNorth, BorderLayout.NORTH);
		pnlCenter.add(pnlUser, BorderLayout.CENTER);


		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(pnlCenter, BorderLayout.CENTER);
		mainPanel.add(southPanel, BorderLayout.SOUTH);

		add(mainPanel, BorderLayout.CENTER);
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
				setBtnDBReg(false);
				try {
					vault.tokenLookupSelf();
					DatabaseRegForm mainForm = new DatabaseRegForm(ManagerMainForm.this);
					setVisible(false);
					mainForm.setVisible(true);
				} catch (VaultException ve) {
					ve.getStackTrace();
				}
			}
		});

		/* 사용자 등록 */
		btnUserReg.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				setBtnUserReg(false);
				try {
					vault.tokenLookupSelf();
					UserRegForm userRegForm = new UserRegForm(ManagerMainForm.this);
					setVisible(false);
					userRegForm.setVisible(true);
				} catch (VaultException ve) {
					ve.getStackTrace();
				}
			}
		});

		// JTable에 마우스 이벤트 리스너 추가
        tbDb.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = tbDb.rowAtPoint(e.getPoint());
                int col = tbDb.columnAtPoint(e.getPoint());
                if (row != -1 && col != -1) {
                    Object value = tbDb.getValueAt(row, col);
                    lblDetail.setText("상세 내용: " + value.toString());
                }
            }
        });

		btnLogout.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(ManagerMainForm.this, "로그아웃 되었습니다" + "\n" + "다음에 또 만나요!", "BYE JAVA",
						JOptionPane.PLAIN_MESSAGE);
				dispose();
				owner.setVisible(true);
			}
		});
	}

	public void setcheck(String userInfo) {
		check.setText(userInfo);
	}

	private void showFrame() {
		pack();
		setLocationRelativeTo(owner); // loginForm이 있는 위치를 기준으로 위치를 조정함
		setDefaultCloseOperation(DISPOSE_ON_CLOSE); // 다이얼로그를 닫을 때 해당 다이얼로그만 닫히고 프로그램이 종료되지는 않음
		setResizable(false); // 창 크기를 조절할 수 없도록 설정함
	}

	/* DB 추가 후 리스트 업데이트 */
	public void updatePnlDB() {
		String[] configs = null;
		try {
			/* User 목록 조회 */
			configs = new VaultDatabaseEngine(vault).configList();
			System.out.println("config : " + configs.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		// spUser.remove(tbUser);
		// 테이블 모델 생성
		DefaultTableModel dtmDbList = new DefaultTableModel(new Object[]{"User List"}, 0);
		for (int i = 0; i < configs.length; i++) {
			dtmDbList.addRow(new Object[]{configs[i]});
		}

		// JTable 생성
		tbDb = new JTable(dtmDbList);
		tbDb.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// JScrollPane 업데이트
		spDb.setViewportView(tbDb);
	}

	/* User 추가 후 리스트 업데이트 */
	public void updatePnlUser() {
		String[] users = null;
		try {
			/* User 목록 조회 */
			users = new VaultUserpassAuth(vault).userList();
			System.out.println("config : " + users.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		// spUser.remove(tbUser);
		// 테이블 모델 생성
		DefaultTableModel dtmUserList = new DefaultTableModel(new Object[]{"User List"}, 0);
		for (int i = 0; i < users.length; i++) {
			dtmUserList.addRow(new Object[]{users[i]});
		}

		// JTable 생성
		tbUser = new JTable(dtmUserList);
		tbUser.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// JScrollPane 업데이트
		spUser.setViewportView(tbUser);
	}

	/* DB 등록버튼 ON/OFF */
	public void setBtnDBReg(Boolean flag) {
		btnDBReg.setEnabled(flag);
	}
	
	/* USER 등록버튼 ON/OFF */
	public void setBtnUserReg(Boolean flag) {
		btnUserReg.setEnabled(flag);
	}
}