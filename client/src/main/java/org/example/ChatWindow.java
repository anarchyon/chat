package org.example;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class ChatWindow extends JFrame {
    public static final int MAIN_WINDOW_MINIMUM_WIDTH = 800;
    public static final int MAIN_WINDOW_MINIMUM_HEIGHT = 600;
    public static final int LOGON_MINIMUM_WIDTH = 380;
    public static final int LOGON_MINIMUM_HEIGHT = 220;


    private JFrame registerLogon;
    private JTextArea listArea;
    private JTextArea chatArea;
    private JTextField yourMessage;

    private JTextField loginField;
    private JTextField passwordField;
    private JTextField registerLoginField;
    private JTextField registerPasswordField;
    private JTextField nickField;

    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    private final Client client;
    private WindowListener closeListener = new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent e) {
            sendMessage(Client.SYSTEM_FLAG_DISCONNECT);
            super.windowClosing(e);
        }
    };

    public ChatWindow() {
        this.client = new Client();

        setCallbacks();
        setMainWindow();

        client.connect();
        if (!client.isConnectionOk()) {
            JOptionPane.showMessageDialog(this, "Unable to connect to server");
            System.exit(0);
        }

        setRegisterLogonWindow();
    }

    private void setCallbacks() {
        client.setCallOnChangeClientList(this::listMembers);
        client.setCallOnMsgReceived(this::appendText);
        client.setGetAuthStatus(this::showMainWindow);
        client.setCallChangeNick(this::setTitle);
        client.setCallErrors(this::showError);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    private void showMainWindow(int authStatus) {
        switch (authStatus) {
            case Client.AUTHORIZATION_OK:
                registerLogon.setVisible(false);
                this.setTitle(client.getLogin());
                this.setVisible(true);
                yourMessage.grabFocus();
                break;
            case Client.AUTHORIZATION_BAD:
                JOptionPane.showMessageDialog(this, "Invalid login data");
                break;
            case Client.AUTHORIZATION_BUSY:
                JOptionPane.showMessageDialog(this, "Client with such data is logged in already");
                break;
            case Client.AUTHORIZATION_TIMEOUT:
                JOptionPane.showMessageDialog(this, "Authorization timeout");
                break;
        }
    }

    private void setMainWindow() {
        Dimension windowSize = new Dimension(MAIN_WINDOW_MINIMUM_WIDTH, MAIN_WINDOW_MINIMUM_HEIGHT);
        setSize(windowSize);
        setMinimumSize(windowSize);
        setLocation((screenSize.width - windowSize.width) / 2, (screenSize.height - windowSize.height) / 2);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(closeListener);

        add(listPanel(), BorderLayout.NORTH);
        add(chatPanel());
        add(writePanel(), BorderLayout.SOUTH);
    }

    private JPanel listPanel() {
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBorder(BorderFactory.createTitledBorder("List"));
        listArea = new JTextArea();
        listArea.setEditable(false);

        listPanel.add(new JScrollPane(listArea, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));

        return listPanel;
    }

    private JPanel chatPanel() {
        JPanel chatPanel = new JPanel(new BorderLayout());
        chatPanel.setBorder(BorderFactory.createTitledBorder("Chat"));
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setAutoscrolls(true);
        DefaultCaret caret = (DefaultCaret) chatArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        chatPanel.add(new JScrollPane(chatArea));

        return chatPanel;
    }

    private JPanel writePanel() {
        JPanel writePanel = new JPanel(new BorderLayout());
        yourMessage = new JTextField();
        JButton send = new JButton("Отправить");

        ActionListener sendListener = e -> {
            sendMessage(yourMessage.getText());
            yourMessage.setText("");
        };

        send.addActionListener(sendListener);
        yourMessage.addActionListener(sendListener);

        writePanel.add(yourMessage);
        writePanel.add(send, BorderLayout.EAST);
        return writePanel;
    }

    private void setRegisterLogonWindow() {
        registerLogon = new JFrame();
        Dimension logonSize = new Dimension(LOGON_MINIMUM_WIDTH, LOGON_MINIMUM_HEIGHT);
        registerLogon.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        registerLogon.setSize(logonSize);
        registerLogon.setResizable(false);
        registerLogon.setLocation((screenSize.width - logonSize.width) / 2, (screenSize.height - logonSize.height) / 2);
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);

        tabbedPane.addTab("register", registerPanel());
        tabbedPane.addTab("logon", logonPanel());
        tabbedPane.setSelectedIndex(1);
        tabbedPane.addChangeListener(e -> {
            loginField.setText("");
            passwordField.setText("");
            registerLoginField.setText("");
            registerPasswordField.setText("");
            nickField.setText("");
        });
        registerLogon.add(tabbedPane);
        registerLogon.addWindowListener(closeListener);
        registerLogon.setVisible(true);

    }

    private JPanel logonPanel() {
        JPanel logonPanel = new JPanel();
        logonPanel.setLayout(new BorderLayout());

        JPanel fieldsGrid = new JPanel(new GridLayout(2, 1));
        JPanel loginPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel loginLabel = new JLabel("Login:", SwingConstants.RIGHT);
        loginField = new JTextField();
        loginField.setPreferredSize(new Dimension(220, 25));
        ((AbstractDocument)loginField.getDocument()).setDocumentFilter(new LoginPassDocumentFilter());
        loginPanel.add(loginLabel);
        loginPanel.add(loginField);

        JPanel passwordPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel passwordLabel = new JLabel("Password:", SwingConstants.RIGHT);
        passwordField = new JTextField();
        passwordField.setPreferredSize(new Dimension(220, 25));
        ((AbstractDocument)passwordField.getDocument()).setDocumentFilter(new LoginPassDocumentFilter());
        passwordPanel.add(passwordLabel);
        passwordPanel.add(passwordField);
        fieldsGrid.add(loginPanel);
        fieldsGrid.add(passwordPanel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logonButton = new JButton("Вход");
        logonButton.setPreferredSize(new Dimension(150, 30));
        buttonPanel.add(logonButton);

        logonPanel.add(fieldsGrid, BorderLayout.NORTH);
        logonPanel.add(buttonPanel, BorderLayout.SOUTH);

        logonButton.addActionListener(e -> {
            String login = loginField.getText();
            String password = passwordField.getText();
            loginField.setText("");
            passwordField.setText("");
            if (login.trim().isEmpty() || password.trim().isEmpty()) {
                return;
            }
            sendMessage(Client.SYSTEM_FLAG_AUTHORIZATION
                    + " " + login.trim()
                    + " " + password.trim());
        });

        return logonPanel;
    }

    private JPanel registerPanel() {
        JPanel registerPanel = new JPanel();
        registerPanel.setLayout(new BorderLayout());

        JPanel loginPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel loginLabel = new JLabel("Login:", SwingConstants.RIGHT);
        registerLoginField = new JTextField();
        registerLoginField.setPreferredSize(new Dimension(220, 25));
        ((AbstractDocument)registerLoginField.getDocument()).setDocumentFilter(new LoginPassDocumentFilter());
        loginPanel.add(loginLabel);
        loginPanel.add(registerLoginField);

        JPanel passwordPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel passwordLabel = new JLabel("Password:", SwingConstants.RIGHT);
        registerPasswordField = new JTextField();
        registerPasswordField.setPreferredSize(new Dimension(220, 25));
        ((AbstractDocument)registerPasswordField.getDocument()).setDocumentFilter(new LoginPassDocumentFilter());
        passwordPanel.add(passwordLabel);
        passwordPanel.add(registerPasswordField);

        JPanel nickPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel nickLabel = new JLabel("Nickname:", SwingConstants.RIGHT);
        nickField = new JTextField();
        nickField.setPreferredSize(new Dimension(220, 25));
        nickPanel.add(nickLabel);
        nickPanel.add(nickField);

        JPanel fieldsGrid = new JPanel(new GridLayout(3, 1));
        fieldsGrid.add(loginPanel);
        fieldsGrid.add(passwordPanel);
        fieldsGrid.add(nickPanel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton registerButton = new JButton("Регистрация");
        registerButton.setPreferredSize(new Dimension(150, 30));
        buttonPanel.add(registerButton);

        registerButton.addActionListener(e -> JOptionPane.showMessageDialog(registerLogon, "//TODO"));

        registerPanel.add(fieldsGrid, BorderLayout.NORTH);
        registerPanel.add(buttonPanel, BorderLayout.SOUTH);

        registerPanel.setEnabled(false);
        return registerPanel;
    }

    public void appendText(String message) {
        chatArea.append(message);
    }

    private void listMembers(String list) {
        listArea.setText(list);
    }

    private void sendMessage(String message) {
        if (message.trim().isEmpty()) {
            return;
        }
        client.sendMessage(message.trim());
    }
}
