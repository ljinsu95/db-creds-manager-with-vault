package manager.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
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

import common.Common;
import common.model.Vault;
import common.service.VaultDatabaseEngine;

public class MainForm extends JDialog {
	private Vault vault;

	private LoginForm owner;
	// private UsersData users;
	private String userId;

	private JScrollPane scrollPane;
	private JTable tbDb;
	private JLabel lblDetail;

	private JTextArea check;

	private JButton btnDBReg;

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
		String[] configs = new VaultDatabaseEngine(vault).configList();

		System.out.println("config : " + configs.toString());

		



		//

		Dimension btnSize = new Dimension(100, 25);

		// 테이블 모델 생성
        DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"DB Type"}, 0);
        for (int i = 0; i < configs.length; i++) {
            tableModel.addRow(new Object[]{configs[i]});
        }

		// JTable 생성
        tbDb = new JTable(tableModel);
        tbDb.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// tbDb.setSize(btnSize);

		// JScrollPane 생성 및 JTable 추가
        scrollPane = new JScrollPane(tbDb);
		scrollPane.setPreferredSize(new Dimension(200, 100));

		// 상세 내용을 표시할 JLabel 생성
        lblDetail = new JLabel("상세 내용: 마우스를 항목 위에 올려보세요");
        lblDetail.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		//

		check = new JTextArea(10, 30);
		check.setEditable(false);

		btnDBReg = new JButton("DB 등록");
		btnDBReg.setPreferredSize(btnSize);

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
		pnlNorth.add(scrollPane);
		// pnlNorth.add(lblDetail);

		JPanel southPanel = new JPanel();
		southPanel.add(btnLogout);
		southPanel.add(btnWithdraw);

		JPanel centerPnl = new JPanel();
		centerPnl.add(btnDBReg);

		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(scrollPane, BorderLayout.WEST);
		mainPanel.add(lblDetail, BorderLayout.CENTER);
		// mainPanel.add(new JScrollPane(check), BorderLayout.NORTH);
		mainPanel.add(centerPnl, BorderLayout.EAST);
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
				btnDBReg.setEnabled(false);
				if (owner.getVault().tokenLookupSelf() == Common.SUCCESS_CODE) {
					DatabaseRegForm mainForm = new DatabaseRegForm(MainForm.this, vault);

					// InformationForm informationForm = new InformationForm(LoginForm.this, title);
					// mainForm.setcheck(users.getUser(idTxt.getText()).toString());
					setVisible(false);
					mainForm.setVisible(true);
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

	public void setBtnDBReg(Boolean flag) {
		btnDBReg.setEnabled(flag);
	}
}