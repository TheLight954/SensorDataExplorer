import processing.core.PApplet;

public class DataTableDisplay {
	private int x, y, w, h;
	private int rows, cols;
	private double[][] data;
	private String[] names;
	private int currentRow;
	private PApplet window;

	private float dx, dy;
	private double initialTime;

	public DataTableDisplay(PApplet win, int x, int y, int w, int h, int rows, int cols, double[][] data, String[] names) {
		super();
		this.window = win;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.rows = rows;
		this.cols = cols;
		this.data = data;
		
		this.names = names;

		dx = (float) w / cols;
		dy = (float) h / (rows+1);
	}

	public void draw() {
		window.textAlign(window.CENTER, window.CENTER);
		window.textSize(16);
		
		for (int c = 0; c <= cols; c++) {			
			window.fill(window.color(255, 255, 255));
			window.rect(x + dx * c, y, dx, dy);
			window.fill(window.color(0, 0, 0));
			if (c == 0) {
				window.text("index", x + dx * c + dx / 2, y + dy / 2);
			} else
				window.text(names[c-1], x + dx * c + dx / 2, y + dy / 2);
		}
				
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c <= cols; c++) {
				window.fill(window.color(255, 255, 255));
				window.rect(x + dx * c, dy + y + dy * r, dx, dy);

				String val;

				if (c == 0) {
					val = "" + (currentRow - (rows - 1) / 2 + r);
				} else if (isInBounds(currentRow - (rows - 1) / 2 + r, c - 1)) {
					double numval = data[currentRow - (rows - 1) / 2 + r][c - 1];
					if (c == 1) {	// set time to be ellapsed time!!
						val = "" + (numval - initialTime)/1000.0;
					} else 
						val = "" + numval; 
				} else {
					val = "";
				}

				window.fill(window.color(0, 0, 0));
				// window.stroke( window.color(0, 0, 0 ));
				window.text(val, x + dx * c + dx / 2, dy + y + dy * r + dy / 2);
			}
		}
		
		int highlight = window.color(246, 255, 0, 100);
		window.fill(highlight);
		window.rect(x, y + dy*(rows+1)/2, w + dx, dy);
	}

	public void jumpToRow(int r) {
		currentRow = r;
	}

	private boolean isInBounds(int r, int c) {
		if (r < 0 || c < 0)
			return false;
		if (r >= data.length || c >= data[0].length)
			return false;
		return true;
	}

	public void setInitialTime(double t) {
		initialTime = t;
	}
}
