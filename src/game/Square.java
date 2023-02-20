package game;
public class Square {

    /** The file of the {@link Square}. */
    private int file;

    /** The rank of the {@link Square}. */
    private int rank;

    /**
     * Creates a new {@link Square} object.
     * 
     * @param file The file (column).
     * @param rank The rank (row).
     */
    public Square(int file, int rank) {

        this.file = file;
        this.rank = rank;

    }

    /**
     * Checks if square is a valid board position (within bounds).
     * 
     * @return If square is a valid board square.
     */
    public boolean isValid() {

        return file > 0 && file <= 8 && rank > 0 && rank <= 8;

    }

    /**
     * A string representation of the square, with the file as a lowercase
     * letter.<br>
     * <br>
     * Ex: "h4"
     */
    public String toString() {

        return "" + (char) (96 + file) + rank;

    }

    /**
     * Compares two Square objects.
     * 
     * @param compare The square to compare to.
     * @return Whether or not the file and rank match.
     */
    public boolean equals(Square compare) {

        return (file == compare.getFile()) && (rank == compare.getRank());

    }

    /**
     * @return The file (column) of the square.
     */
    public int getFile() {
        return file;
    }

    /**
     * Sets the file (column) of the square.
     * 
     * @param file The numeric file to set the square to.
     */
    public void setFile(int file) {
        this.file = file;
    }

    /**
     * @return The rank (row) of the square.
     */
    public int getRank() {
        return rank;
    }

    /**
     * Sets the rank (row) of the square.
     * 
     * @param file The numeric rank to set the square to.
     */
    public void setRank(int rank) {
        this.rank = rank;
    }

}
