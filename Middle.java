import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import javax.swing.plaf.basic.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import java.util.List;
import java.util.stream.*;

public class Middle extends JFrame {

    // ── 데이터 모델 ──────────────────────────────────────────────
    static class Transaction {
        String date, type, category, memo;
        int amount;

        Transaction(String date, String type, String category, int amount, String memo) {
            this.date = date; this.type = type;
            this.category = category; this.amount = amount; this.memo = memo;
        }

        String toCsv() { return date+","+type+","+category+","+amount+","+memo; }

        static Transaction fromCsv(String line) {
            String[] p = line.split(",", 5);
            if (p.length < 5) return null;
            try { return new Transaction(p[0],p[1],p[2],Integer.parseInt(p[3]),p[4]); }
            catch (Exception e) { return null; }
        }
    }

    // ── 색상 ─────────────────────────────────────────────────────
    static final Color BG      = new Color(18, 18, 20);
    static final Color CARD    = new Color(30, 30, 34);
    static final Color CARD2   = new Color(38, 38, 43);
    static final Color ACCENT  = new Color(99, 102, 241);   // indigo
    static final Color GREEN   = new Color(34, 197, 94);
    static final Color RED     = new Color(239, 68, 68);
    static final Color TEXT    = new Color(245, 245, 250);
    static final Color SUBTEXT = new Color(160, 160, 175);
    static final Color SUBTLE  = new Color(50, 50, 58);

    // ── 상태 ─────────────────────────────────────────────────────
    List<Transaction> transactions = new ArrayList<>();
    String csvPath = System.getProperty("user.home") + "/budget_data.csv";

    DefaultTableModel tableModel;
    JTable table;
    JLabel lblIncome, lblExpense, lblBalance;
    JComboBox<String> cbType, cbCategory;
    JTextField tfAmount, tfMemo;
    JPanel chartRef;

    static final String[] INCOME_CAT  = {"급여","용돈","알바","기타"};
    static final String[] EXPENSE_CAT = {"식비","교통","쇼핑","문화","통신","기타"};

    // ── 생성자 ───────────────────────────────────────────────────
    public Middle() {
        setTitle("가계부");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(860, 600);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG);

        loadCsv();
        buildUI();
        refresh();
        setVisible(true);
    }

    // ── UI 구성 ──────────────────────────────────────────────────
    void buildUI() {
        setLayout(new BorderLayout(0, 0));
        add(buildSummary(), BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(1, 2, 12, 0));
        center.setBackground(BG);
        center.setBorder(BorderFactory.createEmptyBorder(0, 16, 16, 16));
        center.add(buildInputPanel());
        center.add(buildListPanel());
        add(center, BorderLayout.CENTER);
    }

    // ── 상단 요약 카드 ───────────────────────────────────────────
    JPanel buildSummary() {
        JPanel p = new JPanel(new GridLayout(1, 3, 12, 0));
        p.setBackground(BG);
        p.setBorder(BorderFactory.createEmptyBorder(16, 16, 12, 16));

        lblIncome  = new JLabel("0 원", SwingConstants.LEFT);
        lblExpense = new JLabel("0 원", SwingConstants.LEFT);
        lblBalance = new JLabel("0 원", SwingConstants.LEFT);

        p.add(summaryCard("수입",  lblIncome,  GREEN));
        p.add(summaryCard("지출",  lblExpense, RED));
        p.add(summaryCard("잔액",  lblBalance, ACCENT));
        return p;
    }

    JPanel summaryCard(String title, JLabel valueLabel, Color accent) {
        JPanel card = new RoundPanel(16, CARD);
        card.setLayout(new BorderLayout(0, 6));
        card.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        titleLabel.setForeground(SUBTEXT);

        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        valueLabel.setForeground(TEXT);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    // ── 입력 패널 ────────────────────────────────────────────────
    JPanel buildInputPanel() {
        JPanel p = new RoundPanel(16, CARD);
        p.setLayout(new GridBagLayout());
        p.setBorder(BorderFactory.createEmptyBorder(20, 22, 20, 22));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(5, 4, 5, 4);
        gc.fill = GridBagConstraints.HORIZONTAL;

        // 제목
        JLabel title = new JLabel("새 거래 입력");
        title.setFont(new Font("SansSerif", Font.BOLD, 15));
        title.setForeground(TEXT);
        gc.gridx=0; gc.gridy=0; gc.gridwidth=2; gc.insets=new Insets(0,4,14,4);
        p.add(title, gc);
        gc.gridwidth=1; gc.insets=new Insets(5,4,5,4);

        // 유형
        addFormRow(p, gc, 1, "유형",
                cbType = styledCombo(new String[]{"지출","수입"}));
        cbType.addActionListener(e -> cbCategory.setModel(new DefaultComboBoxModel<>(
                "수입".equals(cbType.getSelectedItem()) ? INCOME_CAT : EXPENSE_CAT)));

        // 카테고리
        addFormRow(p, gc, 2, "카테고리",
                cbCategory = styledCombo(EXPENSE_CAT));

        // 금액
        tfAmount = modernField("금액을 입력하세요");
        addFormRow(p, gc, 3, "금액(원)", tfAmount);

        // 메모
        tfMemo = modernField("메모를 입력하세요 (선택)");
        addFormRow(p, gc, 4, "메모", tfMemo);

        // 추가 버튼
        JButton btnAdd = roundButton("추가하기", ACCENT, Color.WHITE);
        btnAdd.addActionListener(e -> addTransaction());
        gc.gridx=0; gc.gridy=5; gc.gridwidth=2;
        gc.fill=GridBagConstraints.HORIZONTAL;
        gc.insets=new Insets(14,4,8,4);
        p.add(btnAdd, gc);

        // 차트
        chartRef = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawSimpleChart((Graphics2D)g, getWidth(), getHeight());
            }
        };
        chartRef.setOpaque(false);
        chartRef.setPreferredSize(new Dimension(0, 110));
        gc.gridx=0; gc.gridy=6; gc.gridwidth=2;
        gc.fill=GridBagConstraints.BOTH; gc.weightx=1; gc.weighty=1;
        gc.insets=new Insets(10,0,0,0);
        p.add(chartRef, gc);

        return p;
    }

    void addFormRow(JPanel p, GridBagConstraints gc, int row, String lbl, JComponent comp) {
        gc.gridx=0; gc.gridy=row; gc.gridwidth=1;
        gc.fill=GridBagConstraints.NONE; gc.anchor=GridBagConstraints.WEST;
        gc.weightx=0;
        JLabel l = new JLabel(lbl);
        l.setFont(new Font("SansSerif", Font.PLAIN, 12));
        l.setForeground(SUBTEXT);
        l.setPreferredSize(new Dimension(60, 20));
        p.add(l, gc);
        gc.gridx=1; gc.fill=GridBagConstraints.HORIZONTAL; gc.weightx=1;
        p.add(comp, gc);
    }

    // ── 내역 패널 ────────────────────────────────────────────────
    JPanel buildListPanel() {
        JPanel p = new RoundPanel(16, CARD);
        p.setLayout(new BorderLayout(0, 10));
        p.setBorder(BorderFactory.createEmptyBorder(20, 18, 16, 18));

        // 상단: 타이틀
        JLabel title = new JLabel("거래 내역");
        title.setFont(new Font("SansSerif", Font.BOLD, 15));
        title.setForeground(TEXT);
        p.add(title, BorderLayout.NORTH);

        // 테이블
        String[] cols = {"날짜","유형","카테고리","금액(원)","메모"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setBackground(CARD);
        table.setForeground(TEXT);
        table.setGridColor(SUBTLE);
        table.setRowHeight(30);
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.getTableHeader().setBackground(CARD);
        table.getTableHeader().setForeground(SUBTEXT);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 11));
        table.getTableHeader().setBorder(BorderFactory.createEmptyBorder());
        table.setSelectionBackground(new Color(99,102,241,60));
        table.setSelectionForeground(TEXT);
        table.setFillsViewportHeight(true);
        table.setIntercellSpacing(new Dimension(0, 1));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBackground(CARD);
        scroll.getViewport().setBackground(CARD);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        p.add(scroll, BorderLayout.CENTER);

        // 하단 우측: 삭제 버튼
        JButton btnDel = ghostButton("🗑  선택 삭제");
        btnDel.addActionListener(e -> deleteSelected());
        btnDel.setPreferredSize(new Dimension(100, 30));
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 4));
        bottom.setOpaque(false);
        bottom.add(btnDel);
        p.add(bottom, BorderLayout.SOUTH);

        return p;
    }

    // ── 차트 ─────────────────────────────────────────────────────
    void drawSimpleChart(Graphics2D g2, int w, int h) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Map<String,Integer> map = new LinkedHashMap<>();
        for (String c : EXPENSE_CAT) map.put(c, 0);
        transactions.stream().filter(t->t.type.equals("지출"))
                .forEach(t->map.merge(t.category, t.amount, Integer::sum));

        int total = map.values().stream().mapToInt(i->i).sum();
        if (total == 0) {
            g2.setColor(SUBTEXT);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
            g2.drawString("지출 내역이 없습니다", w/2-55, h/2);
            return;
        }

        Color[] palette = {RED, ACCENT, GREEN, new Color(251,191,36),
                new Color(167,139,250), new Color(251,146,60)};

        long entries = map.values().stream().filter(v->v>0).count();
        int barW = entries>0 ? (int)((w-20)/entries)-8 : 0;
        int maxV = map.values().stream().mapToInt(i->i).max().orElse(1);
        int x=10; int idx=0;

        g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
        for (Map.Entry<String,Integer> e : map.entrySet()) {
            if (e.getValue()==0) continue;
            int barH = (int)((double)e.getValue()/maxV*(h-30));
            int y = h-20-barH;
            g2.setColor(palette[idx%palette.length]);
            g2.fillRoundRect(x, y, barW, barH, 8, 8);
            g2.setColor(SUBTEXT);
            FontMetrics fm = g2.getFontMetrics();
            String cat = e.getKey();
            g2.drawString(cat, x+(barW-fm.stringWidth(cat))/2, h-5);
            x+=barW+8; idx++;
        }
    }

    // ── 거래 추가 ────────────────────────────────────────────────
    void addTransaction() {
        try {
            String type = (String)cbType.getSelectedItem();
            String cat  = (String)cbCategory.getSelectedItem();
            int amount  = Integer.parseInt(tfAmount.getText().trim().replace(",",""));
            if (amount<=0) { alert("금액은 0보다 커야 합니다."); return; }
            String memo = tfMemo.getText().trim().isEmpty() ? "-" : tfMemo.getText().trim();
            String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            transactions.add(new Transaction(date,type,cat,amount,memo));
            saveCsv(); tfAmount.setText(""); tfMemo.setText(""); refresh();
        } catch (NumberFormatException ex) { alert("금액에 숫자만 입력해주세요."); }
    }

    // ── 선택 삭제 ────────────────────────────────────────────────
    void deleteSelected() {
        int[] rows = table.getSelectedRows();
        if (rows.length==0) { alert("삭제할 항목을 선택하세요."); return; }
        int c = JOptionPane.showConfirmDialog(this, rows.length+"건을 삭제할까요?","확인",JOptionPane.YES_NO_OPTION);
        if (c!=JOptionPane.YES_OPTION) return;
        int[] sorted = Arrays.stream(rows).boxed()
                .sorted(Comparator.reverseOrder()).mapToInt(i->i).toArray();
        for (int r:sorted) transactions.remove(r);
        saveCsv(); refresh();
    }

    // ── 화면 갱신 ────────────────────────────────────────────────
    void refresh() {
        tableModel.setRowCount(0);
        for (Transaction t:transactions) {
            tableModel.addRow(new Object[]{
                    t.date, t.type, t.category,
                    String.format("%,d",t.amount)+" 원", t.memo});
        }
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable tbl,Object v,boolean sel,boolean focus,int row,int col) {
                super.getTableCellRendererComponent(tbl,v,sel,focus,row,col);
                boolean isIncome = row<transactions.size() && transactions.get(row).type.equals("수입");
                setBackground(sel ? new Color(99,102,241,80) : (row%2==0 ? CARD : CARD2));
                setForeground(col==1 ? (isIncome?GREEN:RED) : TEXT);
                setBorder(BorderFactory.createEmptyBorder(2,10,2,10));
                setFont(new Font("SansSerif",Font.PLAIN,12));
                return this;
            }
        });

        int income  = transactions.stream().filter(t->t.type.equals("수입")).mapToInt(t->t.amount).sum();
        int expense = transactions.stream().filter(t->t.type.equals("지출")).mapToInt(t->t.amount).sum();
        int balance = income-expense;
        lblIncome .setText(String.format("%,d 원",income));
        lblExpense.setText(String.format("%,d 원",expense));
        lblBalance.setText(String.format("%,d 원",Math.abs(balance))+(balance<0?" ▾":""));
        lblBalance.setForeground(balance<0?RED:TEXT);

        if (chartRef!=null) chartRef.repaint();
    }

    // ── CSV ──────────────────────────────────────────────────────
    void saveCsv() {
        try (PrintWriter pw=new PrintWriter(new FileWriter(csvPath))) {
            pw.println("date,type,category,amount,memo");
            for (Transaction t:transactions) pw.println(t.toCsv());
        } catch (IOException e) { e.printStackTrace(); }
    }

    void loadCsv() {
        File f=new File(csvPath);
        if (!f.exists()) return;
        try (BufferedReader br=new BufferedReader(new FileReader(f))) {
            String line; br.readLine();
            while ((line=br.readLine())!=null) {
                Transaction t=Transaction.fromCsv(line);
                if (t!=null) transactions.add(t);
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    // ── 컴포넌트 팩토리 ──────────────────────────────────────────

    /** 둥근 모서리 패널 */
    static class RoundPanel extends JPanel {
        int radius; Color bg;
        RoundPanel(int r, Color bg) { this.radius=r; this.bg=bg; setOpaque(false); }
        protected void paintComponent(Graphics g) {
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg);
            g2.fillRoundRect(0,0,getWidth(),getHeight(),radius,radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** 모던 텍스트 필드 */
    JTextField modernField(String hint) {
        JTextField tf = new JTextField() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD2);
                g2.fillRoundRect(0,0,getWidth()-1,getHeight()-1,10,10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        tf.setOpaque(false);
        tf.setBackground(new Color(0,0,0,0));
        tf.setForeground(TEXT);
        tf.setCaretColor(ACCENT);
        tf.setFont(new Font("SansSerif",Font.PLAIN,13));
        tf.setBorder(BorderFactory.createEmptyBorder(6,10,6,10));
        tf.setPreferredSize(new Dimension(180,34));
        return tf;
    }

    /** 모던 콤보박스 — 선택 후 글씨 검정 */
    JComboBox<String> styledCombo(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0,0,getWidth()-1,getHeight()-1,10,10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        cb.setOpaque(false);
        cb.setBackground(Color.WHITE);
        cb.setForeground(Color.BLACK);   // ← 선택된 값 글씨 검정
        cb.setFont(new Font("SansSerif",Font.PLAIN,13));
        cb.setBorder(BorderFactory.createEmptyBorder(2,6,2,6));
        cb.setPreferredSize(new Dimension(180,34));

        // 드롭다운 목록 렌더러
        cb.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> list,Object value,int index,boolean sel,boolean focus) {
                super.getListCellRendererComponent(list,value,index,sel,focus);
                setBackground(sel ? ACCENT : Color.WHITE);
                setForeground(sel ? Color.WHITE : Color.BLACK);  // ← 목록 글씨 검정
                setBorder(BorderFactory.createEmptyBorder(6,12,6,12));
                setFont(new Font("SansSerif",Font.PLAIN,13));
                return this;
            }
        });

        // 화살표 버튼 스타일 제거
        for (Component comp : cb.getComponents()) {
            if (comp instanceof AbstractButton) {
                ((AbstractButton)comp).setContentAreaFilled(false);
                ((AbstractButton)comp).setBorderPainted(false);
            }
        }
        return cb;
    }

    /** 메인 액션 버튼 (둥근) */
    JButton roundButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed()?bg.darker():getModel().isRollover()?bg.brighter():bg);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),12,12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("SansSerif",Font.BOLD,13));
        btn.setForeground(fg);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(180,38));
        return btn;
    }

    /** 삭제용 고스트 버튼 */
    JButton ghostButton(String text) {
        JButton btn = new JButton(text) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover()?new Color(239,68,68,30):new Color(0,0,0,0));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),8,8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("SansSerif",Font.PLAIN,12));
        btn.setForeground(RED);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(52,28));
        return btn;
    }

    void alert(String msg) {
        JOptionPane.showMessageDialog(this,msg,"알림",JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        SwingUtilities.invokeLater(Middle::new);
    }
}