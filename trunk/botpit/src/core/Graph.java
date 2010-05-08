package core;
/**
 * Most of the code here is a modified version of the code from 
 * opentestbed. http://code.google.com/p/opentestbed
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.Timer;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class Graph {
	private JFrame frame;
	private JProgressBar progress_bar;
	private Timer timer;
	private Botpit gi;
	private ChartPanel chartPanel = new ChartPanel(null);
//	private Map<Integer, List<Map<String, Double>>> stats = new HashMap<Integer, List<Map<String, Double>>>();
	StringBuffer sb = new StringBuffer();
	public Graph(Botpit gi) {
		this.gi = gi;
	}
	public void game_started() {
		create_frame();
		update_graph();
		frame.setVisible(true);
	}

	private void create_frame() {
		frame = new JFrame("progress");
		frame.setSize(1000,600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		progress_bar = new JProgressBar(0,Botpit.NUMSIMROUNDS);
		progress_bar.setStringPainted(true);
		frame.add(progress_bar, BorderLayout.NORTH);
		frame.add(chartPanel,BorderLayout.CENTER);
	}
	private JFreeChart calculate_chart(int snapshot) {
		Map<String, Double> playerToBankRoll = new HashMap<String, Double>();
		Map<String, XYSeries> playerToXYSeries = new HashMap<String, XYSeries>();
		for (int i=0;i<Botpit.NUMPLAYERS;i++) {
			playerToBankRoll.put("seat " + i, new Double(0));
			playerToXYSeries.put("seat " + i, new XYSeries("seat "+i));
		}
		calculateBankrolls(playerToBankRoll, playerToXYSeries, snapshot);

		XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
		for (XYSeries playerXYSeries : playerToXYSeries.values()) {
			xySeriesCollection.addSeries(playerXYSeries);
		}

		final JFreeChart chart = createJFreeChart(playerToBankRoll, xySeriesCollection, snapshot);
		return chart;
	}

	private JFreeChart createJFreeChart(
			Map<String, Double> playerToBankRoll,
			XYSeriesCollection xySeriesCollection, int snapshot) {
		final JFreeChart chart = ChartFactory.createXYLineChart("Live Stack Graph", "Games", "Stack",
				xySeriesCollection, PlotOrientation.VERTICAL, true, false, false);
		
		for (int i=0;i<Botpit.NUMPLAYERS;i++) {
			String playerName = "seat "+i;
			double finalBankroll = playerToBankRoll.get(playerName);
			DecimalFormat moneyFormat = new DecimalFormat("0.00");
			String resultString = playerName + ": $" + moneyFormat.format(finalBankroll) + " ($"
					+ moneyFormat.format(finalBankroll / (snapshot / 100D)) + "/100)";
			final XYPointerAnnotation pointer = new XYPointerAnnotation(resultString, Math.min(snapshot, Botpit.NUMSIMROUNDS), finalBankroll,
					Math.PI * 5.9 / 6);
			pointer.setBaseRadius(130.0);
			pointer.setTipRadius(1.0);
			pointer.setLabelOffset(10.0);
			pointer.setOutlineVisible(true);
			pointer.setBackgroundPaint(Color.WHITE);
			chart.getXYPlot().addAnnotation(pointer);
		}
		
		return chart;
	}

	private void calculateBankrolls(
			Map<String, Double> playerToBankRoll,
			Map<String, XYSeries> playerToXYSeries, int snapshot) {
		for (int game = 0; game < Math.min(Botpit.NUMSIMROUNDS, snapshot); game++) {
			for (int i=0;i<Botpit.NUMPLAYERS;i++) {
				double playerBankRoll = gi.stacks[i];
				playerToBankRoll.put("seat "+i, playerBankRoll);
				playerToXYSeries.get("seat "+i).add(game+1, playerBankRoll);
			}
		}
	}
	
	private void update_graph() {
		timer = new Timer(1000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int snapshot = gi.current_hand;
				if (snapshot == Botpit.NUMSIMROUNDS) timer.stop();
				long now = System.currentTimeMillis();
				long past_time = now - Botpit.game_start_time;
				float gamespeed_avg = snapshot > 0 ? (snapshot / (past_time/1000)) : 0;
				Date expectedEnd = new Date(System.currentTimeMillis() + (Botpit.NUMSIMROUNDS - snapshot) * (int)gamespeed_avg);
				long progress = snapshot * 100 / Botpit.NUMSIMROUNDS;
				progress_bar.setValue(snapshot);
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy HH:mm");
				sb.append("Completed:");
				sb.append(snapshot); sb.append("/");
				sb.append(Botpit.NUMSIMROUNDS);
				sb.append(" - %");sb.append(progress);
				sb.append(" Speed (g/s):");sb.append((Math.round(gamespeed_avg* 100)) / 100D);
				sb.append(" ETF:");sb.append(dateFormat.format(expectedEnd));
				progress_bar.setString(sb.toString());
				sb.setLength(0);
				final JFreeChart chart = calculate_chart(snapshot);
				chartPanel.setChart(chart);
			}
		});
		timer.start();
	}
}
