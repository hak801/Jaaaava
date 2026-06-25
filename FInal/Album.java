package FInal;

import java.util.ArrayList;

public class Album extends Media implements Printable {
    private int               trackCount; // 앨범 전체 트랙 수
    private ArrayList<String> tracklist;  // 실제 등록된 트랙 목록

    // ─── 생성자 ──────────────────────────────────────────────────────────
    public Album(String title, String artist, int durationSeconds, String genre, int trackCount) {
        super(title, artist, durationSeconds, genre);
        this.trackCount = trackCount;
        this.tracklist  = new ArrayList<>();
    }

    // ─── 메서드 오버라이딩 ────────────────────────────────────────────────
    @Override
    public void displayInfo() {
        System.out.println("[💿 Album]  " + getTitle() + " - " + getArtist());
        System.out.println("           장르: " + getGenre()
                + " | 총 트랙: " + trackCount + "곡"
                + " | 등록된 트랙: " + tracklist.size() + "개");
    }

    @Override
    public void play() {
        if (tracklist.isEmpty()) {
            System.out.println("  ▶ 앨범 \"" + getTitle() + "\" 셔플 재생 (트랙 목록 미등록)");
        } else {
            System.out.println("  ▶ 앨범 \"" + getTitle() + "\" 순서 재생: \""
                    + tracklist.get(0) + "\" 외 " + (tracklist.size() - 1) + "곡");
        }
    }

    @Override
    public boolean isRecommended() {
        return trackCount >= 10; // 10곡 이상 앨범만 추천
    }

    // ─── Printable 인터페이스 메서드 구현 ────────────────────────────────
    @Override
    public void printDetails() {
        System.out.println("  [상세] 제목: " + getTitle()
                + " | 아티스트: " + getArtist()
                + " | 장르: " + getGenre()
                + " | 총 트랙: " + trackCount);
    }

    // ─── Album 전용 메서드 ───────────────────────────────────────────────
    public void addTrack(String trackName) {
        tracklist.add(trackName);
        System.out.println("  + 트랙 추가  [" + tracklist.size() + "] " + trackName);
    }

    public void displayTracklist() {
        System.out.println("  ── 트랙 목록: " + getTitle() + " ──");
        if (tracklist.isEmpty()) {
            System.out.println("  (등록된 트랙 없음)");
            return;
        }
        for (int i = 0; i < tracklist.size(); i++) {
            System.out.println("    " + (i + 1) + ". " + tracklist.get(i));
        }
    }

    public int getTrackCount() { return trackCount; }
}
