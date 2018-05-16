package test;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import draw.MNISTDrawablePanel;
import layer.FCLayer;
import network.SequentialNN;
import utils.Activation;
import utils.Tensor;
import utils.Utils;

public class TestMNIST2{
	public static void main(String[] args){
		SequentialNN nn = new SequentialNN(784);
		nn.add(new FCLayer(300, Activation.sigmoid));
		nn.add(new FCLayer(10, Activation.softmax));
		nn.loadFromFile("mnist_weights.nn");
		
		JFrame frame = new JFrame();
		frame.setLayout(new FlowLayout());
		MNISTDrawablePanel drawablePanel = new MNISTDrawablePanel(1000, 1000, 100, 100);
		frame.add(drawablePanel);
		
		JLabel label = new JLabel("Result: ");
		label.setFont(label.getFont().deriveFont(30.0f));
		frame.add(label);
		
		JButton submitButton = new JButton("Submit");
		submitButton.setPreferredSize(new Dimension(200, 100));
		submitButton.setFont(submitButton.getFont().deriveFont(30.0f));
		submitButton.addActionListener((e) -> {
			Tensor data = drawablePanel.getData(20, 20, 28, 28);
			Tensor result = nn.predict(data.T().flatten());
			label.setText("Result: " + Utils.argMax(result));
		});
		frame.add(submitButton);
		
		JButton clearButton = new JButton("Clear");
		clearButton.setPreferredSize(new Dimension(200, 100));
		clearButton.setFont(clearButton.getFont().deriveFont(30.0f));
		clearButton.addActionListener((e) -> {
			drawablePanel.clear();
		});
		frame.add(clearButton);
		
		frame.setSize(1200, 1200);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
