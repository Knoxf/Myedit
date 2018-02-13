package com.knox.myedit;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

@SuppressWarnings("serial")

public class FileWindow extends JFrame implements ActionListener, Runnable {

	/*
	 * because usedActionListener
	 * and Runnable.so use double interface method
	 */

	Thread compiler = null;
	Thread run_prom = null;
	boolean bn = true;
	CardLayout mycard; // Declare the layout
	File file_saved = null;
	JButton button_input_txt, // Define button
			button_compiler_text, button_compiler,
			button_run_prom,
			button_see_doswin;

	JPanel p = new JPanel();
	JTextArea input_text = new JTextArea(); // program input area
	JTextArea compiler_text = new JTextArea();// write worry information display area
	JTextArea dos_out_text = new JTextArea();// program output information

	JTextField input_file_name_text = new JTextField();
	JTextField run_file_name_text = new JTextField();

	public FileWindow() {
		// TODO Auto-generated constructor stub
		super("Knox Edite");
		mycard = new CardLayout();
		compiler = new Thread(this);
		run_prom = new Thread(this);
		button_input_txt = new JButton("Program input（White）");
		button_compiler_text = new JButton("Compile the results(Pink）");
		button_see_doswin = new JButton("Program running results（blue）");
		button_compiler = new JButton("Compile the program");
		button_run_prom = new JButton("run program");

		p.setLayout(mycard);// setting layout
		p.add("input", input_text);// setting card name
		p.add("compiler", compiler_text);
		p.add("dos", dos_out_text);
		add(p, "Center");

		compiler_text.setBackground(Color.pink); // setting color
		dos_out_text.setBackground(Color.cyan);
		JPanel p1 = new JPanel();

		p1.setLayout(new GridLayout(3, 3)); // setting table layout
		// add a component
		p1.add(button_input_txt);
		p1.add(button_compiler_text);
		p1.add(button_see_doswin);
		p1.add(new JLabel("Enter the compiled file name（.java）："));
		p1.add(input_file_name_text);
		p1.add(button_compiler);
		p1.add(new JLabel("Enter the application primary class name"));
		p1.add(run_file_name_text);
		p1.add(button_run_prom);
		add(p1, "North");

		// Define events
		button_input_txt.addActionListener(this);
		button_compiler.addActionListener(this);
		button_compiler_text.addActionListener(this);
		button_run_prom.addActionListener(this);
		button_see_doswin.addActionListener(this);

	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == button_input_txt) {      //刚刚解决了一个存在两个月的语法错误，错误主要是if和后面else语句的位置错误
			mycard.show(p, "input");
		} else if (e.getSource() == button_compiler_text) {  //以后不能这么粗心
			mycard.show(p, "compiler");
		} else if (e.getSource() == button_see_doswin) {
			mycard.show(p, "dos");
		} else if (e.getSource() == button_compiler) {
			if (!(compiler.isAlive())) {
				compiler = new Thread(this);
			}
			try {
				compiler.start();

			} catch (Exception e2) {
				// TODO: handle exception
				e2.printStackTrace();
			}

			mycard.show(p, "compiler");

		} else if (e.getSource() == button_run_prom) {
			if (!(run_prom.isAlive())) {
				run_prom = new Thread(this);
			}
			try {
				run_prom.start();
			} catch (Exception e2) {
				// TODO: handle exception
				e2.printStackTrace();
			}
			mycard.show(p, "dos");
		}

	}


	@Override
	public void run() {
		// TODO Auto-generated method stub
		if (Thread.currentThread() == compiler) {
			compiler_text.setText(null);
			String temp = input_text.getText().trim();
			byte[] buffer = temp.getBytes();
			int b = buffer.length;
			String file_name = null;
			file_name = input_file_name_text.getText().trim();

			try {
				file_saved = new File(file_name);
				FileOutputStream writefile = null;
				writefile = new FileOutputStream(file_saved);
				writefile.write(buffer, 0, b);
				writefile.close();
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println("ERROR");
			}
			try {

				// 获得该进程的错误流，才可以知道运行结果到底是失败了还是成功。
				Runtime rt = Runtime.getRuntime();
				InputStream in = rt.exec("javac " + file_name).getErrorStream(); // 通过Runtime调用javac命令

				BufferedInputStream bufIn = new BufferedInputStream(in);

				byte[] shuzu = new byte[100];
				int n = 0;
				boolean flag = true;

				// input error information
				while ((n = bufIn.read(shuzu, 0, shuzu.length)) != -1) {
					String s = null;
					s = new String(shuzu, 0, n);
					compiler_text.append(s);
					if (s != null) {
						flag = false;
					}
				}
				// 判断是否编译成功
				if (flag) {
					compiler_text.append("Compile Succeed!");
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		} else if (Thread.currentThread() == run_prom) {
			// run file and make run result to dos_out_text

			dos_out_text.setText(null);

			try {
				Runtime rt = Runtime.getRuntime();
				String path = run_file_name_text.getText().trim();
				Process stream = rt.exec("java " + path);// 调用java命令

				InputStream in = stream.getInputStream();
				BufferedInputStream bisErr = new BufferedInputStream(
						stream.getErrorStream());
				BufferedInputStream bisIn = new BufferedInputStream(in);

				byte[] buf = new byte[150];
				byte[] err_buf = new byte[150];

				@SuppressWarnings("unused")
				int m = 0;
				@SuppressWarnings("unused")
				int i = 0;
				String s = null;
				String err = null;

				// Print compilation information and error messages
				while ((m = bisIn.read(buf, 0, 150)) != -1) {
					s = new String(buf, 0, 150);
					dos_out_text.append(s);
				}
				while ((i = bisErr.read(err_buf)) != -1) {
					err = new String(err_buf, 0, 150);
					dos_out_text.append(err);
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}
}

