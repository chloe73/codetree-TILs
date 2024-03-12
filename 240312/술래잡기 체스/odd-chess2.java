import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.TreeMap;

public class Main {

	static int result;
	// 					↑, ↖, ←, ↙, ↓, ↘, →, ↗
	static int[] dx = {0,-1,-1,0,1,1,1,0,-1};
	static int[] dy = {0,0,-1,-1,-1,0,1,1,1};
	static class Horse {
		int x,y,p,d;
		public Horse(int x, int y, int p, int d) {
			this.x = x;
			this.y = y;
			this.p = p;
			this.d = d;
		}
	}

	public static void main(String[] args) throws IOException{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		int[][] board = new int[4][4];
		TreeMap<Integer, Horse> horseMap = new TreeMap<>();
		
		for(int i=0;i<4;i++) {
			StringTokenizer st = new StringTokenizer(br.readLine());
			for(int j=0;j<4;j++) {
				int p = Integer.parseInt(st.nextToken());
				int d = Integer.parseInt(st.nextToken());
				
				horseMap.put(p, new Horse(i, j, p, d));
				board[i][j] = p;
			}
		} // input end
		
		solve(0,0,0,board,horseMap,result);
		
		System.out.println(result);
	}
	
	private static void solve(int tx, int ty, int td, int[][] b, TreeMap<Integer, Horse> h, int score) {
		
		int[][] board = new int[4][4];
		for(int i=0;i<4;i++) {
			board[i] = Arrays.copyOf(b[i], 4);
		}
		
		TreeMap<Integer, Horse> horseMap = new TreeMap<>();
		for(int i : h.keySet()) {
			Horse horse = h.get(i);
			horseMap.put(i, new Horse(horse.x, horse.y, horse.p, horse.d));
		}
		
		int num = board[tx][ty];
		td = horseMap.get(num).d;
		horseMap.remove(num);
		score += num;
		board[tx][ty] = -1;
		
		if(result < score) result = score;

		for(int i : horseMap.keySet()) {
			Horse temp = horseMap.get(i);
			
			int nx = temp.x;
			int ny = temp.y;
			int nd = temp.d;
			
			boolean flag = false;
			for(int k=0;k<8;k++) {
				nx = temp.x + dx[nd];
				ny = temp.y + dy[nd];
				
				if(isValid(nx, ny) && board[nx][ny] != -1) {
					flag = true;
					break;
				}
				
				nd = changeDir(nd);
			}
			
			if(flag) {
				// 이동 가능한 경우 현재 도둑말의 위치와 방향 갱신해준다.
				int tmp = board[nx][ny];
				board[nx][ny] = i;
				board[temp.x][temp.y] = tmp;
				
				if(tmp > 0) {					
					horseMap.get(tmp).x = temp.x;
					horseMap.get(tmp).y = temp.y;
				}
				
				horseMap.get(i).x = nx;
				horseMap.get(i).y = ny;
				horseMap.get(i).d = nd;
			}
		}
		
		// 술래말 이동
		for(int i=1;i<=3;i++) {
			int nx = tx + dx[td]*i;
			int ny = ty + dy[td]*i;
			
			if(!isValid(nx, ny)) break;
			
			if(isValid(nx, ny) && board[nx][ny] > 0) {
				solve(nx, ny, td, board, horseMap, score);
			}
		}
		
	}
	
	private static int changeDir(int d) {
		if(d == 8) return 1;
		else return d+1;
	}

	private static boolean isValid(int r, int c) {
		if(r<0 || c<0 || r>=4 || c>=4) return false;
		return true;
	}

}