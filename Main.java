import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;

import engine.AnPolyApprox;

@SuppressWarnings({ "unused", "serial" })
public class Main extends JFrame {

	private static final long serialVersionUID = 1L;

	List<Point2D> points = new ArrayList<>(); // points to draw
	List<Double> values = new ArrayList<>(); // values for all points
	List<Double> diffs = new ArrayList<>(); // differentiated values
	List<Integer> crit = new ArrayList<>(); // critical points
	List<String> frac = new ArrayList<>(); // mole fractions

	static double concA;
	static double concB;
	static double vA;
	static double a;
	static double aa;
	static double aaa;
	static double b;
	double v;

	final double w = 1e-14;

	boolean running = false;
	boolean isAMode = true;
	int mode = 1;

	int pointx = -1;

	final String[][] s = new String[][] { { "Molarity of acid", "Volume of acid(mL)", "Ka of acid" },
			{ "Molarity of base", "Volume of base(mL)", "Kb of base" } };

	Image i, i2, i3;

	JTextField acid, base, macid, mbase, vacid;
	JButton but;
	JLabel a1, a2, a3, b1, b3, status, status2, status3;
	ButtonGroup bg, bg2;
	JRadioButton aMode, bMode, mode1, mode2, mode3;
	JMenuBar mb;
	JMenuItem png, txt;
	final JFileChooser fc = new JFileChooser();

	Graph p;
	Converter c;
	Help h;

	Main m = this;

	Task t;
	Task2 t2;

	TxtFilter tf = new TxtFilter();
	PngFilter pf = new PngFilter();

	public Main() {
		super("Graph! (pre-12)");

		t = new Task();
		t2 = new Task2();

		try {
			i = ImageIO.read(getClass().getResource("graph.png"));
			i2 = ImageIO.read(getClass().getResource("graph2.png"));
			i3 = ImageIO.read(getClass().getResource("graph3.png"));
		} catch (IOException e) {
			System.exit(0);
		}

		getContentPane().setLayout(new BorderLayout());
		setResizable(false);
		GridBagConstraints g = new GridBagConstraints();

		p = new Graph();
		getContentPane().add(p, BorderLayout.CENTER);

		mb = new JMenuBar();

		JMenu file = new JMenu("Export");
		file.setMnemonic(KeyEvent.VK_E);

		txt = new JMenuItem("Txt of data...");
		txt.setMnemonic(KeyEvent.VK_T);
		txt.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, KeyEvent.ALT_DOWN_MASK));

		png = new JMenuItem("Current Window to png...");
		png.setMnemonic(KeyEvent.VK_P);
		png.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.ALT_DOWN_MASK));

		file.add(txt);
		file.add(png);
		mb.add(file);

		JMenu help = new JMenu("Help");
		help.setMnemonic(KeyEvent.VK_H);

		JMenuItem how = new JMenuItem("Help...");
		how.setMnemonic(KeyEvent.VK_H);
		how.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, KeyEvent.ALT_DOWN_MASK));

		help.add(how);
		mb.add(help);

		setJMenuBar(mb);

		JPanel pane = new JPanel();
		getContentPane().add(pane, BorderLayout.SOUTH);
		pane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 2), "Settings"));
		pane.setOpaque(true);
		pane.setBackground(new Color(191, 191, 255));
		pane.setLayout(new GridLayout(1, 2, 0, 0));

		JPanel pane2 = new JPanel();
		pane.add(pane2);
		pane2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK),
				"Method / N-protic Option"));
		pane2.setLayout(new GridBagLayout());
		pane2.setOpaque(false);

		bg = new ButtonGroup();
		bg2 = new ButtonGroup();

		g.gridx = 0;
		g.gridy = 1;
		g.ipadx = 20;
		g.gridwidth = 2;
		aMode = new JRadioButton("Titrate acid with base");
		aMode.setSelected(true);
		aMode.setOpaque(false);
		bg.add(aMode);
		pane2.add(aMode, g);

		g.gridx = 2;
		g.ipadx = 0;
		bMode = new JRadioButton("Titrate base with acid");
		bMode.setOpaque(false);
		bg.add(bMode);
		pane2.add(bMode, g);

		g.gridx = 0;
		g.gridy = 2;
		g.ipadx = 20;
		g.gridwidth = 1;
		mode1 = new JRadioButton("Titrate mono-...");
		mode1.setSelected(true);
		mode1.setOpaque(false);
		bg2.add(mode1);
		pane2.add(mode1, g);

		g.gridx = 1;
		g.ipadx = 0;
		g.gridwidth = 2;
		mode2 = new JRadioButton("Titrate di-...");
		mode2.setOpaque(false);
		bg2.add(mode2);
		pane2.add(mode2, g);

		g.gridx = 3;
		g.gridwidth = 1;
		mode3 = new JRadioButton("Titrate tri-...");
		mode3.setOpaque(false);
		bg2.add(mode3);
		pane2.add(mode3, g);

		JPanel panel = new JPanel();
		pane.add(panel);
		panel.setOpaque(false);
		panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK),
				"Molarity / Volume / K Option"));
		panel.setLayout(new GridBagLayout());

		g.gridx = 0;
		g.gridy = 0;
		g.ipadx = 20;
		g.gridwidth = 1;
		panel.add((a1 = new JLabel(s[0][0])), g);
		a1.setOpaque(false);

		g.gridy = 1;
		panel.add((a2 = new JLabel(s[0][1])), g);
		a2.setOpaque(false);

		g.gridy = 2;
		panel.add((a3 = new JLabel("<html><u>" + s[0][2] + "</u> <font size=2>?</font></html>")), g);
		a3.setToolTipText(
				"Separate K" + (isAMode ? "a" : "b") + "1, K" + (isAMode ? "a" : "b") + "2, ... by ;(semicolon).");
		a3.setOpaque(false);

		g.gridx = 2;
		g.gridy = 0;
		panel.add((b1 = new JLabel(s[1][0])), g);
		b1.setOpaque(false);

		g.gridy = 2;
		panel.add((b3 = new JLabel(s[1][2])), g);
		b3.setOpaque(false);

		g.gridx = 1;
		g.gridy = 0;
		macid = new JTextField();
		macid.setPreferredSize(new Dimension(80, 25));
		panel.add(macid, g);

		g.gridy = 1;
		vacid = new JTextField();
		vacid.setPreferredSize(new Dimension(80, 25));
		panel.add(vacid, g);

		g.gridy = 2;
		acid = new JTextField();
		acid.setPreferredSize(new Dimension(80, 25));
		panel.add(acid, g);

		g.gridx = 3;
		g.gridy = 0;
		g.ipadx = 0;
		mbase = new JTextField();
		mbase.setPreferredSize(new Dimension(100, 25));
		panel.add(mbase, g);

		g.gridy = 2;
		base = new JTextField();
		base.setPreferredSize(new Dimension(100, 25));
		panel.add(base, g);

		g.gridy = 3;
		g.gridx = 0;
		g.gridwidth = 4;
		g.fill = GridBagConstraints.HORIZONTAL;
		but = new JButton("Start!");
		panel.add(but, g);

		JPanel pane3 = new JPanel();
		pane3.setLayout(new FlowLayout());
		pane3.setPreferredSize(new Dimension(200, 640));
		pane3.setOpaque(true);
		pane3.setBackground(new Color(255, 191, 191));
		pane3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 2), "Status"));
		getContentPane().add(pane3, BorderLayout.EAST);

		status = new JLabel("<html>&nbsp;&nbsp;V = <br>&nbsp;&nbsp;pH = <br>&nbsp;&nbsp;dy/dx = <br><br>&nbsp;&nbsp;x("
				+ (isAMode ? "HA" : "BOH") + ") = <br>&nbsp;&nbsp;x(" + (isAMode ? "A-" : "B+") + ") = </html>");
		status.setPreferredSize(new Dimension(200, 210));
		status.setHorizontalAlignment(SwingConstants.LEFT);
		status.setVerticalAlignment(SwingConstants.CENTER);
		status.setVerticalTextPosition(SwingConstants.CENTER);
		status.setOpaque(false);
		status.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Moving Point"));
		pane3.add(status);

		status3 = new JLabel("<html>&nbsp;&nbsp;V = <br>&nbsp;&nbsp;pH = <br>&nbsp;&nbsp;dy/dx = <br><br>&nbsp;&nbsp;x("
				+ (isAMode ? "HA" : "BOH") + ") = <br>&nbsp;&nbsp;x(" + (isAMode ? "A-" : "B+") + ") = </html>");
		status3.setPreferredSize(new Dimension(200, 210));
		status3.setHorizontalAlignment(SwingConstants.LEFT);
		status3.setVerticalAlignment(SwingConstants.CENTER);
		status3.setVerticalTextPosition(SwingConstants.CENTER);
		status3.setOpaque(false);
		status3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Fixed Point"));
		pane3.add(status3);

		status2 = new JLabel();
		status2.setPreferredSize(new Dimension(200, 210));
		status2.setHorizontalAlignment(SwingConstants.LEFT);
		status2.setVerticalAlignment(SwingConstants.CENTER);
		status2.setVerticalTextPosition(SwingConstants.CENTER);
		status2.setOpaque(false);
		status2.setBorder(
				BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Equivalence Point"));
		pane3.add(status2);

		txt.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				try {
					Double d = values.get(1000);
					d.doubleValue();
				} catch (IndexOutOfBoundsException e) {
					JOptionPane.showMessageDialog(m, "There is no data to export!", "export error",
							JOptionPane.WARNING_MESSAGE);
					return;
				}

				fc.setFileFilter(tf);
				int result = fc.showSaveDialog(m);
				if (result == JFileChooser.APPROVE_OPTION) {
					File f = fc.getSelectedFile();
					if (getExtension(f).equalsIgnoreCase("txt")) {
						String s = f.toString();
						f = new File(s.substring(0, s.length() - 3) + "txt");
					} else {
						f = new File(f.toString() + ".txt");
					}

					saveTxt(f);
				}
			}

		});

		png.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				BufferedImage tmp = new BufferedImage(m.getWidth(), m.getHeight(), BufferedImage.TYPE_INT_RGB);
				m.paint(tmp.getGraphics());
				fc.setFileFilter(pf);
				int result = fc.showSaveDialog(m);
				if (result == JFileChooser.APPROVE_OPTION) {
					File f = fc.getSelectedFile();
					if (getExtension(f).equalsIgnoreCase("png")) {
						String s = f.toString();
						f = new File(s.substring(0, s.length() - 3) + "png");
					} else {
						f = new File(f.toString() + ".png");
					}
					try {
						ImageIO.write(tmp, "png", f);
					} catch (IOException ex) {
					}
				}
			}

		});

		how.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				h.init();
				h.setVisible(true);
			}

		});

		aMode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!isAMode) {
					init();
				}
				isAMode = true;
				mUpdate();
			}
		});

		bMode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (isAMode) {
					init();
				}
				isAMode = false;
				mUpdate();
			}
		});

		mode1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (mode != 1) {
					init();
				}
				mode = 1;
				nUpdate();
			}
		});

		mode2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (mode != 2) {
					init();
				}
				mode = 2;
				nUpdate();
			}
		});

		mode3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (mode != 3) {
					init();
				}
				mode = 3;
				nUpdate();
			}
		});

		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!running) {
					try {
						concA = Double.parseDouble(macid.getText());
						concB = Double.parseDouble(mbase.getText());
						vA = Double.parseDouble(vacid.getText());
						switch (mode) {
						case 1:
							a = Double.parseDouble(acid.getText());
							break;
						case 2:
							StringTokenizer st = new StringTokenizer(acid.getText(), "; ");
							a = Double.parseDouble(st.nextToken());
							aa = Double.parseDouble(st.nextToken());
							break;
						case 3:
							StringTokenizer st2 = new StringTokenizer(acid.getText(), "; ");
							a = Double.parseDouble(st2.nextToken());
							aa = Double.parseDouble(st2.nextToken());
							aaa = Double.parseDouble(st2.nextToken());
						}
						b = Double.parseDouble(base.getText());
						if (mode <= 2) {
							v = vA * concA / concB * mode;
						} else {
							v = vA * concA * 2 / concB;
						}
						if (a < 0 || b < 0) {
							JOptionPane.showMessageDialog(m, "Only positive number is allowed", "input error",
									JOptionPane.WARNING_MESSAGE);
							return;
						}
					} catch (NumberFormatException ex) {
						JOptionPane.showMessageDialog(m, "Input numbers in textfield", "input error",
								JOptionPane.WARNING_MESSAGE);
						return;
					} catch (ArithmeticException ex) {
						JOptionPane.showMessageDialog(m, "Zero is not allowed", "input error",
								JOptionPane.WARNING_MESSAGE);
						return;
					}
					start();
					running = true;
					t.start();
					t2.start();
				} else {
					stop();
					running = false;
				}
			}
		});

		getRootPane().getInputMap(JRootPane.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0),
				"move point left");
		getRootPane().getInputMap(JRootPane.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0),
				"move point right");
		getRootPane().getActionMap().put("move point left", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (pointx > 49) {
					pointx--;
				}
				p.repaint();
				pUpdate();
			}

		});
		getRootPane().getActionMap().put("move point right", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (pointx < 1049) {
					pointx++;
				}
				p.repaint();
				pUpdate();
			}

		});

		c = new Converter();
		h = new Help();

		setSize(1305, 873);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		p.repaint();
		/*
		 * for (int i = -9; i < 11; i++) { if (i == 0) { macro(new String("1"));
		 * } else { macro(new String("1e" + i)); } }
		 */
	}

	class KeyKey implements KeyListener {

		@Override
		public void keyPressed(KeyEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void keyReleased(KeyEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void keyTyped(KeyEvent e) {
			// TODO Auto-generated method stub
			if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				pointx--;
			} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
				pointx++;
			}
			p.repaint();
		}

	}

	class Graph extends JPanel {
		/**
		 *
		 */
		private static final long serialVersionUID = 3L;
		Point p = new Point(-30, -30);
		Point dp = new Point(-30, -30);

		Image buf = i;

		Graph() {
			setLayout(new FlowLayout());
			setSize(1001, 561);
			setOpaque(true);
			setVisible(true);

			this.addMouseListener(new MouseListener() {

				@Override
				public void mouseClicked(MouseEvent e) {
					// TODO Auto-generated method stub
					if (e.getButton() == MouseEvent.BUTTON1) {
						int x = e.getX();
						int y = e.getY();
						if (x >= 49 && x < 1050 && y >= 53 && y <= 613) {
							pointx = x;
							pUpdate();
						} else {
							pointx = -1;
							pUpdate();
						}
						((Graph) e.getSource()).requestFocus();
					}
				}

				@Override
				public void mouseEntered(MouseEvent e) {
					// TODO Auto-generated method stub

				}

				@Override
				public void mouseExited(MouseEvent e) {
					// TODO Auto-generated method stub

				}

				@Override
				public void mousePressed(MouseEvent e) {
					// TODO Auto-generated method stub

				}

				@Override
				public void mouseReleased(MouseEvent e) {
					// TODO Auto-generated method stub

				}

			});

			this.addMouseMotionListener(new MouseMotionListener() {

				@Override
				public void mouseDragged(MouseEvent e) {
					// TODO Auto-generated method stub

				}

				@Override
				public void mouseMoved(MouseEvent e) {
					// TODO Auto-generated method stub
					int x = e.getX();
					int y = e.getY();
					if (x >= 49 && x < 1050 && y >= 53 && y <= 613) {
						p = e.getPoint();
					} else {
						p = new Point(-30, -30);
					}
					if (!running) {
						repaint();
						m.repaint();
					}
				}

			});
		}

		public void update() {
			switch (mode) {
			case 1:
				buf = i;
				break;
			case 2:
				buf = i2;
				break;
			case 3:
				buf = i3;
			}
			repaint();
		}

		public void paint(Graphics g2) {
			Graphics2D g = (Graphics2D) g2;
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			// g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
			// RenderingHints.VALUE_STROKE_PURE);
			g.drawImage(buf, 0, 0, null);

			if (pointx != -1) {
				g.setColor(Color.GREEN);
				try {
					Point2D p = points.get(pointx - 49);
					g.draw(new Line2D.Double(p.getX() + 49, 53, p.getX() + 49, 613));
					g.draw(new Line2D.Double(49, p.getY() + 53, 1049, p.getY() + 53));

					g.setColor(new Color(191, 64, 0));
					g.draw(new Ellipse2D.Double(p.getX() + 45, p.getY() + 49, 8, 8));
				} catch (Exception e) {
				}
			}

			g.setColor(Color.CYAN);
			g.drawLine(p.x, 53, p.x, 613);
			g.drawLine(49, p.y, 1049, p.y);
			try {
				if (points.get(points.size() - 1).getX() + 49 >= p.x && p.x >= 0) {
					Point2D tmp = points.get(p.x - 49);

					g.setColor(Color.BLACK);
					g.draw(new Ellipse2D.Double(tmp.getX() + 45, tmp.getY() + 49, 8, 8));
					g.drawString("V " + roundDouble(v * (p.x - 49) / 500, 2) + "mL", (int) tmp.getX() + 55,
							(int) tmp.getY() + (isAMode ? 68 : 24));
					g.drawString("pH " + roundDouble(values.get(p.x - 49), 2), (int) tmp.getX() + 55,
							(int) tmp.getY() + (isAMode ? 80 : 36));
					status.setText(
							"<html>&nbsp;&nbsp;V = " + roundDouble(v * (p.x - 49) / 500, 2) + " mL<br>&nbsp;&nbsp;pH = "
									+ roundDouble(values.get(p.x - 49), 2) + "<br>&nbsp;&nbsp;dy/dx = Not Defined<br>");
					switch (mode) {
					case 1:
						status.setText(status.getText() + "<br>&nbsp;&nbsp;x(" + (isAMode ? "HA" : "BOH")
								+ ") = <br>&nbsp;&nbsp;x(" + (isAMode ? "A-" : "B+") + ") = ");
						break;
					case 2:
						status.setText(status.getText() + "<br>&nbsp;&nbsp;x(" + (isAMode ? "H2A" : "B(OH)2")
								+ ") = <br>&nbsp;&nbsp;x(" + (isAMode ? "HA-" : "B(OH)-") + ") = <br>&nbsp;&nbsp;x("
								+ (isAMode ? "A2-" : "B2+") + ") = ");
						break;
					case 3:
						status.setText(status.getText() + "<br>&nbsp;&nbsp;x(" + (isAMode ? "H3A" : "B(OH)3")
								+ ") = <br>&nbsp;&nbsp;x(" + (isAMode ? "H2A-" : "B(OH)2 +") + ") = <br>&nbsp;&nbsp;x("
								+ (isAMode ? "HA2-" : "B(OH) 2+") + ") = <br>&nbsp;&nbsp;x(" + (isAMode ? "A3-" : "B3+")
								+ ") = ");
						break;
					default:
					}
					status.setText(status.getText() + "</html>");
					if (!(p.x - 49 == 0 || p.x - 49 == 1000)) {
						g.drawString("dy/dx " + roundDouble(-diffs.get(p.x - 49), 2) + "/mL", (int) tmp.getX() + 55,
								(int) tmp.getY() + (isAMode ? 92 : 48));
						status.setText("<html>&nbsp;&nbsp;V = " + roundDouble(v * (p.x - 49) / 500, 2)
								+ " mL<br>&nbsp;&nbsp;pH = " + roundDouble(values.get(p.x - 49), 2)
								+ "<br>&nbsp;&nbsp;dy/dx = " + roundDouble(-diffs.get(p.x - 49), 2) + " / mL<br>");
					}
				}
			} catch (Exception e) {
			}
			if (!running) {
				try {
					StringTokenizer st = new StringTokenizer(frac.get(p.x - 49));
					switch (mode) {
					case 1:
						status.setText(status.getText() + "<br>&nbsp;&nbsp;x(" + (isAMode ? "HA" : "BOH") + ") = "
								+ roundDouble(new Double(st.nextToken()), 4) + "<br>&nbsp;&nbsp;x("
								+ (isAMode ? "A-" : "B+") + ") = " + roundDouble(new Double(st.nextToken()), 4));
						break;
					case 2:
						status.setText(status.getText() + "<br>&nbsp;&nbsp;x(" + (isAMode ? "H2A" : "B(OH)2") + ") = "
								+ roundDouble(new Double(st.nextToken()), 4) + "<br>&nbsp;&nbsp;x("
								+ (isAMode ? "HA-" : "B(OH)+") + ") = " + roundDouble(new Double(st.nextToken()), 4)
								+ "<br>&nbsp;&nbsp;x(" + (isAMode ? "A2-" : "B2+") + ") = "
								+ roundDouble(new Double(st.nextToken()), 4));
						break;
					case 3:
						status.setText(status.getText() + "<br>&nbsp;&nbsp;x(" + (isAMode ? "H3A" : "B(OH)3") + ") = "
								+ roundDouble(new Double(st.nextToken()), 4) + "<br>&nbsp;&nbsp;x("
								+ (isAMode ? "H2A-" : "B(OH)2 +") + ") = " + roundDouble(new Double(st.nextToken()), 4)
								+ "<br>&nbsp;&nbsp;x(" + (isAMode ? "HA2-" : "B(OH) 2+") + ") = "
								+ roundDouble(new Double(st.nextToken()), 4) + "<br>&nbsp;&nbsp;x("
								+ (isAMode ? "A3-" : "B3+") + ") = " + roundDouble(new Double(st.nextToken()), 4));
						break;
					}
				} catch (IndexOutOfBoundsException e) {
				}
			}
			status.setText(status.getText() + "</html>");

			Iterator<Point2D> i = points.iterator();
			Point2D tmp = new Point(0, 0);
			Point2D tmp2;
			try {
				tmp2 = i.next();
			} catch (Exception e) {
				return;
			}
			g.setColor(Color.RED);
			while (i.hasNext()) {
				try {
					tmp = tmp2;
					tmp2 = i.next();
					g.draw(new Line2D.Double(tmp.getX() + 49, tmp.getY() + 53, tmp2.getX() + 49, tmp2.getY() + 53));
				} catch (Exception e) {
					return;
				}
			}

			g.setColor(Color.BLUE);
			try {
				for (int a : crit) {
					Point2D pp = points.get(a);
					g.draw(new Ellipse2D.Double(a + 45, pp.getY() + 49, 8, 8));
					g.drawString("V " + roundDouble(pp.getX() * v / 500.0, 2) + "mL",
							pp.getX() - 45 >= 0 ? (int) pp.getX() - 45 : 0, (int) pp.getY() + (isAMode ? 24 : 68));
					g.drawString("pH " + roundDouble(values.get((int) pp.getX()), 2),
							pp.getX() - 45 >= 0 ? (int) pp.getX() - 45 : 0, (int) pp.getY() + (isAMode ? 36 : 80));
					g.drawString("dy/dx " + roundDouble(-diffs.get((int) pp.getX()), 2) + "/mL",
							pp.getX() - 45 >= 0 ? (int) pp.getX() - 45 : 0, (int) pp.getY() + (isAMode ? 48 : 92));
				}
			} catch (Exception e) {
			}

			try {
				Point2D tar = points.get(p.x - 49);
				double dx = diffs.get(p.x - 49) * 3 * v / 35;

				int x = (int) (50 / Math.sqrt(1 + dx * dx));
				int y = (int) (50 * dx / Math.sqrt(1 + dx * dx));

				if (!(p.x - 49 == 0 || p.x - 49 == 1000)) {
					g.setColor(new Color(0, 128, 0));
					g.draw(new Line2D.Double(tar.getX() - x + 49, tar.getY() - y + 53, tar.getX() + x + 49,
							tar.getY() + y + 53));
				}
			} catch (Exception e) {
			}
		}
	}

	class Converter extends JFrame {
		private static final long serialVersionUID = 2L;

		JTextField kf, pkf;
		boolean down = true;

		public Converter() {
			setLayout(new FlowLayout());

			JPanel p = new JPanel();
			add(p);
			p.setPreferredSize(new Dimension(200, 50));
			p.setLayout(new GridLayout(2, 2, 0, 0));

			JLabel k = new JLabel("K");
			p.add(k);

			kf = new JTextField();
			p.add(kf);

			JLabel pk = new JLabel("pK");
			p.add(pk);

			pkf = new JTextField();
			pkf.setEditable(false);
			p.add(pkf);

			JLabel l = new JLabel(
					"<html>You can convert K to pK, and vice versa.<br>Just put value in white textfield.<br>Click gray textfield to make it white.</html>");
			l.setVerticalTextPosition(SwingConstants.CENTER);
			l.setVerticalAlignment(SwingConstants.CENTER);
			add(l);

			kf.addMouseListener(new ClickEvent());
			kf.getDocument().putProperty("owner", kf);
			kf.getDocument().addDocumentListener(new ChangeEvent());

			pkf.addMouseListener(new ClickEvent());
			pkf.getDocument().putProperty("owner", pkf);
			pkf.getDocument().addDocumentListener(new ChangeEvent());

			setVisible(true);
			setSize(300, 200);
			setLocation(1318, 0);
			setTitle("K↔pK");
		}

		class ClickEvent implements MouseListener {

			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				JTextField f = (JTextField) e.getSource();
				if (f.equals(kf)) {
					kf.setEditable(true);
					pkf.setEditable(false);
				} else if (f.equals(pkf)) {
					kf.setEditable(false);
					pkf.setEditable(true);
				}
			}

			public void mouseEntered(MouseEvent e) {
			}

			public void mouseExited(MouseEvent e) {
			}

			public void mousePressed(MouseEvent e) {
			}

			public void mouseReleased(MouseEvent e) {
			}

		}

		class ChangeEvent implements DocumentListener {

			public void changedUpdate(DocumentEvent arg0) {
				run((JTextField) arg0.getDocument().getProperty("owner"));
			}

			public void insertUpdate(DocumentEvent arg0) {
				run((JTextField) arg0.getDocument().getProperty("owner"));
			}

			public void removeUpdate(DocumentEvent arg0) {
				run((JTextField) arg0.getDocument().getProperty("owner"));
			}

			public void run(JTextField f) {
				if (f.equals(kf) && kf.isEditable()) {
					if (kf.getText().length() == 0) {
						pkf.setText("");
					}
					try {
						double d = Double.parseDouble(kf.getText());
						pkf.setText(roundDouble(-Math.log10(d), 2));
					} catch (NumberFormatException e) {
					}
				} else if (f.equals(pkf) && pkf.isEditable()) {
					if (pkf.getText().length() == 0) {
						kf.setText("");
					}
					try {
						double d = Double.parseDouble(pkf.getText());
						String s = String.format("%.2E", Math.pow(10, -d));
						StringTokenizer st = new StringTokenizer(s, "+");
						String result = "";
						while (st.hasMoreTokens()) {
							result += st.nextToken();
						}
						kf.setText(result);
					} catch (NumberFormatException e) {
					}
				}
			}

		}
	}

	class Help extends JFrame {
		final String[] contents = {
				"<html>1. This program shows titraton curve(even when it can't be<br>titrated).</html>",
				"2. First, select options in lower left side.", "3. Next, write values of acid and base to titrate.",
				"4. Press 'Start!' button to begin drawing curve.",
				"5. You can see values of points if you move mouse on the graph.",
				"6. You can see values of a point if you click on the graph.",
				"<html>7. You can use arrows on keyboard to move fixed point(which<br>was created by clicking).</html>",
				"8. You can export data by picture(.png) and text(.txt)",
				"<html>9. There are hot keys for functions such as exporting and<br>showing this window.</html>" };
		int page = 0;

		JLabel l;

		public Help() {
			super("Help");
			setVisible(false);
			setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			setSize(400, 200);

			setLayout(new BorderLayout());

			l = new JLabel(contents[0]);
			l.setSize(400, 50);
			l.setHorizontalAlignment(SwingConstants.CENTER);
			l.setHorizontalTextPosition(SwingConstants.CENTER);
			l.setAutoscrolls(true);
			add(l, BorderLayout.NORTH);

			JPanel p = new JPanel();
			p.setLayout(new GridLayout(1, 2, 0, 0));

			JButton left = new JButton("<-");
			p.add(left);

			JButton right = new JButton("->");
			p.add(right);

			add(p, BorderLayout.SOUTH);

			left.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					if (page != 0) {
						l.setText(contents[--page]);
					}
				}

			});

			right.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					if (page != 8) {
						l.setText(contents[++page]);
					}
				}

			});

			addWindowListener(new WindowListener() {

				@Override
				public void windowActivated(WindowEvent arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void windowClosed(WindowEvent arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void windowClosing(WindowEvent arg0) {
					// TODO Auto-generated method stub
					h.setVisible(false);
				}

				@Override
				public void windowDeactivated(WindowEvent arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void windowDeiconified(WindowEvent arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void windowIconified(WindowEvent arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void windowOpened(WindowEvent arg0) {
					// TODO Auto-generated method stub

				}

			});
		}

		public void init() {
			page = 0;
			l.setText(contents[0]);
		}
	}

	class Task extends Thread {
		public void run() {
			init();
			for (int i = 0; i < 1001; i++) {
				double j = v * i / 500;
				System.out.println(roundDouble(j, 2) + " mL started.");
				double ph = 0;
				switch (mode) {
				case 1:
					ph = getRoot(a, b, j);
					System.out.println("\tfound root...");
					break;
				case 2:
					ph = getRoot(a, aa, b, j);
					break;
				case 3:
					ph = getRoot(a, aa, aaa, b, j);
				}
				double pH = Math.log10(ph);
				System.out.println("\tfound pH...");
				points.add(new Point2D.Double(i, (isAMode ? 14 + pH : -pH) * 40));
				values.add(isAMode ? -pH : 14 + pH);
				frac.add(getFrac(ph));
				if (i == 0) {
					System.out.println("0.0 mL done. pH=" + (isAMode ? -pH : 14 + pH));
				} else {
					System.out.println(roundDouble(j, 2) + " mL done. pH=" + (isAMode ? -pH : pH + 14));
				}
				if (!running) {
					return;
				}
			}
			for (int i = 0; i < 1001; i++) {
				double dx = 0;
				switch (mode) {
				case 1:
					diffs.add((dx = -getDiff(a, b, v * i / 500, values.get(i))));
					break;
				case 2:
					diffs.add((dx = -getDiff(a, aa, b, v * i / 500, values.get(i))));
					break;
				case 3:
					diffs.add((dx = -getDiff(a, aa, aaa, b, v * i / 500, values.get(i))));
				}
				System.out.println(roundDouble(v * i / 500, 2) + " mL done. dy/dx=" + -dx);
				if (!running) {
					return;
				}
			}
			for (int i = 0; i < 1001; i++) {
				try {
					double tar = diffs.get(i);
					double pre = diffs.get(i - 1);
					double nex = diffs.get(i + 1);
					if ((tar > pre && tar > nex) || (tar < pre && tar < nex)) {
						crit.add(i);
					}
				} catch (Exception e) {
				}
				if (!running) {
					return;
				}
			}
			m.stop();
			/*
			 * // for macro saveMacro(); // for macro end
			 */
		}
	}

	class Task2 extends Thread {
		public void run() {
			while (running) {
				p.repaint();
				repaint();
			}
		}
	}

	class TxtFilter extends FileFilter {

		@Override
		public boolean accept(File f) {
			if (f.isDirectory()) {
				return true;
			}

			String extension = getExtension(f);
			if (extension.equals("txt")) {
				return true;
			}

			return false;
		}

		@Override
		public String getDescription() {
			return "txt files";
		}

	}

	class PngFilter extends FileFilter {
		@Override
		public boolean accept(File f) {
			if (f.isDirectory()) {
				return true;
			}

			String extension = getExtension(f);
			if (extension.equals("png")) {
				return true;
			}

			return false;
		}

		@Override
		public String getDescription() {
			return "png files";
		}
	}

	public static void main(String[] args) {
		Main m = new Main();
	}

	public void add(double x, double y) {
		points.add(new Point2D.Double(x, y));
	}

	public void start() {
		enableJ(macid, false);
		enableJ(mbase, false);
		enableJ(vacid, false);
		enableJ(acid, false);
		enableJ(base, false);
		aMode.setEnabled(false);
		bMode.setEnabled(false);
		mode1.setEnabled(false);
		mode2.setEnabled(false);
		mode3.setEnabled(false);
		but.setText("Stop!");
		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		for (int i = 0; i < mode; i++) {
			sb.append("&nbsp;&nbsp;<font size=4>V</font><font size=2>" + i + "</font> = "
					+ roundDouble(v * (mode <= 2 ? 1 : 1.5) * (i + 1) / mode, 2) + " mL");
			if (i == mode - 1) {
				break;
			}
			sb.append("<br>");
		}
		sb.append("</html>");
		status2.setText(sb.toString());
		pointx = -1;
	}

	public void stop() {
		enableJ(macid, true);
		enableJ(mbase, true);
		enableJ(vacid, true);
		enableJ(acid, true);
		enableJ(base, true);
		aMode.setEnabled(true);
		bMode.setEnabled(true);
		mode1.setEnabled(true);
		mode2.setEnabled(true);
		mode3.setEnabled(true);
		but.setText("Start!");
		t = new Task();
		t2 = new Task2();
		running = false;
		pUpdate();
	}

	public void enableJ(JTextField f, boolean b) {
		f.setEnabled(b);
		f.setEditable(b);
	}

	public void init() {
		points = new ArrayList<>();
		values = new ArrayList<>();
		diffs = new ArrayList<>();
		crit = new ArrayList<>();
		frac = new ArrayList<>();
		repaint();
		m.repaint();
	}

	public void update() {
		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		for (int i = 0; i < mode; i++) {
			sb.append("&nbsp;&nbsp;<font size=4>V</font><font size=2>" + i + "</font> = "
					+ roundDouble(v * (mode <= 2 ? 1 : 1.5) * (i + 1) / mode, 2) + " mL");
			if (i == mode - 1) {
				break;
			}
			sb.append("<br>");
		}
		sb.append("</html>");
		status2.setText(sb.toString());
	}

	public void mUpdate() {
		a1.setText(s[0 + (isAMode ? 0 : 1)][0]);
		a2.setText(s[0 + (isAMode ? 0 : 1)][1]);
		a3.setText("<html><u>" + s[0 + (isAMode ? 0 : 1)][2] + "</u> <font size=2>?</font></html>");
		a3.setToolTipText(
				"Separate K" + (isAMode ? "a" : "b") + "1, K" + (isAMode ? "a" : "b") + "2, ... by ;(semicolon).");
		b1.setText(s[0 + (isAMode ? 1 : 0)][0]);
		b3.setText(s[0 + (isAMode ? 1 : 0)][2]);
	}

	public void nUpdate() {
		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		for (int i = 0; i < mode; i++) {
			sb.append("&nbsp;&nbsp;<font size=4>V</font><font size=2>" + i + "</font> =  mL");
			if (i == mode - 1) {
				break;
			}
			sb.append("<br>");
		}
		sb.append("</html>");
		status2.setText(sb.toString());
		p.update();
	}

	public void pUpdate() {
		try {
			status3.setText("<html>&nbsp;&nbsp;V = " + roundDouble(v * (pointx - 49) / 500, 2)
					+ " mL<br>&nbsp;&nbsp;pH = " + roundDouble(values.get(pointx - 49), 2) + "<br>&nbsp;&nbsp;dy/dx = "
					+ roundDouble(-diffs.get(pointx - 49), 2) + " / mL<br>");
			StringTokenizer st = new StringTokenizer(frac.get(pointx - 49));
			System.out.println(frac.get(pointx - 49));// FIXME
			switch (mode) {
			case 1:
				status3.setText(status3.getText() + "<br>&nbsp;&nbsp;x(" + (isAMode ? "HA" : "BOH") + ") = "
						+ roundDouble(Double.parseDouble(st.nextToken()), 4) + "<br>&nbsp;&nbsp;x("
						+ (isAMode ? "A-" : "B+") + ") = " + roundDouble(Double.parseDouble(st.nextToken()), 4));
				break;
			case 2:
				status3.setText(status3.getText() + "<br>&nbsp;&nbsp;x(" + (isAMode ? "H2A" : "B(OH)2") + ") = "
						+ roundDouble(Double.parseDouble(st.nextToken()), 4) + "<br>&nbsp;&nbsp;x("
						+ (isAMode ? "HA-" : "B(OH)+") + ") = " + roundDouble(Double.parseDouble(st.nextToken()), 4)
						+ "<br>&nbsp;&nbsp;x(" + (isAMode ? "A2-" : "B2+") + ") = "
						+ roundDouble(Double.parseDouble(st.nextToken()), 4));
				break;
			case 3:
				status3.setText(status3.getText() + "<br>&nbsp;&nbsp;x(" + (isAMode ? "H3A" : "B(OH)3") + ") = "
						+ roundDouble(Double.parseDouble(st.nextToken()), 4) + "<br>&nbsp;&nbsp;x("
						+ (isAMode ? "H2A-" : "B(OH)2 +") + ") = " + roundDouble(Double.parseDouble(st.nextToken()), 4)
						+ "<br>&nbsp;&nbsp;x(" + (isAMode ? "HA2-" : "B(OH) 2+") + ") = "
						+ roundDouble(Double.parseDouble(st.nextToken()), 4) + "<br>&nbsp;&nbsp;x("
						+ (isAMode ? "HA3-" : "B3+") + ") = " + roundDouble(Double.parseDouble(st.nextToken()), 4));
				break;
			default:
			}
			status3.setText(status3.getText() + "</html>");

		} catch (IndexOutOfBoundsException e) {
			status3.setText("<html>&nbsp;&nbsp;V = <br>&nbsp;&nbsp;pH = <br>&nbsp;&nbsp;dy/dx = <br>");
			switch (mode) {
			case 1:
				status3.setText(status3.getText() + "<br>&nbsp;&nbsp;x(" + (isAMode ? "HA" : "BOH")
						+ ") = <br>&nbsp;&nbsp;x(" + (isAMode ? "A-" : "B+") + ") = ");
				break;
			case 2:
				status3.setText(status3.getText() + "<br>&nbsp;&nbsp;x(" + (isAMode ? "H2A" : "B(OH)2")
						+ ") = <br>&nbsp;&nbsp;x(" + (isAMode ? "HA-" : "B(OH)+") + ") = <br>&nbsp;&nbsp;x("
						+ (isAMode ? "A2-" : "B2+") + ") = ");
				break;
			case 3:
				status3.setText(status3.getText() + "<br>&nbsp;&nbsp;x(" + (isAMode ? "H3A" : "B(OH)3")
						+ ") = <br>&nbsp;&nbsp;x(" + (isAMode ? "H2A-" : "B(OH)2 +") + ") = <br>&nbsp;&nbsp;x("
						+ (isAMode ? "HA2-" : "B(OH) 2+") + ") = <br>&nbsp;&nbsp;x(" + (isAMode ? "A3-" : "B3+")
						+ ") = ");
				break;
			default:
			}
			status3.setText(status3.getText() + "</html>");
		} catch (NullPointerException e) {
		}
		p.repaint();
	}

	public String getFrac(double H) {
		double lower;
		switch (mode) {
		case 1:
			lower = a + H;
			double a1 = H / lower;
			double a2 = a / lower;
			return a1 + " " + a2;
		case 2:
			lower = aa * a + (H * (a + H));
			double b1 = H * H / lower;
			double b2 = H * a / lower;
			double b3 = aa * a / lower;
			return b1 + " " + b2 + " " + b3;
		case 3:
			lower = aaa * aa * a + (H * (aa * a + H * (a + H)));
			double c1 = H * H * H / lower;
			double c2 = H * H * a / lower;
			double c3 = H * a * aa / lower;
			double c4 = a * aa * aaa / lower;
			return c1 + " " + c2 + " " + c3 + " " + c4;
		default:
			return null;
		}
	}

	public String roundDouble(double d, int n) {
		return String.format("%." + n + "f", d);
	}

	public String getExtension(File f) {
		String ext;
		String s = f.getName();
		int i = s.lastIndexOf('.');

		if (i != -1) {
			ext = s.substring(i + 1).toLowerCase();
			return ext;
		}
		return "";
	}

	private double getSpecialRoot(double a, double u) {
		double A = vA * concA / (vA + u);

		double a3 = a;
		double a2 = -a * A - w;
		double a1 = -a * w;

		List<Double> roots = AnPolyApprox.getRoot(a3, a2, a1);
		if (roots.size() == 0) {
			return 0;
		}
		return compareRoot(roots);
	}

	double getRoot(double a, double b, double u) {
		if (b == 0) {
			return getSpecialRoot(a, u);
		}
		double A = vA * concA / (vA + u);
		double B = u * concB / (vA + u);

		double a4 = (a * b + w + B * b) / b;
		double a3 = (a * w + a * B * b - A * a * b - b * w) / b;
		double a2 = -(A * a * w + a * b * w + w * w) / b;
		double a1 = -a * w * w / b;

		List<Double> roots = AnPolyApprox.getRoot(a4, a3, a2, a1);
		if (roots.size() == 0) {
			return 0;
		}
		return compareRoot(roots);
	}

	double getDiff(double a, double b, double u, double x) {
		x = Math.pow(10, -x);
		double A = vA / (vA + u) * concA;
		double B = u / (vA + u) * concB;

		double a4 = (a * b + w + B * b) / b;
		double a3 = (a * w + a * B * b - A * a * b - b * w) / b;
		double a2 = -(A * a * w + a * b * w + w * w) / b;
		double a1 = -a * w * w / b;

		double rx = AnPolyApprox.fpx(x, a4, a3, a2, a1) * b;
		double ru = vA / ((vA + u) * (vA + u))
				* (concB * b * x * x * x + concA * a * b * x * x + concB * a * b * x * x + concA * a * w * x);
		double diff = 1 / (x * Math.log(10)) * ru / rx;
		return diff;
	}

	private double getSpecialRoot(double a, double aa, double u) {
		double A = vA * concA / (vA + u);
		double B = u * concB / (vA + u);

		double a4 = a;
		double a3 = a * aa - w - a * A;
		double a2 = -(a * w + 2 * a * aa * A);
		double a1 = -a * aa * w;

		List<Double> roots = AnPolyApprox.getRoot(a4, a3, a2, a1);
		if (roots.size() == 0) {
			return 0;
		}
		return compareRoot(roots);
	}

	double getRoot(double a, double aa, double b, double u) {
		if (b == 0) {
			return getSpecialRoot(a, aa, u);
		}
		double A = vA * concA / (vA + u);
		double B = u * concB / (vA + u);

		double a5 = (a * b + w + B * b) / b;
		double a4 = (a * aa * b + a * w + a * B * b - b * w - a * A * b) / b;
		double a3 = (a * aa * w + a * aa * B * b - a * b * w - w * w - a * A * w - 2 * a * aa * A * b) / b;
		double a2 = -(a * w * w + a * aa * b * w + 2 * a * aa * A * w) / b;
		double a1 = -a * aa * w * w / b;

		List<Double> roots = AnPolyApprox.getRoot(a5, a4, a3, a2, a1);
		if (roots.size() == 0) {
			return 0;
		}
		return compareRoot(roots);
	}

	double getDiff(double a, double aa, double b, double u, double x) {
		x = Math.pow(10, -x);
		double A = vA / (vA + u) * concA;
		double B = u / (vA + u) * concB;

		double a5 = (a * b + w + B * b) / b;
		double a4 = (a * aa * b + a * w + a * B * b - b * w - a * A * b) / b;
		double a3 = (a * aa * w + a * aa * B * b - a * b * w - w * w - a * A * w - 2 * a * aa * A * b) / b;
		double a2 = -(a * w * w + a * aa * b * w + 2 * a * aa * A * w) / b;
		double a1 = -a * aa * w * w / b;

		double rx = AnPolyApprox.fpx(x, a5, a4, a3, a2, a1) * b;
		double ru = vA / ((vA + u) * (vA + u))
				* (concB * b * x * x * x * x + concB * a * b * x * x * x + concA * a * b * x * x * x
						+ concB * a * aa * b * x * x + concA * a * w * x * x + 2 * concA * a * aa * b * x * x
						+ 2 * concA * a * aa * w * x);
		double diff = 1 / (x * Math.log(10)) * ru / rx;
		return diff;
	}

	private double getSpecialRoot(double a, double aa, double aaa, double u) {
		double A = vA * concA / (vA + u);
		double B = u * concB / (vA + u);

		double a5 = a;
		double a4 = a * aa - w - a * A;
		double a3 = a * aa * aaa - a * w - 2 * a * aa * A;
		double a2 = -(a * aa * w + 3 * a * aa * aaa * A);
		double a1 = -a * aa * aaa * w;

		List<Double> roots = AnPolyApprox.getRoot(a5, a4, a3, a2, a1);
		if (roots.size() == 0) {
			return 0;
		}
		return compareRoot(roots);
	}

	double getRoot(double a, double aa, double aaa, double b, double u) {
		double A = vA * concA / (vA + u);
		double B = u * concB / (vA + u);

		double a6 = (a * b + w + B * b) / b;
		double a5 = (a * w + a * B * b - b * w - a * A * b) / b;
		double a4 = (a * aa * w + a * aa * aaa * b + a * aa * B * b - a * b * w - w * w - a * A * w
				- 2 * a * aa * A * b) / b;
		double a3 = (a * aa * aaa * w + a * aa * aaa * B * b - a * aa * b * w - a * w * w - 2 * a * aa * A * w
				- 3 * a * aa * aaa * A * b) / b;
		double a2 = -(a * aa * aaa * b * w + a * aa * w * w + 3 * a * aa * aaa * A * w) / b;
		double a1 = -a * aa * aaa * w * w / b;

		List<Double> roots = AnPolyApprox.getRoot(a6, a5, a4, a3, a2, a1);
		if (roots.size() == 0) {
			return 0;
		}
		return compareRoot(roots);
	}

	double getDiff(double a, double aa, double aaa, double b, double u, double x) {
		x = Math.pow(10, -x);
		double A = vA / (vA + u) * concA;
		double B = u / (vA + u) * concB;

		double a6 = (a * b + w + B * b) / b;
		double a5 = (a * w + a * B * b - b * w - a * A * b) / b;
		double a4 = (a * aa * w + a * aa * aaa * b + a * aa * B * b - a * b * w - w * w - a * A * w
				- 2 * a * aa * A * b) / b;
		double a3 = (a * aa * aaa * w + a * aa * aaa * B * b - a * aa * b * w - a * w * w - 2 * a * aa * A * w
				- 3 * a * aa * aaa * A * b) / b;
		double a2 = -(a * aa * aaa * b * w + a * aa * w * w + 3 * a * aa * aaa * A * w) / b;
		double a1 = -a * aa * aaa * w * w / b;

		double rx = AnPolyApprox.fpx(x, a6, a5, a4, a3, a2, a1) * b;
		double ru = vA / ((vA + u) * (vA + u)) * (concB * b * x * x * x * x * x + concB * a * b * x * x * x * x
				+ concA * a * b * x * x * x * x + concB * a * aa * b * x * x * x + concA * a * w * x * x * x
				+ 2 * concA * a * aa * b * x * x * x + concB * a * aa * aaa * b * x * x + 2 * concA * a * aa * w * x * x
				+ 3 * concA * a * aa * aaa * b * x * x + 3 * concA * a * aa * aaa * w * x);
		double diff = 1 / (x * Math.log(10)) * ru / rx;
		return diff;
	}

	double compareRoot(List<Double> d) {
		double tmp = 0;
		for (double dou : d) {
			if (dou >= 0) {
				if (dou >= tmp) {
					tmp = dou;
				}
			}
		}
		return tmp;
	}

	/*
	 * boolean loadTxt(File f) { int line = 0;
	 * 
	 * boolean isAMode; String q; String qq; int mode; double concA; double vA;
	 * double a; double aa; double aaa; double concB; double b; double v;
	 * List<Double> values = new ArrayList<Double>(); List<Double> diffs = new
	 * ArrayList<Double>(); List<String> frac = new ArrayList<String>(); try {
	 * BufferedReader br = new BufferedReader(new FileReader(f)); List<String>
	 * lines = new ArrayList<String>(); String s; while ((s = br.readLine()) !=
	 * null) { lines.add(s); } br.close();
	 * 
	 * Iterator<String> it = lines.iterator();
	 * 
	 * if (!it.next().equals("Settings:")) {// line 0 return false; }
	 * 
	 * if (!it.next().equals("  Mode:")) {// line 1 return false; }
	 * 
	 * switch ((q = lines.get(line++).substring(0, 4))) {// line 2 case "acid":
	 * qq = "base"; isAMode = true; break; case "base": qq = "acid"; isAMode =
	 * false; break; default: return false; }
	 * 
	 * char c; switch ((c = lines.get(line++).charAt(0))) {// line 3 case '1':
	 * case '2': case '3': mode = c - '0'; break; default: return false; }
	 * 
	 * if (!it.next().equals("  Values:")) {// line 4 return false; }
	 * 
	 * StringTokenizer st = new StringTokenizer(it.next(), ":");// line 5 if
	 * (!st.nextToken().equals("M of " + q + ":")) { return false; } concA = new
	 * double(st.nextToken());
	 * 
	 * st = new StringTokenizer(it.next(), ":");// line 6 if
	 * (!st.nextToken().equals("V of " + q + ":")) { return false; } vA = new
	 * double(st.nextToken());
	 * 
	 * for (int i = 0; i < mode; i++) {// line 7~6+mode switch (i) { case 0: st
	 * = new StringTokenizer(it.next(), ":"); st.nextToken(); a = new
	 * double(st.nextToken()); break; case 1: aa = new double(it.next()); break;
	 * case 2: aaa = new double(it.next()); break; default: return false; } }
	 * 
	 * st = new StringTokenizer(it.next(), ":");// line 7+mode if
	 * (!st.nextToken().equals("M of " + qq + ":")) { return false; } concB =
	 * new double(st.nextToken());
	 * 
	 * st = new StringTokenizer(it.next(), ":");// line 8+mode st.nextToken(); b
	 * = new double(st.nextToken());
	 * 
	 * if (!it.next().equals("----------------------------------------")) {//
	 * line // 9+mode return false; }
	 * 
	 * if (!it.next().equals("Results:")) {// line 10+mode return false; }
	 * 
	 * if (!it.next().equals("  Equivalence point(s):")) {// line 11+mode return
	 * false; }
	 * 
	 * for (int i = 0; i < mode; i++) {// line 12+mode~11+mode*2 st = new
	 * StringTokenizer(it.next(), ":"); if (!st.nextToken().equals("V" + (mode -
	 * 1))) { br.close(); return false; } switch (mode) { case 1: if (i == 1) {
	 * v = new double(st.nextToken()); } break; case 2: if (i == 2) { v = new
	 * double(st.nextToken()); break; } case 3: if (i == 2) { v = new
	 * double(st.nextToken()); break; } } }
	 * 
	 * if (!it.next().equals("  Values at points:")) {//12+mode*2 return false;
	 * }
	 * 
	 * for(int i=0;i<=1000;i++){
	 * 
	 * } } catch (IOException e) { } }
	 */

	void saveTxt(File f) {
		String q = isAMode ? "Ka" : "Kb";
		String g = isAMode ? "Kb" : "Ka";
		String qq = isAMode ? "acid" : "base";
		String gg = isAMode ? "base" : "acid";

		try {
			PrintWriter pw = new PrintWriter(new FileWriter(f));
			write(pw, "Settings:");
			write(pw, "  Mode:");
			write(pw, "    " + gg + "-over-" + qq);
			write(pw, "    " + mode + "-protic");
			write(pw, "  Values:");
			write(pw, "    M of " + qq + ": " + macid.getText());
			write(pw, "    V of " + qq + ": " + vacid.getText());
			StringTokenizer s = new StringTokenizer(acid.getText());
			write(pw, "    " + q + " of " + qq + ": " + s.nextToken());
			if (mode >= 2) {
				write(pw, "                " + s.nextToken());
			}
			if (mode >= 3) {
				write(pw, "                " + s.nextToken());
			}
			write(pw, "    M of " + gg + ": " + mbase.getText());
			write(pw, "    " + g + " of " + gg + ": " + base.getText());
			write(pw, "----------------------------------------");
			write(pw, "Results:");
			write(pw, "  Equivalence point(s):");
			switch (mode) {
			case 1:
				write(pw, "    V0: " + v + " mL");
				break;
			case 2:
				write(pw, "    V0: " + v / 2 + " mL");
				write(pw, "    V1: " + v + " mL");
				break;
			case 3:
				write(pw, "    V0: " + v / 2 + " mL");
				write(pw, "    V1: " + v + " mL");
				write(pw, "    V2: " + 3 * v / 2 + " mL");
				break;
			}
			write(pw, "  Values at points:");
			write(pw, "    Point 0:");
			write(pw, "      V: 0 mL");
			write(pw, "      pH: " + values.get(0));
			write(pw, "      d(pH)/dV: Undefined");
			StringTokenizer st = new StringTokenizer(frac.get(0));
			switch (mode) {
			case 1:
				write(pw, "      " + (isAMode ? "x(HA)" : "x(BOH)") + ": " + st.nextToken());
				write(pw, "      " + (isAMode ? "x(A-)" : "x(B+)") + ": " + st.nextToken());
				break;
			case 2:
				write(pw, "      " + (isAMode ? "x(H2A)" : "x(B(OH)2)") + ": " + st.nextToken());
				write(pw, "      " + (isAMode ? "x(HA-)" : "x(BOH+)") + ": " + st.nextToken());
				write(pw, "      " + (isAMode ? "x(A2-)" : "(B2+)") + ": " + st.nextToken());
				break;
			case 3:
				write(pw, "      " + (isAMode ? "x(H3A)" : "x(B(OH)3)") + ": " + st.nextToken());
				write(pw, "      " + (isAMode ? "x(H2A-)" : "x(B(OH)2 +)") + ": " + st.nextToken());
				write(pw, "      " + (isAMode ? "x(HA2-)" : "x(BOH2+)") + ": " + st.nextToken());
				write(pw, "      " + (isAMode ? "x(A3-)" : "x(B3+)") + ": " + st.nextToken());
			}
			for (int i = 1; i < 1000; i++) {
				write(pw, "    Point " + i + ":");
				write(pw, "      V: " + v * i / 500 + " mL");
				write(pw, "      pH: " + values.get(i));
				write(pw, "      d(pH)/dV: " + (-diffs.get(i)));
				st = new StringTokenizer(frac.get(i));
				switch (mode) {
				case 1:
					write(pw, "      " + (isAMode ? "x(HA)" : "x(BOH)") + ": " + st.nextToken());
					write(pw, "      " + (isAMode ? "x(A-)" : "x(B+)") + ": " + st.nextToken());
					break;
				case 2:
					write(pw, "      " + (isAMode ? "x(H2A)" : "x(B(OH)2)") + ": " + st.nextToken());
					write(pw, "      " + (isAMode ? "x(HA-)" : "x(BOH+)") + ": " + st.nextToken());
					write(pw, "      " + (isAMode ? "x(A2-)" : "(B2+)") + ": " + st.nextToken());
					break;
				case 3:
					write(pw, "      " + (isAMode ? "x(H3A)" : "x(B(OH)3)") + ": " + st.nextToken());
					write(pw, "      " + (isAMode ? "x(H2A-)" : "x(B(OH)2 +)") + ": " + st.nextToken());
					write(pw, "      " + (isAMode ? "x(HA2-)" : "x(BOH2+)") + ": " + st.nextToken());
					write(pw, "      " + (isAMode ? "x(A3-)" : "x(B3+)") + ": " + st.nextToken());
				}
			}
			write(pw, "    Point 1000:");
			write(pw, "      V: " + v * 2 + " mL");
			write(pw, "      pH: " + values.get(1000));
			write(pw, "      d(pH)/dV: Undefined");
			st = new StringTokenizer(frac.get(1000));
			switch (mode) {
			case 1:
				write(pw, "      " + (isAMode ? "x(HA)" : "x(BOH)") + ": " + st.nextToken());
				write(pw, "      " + (isAMode ? "x(A-)" : "x(B+)") + ": " + st.nextToken());
				break;
			case 2:
				write(pw, "      " + (isAMode ? "x(H2A)" : "x(B(OH)2)") + ": " + st.nextToken());
				write(pw, "      " + (isAMode ? "x(HA-)" : "x(BOH+)") + ": " + st.nextToken());
				write(pw, "      " + (isAMode ? "x(A2-)" : "(B2+)") + ": " + st.nextToken());
				break;
			case 3:
				write(pw, "      " + (isAMode ? "x(H3A)" : "x(B(OH)3)") + ": " + st.nextToken());
				write(pw, "      " + (isAMode ? "x(H2A-)" : "x(B(OH)2 +)") + ": " + st.nextToken());
				write(pw, "      " + (isAMode ? "x(HA2-)" : "x(BOH2+)") + ": " + st.nextToken());
				write(pw, "      " + (isAMode ? "x(A3-)" : "x(B3+)") + ": " + st.nextToken());
			}
		} catch (IOException e) {
		}
	}

	void write(PrintWriter pw, String s) {
		pw.println(s);
		pw.flush();
	}
	/*
	 * // macro
	 * 
	 * String molbase;
	 * 
	 * public void macro(String s) { molbase = s; macid.setText("1");
	 * mbase.setText("1"); base.setText(molbase); vacid.setText("500"); File f =
	 * new File("D:/미래졸논/1,1,n," + molbase); if (!f.exists()) { f.mkdir(); } for
	 * (int i = -20; i < 11; i++) { if (i == 0) { acid.setText("1"); } else {
	 * acid.setText("1e" + i); } but.doClick(); try { t.join(); } catch
	 * (InterruptedException e) { } } }
	 * 
	 * public void saveMacro() { long now = System.currentTimeMillis(); while
	 * (System.currentTimeMillis() - now < 300L) { }
	 * 
	 * BufferedImage tmp = new BufferedImage(m.getWidth(), m.getHeight(),
	 * BufferedImage.TYPE_INT_RGB); m.paint(tmp.getGraphics()); File f = new
	 * File("D:/미래졸논/1,1,n," + molbase + "/1,1," + acid.getText() + "," +
	 * molbase + ".png"); tmp.getGraphics().dispose(); try { ImageIO.write(tmp,
	 * "png", f); } catch (IOException ex) { }
	 * 
	 * f = new File("D:/미래졸논/1,1,n," + molbase + "/1,1," + acid.getText() + ","
	 * + molbase + ".txt"); saveTxt(f); }
	 */
}
