package view;

import controller.AuthController;
import util.Session;
import util.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LoginGUI extends BaseFrame {

    public LoginGUI() {
        super("Login", 920, 620);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        buildUI();
        setVisible(true);
    }

    private void buildUI() {

        // ── Left decorative panel ──────────────────────────────────────────
        JPanel left = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(20, 40, 80),
                        getWidth(), getHeight(), new Color(10, 20, 50));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Decorative circles
                g2.setColor(new Color(99, 179, 237, 18));
                g2.fillOval(-60, -60, 300, 300);
                g2.fillOval(50, 200, 200, 200);
                g2.setColor(new Color(99, 179, 237, 10));
                g2.fillOval(100, 80, 400, 400);
                g2.dispose();
            }
        };
        left.setLayout(null);
        left.setBounds(0, 0, 380, 620);
        left.setOpaque(false);
        root.add(left);

        // Bus icon
        JLabel icon = new JLabel("🚌");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        icon.setBounds(120, 140, 120, 80);
        left.add(icon);

        JLabel brand = new JLabel("BTRS");
        brand.setForeground(Theme.ACCENT);
        brand.setFont(new Font("Segoe UI", Font.BOLD, 38));
        brand.setBounds(80, 220, 200, 50);
        left.add(brand);

        JLabel tagline = new JLabel("Bus Ticket Reservation System");
        tagline.setForeground(Theme.TEXT_MUTED);
        tagline.setFont(Theme.FONT_BODY);
        tagline.setBounds(65, 268, 260, 25);
        left.add(tagline);

        // Feature bullets
        String[] features = {"✓  Quick booking in seconds",
                             "✓  Choose your preferred seat",
                             "✓  Track your bookings",
                             "✓  Hassle-free cancellation"};
        int fy = 330;
        for (String f : features) {
            JLabel fl = new JLabel(f);
            fl.setForeground(new Color(160, 200, 240));
            fl.setFont(Theme.FONT_SMALL);
            fl.setBounds(70, fy, 260, 22);
            left.add(fl);
            fy += 26;
        }

        // Divider line
        JPanel divider = new JPanel();
        divider.setBackground(Theme.BORDER);
        divider.setBounds(380, 0, 1, 620);
        root.add(divider);

        // ── Right form panel ───────────────────────────────────────────────
        int fx = 420, fw = 380;

        JLabel welcome = new JLabel("Welcome back");
        welcome.setForeground(Theme.TEXT_MUTED);
        welcome.setFont(Theme.FONT_BODY);
        welcome.setBounds(fx, 110, fw, 25);
        root.add(welcome);

        JLabel loginTitle = new JLabel("Sign in to your account");
        loginTitle.setForeground(Theme.TEXT_PRIMARY);
        loginTitle.setFont(Theme.FONT_TITLE);
        loginTitle.setBounds(fx, 135, fw, 40);
        root.add(loginTitle);

        addRule(fx, 182, 340);

        // Email
        JLabel emailL = Theme.label("Email address", Theme.TEXT_MUTED, Theme.FONT_SMALL);
        emailL.setBounds(fx, 210, fw, 20);
        root.add(emailL);
        JTextField emailF = Theme.styledField("");
        emailF.setBounds(fx, 232, fw, 40);
        root.add(emailF);

        // Password
        JLabel passL = Theme.label("Password", Theme.TEXT_MUTED, Theme.FONT_SMALL);
        passL.setBounds(fx, 292, fw, 20);
        root.add(passL);
        JPasswordField passF = Theme.styledPassField();
        passF.setBounds(fx, 314, fw, 40);
        root.add(passF);

        // Show password toggle
        JCheckBox showPass = new JCheckBox("Show password");
        showPass.setForeground(Theme.TEXT_MUTED);
        showPass.setFont(Theme.FONT_SMALL);
        showPass.setBackground(new Color(0,0,0,0));
        showPass.setOpaque(false);
        showPass.setBounds(fx, 360, 200, 22);
        showPass.addActionListener(e -> passF.setEchoChar(
                showPass.isSelected() ? '\0' : '●'));
        root.add(showPass);

        // Login button
        JButton loginBtn = Theme.primaryButton("Sign In");
        loginBtn.setBounds(fx, 402, fw, 46);
        root.add(loginBtn);

        // Register link
        JLabel orLabel = Theme.label("Don't have an account?", Theme.TEXT_MUTED, Theme.FONT_SMALL);
        orLabel.setBounds(fx, 462, 200, 22);
        root.add(orLabel);

        JButton regBtn = Theme.secondaryButton("Create Account");
        regBtn.setBounds(fx, 488, fw, 40);
        root.add(regBtn);

        // Status label
        JLabel status = Theme.label("", Theme.DANGER, Theme.FONT_SMALL);
        status.setBounds(fx, 540, fw, 20);
        root.add(status);

        // ── Actions ────────────────────────────────────────────────────────
        AuthController ctrl = new AuthController();

        Runnable doLogin = () -> {
            String email = emailF.getText().trim();
            String pass  = new String(passF.getPassword());
            if (email.isBlank() || pass.isBlank()) {
                status.setText("Please fill in all fields.");
                return;
            }
            if (ctrl.login(email, pass)) {
                Session.userId   = ctrl.getUserId(email);
                Session.email    = email;
                Session.userName = ctrl.getName(email);
                new DashboardGUI();
                dispose();
            } else {
                status.setText("Invalid email or password.");
                passF.setText("");
            }
        };

        loginBtn.addActionListener(e -> doLogin.run());
        passF.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) doLogin.run();
            }
        });

        regBtn.addActionListener(e -> { new RegisterGUI(); dispose(); });
    }
}
