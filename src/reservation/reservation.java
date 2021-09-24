package reservation;

import java.util.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import java.sql.*;

public class reservation extends JFrame implements ActionListener {//
	static Scanner sc = new Scanner(System.in);

	Connection conn = null;
	PreparedStatement pstmt = null;

	Container contentPane;
	JPanel panel, panel2;
	JRadioButton[] rb = new JRadioButton[6];
	JComboBox<String> m, d, h, M;
	JTextField NAME;
	JButton r, c;
	ImageIcon blue = new ImageIcon("image/하늘색.png");
	ImageIcon SizeBlue = new ImageIcon(blue.getImage().getScaledInstance(150, 400, Image.SCALE_DEFAULT));
	ImageIcon red = new ImageIcon("image/빨간색.png");
	ImageIcon SizeRed = new ImageIcon(red.getImage().getScaledInstance(150, 400, Image.SCALE_DEFAULT));

	private String Name;
	private String Date;

	reservation() {
		try {
			Class.forName("org.gjt.mm.mysql.Driver").newInstance();
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/reservation?serverTimezone=Asia/Seoul&useSSL=false", "root", "mirim2"); // DB연결
			System.out.println("DB 연결 완료");
		} catch (SQLException ex) {
			System.out.println("SQLException:" + ex);
		} catch (Exception ex) {
			System.out.println("Exception:" + ex);
		}

		setTitle("자리예약 프로그램");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		panel = new JPanel();
		panel2 = new JPanel();
		panel2.setLayout(new GridLayout(2, 3, 5, 5));
		contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		m = new JComboBox<String>();
		for (int i = 0; i < 12; i++) {
			m.addItem(i + 1 + "월");
		}
		d = new JComboBox<String>();
		for (int i = 0; i < 31; i++) {
			d.addItem(i + 1 + "일");
		}
		h = new JComboBox<String>();
		for (int i = 0; i < 24; i++) {
			h.addItem(i + 1 + "시");
		}
		M = new JComboBox<String>();
		for (int i = 0; i < 59; i++) {
			M.addItem(i + 1 + "분");
		}

		NAME = new JTextField(10);

		r = new JButton("예약");
		r.addActionListener(this);
		c = new JButton("취소");
		c.addActionListener(this);

		ButtonGroup g = new ButtonGroup();
		try {
			String sql = "select * from reservation;";
			pstmt = conn.prepareStatement(sql);
			ResultSet srs = pstmt.executeQuery();
			int i = 0;
			while (srs.next()) {
				Name = srs.getString("name");
				String Num = srs.getString("num");
				Date = srs.getString("date");
				if (!(Name == null && Date == null)) {
					rb[i] = new JRadioButton("<html>" + (i + 1) + "<br>" + Name + "<br>" + Date + "</html>", SizeRed);
				} else {
					rb[i] = new JRadioButton((i + 1) + " : 예약가능", SizeBlue);
				}
				rb[i].setBorderPainted(true);
				g.add(rb[i]);
				i++;

			}
		} catch (SQLException ex) {
			System.out.println("SQLException:" + ex);
		} catch (Exception ex) {
			System.out.println("Exception:" + ex);
		}

		panel.add(NAME);
		panel.add(m);
		panel.add(d);
		panel.add(h);
		panel.add(M);
		panel.add(r);
		panel.add(c);

		for (int i = 0; i < 6; i++) {
			panel2.add(rb[i]);
		}
		revalidate();
		repaint();

		contentPane.add(panel, BorderLayout.NORTH);
		contentPane.add(panel2, BorderLayout.CENTER);

		setSize(1100, 750);
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton b = (JButton) e.getSource();
		String Mon, Date, Hour, Min, D;
		String N;
		if (b.getText().equals("예약")) {
			N = NAME.getText();
			Mon = m.getSelectedItem().toString();
			Date = d.getSelectedItem().toString();
			Hour = h.getSelectedItem().toString();
			Min = M.getSelectedItem().toString();
			D = "2019년" + Mon + " " + Date + "-" + Hour + Min;
			for (int i = 0; i < 6; i++) {
				if (rb[i].isSelected()) {
					reservation(N, D, Integer.toString(i + 1));
					rb[i] = new JRadioButton("<html>" + (i + 1) + "<br>" + N + "<br>" + D + "</html>", SizeRed);
					rb[i].setBorderPainted(true);
				}
			}
		} else if (b.getText().equals("취소")) {
			for (int i = 0; i < 6; i++) {
				if (rb[i].isSelected()) {
					System.out.println(i);
					cancellation(Integer.toString(i + 1));
					rb[i] = new JRadioButton((i + 1) + " : 예약가능", SizeBlue);
					rb[i].setBorderPainted(true);
				}
			}
		}

		panel2.revalidate();
		panel2.repaint();
		for (int i = 0; i < 6; i++) {
			panel2.removeAll();
		}
		for (int i = 0; i < 6; i++) {
			panel2.add(rb[i]);
		}
	}

	private void reservation(String name, String date, String n) {
		try {
			String sql = "update reservation set name = ?, date = ? where num = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, name);
			pstmt.setString(2, date);
			pstmt.setString(3, n);
			pstmt.executeUpdate(); // 실행시키는 거

			sql = "select * from reservation";
			pstmt = conn.prepareStatement(sql);
			ResultSet srs = pstmt.executeQuery();
			while (srs.next()) {
				System.out.print(srs.getString("name") + " ");
				System.out.print(srs.getString("num") + " ");
				System.out.print(srs.getString("date") + " ");
				System.out.println();
			}
		} catch (SQLException ex) {
			System.out.println("SQLException:" + ex);
		} catch (Exception ex) {
			System.out.println("Exception:" + ex);
		}
	}

	private void cancellation(String n) {
		try {
			String sql = "update reservation set name = null, date = null where num = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, n);

			pstmt.executeUpdate(); // 실행시키는 거

			sql = "select * from reservation";
			pstmt = conn.prepareStatement(sql);
			ResultSet srs = pstmt.executeQuery();
			while (srs.next()) {
				System.out.print(srs.getString("name") + " ");
				System.out.print(srs.getString("num") + " ");
				System.out.print(srs.getString("date") + " ");
				System.out.println();
			}
		} catch (SQLException ex) {
			System.out.println("SQLException:" + ex);
		} catch (Exception ex) {
			System.out.println("Exception:" + ex);
		}

	}

	public static void main(String[] args) {
		reservation r = new reservation();
	}

}