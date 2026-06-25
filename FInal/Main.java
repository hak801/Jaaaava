package FInal;

public class Main {
    static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║    음악 스트리밍 관리 시스템 v1.0        ║");
        System.out.println("╚══════════════════════════════════════╝\n");

        // ── 다형성: Media 배열에 Song / Podcast / Album 혼합 저장 ──────────
        Media[] playlist = new Media[5];

        playlist[0] = new Song("Blinding Lights", "The Weeknd", 200, "Pop", 4.5);
        playlist[1] = new Song("Dynamite", "BTS", 199, "K-Pop", 4.8);
        playlist[2] = new Podcast("Lex Fridman #400", "Lex Fridman", 5400, "AI & Tech", 12);
        playlist[3] = new Album("Random Access Memories", "Daft Punk", 0, "Electronic", 13);
        playlist[4] = new Podcast("Huberman Lab", "Andrew Huberman", 7200, "Science", 5);

        // ── 다형성: 같은 메서드 호출 → 타입마다 다른 동작 ──────────────────
        System.out.println("┌─────────────────────────────────────────┐");
        System.out.println("│         미디어 목록 (다형성 출력)          │");
        System.out.println("└─────────────────────────────────────────┘");
        for (Media m : playlist) {
            m.displayInfo();
            System.out.println("  추천 여부: " + (m.isRecommended() ? "✓ 추천" : "✗ 비추천"));
            System.out.println();
        }

        // ── 재생 테스트 ──────────────────────────────────────────────────
        System.out.println("┌─────────────────────────────────────────┐");
        System.out.println("│              재생 테스트                  │");
        System.out.println("└─────────────────────────────────────────┘");
        for (Media m : playlist) {
            m.play();
        }

        // ── Song 전용 기능 ───────────────────────────────────────────────
        System.out.println("\n┌─────────────────────────────────────────┐");
        System.out.println("│           Song 전용 기능 테스트             │");
        System.out.println("└─────────────────────────────────────────┘");
        Song song = (Song) playlist[0]; // 다운캐스팅
        song.play();
        song.play();
        song.addToFavorites();
        System.out.println("  현재 평점: " + song.getRating() + " / 5.0");

        // ── Podcast 전용 기능 ────────────────────────────────────────────
        System.out.println("\n┌─────────────────────────────────────────┐");
        System.out.println("│         Podcast 전용 기능 테스트           │");
        System.out.println("└─────────────────────────────────────────┘");
        Podcast pod = (Podcast) playlist[2]; // 다운캐스팅
        pod.setSpeed(1.5);
        pod.play();
        pod.setSpeed(3.0); // 범위 초과 → 오류 메시지 출력

        // ── Album 전용 기능 ──────────────────────────────────────────────
        System.out.println("\n┌─────────────────────────────────────────┐");
        System.out.println("│          Album 전용 기능 테스트            │");
        System.out.println("└─────────────────────────────────────────┘");
        Album album = (Album) playlist[3]; // 다운캐스팅
        album.addTrack("Give Life Back to Music");
        album.addTrack("Get Lucky");
        album.addTrack("Instant Crush");
        album.displayTracklist();
        album.printDetails(); // Printable 인터페이스 메서드

        // ── 통계 요약 ────────────────────────────────────────────────────
        System.out.println("\n┌─────────────────────────────────────────┐");
        System.out.println("│              통계 요약                    │");
        System.out.println("└─────────────────────────────────────────┘");
        MediaStats stats = new MediaStats(playlist);
        stats.printSummary();
    }
}
