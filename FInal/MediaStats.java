package FInal;

public class MediaStats {
    private Media[] mediaList;

    // ─── 생성자 ──────────────────────────────────────────────────────────
    public MediaStats(Media[] mediaList) {
        this.mediaList = mediaList;
    }

    // 추천 미디어 수 카운트
    public int countRecommended() {
        int count = 0;
        for (Media m : mediaList) {
            if (m.isRecommended()) count++;
        }
        return count;
    }

    // 타입별 카운트 (instanceof 연산자 활용)
    public void printSummary() {
        int songCount = 0, podcastCount = 0, albumCount = 0;

        for (Media m : mediaList) {
            if      (m instanceof Song)    songCount++;
            else if (m instanceof Podcast) podcastCount++;
            else if (m instanceof Album)   albumCount++;
        }

        System.out.println("  총 등록 미디어: " + Media.getTotalCount() + "개");
        System.out.println("    - 노래(Song):         " + songCount + "개");
        System.out.println("    - 팟캐스트(Podcast):  " + podcastCount + "개");
        System.out.println("    - 앨범(Album):         " + albumCount + "개");
        System.out.println("  추천 미디어 수:          " + countRecommended() + "개");
    }
}
