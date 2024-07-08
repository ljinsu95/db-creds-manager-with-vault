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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import common.Common;
import common.model.Vault;

public class LoginForm extends JFrame {
	// private static int AUTH_TOKEN = 0;
	// private static int AUTH_USER_PASS = 1;

	private Vault vault;

	private String vaultUrl = "http://127.0.0.1:8200";
	private String vaultAuthType;
	private String vaultToken;
	private String vaultUsernm;
	private String vaultUserpw;
	private String loginInfoSave;

	private String vaultAuthList[] = { Vault.AUTH_TOKEN, Vault.AUTH_USERNAME };

	private JLabel vaultUrlLabel;
	private JLabel vaultAuthTypeLabel;
	private JLabel vaultUsernameLabel;
	private JLabel vaultPasswordLabel;
	private JLabel vaultTokenLabel;

	private JTextField vaultUrlTxt;
	private ButtonGroup btnGroup;
	private JRadioButton rbtnAuth[];
	private JPasswordField vaultTokenTxt;
	private JTextField vaultUsernameTxt;
	private JTextField vaultPasswordTxt;

	private JPanel northPanel;

	private JPanel usernamePnl;
	private JPanel passwordPnl;
	private JPanel tokenPnl;

	private JCheckBox cbAuthInfoSave;

	private JButton logBtn;
	private JButton joinBtn;
	private LayoutManager flowLeft;
	private LayoutManager flowRight;

	public LoginForm() {
		init();
		setDisplay(); // UI 화면을 설정하는 메서드
		addListners(); // 리스너를 추가하는 메서드
		showFrame(); // 프레임을 화면에 표시하는 메서드
	}

	public void init() {
		// Vault 객체 생성
		vault = new Vault();
		// config.properties 값 호출

		Properties properties = new Properties();

		try (InputStream input = new FileInputStream(common.Common.CONFIG_FILE)) {
			// properties 파일을 읽어온다.
			properties.load(input);

			// properties 파일의 값을 출력한다.
			vaultUrl = properties.getProperty("vault.url");
			vaultAuthType = properties.getProperty("vault.auth");
			vaultToken = properties.getProperty("vault.token");
			vaultUsernm = properties.getProperty("vault.usernm");
			vaultUserpw = properties.getProperty("vault.userpw");
			loginInfoSave = properties.getProperty("login.info.save");
			vault.setVaultAuthType(vaultAuthType);
			System.out.println("Vault URL: " + vaultUrl);
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		// 사이즈 통일
		Dimension labelSize = new Dimension(120, 30);
		int txtSize = 20;
		Dimension btnSize = new Dimension(100, 25);

		// 레이블 설정
		vaultUrlLabel = new JLabel("Vault URL : ");
		vaultUrlLabel.setPreferredSize(labelSize);
		vaultAuthTypeLabel = new JLabel("Vault Auth Type : ");
		vaultAuthTypeLabel.setPreferredSize(labelSize);
		vaultUsernameLabel = new JLabel("Username : ");
		vaultUsernameLabel.setPreferredSize(labelSize);
		vaultPasswordLabel = new JLabel("Password : ");
		vaultPasswordLabel.setPreferredSize(labelSize);
		vaultTokenLabel = new JLabel("Token : ");
		vaultTokenLabel.setPreferredSize(labelSize);

		// 필드 설정
		vaultUrlTxt = new JTextField(txtSize);
		vaultUrlTxt.setText(vaultUrl);
		btnGroup = new ButtonGroup();
		rbtnAuth = new JRadioButton[vaultAuthList.length];
		for (int i = 0; i < vaultAuthList.length; i++) {
			rbtnAuth[i] = new JRadioButton(vaultAuthList[i]);
			if (vaultAuthList[i].equals(vaultAuthType)) {
				rbtnAuth[i].setSelected(true);
			}
			btnGroup.add(rbtnAuth[i]);
		}

		vaultUrlTxt = new JTextField(txtSize);
		vaultUrlTxt.setText(vaultUrl);

		vaultUsernameTxt = new JTextField(txtSize);
		vaultUsernameTxt.setText(vaultUsernm);
		vaultPasswordTxt = new JTextField(txtSize);
		vaultPasswordTxt.setText(vaultUserpw);
		vaultTokenTxt = new JPasswordField(txtSize);
		vaultTokenTxt.setText(vaultToken);

		cbAuthInfoSave = new JCheckBox("로그인 정보 저장");
		if(loginInfoSave.equals("true")) {
			cbAuthInfoSave.setSelected(true);
		}

		logBtn = new JButton("로그인");
		logBtn.setPreferredSize(btnSize);
		joinBtn = new JButton("회원가입");
		joinBtn.setPreferredSize(btnSize);
	}

	public void setDisplay() {
		// 컴포넌트를 왼쪽 정렬로 배치
		flowLeft = new FlowLayout(FlowLayout.LEFT);
		flowRight = new FlowLayout(FlowLayout.RIGHT);

		northPanel = new JPanel(new GridLayout(0, 1));

		JPanel urlPnl = new JPanel(flowLeft);
		urlPnl.add(vaultUrlLabel);
		urlPnl.add(vaultUrlTxt);

		JPanel authPnl = new JPanel(flowLeft);
		authPnl.add(vaultAuthTypeLabel);
		for (int i = 0; i < vaultAuthList.length; i++) {
			authPnl.add(rbtnAuth[i]);
		}

		usernamePnl = new JPanel(flowLeft);
		usernamePnl.add(vaultUsernameLabel);
		usernamePnl.add(vaultUsernameTxt);
		passwordPnl = new JPanel(flowLeft);
		passwordPnl.add(vaultPasswordLabel);
		passwordPnl.add(vaultPasswordTxt);
		tokenPnl = new JPanel(flowLeft);
		tokenPnl.add(vaultTokenLabel);
		tokenPnl.add(vaultTokenTxt);

		northPanel.add(urlPnl);
		northPanel.add(authPnl);
		northPanel.add(usernamePnl);
		northPanel.add(passwordPnl);
		// northPanel.add(tokenPnl);

		JPanel southPanel = new JPanel(new BorderLayout());

		JPanel pnlLogin = new JPanel(flowRight);
		pnlLogin.add(logBtn);
		pnlLogin.add(joinBtn);

		southPanel.add(cbAuthInfoSave, BorderLayout.WEST);
		southPanel.add(pnlLogin, BorderLayout.EAST);

		northPanel.setBorder(new EmptyBorder(0, 20, 0, 20));
		southPanel.setBorder(new EmptyBorder(0, 10, 10, 10));

		add(northPanel, BorderLayout.NORTH);
		add(southPanel, BorderLayout.SOUTH);

	}

	public void addListners() {
		// 로그인 버튼
		logBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// vault 객체 정보 설정
				vault.setVaultUrl(vaultUrlTxt.getText());
				vault.setVaultToken(String.valueOf(vaultTokenTxt.getPassword()));
				vault.setVaultUserNm(vaultUsernameTxt.getText());
				vault.setVaultUesrPw(vaultPasswordTxt.getText());

				// Vault URL Health Check
				if (!vault.healthCheck()) {
					JOptionPane.showConfirmDialog(LoginForm.this, "Vault URL 정보가 일치하지 않습니다.", "RETRY",
					JOptionPane.WARNING_MESSAGE);
				} else {
					// Vault Login 시도
					if (vault.tokenLookupSelf() == Common.SUCCESS_CODE) {
						System.out.println("로그인 성공");
						System.out.println(vault.getVaultUrl());
	
						MainForm mainForm = new MainForm(LoginForm.this, vault);
	
						setVisible(false);
						mainForm.setVisible(true);
	
						Properties properties = new Properties();
	
						try (InputStream input = new FileInputStream(common.Common.CONFIG_FILE)) {
							// properties 파일을 읽어온다.
							properties.load(input);
	
							// properties 파일의 값을 업데이트한다.
							properties.setProperty("vault.url", vault.getVaultUrl());
							properties.setProperty("vault.auth", vault.getVaultAuthType());
							properties.setProperty("vault.usernm", vault.getVaultUserNm());
							if (cbAuthInfoSave.isSelected()) {
								properties.setProperty("vault.token", vault.getVaultToken());
								properties.setProperty("login.info.save", "true");
							} else {
								properties.setProperty("vault.token", "");
								properties.setProperty("login.info.save", "false");
								
								vaultTokenTxt.setText("");
								vaultUsernameTxt.setText("");
								vaultPasswordTxt.setText("");
							}
							// Save the updated properties back to the file
							try (OutputStream output = new FileOutputStream(common.Common.CONFIG_FILE)) {
								properties.store(output, null);

								System.out.println("Updated properties saved to " + common.Common.CONFIG_FILE);

							} catch (IOException io) {
								io.printStackTrace();
							}
						} catch (IOException ex) {
							ex.printStackTrace();
						}
					} else {
						JOptionPane.showConfirmDialog(LoginForm.this, "로그인 정보가 일치하지 않습니다.", "RETRY",
								JOptionPane.WARNING_MESSAGE);
					}
				}
			}
		});

		// 라디오 버튼
		for (int i = 0; i < vaultAuthList.length; i++) {
			rbtnAuth[i].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// System.out.println(e.getActionCommand());
					JRadioButton selectedRadioButton = (JRadioButton) e.getSource();
					String selectedValue = selectedRadioButton.getText();
					System.out.println("Selected: " + selectedValue);
					vault.setVaultAuthType(selectedValue);

					updateAuthParamPnl(selectedValue);
				}
			});
		}

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent we) {
				int choice = JOptionPane.showConfirmDialog(
						LoginForm.this, "다음에 또 만나요!", "BYE JAVA", JOptionPane.OK_CANCEL_OPTION);
				if (choice == JOptionPane.OK_OPTION) {
					System.exit(0);
				}
			}
		});

	}

	public void showFrame() {
		setTitle("Welcome to JAVA");
		pack(); // 프레임의 크기를 컨텐츠에 맞게 조정
		// setLocale(null); // 시스템의 기본 로케일로 설정된다는 의미, 이 메서드는 주로 다국어 지원을 고려하여 프로그램 개발 될 때
		// 사용
		setLocationRelativeTo(null); // 프레임을 화면 중앙에 위치 setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); // 프레임이 닫힐 때 아무
										// 동작도 하지 않도록 설정
		setResizable(false); // 프레임의 크기 고정
		setVisible(true);
		updateAuthParamPnl(vaultAuthType);
	}

	private void updateAuthParamPnl(String authType) {
		if (authType.equals(Vault.AUTH_TOKEN)) {
			northPanel.remove(usernamePnl);
			northPanel.remove(passwordPnl);
			northPanel.add(tokenPnl);

			northPanel.revalidate();
			northPanel.repaint();
		} else if (authType.equals(Vault.AUTH_USERNAME)) {
			northPanel.remove(tokenPnl);
			northPanel.add(usernamePnl);
			northPanel.add(passwordPnl);

			northPanel.revalidate();
			northPanel.repaint();
		}
	}

	public Vault getVault() {
		return vault;
	}
}