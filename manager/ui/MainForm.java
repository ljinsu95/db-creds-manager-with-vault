package manager.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import common.Common;
import common.model.Vault;

// 로그인 성공 시 개인 정보가 출력되는 GUI 화면 
// LOGOUT 과 WITHDRAW 기능
// LOGOUT 클릭 시 LOGINFORM 화면으로 이동
// WITHDRAW 클릭 시 탈퇴 및 정보 삭제

public class MainForm extends JDialog {
	private Vault vault;

	private LoginForm owner;
	// private UsersData users;
	private String userId;
	private JTextArea check;

	private JButton dbRegBtn;

	private JButton btnLogout;
	private JButton btnWithdraw;

	public MainForm(LoginForm owner, Vault vault) {
		this.owner = owner;
		this.vault = vault;
		//  this.userId = owner.getidTxt(); // 사용자 ID 저장
	    //  this.users = owner.getUsers();
//		super(owner, "HELLO JAVA", true);
//		this.owner = owner;
//		users = owner.getUsers();
		
		init();
		setDisplay();
		addListners();
		setcheck("사용자 정보 텍스트 등"); // 수정된 부분
		showFrame();
	}

	private void init() {
		Dimension btnSize = new Dimension(100, 25);

		check = new JTextArea(10, 30);
		check.setEditable(false);

		dbRegBtn = new JButton("DB 등록");
		dbRegBtn.setPreferredSize(btnSize);

		btnLogout = new JButton("로그아웃");
		btnLogout.setPreferredSize(btnSize);

		btnWithdraw = new JButton("탈퇴하기");
		btnWithdraw.setPreferredSize(btnSize);
	}

	private void setDisplay() {
		LineBorder lBorder = new LineBorder(Color.GRAY, 1);
		TitledBorder border = new TitledBorder(lBorder, "안녕하세요! 본인의 정보를 확인 할 수 있습니다");
		check.setBorder(border);

		JPanel southPanel = new JPanel();
		southPanel.add(btnLogout);
		southPanel.add(btnWithdraw);

		JPanel centerPnl = new JPanel();
		centerPnl.add(dbRegBtn);

		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(new JScrollPane(check), BorderLayout.NORTH);
		mainPanel.add(centerPnl, BorderLayout.CENTER);
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
		dbRegBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				if (owner.getVault().tokenLookupSelf() == Common.SUCCESS_CODE) {
					DatabaseRegForm mainForm = new DatabaseRegForm(MainForm.this, vault);

					// InformationForm informationForm = new InformationForm(LoginForm.this, title);
					// mainForm.setcheck(users.getUser(idTxt.getText()).toString());
					setVisible(false);
					mainForm.setVisible(true);
				}
			}
		});

		btnWithdraw.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent ae) {
				try {
					// 예외처리 추가
					String userId = MainForm.this.userId;
					if (userId != null && !userId.isEmpty()) {
						// System.out.println("Withdraw button clicked: " + userId);
						// User 객체를 생성하여 ID 가져오기
						User userForWithdraw = new User(userId);
						// withdraw 메서드 호출
						users.withdraw(userForWithdraw.getId());

						JOptionPane.showMessageDialog(MainForm.this, "회원 정보가 삭제되었습니다" + "\n" + "다음에 또 만나요!", "BYE JAVA",
								JOptionPane.PLAIN_MESSAGE);
						dispose();
						owner.setVisible(true);
					} else {
						System.out.println("Withdraw button clicked: userId is null or empty");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		btnLogout.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(MainForm.this, "로그아웃 되었습니다" + "\n" + "다음에 또 만나요!", "BYE JAVA",
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
}