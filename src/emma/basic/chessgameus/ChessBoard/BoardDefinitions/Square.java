package emma.basic.chessgameus.ChessBoard.BoardDefinitions;

import java.util.Arrays;

public class Square{
	int row;
	int column;

	public Square() {
		this.row = -1;
		this.column = -1;
	}

	//given a numeric notation for a square
	public Square(int X, int Y) {

		this.row = X;
	    this.column = Y;
		
	}
	
	//given a numeric notation for a square
	public Square(int X, int Y, float density) {
		this.row = getColumnRow(X, density);
		this.column = getColumnRow(Y, density);
	}
	
	//given an algebraic notation for a square
	public Square(String pos) {

		String[] dictionary = { "a", "b", "c", "d", "e", "f", "g", "h" };
		this.column = Arrays.asList(dictionary).indexOf(
				Character.toString(pos.charAt(0)));
		this.row = 7 - (Integer
				.parseInt(Character.toString(pos.charAt(1))) - 1);
		
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
	
	public String toString(){
		String[] dictionary = { "a", "b", "c", "d", "e", "f", "g", "h" };
		String squ = dictionary[column]
				+ Integer.toString((7 - row) + 1);
		return squ;
	}

	private int getColumnRow(int YX, float density) {
		int[] graphicsNumbers = {38, 76, 114, 152, 190, 228, 266};
		for(int i = 0; i < graphicsNumbers.length; i++){
		  graphicsNumbers[i] *= density;
		}
		
		if (YX <= graphicsNumbers[3]) {
			if (YX <= graphicsNumbers[1]) {
				if (YX <= graphicsNumbers[0]) {
					// column 1
					return 0;
				} else if (YX > graphicsNumbers[0]) {
					// column2
					return 1;
				}
			} else if (YX > graphicsNumbers[1]) {
				if (YX <= graphicsNumbers[2]) {
					// column3
					return 2;
				} else if (YX > graphicsNumbers[2]) {
					// column4
					return 3;
				}
			}
		} else if (YX > graphicsNumbers[3]) {
			if (YX <= graphicsNumbers[5]) {
				if (YX <= graphicsNumbers[4]) {
					// column5
					return 4;
				} else if (YX > graphicsNumbers[4]) {
					// column6
					return 5;
				}
			} else if (YX > graphicsNumbers[5]) {
				if (YX <= graphicsNumbers[6]) {
					// column7
					return 6;
				} else if (YX > graphicsNumbers[6]) {
					// column8
					return 7;
				}
			}

		}
		return 0;
	}
	
}
