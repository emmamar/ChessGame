package emma.basic.chessgameus.ChessBoard.BoardDefinitions;

public class Square {
	int row;
	int column;

	public Square() {
		this.row = -1;
		this.column = -1;
	}

	public Square(int X, int Y) {
		this.row = getColumnRow(X);
		this.column = getColumnRow(Y);
	}

	public void setRow(int row) {
		this.row = row;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	public int getRow() {
		return row;
	}

	public int getColumn() {
		return column;
	}

	private int getColumnRow(int YX) {
		if (YX <= 152) {
			if (YX <= 76) {
				if (YX <= 38) {
					// column 1
					return 0;
				} else if (YX > 38) {
					// column2
					return 1;
				}
			} else if (YX > 76) {
				if (YX <= 114) {
					// column3
					return 2;
				} else if (YX > 114) {
					// column4
					return 3;
				}
			}
		} else if (YX > 152) {
			if (YX <= 228) {
				if (YX <= 190) {
					// column5
					return 4;
				} else if (YX > 190) {
					// column6
					return 5;
				}
			} else if (YX > 228) {
				if (YX <= 266) {
					// column7
					return 6;
				} else if (YX > 266) {
					// column8
					return 7;
				}
			}

		}
		return 0;
	}
}
