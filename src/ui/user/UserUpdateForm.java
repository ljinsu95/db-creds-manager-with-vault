package src.ui.user;

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

import src.common.VaultException;
import src.model.Vault;
import src.service.VaultUserpassAuth;

/* User Password 업데이드
 * 
 */
public class UserUpdateForm extends JDialog {
    private Vault vault;

    private UserMainForm owner;

    private JLabel lblInfo;
    private JLabel lblUsername;
    private JLabel lblPassword;
    private JLabel lblRePassword;

    private JTextField txtUsername;
    private JTextField txtPassword;
    private JTextField txtRePassword;

    private JButton btnUserReg;
    private JButton btnCancel;

    private LayoutManager flowLeft;
    private JPanel pnlCenter;
    private JPanel pnlSouth;


    public UserUpdateForm(UserMainForm owner) {
        this.owner = owner;

        init();
        setDisplay();
        addListners();
        showFrame();
    }

    private void init() {
        vault = Vault.getInstance();

        // 사이즈 통일
		Dimension labelSize = new Dimension(120, 30);
		int txtSize = 20;
		Dimension btnSize = new Dimension(100, 25);
        
        /* 레이블 설정 */
        lblInfo = new JLabel("사용자 생성 후 사용자가 직접 패스워드 변경이 가능합니다.");
        lblUsername = new JLabel("Username : ");
        lblUsername.setPreferredSize(labelSize);
        lblPassword = new JLabel("New Password : ");
        lblPassword.setPreferredSize(labelSize);
        lblRePassword = new JLabel("Re-enter Password : ");
        lblRePassword.setPreferredSize(labelSize);

        /* 필드 설정 */
        txtUsername = new JTextField(txtSize);
        txtUsername.setText(vault.getVaultUserNm());
        txtUsername.setEnabled(false);
        txtPassword = new JTextField(txtSize);
        txtRePassword = new JTextField(txtSize);

        btnUserReg = new JButton("패스워드 변경");
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
        
        JPanel pnlRePassword = new JPanel(flowLeft);
        pnlPassword.add(lblRePassword);
        pnlPassword.add(txtRePassword);

        pnlCenter.add(lblInfo);
        pnlCenter.add(pnlUsername);
        pnlCenter.add(pnlPassword);
        pnlCenter.add(pnlRePassword);

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
                    // TODO : 패스워드 변경 시 권한 에러에 대한 예외처리 해야함....
                    /* 사용자 생성 */
                    new VaultUserpassAuth(vault).updateUser(txtUsername.getText(), txtPassword.getText());
                } catch (VaultException ve) {
                    System.out.println("출력 확인");
                    System.out.println(ve.getMessage());

                    ve.printStackTrace();
                }
				dispose();
				owner.setVisible(true);
				// owner.setBtnUserReg(true);
            }
        });

        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                ae.getActionCommand();
				dispose();
				owner.setVisible(true);
				// owner.setBtnUserReg(true);
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
