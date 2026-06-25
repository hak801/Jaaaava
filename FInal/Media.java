package FInal;

public abstract class Media {
    private String title;
    private String artist;
    private int durationSeconds;
    private String genre;
    private static int totalCount = 0;

    public Media(String title, String artist, int durationSeconds, String genre) {
        this.title = title;
        this.artist = artist;
        this.durationSeconds = durationSeconds;
        this.genre = genre;
        totalCount++;
    }

    public String getTitle() {
        return title; }

    public String getArtist() {
        return artist;}

    public int getDurationSeconds() {
        return durationSeconds;
    }

    public String getGenre() {
        return genre;
    }

    public static int getTotalCount() {
        return totalCount;
    }

    public void setDurationSeconds(int sec) {
        if (sec >= 0) this.durationSeconds = sec;
    }

    protected String formatDuration(int seconds) {
        if (seconds == 0) return "N/A";
        int min = seconds / 60;
        int sec = seconds % 60;
        return min + "분" + String.format("%02d", sec) + "초";
    }

    public abstract void displayInfo();
    public abstract void play();
    public abstract boolean isRecommended();
}
