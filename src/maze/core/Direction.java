package maze.core;


public enum Direction {
	N {
		@Override
		public MazeCell successor(MazeCell location) {
			return new MazeCell(location.X(), location.Y() - 1);
		}
	}, 
	S {
		@Override
		public MazeCell successor(MazeCell location) {
			return new MazeCell(location.X(), location.Y() + 1);
		}
	}, 
	E {
		@Override
		public MazeCell successor(MazeCell location) {
			return new MazeCell(location.X() + 1, location.Y());
		}
	}, 
	W {
		@Override
		public MazeCell successor(MazeCell location) {
			return new MazeCell(location.X() - 1, location.Y());
		}
	};
	
	abstract public MazeCell successor(MazeCell location);
	
	public static Direction between(MazeCell start, MazeCell end) {
		int xDiff = start.X() - end.X();
		int yDiff = start.Y() - end.Y();
		if (Math.abs(xDiff) > Math.abs(yDiff)) {
			return xDiff > 0 ? W : E;
		} else {
			return yDiff > 0 ? N : S;
		}
	}
}
