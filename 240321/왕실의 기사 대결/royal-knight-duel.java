import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Stack;
import java.util.StringTokenizer;

public class Main {
	
	static int L,N,Q,result;
	static int[][] board,kBoard;
//	d는 0, 1, 2, 3 중에 하나이며 각각 위쪽, 오른쪽, 아래쪽, 왼쪽 방향을 의미합니다.
	static int[] dx = {-1,0,1,0};
	static int[] dy = {0,1,0,-1};
	static HashMap<Integer, Knight> kMap;
	static class Knight {
		int x,y,h,w,k;
		int damage;
		boolean isAlive;
		public Knight(int x, int y, int h, int w, int k) {
			this.x = x;
			this.y = y;
			this.h = h;
			this.w = w;
			this.k = k;
			isAlive = true;
			damage = 0;
		}
	}

	public static void main(String[] args) throws IOException{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		StringTokenizer st = new StringTokenizer(br.readLine());
		L = Integer.parseInt(st.nextToken());
		N = Integer.parseInt(st.nextToken());
		Q = Integer.parseInt(st.nextToken());

		board = new int[L][L];
		for(int i=0;i<L;i++) {
			st = new StringTokenizer(br.readLine());
			for(int j=0;j<L;j++) {
				board[i][j] = Integer.parseInt(st.nextToken());
			}
		}
		
		kMap = new HashMap<>();
		kBoard = new int[L][L];
		for(int i=1;i<=N;i++) {
			st = new StringTokenizer(br.readLine());
			int x = Integer.parseInt(st.nextToken())-1;
			int y = Integer.parseInt(st.nextToken())-1;
			int h = Integer.parseInt(st.nextToken());
			int w = Integer.parseInt(st.nextToken());
			int k = Integer.parseInt(st.nextToken());
			
			kMap.put(i, new Knight(x, y, h, w, k));
			for(int a=0;a<h;a++) {
				for(int b=0;b<w;b++) {
					kBoard[x+a][y+b] = i;
				}
			}
		}
		
		for(int turn=0;turn<Q;turn++) {
			st = new StringTokenizer(br.readLine());
			int i = Integer.parseInt(st.nextToken());
			int d = Integer.parseInt(st.nextToken());
			
			solve(i,d);
		} // input end
		
//		Q 개의 명령이 진행된 이후, 생존한 기사들이 총 받은 대미지의 합을 출력합니다.
		for(int i=1;i<=N;i++) {
			if(!kMap.get(i).isAlive) continue;
			result += kMap.get(i).damage;
		}
		
		System.out.println(result);
	}

	private static void solve(int idx, int d) {
		Knight temp = kMap.get(idx);
		
//		또, 체스판에서 사라진 기사에게 명령을 내리면 아무런 반응이 없게 됩니다.
		if(!temp.isAlive) return;
		
		boolean flag = true;
		int[][] ret = isPossible(idx, d);
		
		if(ret[0][0] == -1)
			flag = false;
		
		// 밀릴 수 없는 경우 아무 일도 안 일어난다.
		if(!flag) return;
		
		kBoard = ret;
	}
	
	private static int[][] isPossible(int idx, int d) {
		boolean flag = true;
		Knight temp = kMap.get(idx);
		
		int tx = temp.x;
		int ty = temp.y;
		
		int[][] renewal = new int[L][L];
		Stack<Integer> stack = new Stack<>();
		stack.push(idx);
		
		if(d == 0) {
			// 위로 이동
			tx = temp.x;
			ty = temp.y;
			
			for(int j=0;j<temp.w;j++) {
				if(!isValid(tx-1, ty+j) || board[tx-1][ty+j] == 2) {
					flag = false;
					break;
				}
				// 다른 기사가 있는 경우 연쇄적으로 밀릴 수 있는지 확인
				if(kBoard[tx-1][ty+j] > 0) {
					if(isPossible(kBoard[tx-1][ty+j], d)[0][0] == -1) {
						flag = false;
						break;
					}
					else {
						stack.push(kBoard[tx-1][ty+j]);
					}
				}
			}
		}
		else if(d == 1) {
			// 밀릴 수 있는지 체크 (범위 밖으로 이동하거나, 벽이거나, 연쇄적으로 기사들이 모두 밀리지 못하는 경우)
			flag = true;
			// 오른쪽 이동
			tx = temp.x;
			ty = temp.y + temp.w -1;
			
			// 우선, 가장 끝쪽 열 한칸 이동시켜서 확인 ( 빈칸인지, 벽인지, 다른 기사가 있는지 )
			for(int i=0;i<temp.h;i++) {
				// 행 : 증가하는 방향으로 이동
				// 범위 밖이거나, 벽인 경우 이동 불가
				if(!isValid(tx+i, ty+1) || board[tx+i][ty+1] == 2) {
					flag = false;
					break;
				}
				
				// 다른 기사가 있는 경우 연쇄적으로 밀릴 수 있는지 확인
				if(kBoard[tx+i][ty+1] > 0) {
					if(isPossible(kBoard[tx+i][ty+1], d)[0][0] == -1) {
						flag = false;
						break;
					}
					else {
						stack.push(kBoard[tx+i][ty+1]);
					}
				}
			}
		}
		else if(d == 2) {
			// 아래로 이동
			tx = temp.x + temp.h -1;
			ty = temp.y;
			
			for(int j=0;j<temp.w;j++) {
				if(!isValid(tx+1, ty+j) || board[tx+1][ty+j] == 2) {
					flag = false;
					break;
				}
				
				// 다른 기사가 있는 경우 연쇄적으로 밀릴 수 있는지 확인
				if(kBoard[tx+1][ty+j] > 0) {
					if(isPossible(kBoard[tx+1][ty+j], d)[0][0] == -1) {
						flag = false;
						break;
					}
					else {
						stack.push(kBoard[tx+1][ty+j]);
					}
				}
			}
		}
		else {
			// 왼쪽으로 이동
			tx = temp.x;
			ty = temp.y;
			
			for(int i=0;i<temp.h;i++) {
				if(!isValid(tx+i, ty-1) || board[tx+i][ty-1] == 2) {
					flag = false;
					break;
				}
				
				// 다른 기사가 있는 경우 연쇄적으로 밀릴 수 있는지 확인
				if(kBoard[tx+i][ty-1] > 0) {
					if(isPossible(kBoard[tx+i][ty-1], d)[0][0] == -1) {
						flag = false;
						break;
					}
					else {
						stack.push(kBoard[tx+i][ty-1]);
					}
				}
			}
		}
		
		if(!flag) {
			renewal[0][0] = -1;
		}
		
		if(flag && stack.size() > 1) {
			while(!stack.isEmpty()) {
				int tmp = stack.pop();
				// 각 기사들 한 칸씩 밀려남
				moveKnight(tmp, d, renewal, idx);
			}
		}
		
		return renewal;
	}
	
	private static void moveKnight(int idx, int d, int[][] arr, int startIdx){

		int[] point = getStartPoint(idx, d);
		int tx = point[0];
		int ty = point[1];
		Knight temp = kMap.get(idx);
		int damage = 0;
		temp.x = temp.x + dx[d];
		temp.y = temp.y + dy[d];
		
		if(d == 0) {
			for(int i=0;i<temp.h;i++) {
				for(int j=0;j<temp.w;j++) {
					arr[tx+i-1][ty+j] = idx;
					if(idx != startIdx && board[tx+i-1][ty+j] == 1)
						damage++;
				}
			}
		}
		else if(d == 1) {
			for(int j=0;j<temp.w;j++) {
				for(int i=0;i<temp.h;i++) {
					arr[tx+i][ty-j+1] = idx;
					if(idx != startIdx && board[tx+i][ty-j+1] == 1)
						damage++;
				}
			}
		}
		else if(d == 2) {
			for(int i=0;i<temp.h;i++) {
				for(int j=0;j<temp.w;j++) {
					arr[tx-i-1][ty+j] = idx;
					if(idx != startIdx && board[tx-i-1][ty+j] == 1)
						damage++;
				}
			}
		}
		else {
			for(int j=0;j<temp.w;j++) {
				for(int i=0;i<temp.h;i++) {
					arr[tx+i][ty+j-1] = idx;
					if(idx != startIdx && board[tx+i][ty+j-1] == 1) 
						damage++;
				}
			}
		}
		
		if(kMap.get(idx).k - damage <= 0) {
			kMap.get(idx).isAlive = true;
			kMap.get(idx).k = 0;
		}
		else {
			kMap.get(idx).k -= damage;
			kMap.get(idx).damage += damage;
		}
	}
	
	private static int[] getStartPoint(int idx, int d) {
		int[] ret = new int[2];
		Knight temp = kMap.get(idx);
		
		if(d == 0) {
			ret[0] = temp.x;
			ret[1] = temp.y;
		}
		else if(d == 1) {
			ret[0] = temp.x;
			ret[1] = temp.y + temp.w -1;
		}
		else if(d == 2) {
			ret[0] = temp.x + temp.h -1;
			ret[1] = temp.y;
		}
		else {
			ret[0] = temp.x;
			ret[1] = temp.y;
		}
		
		return ret;
	}

	private static boolean isValid(int r, int c) {
		if(r<0 || c<0 || r>=L || c>=L) return false;
		return true;
	}
}