import helper.Stone;

/**
 * Created by mark.banierink on 16-1-2017.
 */
public class Player {

    private Stone stone;
    private String name;
    private int boardsize;
    private Game game;

    Player(String name, int boardsize) {
        this.name = name;
        this.boardsize = boardsize;
    }

    public String getName() {
        return this.name;
    }

    public int getBoardsize() {
        return this.boardsize;
    }

    public Stone getStone() {
        return this.stone;
    }

    public Game getGame() {
        return this.game;
    }

    public Player getOpponent() {
        Player opponent = null;
        for (Player player : getGame().getPlayers()) {
            if (!this.equals(player)) {
                opponent = player;
            }
        }
        return opponent;
    }

    public boolean hasGame() {
        return (this.getGame() != null);
    }

    public void setBoardsize(int boardsize) {
        this.boardsize = boardsize;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void setStone(Stone stone) {
        this.stone = stone;
    }

}
