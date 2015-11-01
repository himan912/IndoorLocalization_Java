import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

class Canvas extends JPanel {
	private static final Color COLOR_ANCHOR = Color.BLACK;
	private static final Color COLOR_LOCATION = new Color(150, 50, 50, 180);
	private static final int SIZE_PLOT = 12;

	private int screenWidth_pixel;
	private float mapWidth_x, mapWidth_y, mapWidth;

	private Location2D currentLocation;
	private HashMap<Integer, Location2D> anchors;

	public Canvas(int screenWidth) {
		this.screenWidth_pixel = screenWidth;
		mapWidth_x = 1;
		mapWidth_y = 1;
		mapWidth = 1;
		anchors = new HashMap<>();
		currentLocation = new Location2D(0,0);
	}

	public void plotLocation(float x, float y){
		currentLocation.setLocation(x, y);
		repaint();
	}

	public void addAnchorNode(int id, float x, float y){
		if(!anchors.containsKey(id)){
			anchors.put(id, new Location2D(x,y));

			for (Location2D loc1 : anchors.values()) {
				for (Location2D loc2 : anchors.values()) {
					if (Math.abs(loc1.getX() - loc2.getX()) > mapWidth_x) {
						mapWidth_x = Math.abs(loc1.getX() - loc2.getX());
					}
					if (Math.abs(loc1.getY() - loc2.getY()) > mapWidth_y) {
						mapWidth_y = Math.abs(loc1.getY() - loc2.getY());
					}
				}
			}
			if(mapWidth_x >= mapWidth_y){
				mapWidth = mapWidth_x;
			}else{
				mapWidth = mapWidth_y;
			}
		}        
	}

	public int convertX(float x){
		return (int) (Math.round((x + ((1.5 * mapWidth) - mapWidth_x) / 2) * (screenWidth_pixel / (mapWidth * 1.5))));
	}

	public int convertY(float y){
		return (int) (screenWidth_pixel - Math.round(((y + ((1.5 * mapWidth) - mapWidth_y) / 2) * (screenWidth_pixel / (mapWidth * 1.5)))));
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
	    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g2d.setPaint(COLOR_ANCHOR);
		for(Location2D loc:anchors.values()){
			g2d.fillOval(convertX(loc.getX()), convertY((int)loc.getY()), SIZE_PLOT, SIZE_PLOT);
		}

		g2d.setColor(COLOR_LOCATION);
		g2d.fillOval(convertX(currentLocation.getX()), convertY((int)currentLocation.getY()), SIZE_PLOT-3, SIZE_PLOT-3);
	}
}

public class IndoorLocalization extends JFrame {
	private final int WIDTH = 600;
	private static ServerSocket serverSocket;
	private static Socket socket;
	private static DataInputStream dis;
	private static Canvas canvas_map;

	public IndoorLocalization() {
		canvas_map = new Canvas(WIDTH);
		add(canvas_map);
		setTitle("Indoor Localization");
		setSize(WIDTH, WIDTH);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void main(String[] args) {
		String receiving = null;
		
		try {
			serverSocket = new ServerSocket(5555);
			socket = serverSocket.accept();
			dis = new DataInputStream(socket.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				IndoorLocalization ex = new IndoorLocalization();
				ex.setVisible(true);
			}
		});
		
		while(true){
			try {
				Thread.sleep(500);
			} catch (InterruptedException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			
			try {				
				receiving = dis.readUTF();
				System.out.println(receiving);
				
				if (receiving == null) continue;
				
				String[] values = receiving.split(",");
				System.out.println(values[0] + ":" + values[1] + ":" + values[2]);
				
				int id = Integer.parseInt(values[0]);
				float x = Float.parseFloat(values[1]);
				float y = Float.parseFloat(values[2]);
				
				if(id == 0){
					canvas_map.plotLocation(x, y);
				}else if(id > 0){
					canvas_map.addAnchorNode(id, x, y);
				}
			} catch (IOException e) {
				try {
					dis.close();
					socket.close();
					serverSocket.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}			
		}
	}
}
