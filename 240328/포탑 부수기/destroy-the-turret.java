import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.StringTokenizer;

public class Main {
	
	static int N,M,K;
	static Top[][] board;
	static boolean[][] visited; // 해당 턴에 공격자 및 공격받은 포탑 체크
	// 우/하/좌/상의 우선순위대로 먼저 움직인 경로가 선택됩니다.
	static int[] dx = {0,1,0,-1};
	static int[] dy = {1,0,-1,0};
	static PriorityQueue<Top> aPq; // 오름차순
//	static PriorityQueue<Top> dPq; // 내림차순
	static class Top implements Comparable<Top>{
		int x,y,p,latest,xySum;
		
		public Top(int x, int y, int p, int latest) {
			this.x = x;
			this.y = y;
			this.p = p;
			this.latest = latest;
			this.xySum = this.x + this.y;
		}
		
//		가장 약한 포탑은 다음의 기준으로 선정됩니다.
		public int compareTo(Top o) {
			if(this.p == o.p) {
				if(this.latest == o.latest) {
					if(o.xySum == this.xySum)
//						만약 그러한 포탑이 2개 이상이라면, 각 포탑 위치의 열 값이 가장 큰 포탑이 가장 약한 포탑입니다.
						return o.y - this.y;
//					만약 그러한 포탑이 2개 이상이라면, 각 포탑 위치의 행과 열의 합이 가장 큰 포탑이 가장 약한 포탑입니다.
					return o.xySum - this.xySum;
				}
//				만약 공격력이 가장 낮은 포탑이 2개 이상이라면, 가장 최근에 공격한 포탑이 가장 약한 포탑입니다. (모든 포탑은 시점 0에 모두 공격한 경험이 있다고 가정하겠습니다.)
				return this.latest - o.latest;
			}
//			공격력이 가장 낮은 포탑이 가장 약한 포탑입니다.
			return this.p - o.p;
		}
	}

	public static void main(String[] args) throws IOException{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		
		N = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());
		K = Integer.parseInt(st.nextToken());

		board = new Top[N][M];
		aPq = new PriorityQueue<>();
//		dPq = new PriorityQueue<>(Collections.reverseOrder());
		
		for(int i=0;i<N;i++) {
			st = new StringTokenizer(br.readLine());
			for(int j=0;j<M;j++) {
				int p = Integer.parseInt(st.nextToken());
				board[i][j] = new Top(i, j, p, 0);
				if(p > 0) {
					aPq.add(board[i][j]);
				}
			}
		} // input end
		
		solve();
		
		// 첫 번째 줄에 K번의 턴이 종료된 후 남아있는 포탑 중 가장 강한 포탑의 공격력을 출력합니다.
		int result = 0;
		while(!aPq.isEmpty()) {
			result = aPq.poll().p;
		}
		System.out.println(result);
	}

	private static void solve() {
		
		while(K-- > 0) {
			visited = new boolean[N][M];
			
			
			// 1. 공격자 선정
			// 부서지지 않은 포탑 중 가장 약한 포탑이 공격자로 선정됩니다. 
			Top attacker = aPq.poll();
			visited[attacker.x][attacker.y] = true;
			// 공격자로 선정되면 가장 약한 포탑이므로, 핸디캡이 적용되어 N+M만큼의 공격력이 증가됩니다.
			attacker.p += (N+M);
			
			// 2. 공격자의 공격
			// 위에서 선정된 공격자는 자신을 제외한 가장 강한 포탑을 공격합니다.
			Top underAttack = null;
			while(!aPq.isEmpty()) {
				underAttack = aPq.poll();
			}
			visited[underAttack.x][underAttack.y] = true;
			attack(attacker, underAttack);

			// 4. 포탑 정비
			// 공격과 무관하다는 뜻은 공격자도 아니고, 공격에 피해를 입은 포탑도 아니라는 뜻입니다.
			aPq.clear();
			for(int i=0;i<N;i++) {
				for(int j=0;j<M;j++) {
					// 공격이 끝났으면, 부서지지 않은 포탑 중 공격과 무관했던 포탑은 공격력이 1씩 올라갑니다. 
					if(!visited[i][j] && board[i][j].p > 0) {
						board[i][j].p += 1;
					}
					if(board[i][j].p > 0) {
						board[i][j].latest++;
						aPq.add(board[i][j]);
					}
				}
			}
			attacker.latest = 0;
			
			// 만약 부서지지 않은 포탑이 1개가 된다면 그 즉시 중지됩니다.
			if(aPq.size() == 1) break;
		}
	}
	
	private static void attack(Top attacker, Top underAttack) {
		// 공격을 할 때에는 레이저 공격을 먼저 시도하고, 만약 그게 안 된다면 포탄 공격을 합니다.
		if(!lazer_attack(attacker, underAttack)) {
			bomb_attack(attacker, underAttack);
		}
		
		// if(!isPossible_lazer_attack(attacker, underAttack))
		// 	bomb_attack(attacker, underAttack);
	}

	private static void bomb_attack(Top attacker, Top underAttack) {
		// 공격 대상에 포탄을 던집니다. 공격 대상은 공격자 공격력 만큼의 피해를 받습니다. 
		// 추가적으로 주위 8개의 방향에 있는 포탑도 피해를 입는데, 공격자 공격력의 절반 만큼의 피해를 받습니다. 
		// (절반이라 함은 공격력을 2로 나눈 몫을 의미합니다.) 공격자는 해당 공격에 영향을 받지 않습니다. 
		// 만약 가장자리에 포탄이 떨어졌다면, 위에서의 레이저 이동처럼 포탄의 추가 피해가 반대편 격자에 미치게 됩니다.
		int[] ddx = {-1,1,0,0,-1,-1,1,1};
		int[] ddy = {0,0,-1,1,1,-1,1,-1};
		
		for(int d=0;d<8;d++) {
			int nx = underAttack.x + ddx[d];
			int ny = underAttack.y + ddy[d];
			
			int[] point = checkRange(nx, ny);
			
			if(point[0] == -1 && point[1] == -1) continue;
			
			nx = point[0];
			ny = point[1];
			
			if(nx == attacker.x && ny == attacker.y) continue;
			
			if(board[nx][ny].p > 0) {
				visited[nx][ny] = true;
				board[nx][ny].p -= attacker.p / 2;
				if(board[nx][ny].p < 0)
					board[nx][ny].p = 0;
			}
		}
		
		visited[underAttack.x][underAttack.y] = true;
		board[underAttack.x][underAttack.y].p -= attacker.p;
		if(board[underAttack.x][underAttack.y].p < 0)
			board[underAttack.x][underAttack.y].p = 0;
	}

	static class Route {
		int x,y,dist;
		String dir;
		public Route(int x, int y, int dist) {
			this.x = x;
			this.y = y;
			this.dist = dist;
			this.dir = "";
		}
	}
	
	private static boolean lazer_attack(Top attacker, Top underAttack) {
		// (1) 레이저 공격
		// 상하좌우의 4개의 방향으로 움직일 수 있습니다.
		// 부서진 포탑이 있는 위치는 지날 수 없습니다.
		// 가장자리에서 막힌 방향으로 진행하고자 한다면, 반대편으로 나옵니다. (예를 들어, 위의 예시에서 (2,3)에서 오른쪽으로 두번 이동한다면, (2,3) -> (2,4) -> (2,1) 순으로 이동합니다.)
		// 레이저 공격은 공격자의 위치에서 공격 대상 포탑까지의 최단 경로로 공격합니다. 만약 그러한 경로가 존재하지 않는다면 (2) 포탄 공격을 진행합니다. 만약 경로의 길이가 똑같은 최단 경로가 2개 이상이라면, 우/하/좌/상의 우선순위대로 먼저 움직인 경로가 선택됩니다.
		
		// 최단 경로가 정해졌으면, 공격 대상에는 공격자의 공격력 만큼의 피해를 입히며, 피해를 입은 포탑은 해당 수치만큼 공격력이 줄어듭니다. 또한 공격 대상을 제외한 레이저 경로에 있는 포탑도 공격을 받게 되는데, 이 포탑은 공격자 공격력의 절반 만큼의 공격을 받습니다. (절반이라 함은 공격력을 2로 나눈 몫을 의미합니다.)
		Queue<Route> q = new LinkedList<>();
		boolean[][] visit = new boolean[N][M];
		q.add(new Route(attacker.x, attacker.y, 0));
		visit[attacker.x][attacker.y] = true;
		int dist = Integer.MAX_VALUE;
		boolean flag = false;
		ArrayList<Integer> routeList = new ArrayList<>();
		
		while(!q.isEmpty()) {
			Route temp = q.poll();
			
			if(temp.x == underAttack.x && temp.y == underAttack.y) {
				if(dist > temp.dist) {
					dist = temp.dist;
					routeList = new ArrayList<>();
					for(int i=0;i<temp.dir.length();i++) {
						routeList.add(temp.dir.charAt(i)-'0');
					}
					flag = true;

				}
			}
			
			for(int d=0;d<4;d++) {
				int nx = temp.x + dx[d];
				int ny = temp.y + dy[d];
				
				int[] point = checkRange(nx, ny);
				
				if(point[0] == -1 && point[1] == -1) continue;
				
				nx = point[0];
				ny = point[1];
				
				if(visit[nx][ny] || board[nx][ny].p == 0) continue;
				
				visit[nx][ny] = true;
				Route next = new Route(nx, ny, temp.dist+1);
				next.dir = temp.dir + ""+d;
				q.add(next);
			}
		}
		
		if(!flag) return false;
		
		if(flag) {
			int nx = attacker.x;
			int ny = attacker.y;
			
			for(int i=0;i<routeList.size();i++) {
				int d = routeList.get(i);
				nx += dx[d];
				ny += dy[d];
				
				int[] point = checkRange(nx, ny);
				
				nx = point[0];
				ny = point[1];
//				visited[nx][ny] = true;
				
				if(nx == underAttack.x && ny == underAttack.y) {
					board[nx][ny].p -= attacker.p;
				}
				else {
					board[nx][ny].p -= attacker.p / 2;						
				}
				
				if(board[nx][ny].p < 0)
					board[nx][ny].p = 0;
			}
		}
		
		return flag;
	}

	private static int[] checkRange(int r, int c) {
		int[] ret = new int[2];
		ret[0] = r;
		ret[1] = c;
		
		// invalid 영역
		if( (r<0 && c<0) || (r>=N && c>=M) || (r<0 && c>=M) || (r>=N && c<0)) {
			ret[0] = -1;
			ret[1] = -1;
		}
		else if(r<0) {
			ret[0] = N-1;
			ret[1] = c;
		}
		else if(c<0) {
			ret[0] = r;
			ret[1] = M-1;
		}
		else if(r>=N) {
			ret[0] = 0;
			ret[1] = c;
		}
		else if(c>=M) {
			ret[0] = r;
			ret[1] = 0;
		}
		return ret;
	}
}