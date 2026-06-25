package FInal;

public class Song extends Media {
    private double rating;
    private boolean favorite;
    private int playCount;

    public Song(String title, String artist, int durationSeconds, String genre, double rating) {
        super(title, artist, durationSeconds, genre);
        this.rating = Math.min(5.0, Math.max(0.0, rating));
        this.favorite = false;
        this.playCount = 0;
    }

    @Override
    public void displayInfo() {
        System.out.println("[♪ Song]    " + getTitle() + " - " + getArtist());
        System.out.println("           장르: " + getGenre()
                + " | 재생시간: " + formatDuration(getDurationSeconds())
                + " | 평점: " + rating + "/5.0"
                + " | 즐겨찾기: " + (favorite ? "★" : "☆"));
    }

    @Override
    public void play() {
        playCount++;
        System.out.println("  ▶ 노래 재생: \"" + getTitle() + "\" by " + getArtist()
                + "  [누적 재생: " + playCount + "회]");
    }

    @Override
    public boolean isRecommended() {
        return rating >= 4.0; // 평점 4.0 이상이면 추천
    }

    // ─── Song 전용 메서드 ────────────────────────────────────────────────
    public void addToFavorites() {
        this.favorite = true;
        System.out.println("  ★ \"" + getTitle() + "\" 이(가) 즐겨찾기에 추가되었습니다.");
    }

    public double getRating()   { return rating; }
    public int    getPlayCount(){ return playCount; }
}