package emma.basic.chessgameus.ChessBoard.BoardDefinitions;

import java.util.ArrayList;
import java.util.Iterator;

import emma.basic.chessgameus.ChessBoard.BoardDefinitions.ChessPiecesDefinitions.Bishop;
import emma.basic.chessgameus.ChessBoard.BoardDefinitions.ChessPiecesDefinitions.Castle;
import emma.basic.chessgameus.ChessBoard.BoardDefinitions.ChessPiecesDefinitions.ChessPiece;
import emma.basic.chessgameus.ChessBoard.BoardDefinitions.ChessPiecesDefinitions.King;
import emma.basic.chessgameus.ChessBoard.BoardDefinitions.ChessPiecesDefinitions.Knight;
import emma.basic.chessgameus.ChessBoard.BoardDefinitions.ChessPiecesDefinitions.Pawn;
import emma.basic.chessgameus.ChessBoard.BoardDefinitions.ChessPiecesDefinitions.Queen;

public class ChessBoardRepresentation implements Iterable<ChessPiece>{
	private ArrayList<ChessPiece> longBoard;
	private ChessPiece kingBlackReference;
	private ChessPiece kingWhiteReference;

	public ChessBoardRepresentation() {
		initialBoardSetup();
	}
	
	public ChessBoardRepresentation(ArrayList<ChessPiece> bor, ChessPiece kingB, ChessPiece kingW) {
		longBoard = bor;
		kingBlackReference = kingB;
		kingWhiteReference = kingW;
	}
	
	public ChessBoardRepresentation(String str){
		longBoard = new ArrayList<ChessPiece>(64);
		String[] rows = str.split("\n");
		for (int i = 0; i < rows.length; i++){
	        String[] pieces = rows[i].split(" ");
	        for(int j = 0; j < pieces.length; j++){
	            int color = 0;
			    if(pieces[j].substring(1).equals("B")){
			    	color = 1;
			    }
			    if(pieces[j].substring(0, 1).equals("B")){
			    	longBoard.add(new Bishop(color, new Square(i, j)));
			    } else if (pieces[j].substring(0, 1).equals("K")){
			    	if(color == 1){
			    		kingBlackReference = new King(color, new Square(i, j));
			    		longBoard.add(kingBlackReference);
			    	}
			    	else {
			    		kingWhiteReference = new King(color, new Square(i, j));
			    		longBoard.add(kingWhiteReference);
			    	}
			    } else if (pieces[j].substring(0, 1).equals("Q")){
			    	longBoard.add(new Queen(color, new Square(i, j)));
			    } else if (pieces[j].substring(0, 1).equals("N")){
			    	longBoard.add(new Knight(color, new Square(i, j)));
			    } else if (pieces[j].substring(0, 1).equals("P")){
			    	longBoard.add(new Pawn(color, new Square(i, j)));
			    } else if (pieces[j].substring(0, 1).equals("C")){
			    	longBoard.add(new Castle(color, new Square(i, j)));
			    } else {
			    	longBoard.add(null);
			    }
	        }
		}
	}
	
	
	public void initialBoardSetup(){
		longBoard = new ArrayList<ChessPiece>(64);
		// set up board
		longBoard.add(0, new Castle(1, new Square(0, 0)));
		longBoard.add(1, new Knight(1, new Square(0, 1)));
		longBoard.add(2, new Bishop(1, new Square(0, 2)));
		longBoard.add(3, new Queen(1, new Square(0, 3)));
		kingBlackReference = new King(1, new Square(0, 4));
		longBoard.add(4, kingBlackReference);
		longBoard.add(5, new Bishop(1, new Square(0, 5)));
		longBoard.add(6, new Knight(1, new Square(0, 6)));
		longBoard.add(7, new Castle(1, new Square(0, 7)));
		for (int i = 8; i < 16; i++) {
			longBoard.add(i, new Pawn(1, new Square(1, (i-8))));
		}
		for (int i = 16; i < 48; i++) {
			longBoard.add(i, null);
		}
		for (int i = 48; i < 56; i++) {
			longBoard.add(i, new Pawn(0, new Square(6, (i-48))));
		}
		longBoard.add(56, new Castle(0, new Square(7, 0)));
		longBoard.add(57, new Knight(0, new Square(7, 1)));
		longBoard.add(58, new Bishop(0, new Square(7, 2)));
		longBoard.add(59, new Queen(0, new Square(7, 3)));
		kingWhiteReference = new King(0, new Square(7, 4));
		longBoard.add(60, kingWhiteReference);
		longBoard.add(61, new Bishop(0, new Square(7, 5)));
		longBoard.add(62, new Knight(0, new Square(7, 6)));
		longBoard.add(63, new Castle(0, new Square(7, 7)));
		
	}
	
	public ChessPiece get(int row, int column){
		return longBoard.get((row*8) + column);
	}
	
	public void set(int row, int column, ChessPiece piece){
		  longBoard.set(((row*8) + column), piece);
	}
	
	public ChessPiece get(Square squ){
		return longBoard.get((squ.getRow()*8) + squ.getColumn());
	}
	
	public void set(Square squ, ChessPiece piece){
		longBoard.set(((squ.getRow()*8) + squ.getColumn()), piece);
	}
	public ChessPiece getKingBlackReference(){
		return kingBlackReference;
	}
	public ChessPiece getKingWhiteReference(){
		return kingWhiteReference;
	}
	
	public void setKingBlackReference(ChessPiece king){
		kingBlackReference = king;
	}
	public void setKingWhiteReference(ChessPiece king){
		kingWhiteReference = king;
	}
	
	public ChessBoardRepresentation deepCopy(){
		ArrayList<ChessPiece> longBoardCopy = new ArrayList<ChessPiece>(64);
		ChessPiece newKingReferenceBlack = null;
		ChessPiece newKingReferenceWhite = null;
		for(int i = 0; i < longBoard.size(); i++){
			if(longBoard.get(i) != null){
			    longBoardCopy.add(longBoard.get(i).deepCopy());
			    if(longBoard.get(i) instanceof King) {
			    	if(longBoard.get(i).getColor() == 1){
			    	  newKingReferenceBlack = longBoard.get(i);
			    	}
			    	else{
			    	  newKingReferenceWhite = longBoard.get(i);
			    	}
			    }
			}
			else{
				longBoardCopy.add(null);
			}
		}
		return new ChessBoardRepresentation(longBoardCopy, newKingReferenceBlack, newKingReferenceWhite);
	}
	
	public Iterator<ChessPiece> iterator() {
		Iterator<ChessPiece> iprof = longBoard.iterator();
        return iprof; 
	}
	
	public String toString(){
		String str = "";
		int column = 0;
	    for(ChessPiece w: longBoard){
	    	if(column < 7){
	    		if(w == null){
	    			str += "00" + " ";
	    		}
	    		else{
	    	        str += w.toString() + " ";
	    		}
	    		column++;
	    	}
	    	else if(column == 7){
	    		if(w == null){
	    			str += "00" + "\n";
	    		}
	    		else{
	    		    str += w.toString() + "\n";
	    		}
	    		column = 0;
	    	}
	    }
	    return str;
	}
}
