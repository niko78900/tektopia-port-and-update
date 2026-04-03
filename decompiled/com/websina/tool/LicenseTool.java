/*
 * Decompiled with CFR 0.152.
 */
package com.websina.tool;

import com.websina.license.License;
import com.websina.license.SignatureUtil;
import com.websina.util.ByteHex;
import com.websina.util.DateParser;
import com.websina.util.FileUtil;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class LicenseTool
extends JFrame {
    private static final String KEY_FILE = "dsakey.cfg";
    private static final String FEATURE_FILE = "feature.txt";
    private static final String DATE_FORMAT = "YYYY-MM-DD";
    private static final long EXPIRATION_TIME = 2592000000L;
    private JButton create;
    private JButton clear;
    private JButton quit;
    private JTextField[] featureField;
    private JTextArea result;
    private byte[] key;
    private License lic;
    private String[] names;
    private Properties feature;
    private WindowListener wl = new WindowAdapter(){

        @Override
        public void windowClosing(WindowEvent e) {
            LicenseTool.this.dispose();
            System.exit(0);
        }
    };

    public LicenseTool() {
        super("License Manager");
        this.feature = new Properties();
        ArrayList<String> nameList = new ArrayList<String>();
        try {
            new FileUtil(FEATURE_FILE).read(nameList, this.feature);
        }
        catch (Exception e) {
            throw new RuntimeException("Can not load the feature list file: feature.txt\nPlease make sure it is in the CLASSPATH");
        }
        this.names = nameList.toArray(new String[0]);
        Properties prop = new Properties();
        try {
            prop.load(ClassLoader.getSystemResourceAsStream(KEY_FILE));
        }
        catch (Exception e) {
            throw new RuntimeException("Can not load the dsa key file: dsakey.cfg\nPlease make sure it is in the CLASSPATH");
        }
        String privateKey = prop.getProperty("private-key");
        this.key = ByteHex.convert(privateKey);
        this.lic = License.newLicense();
        this.setBackground(Color.lightGray);
        int width = 300;
        int offset = 500;
        if (this.names.length > 3) {
            width = 600;
            offset = 300;
        }
        if (this.names.length > 7) {
            width = 1000;
            offset = 100;
        }
        this.setSize(width, 400);
        this.setLocation(offset, 50);
        this.setupGUI();
    }

    public void create() throws Exception {
        String signature = SignatureUtil.sign(this.lic.format(), this.key);
        this.lic.setSignature(signature);
        this.lic.create();
    }

    private void setupGUI() {
        Container container = this.getContentPane();
        this.create = new JButton("Create");
        this.clear = new JButton("Clear");
        this.quit = new JButton("Close");
        this.result = new JTextArea();
        this.result.setText("After click on Create, it might take a few moments to create a license.\nThe result will be shown here ....");
        this.result.setEditable(false);
        this.featureField = new JTextField[this.names.length + 1];
        for (int i = 0; i < this.names.length; ++i) {
            this.featureField[i] = new JTextField(14);
            String value = this.feature.getProperty(this.names[i]);
            if (value == null) continue;
            this.featureField[i].setText(value);
        }
        this.featureField[this.names.length] = new JTextField(14);
        JPanel content = new JPanel();
        content.setLayout(new GridLayout(4, 1));
        JPanel p = null;
        for (int i = 0; i < this.names.length; ++i) {
            p = new JPanel();
            p.add(new JLabel(this.names[i] + "    "));
            p.add(this.featureField[i]);
            content.add(p);
        }
        p = new JPanel();
        p.add(new JLabel("Expiration  "));
        p.add(this.featureField[this.names.length]);
        Date date = new Date(System.currentTimeMillis() + 2592000000L);
        DateParser d = new DateParser(date);
        StringBuffer buf = new StringBuffer(10);
        buf.append(d.getYear()).append('-').append(d.getMonth()).append('-').append(d.getDayOfMonth());
        this.featureField[this.names.length].setText(buf.toString());
        content.add(p);
        JPanel button = new JPanel();
        button.add(this.create);
        button.add(this.clear);
        button.add(this.quit);
        container.add((Component)new JScrollPane(this.result), "Center");
        container.add((Component)button, "South");
        container.add((Component)content, "North");
        ButtonEvent listener = new ButtonEvent();
        this.create.addActionListener(listener);
        this.quit.addActionListener(listener);
        this.clear.addActionListener(listener);
        this.addWindowListener(this.wl);
    }

    private boolean validDate(String d) {
        Date date = DateParser.toUtilDate(d);
        return date != null;
    }

    public static void main(String[] args) {
        LicenseTool manager = new LicenseTool();
        manager.setVisible(true);
    }

    private class ButtonEvent
    implements ActionListener {
        private ButtonEvent() {
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Object s = e.getSource();
            if (s == LicenseTool.this.create) {
                for (int i = 0; i < LicenseTool.this.names.length; ++i) {
                    LicenseTool.this.lic.setFeature(LicenseTool.this.names[i], LicenseTool.this.featureField[i].getText());
                }
                String d = LicenseTool.this.featureField[LicenseTool.this.names.length].getText();
                if (d == null || d.trim().length() == 0) {
                    LicenseTool.this.lic.setExpiration("never");
                } else if (LicenseTool.this.validDate(d)) {
                    LicenseTool.this.lic.setExpiration(d);
                } else {
                    LicenseTool.this.featureField[LicenseTool.this.names.length].setText("");
                    LicenseTool.this.result.setText("Invalid date format, please enter\nit as YYYY-MM-DD");
                    return;
                }
                try {
                    LicenseTool.this.create();
                    LicenseTool.this.result.setText("License is created successfully\nand is written to the file license.lic");
                }
                catch (Exception ex) {
                    LicenseTool.this.result.setText(ex.getMessage());
                }
            } else if (s == LicenseTool.this.clear) {
                for (int i = 0; i < LicenseTool.this.names.length; ++i) {
                    String value = LicenseTool.this.feature.getProperty(LicenseTool.this.names[i]);
                    if (value == null) continue;
                    LicenseTool.this.featureField[i].setText(value);
                }
                LicenseTool.this.featureField[LicenseTool.this.names.length].setText("");
                LicenseTool.this.result.setText("");
            } else if (s == LicenseTool.this.quit) {
                LicenseTool.this.dispose();
                System.exit(0);
            }
        }
    }
}
