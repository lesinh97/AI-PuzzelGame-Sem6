package XepHinh;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;



public class FifteenPuzzle {

  private class TilePos {
		public int x;
		public int y;
		
		public TilePos(int x, int y) {
			this.x=x;
			this.y=y;
		}
		
	}
	
	public final static int DIMS=4;
	private int[][] tiles;
	private int display_width;
	private TilePos blank;
	
	public FifteenPuzzle() {
		tiles = new int[DIMS][DIMS];
		int cnt=1;
		for(int i=0; i<DIMS; i++) {
			for(int j=0; j<DIMS; j++) {
				tiles[i][j]=cnt;
				cnt++;
			}
		}
		display_width=Integer.toString(cnt).length();
		
		// init blank
		blank = new TilePos(DIMS-1,DIMS-1);
		tiles[blank.x][blank.y]=0;
	}
	
	public final static FifteenPuzzle SOLVED=new FifteenPuzzle();
	
	
	public FifteenPuzzle(FifteenPuzzle toClone) {
		this(); 
		for(TilePos p: allTilePos()) { 
			tiles[p.x][p.y] = toClone.tile(p);
		}
		blank = toClone.getBlank();
	}

	public List<TilePos> allTilePos() {
		ArrayList<TilePos> out = new ArrayList<TilePos>();
		for(int i=0; i<DIMS; i++) {
			for(int j=0; j<DIMS; j++) {
				out.add(new TilePos(i,j));
			}
		}
		return out;
	}

	
	public int tile(TilePos p) {
		return tiles[p.x][p.y];
	}
	
	
	public TilePos getBlank() {
		return blank;
	}
	
	
	public TilePos whereIs(int x) {
		for(TilePos p: allTilePos()) { 
			if( tile(p) == x ) {
				return p;
			}
		}
		return null;
	}
	
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof FifteenPuzzle) {
			for(TilePos p: allTilePos()) { 
				if( this.tile(p) != ((FifteenPuzzle) o).tile(p)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	
	@Override 
	public int hashCode() {
		int out=0;
		for(TilePos p: allTilePos()) {
			out= (out*DIMS*DIMS) + this.tile(p);
		}
		return out;
	}
	
	
	public void show() {
		System.out.println("");
		for(int i=0; i<DIMS; i++) {
			System.out.print(" ");
			for(int j=0; j<DIMS; j++) {
				int n = tiles[i][j];
				String s;
				if( n>0) {
					s = Integer.toString(n);	
				} else {
					s = "";
				}
				while( s.length() < display_width ) {
					s += " ";
				}
				System.out.print(s + " ");
			}
			System.out.print("\n");
		}
		System.out.print("-----------------\n\n");
	}
	
	
	public List<TilePos> allValidMoves() {
		ArrayList<TilePos> out = new ArrayList<TilePos>();
		for(int dx=-1; dx<2; dx++) {
			for(int dy=-1; dy<2; dy++) {
				TilePos tp = new TilePos(blank.x + dx, blank.y + dy);
				if( isValidMove(tp) ) {
					out.add(tp);
				}
			}
		}
		return out;
	}
	
	
	public boolean isValidMove(TilePos p) {
		if( ( p.x < 0) || (p.x >= DIMS) ) {
			return false;
		}
		if( ( p.y < 0) || (p.y >= DIMS) ) {
			return false;
		}
		int dx = blank.x - p.x;
		int dy = blank.y - p.y;
		if( (Math.abs(dx) + Math.abs(dy) != 1 ) || (dx*dy != 0) ) {
			return false;
		}
		return true;
	}
	
	
	public void move(TilePos p) {
		if( !isValidMove(p) ) {
			throw new RuntimeException("Invalid move");
		}
		assert tiles[blank.x][blank.y]==0;
		tiles[blank.x][blank.y] = tiles[p.x][p.y];
		tiles[p.x][p.y]=0;
		blank = p;
	}
	
	//tra ve board voi mot move moi 
	public FifteenPuzzle moveClone(TilePos p) {
		FifteenPuzzle out = new FifteenPuzzle(this);
		out.move(p);
		return out;
	}

	
	public void shuffle(int howmany) {
		for(int i=0; i<howmany; i++) {
			List<TilePos> possible = allValidMoves();
			int which =  (int) (Math.random() * possible.size());
			TilePos move = possible.get(which);
			this.move(move);
		}
	}


	
	public int numberMisplacedTiles() {
		int wrong=0;
		for(int i=0; i<DIMS; i++) {
			for(int j=0; j<DIMS; j++) {
				if( (tiles[i][j] >0) && ( tiles[i][j] != SOLVED.tiles[i][j] ) ){
					wrong++;
				}
			}
		}
		return wrong;
	}
	
	
	public boolean isSolved() {
		return numberMisplacedTiles() == 0;
	}
	
	

	public int manhattanDistance() {
		int sum=0;
		for(TilePos p: allTilePos()) {
			int val = tile(p);
			if( val > 0 ) {
				TilePos correct = SOLVED.whereIs(val);
				sum += Math.abs( correct.x = p.x );
				sum += Math.abs( correct.y = p.y );
			}
		}
		return sum;
	}

	// khoang cach uoctinh
	public int estimateError() {
		return this.numberMisplacedTiles();
	}
	
	
	public List<FifteenPuzzle> allAdjacentPuzzles() {
		ArrayList<FifteenPuzzle> out = new ArrayList<FifteenPuzzle>();
		for( TilePos move: allValidMoves() ) {
			out.add( moveClone(move) );
		}
		return out;
	}
	
	
	// tra ve mot danh sach cac board neu co the giai
	public List<FifteenPuzzle> aStarSolve() {
	  	HashMap<FifteenPuzzle,FifteenPuzzle> before = new HashMap<FifteenPuzzle,FifteenPuzzle>();
	  	HashMap<FifteenPuzzle,Integer> depth = new HashMap<FifteenPuzzle,Integer>();
	  	final HashMap<FifteenPuzzle,Integer> closed = new HashMap<FifteenPuzzle,Integer>();
	  	Comparator<FifteenPuzzle> comparator = new Comparator<FifteenPuzzle>() {
	  		@Override
	  		public int compare(FifteenPuzzle a, FifteenPuzzle b) {
	  			return closed.get(a) - closed.get(b);
	  		}
	  	};
	  	PriorityQueue<FifteenPuzzle> toVisit = new PriorityQueue<FifteenPuzzle>(10000,comparator);

	  	before.put(this, null);
	  	depth.put(this,0);
	  	closed.put(this, this.estimateError());
	  	toVisit.add(this);
	  	int cnt=0;
	  	while( toVisit.size() > 0) {
	  		FifteenPuzzle result = toVisit.remove();
	  		cnt++;
	  		if( cnt % 10000 == 0) {
	  			System.out.printf("Dang giai %,d vi tri. Queue = %,d\n", cnt, toVisit.size());
	  		}
	  		if( result.isSolved() ) {
	  			System.out.printf("Da danh gia %d boards\n", cnt);
	  			LinkedList<FifteenPuzzle> solution = new LinkedList<FifteenPuzzle>();
	  			FifteenPuzzle backtrace=result;
	  			while( backtrace != null ) {
	  				solution.addFirst(backtrace);
	  				backtrace = before.get(backtrace);
	  			}
	  			return solution;
	  		}
	  		for(FifteenPuzzle fp: result.allAdjacentPuzzles()) {
	  			if( !before.containsKey(fp) ) {
	  				before.put(fp,result);
	  				depth.put(fp, depth.get(result)+1);
	  				int estimate = fp.estimateError();
					closed.put(fp, depth.get(result)+1 + estimate);
	  				
	  				toVisit.add(fp);
	  			}
	  		}
	  	}
	  	return null;
	}
	
	private static void showSolution(List<FifteenPuzzle> solution) {
		if (solution != null ) {
			System.out.printf("Thanh cong!  Voi %d di chuyen:\n", solution.size());
			for( FifteenPuzzle sp: solution) {
				sp.show();
			}
		} else {
			System.out.println("Khong giai duoc");			
		}
	}
	
	
	public static void main(String[] args) {
		FifteenPuzzle p = new FifteenPuzzle();
		p.shuffle(90);  // Hon 100 thi qua kho 
		System.out.println("Bang da duoc xao tron:");
		p.show();
		
		List<FifteenPuzzle> solution;
		long startTime = System.nanoTime();
		System.out.println("Dang giai");
		solution = p.aStarSolve();
		showSolution(solution);
		long endTime = System.nanoTime();
		long timeEx = endTime - startTime;
		System.out.println(timeEx/1000000);


	}

}
