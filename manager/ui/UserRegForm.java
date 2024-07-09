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

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import common.Common;
import common.VaultException;
import common.model.Vault;
import common.service.VaultDatabaseEngine;
import common.service.VaultUserpassAuth;

/* User 생성
 * 
 */
public class UserRegForm extends JDialog {
    private Vault vault;

    private ManagerMainForm owner;

    private JLabel lblInfo;
    private JLabel lblUsername;
    private JLabel lblPassword;

    private JTextField txtUsername;
    private JTextField txtPassword;

    private JButton btnUserReg;
    private JButton btnCancel;

    private LayoutManager flowLeft;
    private JPanel pnlCenter;
    private JPanel pnlSouth;


    public UserRegForm(ManagerMainForm owner) {
        this.owner = owner;

        init();
        setDisplay();
        addListners();
        showFrame();
    }

    private void init() {
        vault = Vault.getInstance();
        try {
            // TODO : 사용자 생성 시 Policy 생성으로 변경 필요
            new VaultUserpassAuth(vault).createPolicy();
            // vault userpass auth check
            if (new VaultUserpassAuth(vault).authCheck() == null) {
                System.out.println("Vault Userpass Auth 없음.");
                new VaultUserpassAuth(vault).authEnable();
            }
        } catch (VaultException e) {
            e.getStackTrace();
        }

        // 사이즈 통일
		Dimension labelSize = new Dimension(120, 30);
		int txtSize = 20;
		Dimension btnSize = new Dimension(100, 25);
        
        /* 레이블 설정 */
        lblInfo = new JLabel("사용자 생성 후 사용자가 직접 패스워드 변경이 가능합니다.");
        lblUsername = new JLabel("Username : ");
        lblUsername.setPreferredSize(labelSize);
        lblPassword = new JLabel("Password : ");
        lblPassword.setPreferredSize(labelSize);

        /* 필드 설정 */
        txtUsername = new JTextField(txtSize);
        txtPassword = new JTextField(txtSize);

        btnUserReg = new JButton("사용자 생성");
        btnUserReg.setPreferredSize(btnSize);
        btnCancel = new JButton("취소");
        btnCancel.setPreferredSize(btnSize);
    }

    private void setDisplay() {
        // 컴포넌트를 왼쪽 정렬로 배치
		flowLeft = new FlowLayout(FlowLayout.LEFT);

        pnlCenter = new JPanel(new GridLayout(0, 1));

        JPanel pnlUsername = new JPanel(flowLeft);
        pnlUsername.add(lblUsername);
        pnlUsername.add(txtUsername);
        
        JPanel pnlPassword = new JPanel(flowLeft);
        pnlPassword.add(lblPassword);
        pnlPassword.add(txtPassword);

        pnlCenter.add(lblInfo);
        pnlCenter.add(pnlUsername);
        pnlCenter.add(pnlPassword);

        pnlSouth = new JPanel();
		pnlSouth.add(btnUserReg);
		pnlSouth.add(btnCancel);


		pnlCenter.setBorder(new EmptyBorder(0, 20, 0, 20));
		pnlSouth.setBorder(new EmptyBorder(0, 0, 10, 0));

        add(pnlCenter, BorderLayout.CENTER);
        add(pnlSouth, BorderLayout.SOUTH);
    }

    private void addListners() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent we) {
				dispose();
				owner.setVisible(true);
			}
		});

        btnUserReg.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                ae.getActionCommand();
                try {
                    /* 사용자 생성 */
                    new VaultUserpassAuth(vault).createUser(txtUsername.getText(), txtPassword.getText());
                    /* DB Connection 목록 조회 */
                    String[] configList = new VaultDatabaseEngine(vault).configList();
                    
                    String policyName = null;
                    for (int i = 0; i < configList.length; i++) {
                        policyName += ", creds-"+configList[i];

                        String statements = Common.getNestedJsonToStr(new VaultDatabaseEngine(vault).readRole(configList[i]), "data", "creation_statements");
                        // TODO : 대괄호 지워줘야함
                        statements = statements.substring(1, statements.length()-1);
                        new VaultDatabaseEngine(vault).createRole(txtUsername.getText(), configList[i], statements);
                    }

                    /* 이미 생성된 DB Connection에 접근 가능하도록 Policy 추가 */
                    new VaultUserpassAuth(vault).updateUserPolicy(txtUsername.getText(), new VaultUserpassAuth(vault).getUserPolicy(txtUsername.getText())+policyName);
                } catch (VaultException e) {
                    e.getStackTrace();
                }
				dispose();
                owner.updatePnlUser();
				owner.setVisible(true);
				owner.setBtnUserReg(true);
            }
        });

        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                ae.getActionCommand();
				dispose();
				owner.setVisible(true);
				owner.setBtnUserReg(true);
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
