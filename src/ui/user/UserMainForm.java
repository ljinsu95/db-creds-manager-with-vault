package src.ui.user;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import src.common.Common;
import src.common.VaultException;
import src.model.Vault;
import src.service.VaultDatabaseEngine;
import src.ui.LoginForm;

public class UserMainForm extends JDialog {
	private Vault vault;

	private LoginForm owner;


	private JScrollPane spDb;
	private JTable tbDb;
	private JLabel lblDetail;

    private JButton btnDBCreds;
	private JButton btnPwChange;
	private JButton btnLogout;

    public UserMainForm(LoginForm owner) {
		this.owner = owner;
		
		init();
		setDisplay();
		addListners();
		// setcheck("사용자 정보 텍스트 등"); // 수정된 부분
		showFrame();
	}

    private void init() {
        vault = Vault.getInstance();
        /* Policy 목록 조회 */
        String[] policyStrArr = null;
        try {
            policyStrArr = Common.getNestedJsonToStrArr(vault.tokenLookupSelf(), "data", "policies");
        } catch (VaultException ve) {
            ve.printStackTrace();
        }
        // String policies = new VaultUserpassAuth(vault).getUserPolicy(vault.getVaultUserNm());
        // String[] policyStrArr = policies.replace(" ", "").split(",");

        // "creds-"로 시작하는 문자열을 필터링하고 "creds-"를 제거한 새로운 리스트를 만듦
        List<String> resultList = new ArrayList<>();
        for (String s : policyStrArr) {
            if (s.startsWith("creds-")) {
                resultList.add(s.substring(6)); // "creds-" 제거
            }
        }

        // 리스트를 배열로 변환
        String[] resultArray = resultList.toArray(new String[0]);

        // 결과 출력
        System.out.println("Filtered and transformed array: " + String.join(", ", resultArray));



        /* 버튼 사이즈 통일 */
		Dimension btnSize = new Dimension(100, 25);

        /* DB List 출력 화면 Start */
		/* DB List 화면 */
        DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"DB Type"}, 0);
        for (int i = 0; i < resultArray.length; i++) {
			tableModel.addRow(new Object[]{resultArray[i]});
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
		btnDBCreds = new JButton("계정 발급");
		btnDBCreds.setPreferredSize(btnSize);
		/* DB List 출력 화면 End */

        btnLogout = new JButton("로그아웃");
		btnLogout.setPreferredSize(btnSize);
        btnPwChange = new JButton("패스워드 변경");
		btnPwChange.setPreferredSize(btnSize);
    }

    private void setDisplay() {
		JPanel pnlNorth = new JPanel(new GridLayout(0, 1));
		pnlNorth.add(spDb);

        JPanel pnlCenter = new JPanel(new BorderLayout());

		// DB Pnl
		JPanel pnlCNorth = new JPanel(new BorderLayout());

		JPanel pnlCNEast = new JPanel();
		pnlCNEast.add(btnDBCreds);

		pnlCNorth.add(spDb, BorderLayout.WEST);
		pnlCNorth.add(lblDetail, BorderLayout.CENTER);
		// pnlCenNorth.add(new JScrollPane(check), BorderLayout.NORTH);
		pnlCNorth.add(pnlCNEast, BorderLayout.EAST);

		pnlCenter.add(pnlCNorth, BorderLayout.NORTH);

		JPanel southPanel = new JPanel(new BorderLayout());
		southPanel.add(btnPwChange, BorderLayout.WEST);
		southPanel.add(btnLogout, BorderLayout.EAST);


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

        /* 계정 발급 버튼 */
        btnDBCreds.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				// setBtnDBReg(false);
				try {
					vault.tokenLookupSelf();
                    System.out.println(tbDb.getValueAt(tbDb.getSelectedRow(), 0).toString());
                    String dbName = tbDb.getValueAt(tbDb.getSelectedRow(), 0).toString();
					// revoke creds lease
					try {
						new VaultDatabaseEngine(vault).revokeCreds(dbName);
					} catch (Exception e) {
						System.out.println("lease 없음.");
					}
					
                    String credsInfo = new VaultDatabaseEngine(vault).creds(dbName);
                    String dbPassword = Common.getNestedJsonToStr(credsInfo, "data", "password");
					DatabaseCredsForm dbCredsForm = new DatabaseCredsForm(UserMainForm.this, "", vault.getVaultUserNm(), dbPassword);
					setVisible(false);
					dbCredsForm.setVisible(true);
				} catch (VaultException ve) {
					ve.printStackTrace();
				}
			}
		});

		/* 패스워드 변경 버튼 */
		btnPwChange.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				UserUpdateForm userUpdateForm = new UserUpdateForm(UserMainForm.this);
				setVisible(false);
				userUpdateForm.setVisible(true);
			}
		});

		btnLogout.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(UserMainForm.this, "로그아웃 되었습니다" + "\n" + "다음에 또 만나요!", "BYE JAVA",
						JOptionPane.PLAIN_MESSAGE);
				dispose();
				owner.setVisible(true);
			}
		});
    }

    private void showFrame() {
		setTitle(vault.getVaultUserNm());
		pack();
		setLocationRelativeTo(owner); // loginForm이 있는 위치를 기준으로 위치를 조정함
		setDefaultCloseOperation(DISPOSE_ON_CLOSE); // 다이얼로그를 닫을 때 해당 다이얼로그만 닫히고 프로그램이 종료되지는 않음
		setResizable(false); // 창 크기를 조절할 수 없도록 설정함
	}
}
