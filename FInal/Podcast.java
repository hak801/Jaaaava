package FInal;

public class Podcast extends Media {
    private String category;
    private int    episodeNumber;
    private double playbackSpeed; // 재생 배속 (0.5x ~ 2.0x)

    // ─── 생성자 오버로딩 (Constructor Overloading) ───────────────────────
    // 에피소드 번호를 생략할 수 있는 버전 → this()로 다른 생성자에 위임
    public Podcast(String title, String host, int durationSeconds, String category) {
        this(title, host, durationSeconds, category, 1);
    }

    // 에피소드 번호를 명시하는 버전 (메인 생성자)
    public Podcast(String title, String host, int durationSeconds, String category, int episodeNumber) {
        super(title, host, durationSeconds, category);
        this.category      = category;
        this.episodeNumber = episodeNumber;
        this.playbackSpeed = 1.0;
    }

    // ─── 메서드 오버라이딩 ────────────────────────────────────────────────
    @Override
    public void displayInfo() {
        System.out.println("[🎙 Podcast] " + getTitle() + " | 호스트: " + getArtist());
        System.out.println("           카테고리: " + category
                + " | 재생시간: " + formatDuration(getDurationSeconds())
                + " | Ep." + episodeNumber
                + " | 배속: " + playbackSpeed + "x");
    }

    @Override
    public void play() {
        double adjustedSec = getDurationSeconds() / playbackSpeed;
        System.out.println("  ▶ 팟캐스트 재생: \"" + getTitle()
                + "\"  (배속 " + playbackSpeed + "x → 실제 소요: "
                + formatDuration((int) adjustedSec) + ")");
    }

    @Override
    public boolean isRecommended() {
        return getDurationSeconds() > 3600; // 1시간 초과 에피소드만 추천
    }

    // ─── Podcast 전용 메서드 ─────────────────────────────────────────────
    public void setSpeed(double speed) {
        if (speed >= 0.5 && speed <= 2.0) {
            this.playbackSpeed = speed;
            System.out.println("  ⚙ 재생 배속 변경 → " + speed + "x");
        } else {
            System.out.println("  ✗ 배속 범위 오류 (0.5x ~ 2.0x 사이만 허용)");
        }
    }

    public double getPlaybackSpeed() {
        return playbackSpeed; }

    public int    getEpisodeNumber() {
        return episodeNumber; }
}
