[![Presentation](https://img.shields.io/badge/Presentation-View_Slide-E67E22?style=for-the-badge&logo=google-chrome)](https://sanghyeonj.github.io/movieWorld/)

# Movie World

ユーザーのレビューを基に**Google Gemini AI**が好みを分析し、**TMDB**の映画データと連携してパーソナライズ推薦を提供するWebアプリケーションです。  
Spring Bootで構築した学習・ポートフォリオ用プロジェクトです。

---

## 技術スタック

| 分類             | 技術                                                                  |
| ---------------- | --------------------------------------------------------------------- |
| 言語・ランタイム | Java 17                                                               |
| フレームワーク   | Spring Boot 3.5.10                                                    |
| セキュリティ     | Spring Security（フォームログイン、BCrypt）                           |
| ORM              | MyBatis 3.0.5                                                         |
| DB               | MySQL 8.0                                                             |
| テンプレート     | Thymeleaf、thymeleaf-layout-dialect、thymeleaf-extras-springsecurity6 |
| フロント         | Bootstrap 5、JavaScript（AJAX）                                       |
| ビルド           | Gradle                                                                |
| 外部API          | TMDB API（映画データ）、Google Gemini API（AI推薦）                   |

---

## 主な機能

- **映画検索・一覧**  
  TMDB API連携。人気順・最新順・評点順ソート、タイトル検索、映画詳細ページ。

- **レビュー**  
  映画ごとに1〜5点の評価とコメント。1ユーザー1映画あたり1件（編集・削除可）。

- **AI映画推薦**  
  レビュー5件以上で利用可能。Geminiが好みを分析し、推薦5本を理由付きで表示。日30回・分1回のレート制限、結果キャッシュオプションあり。

- **掲示板**  
  投稿の作成・編集・削除、コメント、検索、ページング。

- **多言語対応**  
  韓国語／日本語。セッションで言語切替。MessageSourceによる国際化。TMDBの映画情報・AI推薦理由も選択言語に連動。

- **管理者機能**  
  会員・映画・レビュー・掲示板・推薦件数の統計ダッシュボード、映画同期ログ一覧、手動同期（AJAX＋プログレスモーダル）、Spring Schedulerによる自動同期（毎日2時・3時・4時）。

---

## データベース設計

MySQL（データベース名: `movieworlddb`）で、以下の12テーブルを設計・使用しています。

| テーブル名                | 説明                                                                   |
| ------------------------- | ---------------------------------------------------------------------- |
| **roles**                 | 権限（ROLE_USER, ROLE_ADMIN）                                          |
| **users**                 | 会員（メール・BCryptパスワード・名前・role_id）                        |
| **genres**                | ジャンル（TMDB連携用）                                                 |
| **movies**                | 映画情報（TMDB同期で投入）                                             |
| **movie_genres**          | 映画とジャンルの多対多対応                                             |
| **reviews**               | レビュー（user_id, movie_id, rating 1〜5, content）。1ユーザー1映画1件 |
| **boards**                | 掲示板投稿                                                             |
| **board_comments**        | 掲示板コメント                                                         |
| **recommendations**       | AI推薦結果キャッシュ（ユーザーあたり有効1件）                          |
| **recommendation_items**  | 推薦内の映画（順位・推薦理由）                                         |
| **recommend_rate_limits** | 推薦リクエストの日別回数制限                                           |
| **movie_sync_log**        | TMDB同期実行ログ（BULK / NOW_PLAYING / UPCOMING）                      |

---

## 画面・主要URL

| パス           | 説明                                        |
| -------------- | ------------------------------------------- |
| `/`            | ホーム（人気・最新映画）                    |
| `/movies`      | 映画一覧・検索                              |
| `/movies/{id}` | 映画詳細・レビュー                          |
| `/recommend`   | AI推薦リクエスト・結果                      |
| `/boards`      | 掲示板一覧・投稿詳細・コメント              |
| `/admin`       | 管理者ダッシュボード・同期管理（ADMIN権限） |

ログイン・会員登録はSpring Securityでフォーム認証。掲示板の閲覧は未ログイン可、投稿・編集・AI推薦はログイン必要です。

---

## プロジェクト構成（抜粋）

```
src/main/java/com/movieWorld/
├── config/          # Security, Message, Web, RecommendProperties, SchedulerProperties
├── controller/      # Home, User, Movie, Review, Recommend, Board, Admin
├── service/         # User, Movie, Review, MovieSync, Recommend, Board, BoardComment
├── mapper/          # MyBatis（各テーブル対応）
├── domain/          # エンティティ
├── dto/             # request, response, api（TMDB・Gemini用）
├── api/             # MovieApiClient, GeminiRecommendClient
├── scheduler/       # MovieSyncScheduler
└── util/            # TmdbLocaleUtil
src/main/resources/
├── messages*.properties   # 韓国語・日本語メッセージ
├── templates/       # Thymeleaf（layout, fragments, 各画面）
└── mapper/*.xml     # MyBatis XML
```

---

# Movie World（한국어）

사용자 리뷰를 바탕으로 **Google Gemini AI**가 취향을 분석하고, **TMDB** 영화 데이터와 연동해 맞춤 추천을 제공하는 웹 애플리케이션입니다.  
Spring Boot로 구현한 학습·포트폴리오용 프로젝트입니다.

---

## 기술 스택

| 구분        | 기술                                                                  |
| ----------- | --------------------------------------------------------------------- |
| 언어·런타임 | Java 17                                                               |
| 프레임워크  | Spring Boot 3.5.10                                                    |
| 보안        | Spring Security（폼 로그인, BCrypt）                                  |
| ORM         | MyBatis 3.0.5                                                         |
| DB          | MySQL 8.0                                                             |
| 템플릿      | Thymeleaf, thymeleaf-layout-dialect, thymeleaf-extras-springsecurity6 |
| 프론트      | Bootstrap 5, JavaScript（AJAX）                                       |
| 빌드        | Gradle                                                                |
| 외부 API    | TMDB API（영화 데이터）, Google Gemini API（AI 추천）                 |

---

## 주요 기능

- **영화 검색·목록**  
  TMDB API 연동. 인기순·최신순·평점순 정렬, 제목 검색, 영화 상세 페이지.

- **리뷰**  
  영화별 1~5점 평가와 코멘트. 1인 1영화당 1건（수정·삭제 가능）.

- **AI 영화 추천**  
  리뷰 5개 이상 시 이용 가능. Gemini가 취향을 분석해 추천 5편을 이유와 함께 표시. 일 30회·분 1회 레이트 제한, 결과 캐시 옵션 지원.

- **게시판**  
  글 작성·수정·삭제, 댓글, 검색, 페이징.

- **다국어**  
  한국어/일본어. 세션으로 언어 전환. MessageSource 기반 국제화. TMDB 영화 정보·AI 추천 이유도 선택 언어에 맞춰 표시.

- **관리자 기능**  
  회원·영화·리뷰·게시판·추천 건수 통계 대시보드, 영화 동기화 로그 목록, 수동 동기화（AJAX + 진행 모달）, Spring Scheduler를 이용한 자동 동기화（매일 2시·3시·4시）.

---

## 데이터베이스 설계

MySQL（데이터베이스명: `movieworlddb`）에 아래 12개 테이블을 설계·사용했습니다.

| 테이블명                  | 설명                                                          |
| ------------------------- | ------------------------------------------------------------- |
| **roles**                 | 권한（ROLE_USER, ROLE_ADMIN）                                 |
| **users**                 | 회원（이메일, BCrypt 비밀번호, 이름, role_id）                |
| **genres**                | 장르（TMDB 연동용）                                           |
| **movies**                | 영화 정보（TMDB 동기화로 적재）                               |
| **movie_genres**          | 영화–장르 다대다 매핑                                         |
| **reviews**               | 리뷰（user_id, movie_id, rating 1~5, content）. 1인 1영화 1건 |
| **boards**                | 게시판 글                                                     |
| **board_comments**        | 게시판 댓글                                                   |
| **recommendations**       | AI 추천 결과 캐시（사용자당 유효 1건）                        |
| **recommendation_items**  | 추천 내 영화（순위, 추천 이유）                               |
| **recommend_rate_limits** | 추천 요청 일별 횟수 제한                                      |
| **movie_sync_log**        | TMDB 동기화 실행 로그（BULK / NOW_PLAYING / UPCOMING）        |

---

## 화면·주요 URL

| 경로           | 설명                                      |
| -------------- | ----------------------------------------- |
| `/`            | 홈（인기·최신 영화）                      |
| `/movies`      | 영화 목록·검색                            |
| `/movies/{id}` | 영화 상세·리뷰                            |
| `/recommend`   | AI 추천 요청·결과                         |
| `/boards`      | 게시판 목록·글 상세·댓글                  |
| `/admin`       | 관리자 대시보드·동기화 관리（ADMIN 권한） |

로그인·회원가입은 Spring Security 폼 인증. 게시판 조회는 비로그인 가능, 글쓰기·수정·AI 추천은 로그인 필요입니다.

---

## 프로젝트 구조（요약）

```
src/main/java/com/movieWorld/
├── config/          # Security, Message, Web, RecommendProperties, SchedulerProperties
├── controller/      # Home, User, Movie, Review, Recommend, Board, Admin
├── service/         # User, Movie, Review, MovieSync, Recommend, Board, BoardComment
├── mapper/          # MyBatis（각 테이블 대응）
├── domain/          # 엔티티
├── dto/             # request, response, api（TMDB·Gemini용）
├── api/             # MovieApiClient, GeminiRecommendClient
├── scheduler/       # MovieSyncScheduler
└── util/            # TmdbLocaleUtil
src/main/resources/
├── messages*.properties   # 한국어·일본어 메시지
├── templates/       # Thymeleaf（layout, fragments, 각 화면）
└── mapper/*.xml     # MyBatis XML
```
