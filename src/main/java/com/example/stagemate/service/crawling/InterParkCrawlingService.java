package com.example.stagemate.service.crawling;

import com.example.stagemate.domain.performance.PerformanceGenre;
import com.example.stagemate.dto.data.CrawledPerformanceInfo;
import com.example.stagemate.domain.performance.PerformanceType;
import com.example.stagemate.domain.performance.PerformanceStatus;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
public class InterParkCrawlingService {
    private static final String BASE_URL = "https://tickets.interpark.com";
    private static final String MUSICAL_URL = BASE_URL + "/contents/genre/musical";
    private static final String PLAY_URL = BASE_URL + "/contents/genre/play";
    private static final String FAMILY_URL = BASE_URL + "/contents/genre/family";
    private static final Duration DEFAULT_WAIT_TIMEOUT = Duration.ofSeconds(10);
    private static final int MAX_NO_NEW_DATA_ATTEMPTS = 5;

    private WebDriver driver;
    private WebDriverWait wait;
    private final Set<String> collectedPerformanceIds = new HashSet<>();


    public List<CrawledPerformanceInfo> crawlMusicalInfo() {
        return crawlInterPark(MUSICAL_URL, PerformanceType.MUSICAL);
    }

    public List<CrawledPerformanceInfo> crawlPlayInfo() {
        return crawlInterPark(PLAY_URL, PerformanceType.PLAY);
    }

    public List<CrawledPerformanceInfo> crawlChildrenAndFamilyInfo() {
        //아동/가족 공연은 뮤지컬, 공연 둘다 가능
        return crawlInterPark(FAMILY_URL, null);
    }

    public List<CrawledPerformanceInfo> crawlPerformances() {
        List<CrawledPerformanceInfo> result = new ArrayList<>();
        result.addAll(crawlMusicalInfo());
        result.addAll(crawlPlayInfo());
        result.addAll(crawlChildrenAndFamilyInfo());
        return result;
    }


    private void safeClick(WebElement element) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(element));
            element.click();
        } catch (Exception e) {
            // Scroll the element into view and try clicking again
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
            try {
                Thread.sleep(500); // Small delay for scrolling
                element.click();
            } catch (Exception ex) {
                // If still fails, try with JavaScript click
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
            }
        }
    }
    
    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    private void scrollDown() {
        try {
            // 부드러운 스크롤을 사용하여 자연스럽게 스크롤 다운
            ((JavascriptExecutor) driver).executeScript(
                "window.scrollBy({top: window.innerHeight * 0.8, behavior: 'smooth'})");
            
            // 스크롤이 완료될 때까지 대기 (필요에 따라 조정 가능)
            sleep(300);
            
        } catch (Exception e) {
            log.warn("스크롤 중 오류 발생: {}", e.getMessage());
            // 스크립트 실행이 실패하면 기본 스크롤 방식으로 대체
            ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, window.innerHeight * 0.8)");
        }
    }
    
    private void scrollToElement(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
        sleep(500);
    }
    
    private void cleanup() {
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception e) {
                log.error("❌ WebDriver 종료 중 오류 발생", e);
            }
        }
    }
    
    private void openRegionFilter() {
        try {
            WebElement filterBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button[aria-label='지역 선택 필터 열기']")));
            safeClick(filterBtn);
        } catch (TimeoutException e) {
            log.error("❌ 지역 필터 열기 실패", e);
            throw new RuntimeException("지역 필터를 열 수 없습니다.", e);
        }
    }


    private void closePopupIfExists() {
        try {
            WebDriverWait popupWait = new WebDriverWait(driver, Duration.ofSeconds(3));
            WebElement closeBtn = popupWait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//div[contains(@class, 'popupHeader')]//button[contains(@class, 'close')]")));
            safeClick(closeBtn);
            log.info("❎ 팝업 닫음");
        } catch (TimeoutException e) {
            log.debug("ℹ️ 팝업 없음");
        } catch (Exception e) {
            log.warn("❌ 팝업 닫기 중 오류 발생", e);
        }
    }

    private Collection<CrawledPerformanceInfo> crawlPerformances(String regionName, PerformanceType performanceType) {

        Collection<CrawledPerformanceInfo> collectedPerformances = new HashSet<>();
        int noNewDataCount = 0;
        collectedPerformanceIds.clear();
        long lastScrollPosition = 0;
        int scrollAttempts = 0;
        final int MAX_SCROLL_ATTEMPTS = 100; // 최대 스크롤 시도 횟수

        while (noNewDataCount < MAX_NO_NEW_DATA_ATTEMPTS && scrollAttempts < MAX_SCROLL_ATTEMPTS) {
            // 현재 스크롤 위치 저장
            long currentScrollPosition = (Long) ((JavascriptExecutor) driver).executeScript(
                "return window.pageYOffset || document.documentElement.scrollTop || document.body.scrollTop || 0;");
            
            // 이전에 본 공연 수 저장
            int beforeSize = collectedPerformanceIds.size();
            
            // 스크롤 다운
            scrollDown();
            
            // 새로운 콘텐츠 로드를 위한 대기 (동적 로딩을 위해 충분한 시간 확보)
            sleep(1000); // 1초 대기 (필요에 따라 조정 가능)
            
            // 페이지 하단에 도달했는지 확인
            long newScrollPosition = (Long) ((JavascriptExecutor) driver).executeScript(
                "return window.pageYOffset || document.documentElement.scrollTop || document.body.scrollTop || 0;");
                
            // 스크롤이 더 이상 내려가지 않으면 하단에 도달한 것으로 간주
            if (newScrollPosition == lastScrollPosition) {
                log.debug("📜 페이지 하단에 도달했습니다. (시도: {}/{})", scrollAttempts + 1, MAX_SCROLL_ATTEMPTS);
                noNewDataCount++;
            } else {
                noNewDataCount = 0; // 새로운 콘텐츠가 로드되면 카운터 초기화
            }
            
            lastScrollPosition = newScrollPosition;
            
            // 현재 페이지의 공연 항목 처리
            Collection<CrawledPerformanceInfo> performanceInfos = processPerformanceItems(regionName, performanceType);

            collectedPerformances.addAll(performanceInfos);
            
            // 현재까지 수집된 공연 수 로깅
            int afterSize = collectedPerformanceIds.size();
            
            scrollAttempts++;
        }

        if (noNewDataCount >= MAX_NO_NEW_DATA_ATTEMPTS) {
            log.info("✅ [{}] 모든 공연을 수집했습니다. (총 {}개)", regionName, collectedPerformanceIds.size());
        } else if (scrollAttempts >= MAX_SCROLL_ATTEMPTS) {
            log.warn("⚠️ [{}] 최대 스크롤 시도 횟수({}회)에 도달하여 크롤링을 중단합니다. (수집된 공연: {}개)", 
                   regionName, MAX_SCROLL_ATTEMPTS, collectedPerformanceIds.size());
        }

        return collectedPerformances;

    }

    private Collection<CrawledPerformanceInfo> processPerformanceItems(String regionName, PerformanceType performanceType) {
        Collection<CrawledPerformanceInfo> performanceInfos = new HashSet<>();
        List<WebElement> performances = driver.findElements(
                By.cssSelector("a[class*='TicketItem_ticketItem__']"));

        CrawledPerformanceInfo performanceInfo = null;

        for (WebElement performance : performances) {
            try {
                performanceInfo = processSinglePerformance(performance, regionName, performanceType);
                if (performanceInfo != null) {
                    performanceInfos.add(performanceInfo);
                }
            } catch (Exception e) {
                log.warn("❌ 공연 항목 처리 중 오류 발생", e);
            }
        }


        return performanceInfos;
    }

    private CrawledPerformanceInfo processSinglePerformance(WebElement performance, String regionName, PerformanceType performanceType) {
        String imageUrl = performance.findElement(By.tagName("img")).getAttribute("src");
        String itemId = extractItemId(imageUrl);

        if (itemId == null || collectedPerformanceIds.contains(itemId)) {
            return null;
        }

        collectedPerformanceIds.add(itemId);
        CrawledPerformanceInfo performanceInfo = extractPerformanceInfo(performance, itemId, regionName, performanceType);
//        log.info("✅ 공연 정보 수집됨: {}", performanceInfo);

        return performanceInfo;
    }

    private String extractItemId(String imageUrl) {
        java.util.regex.Matcher matcher = java.util.regex.Pattern
                .compile(".*/([A-Z0-9]+)_p\\.(gif|jpg|jpeg|png)$", java.util.regex.Pattern.CASE_INSENSITIVE)
                .matcher(imageUrl);
        return matcher.find() ? matcher.group(1) : null;
    }



    private CrawledPerformanceInfo extractPerformanceInfo(WebElement performance, String itemId,
                                                        String regionName, PerformanceType performanceType) {
        String title = performance.findElement(By.className("TicketItem_goodsName__Ju76j")).getText();
        String place = performance.findElement(By.className("TicketItem_placeName__ls_9C")).getText();
        String date = performance.findElement(By.className("TicketItem_playDate__5ePr2")).getText();
        String imageUrl = performance.findElement(By.cssSelector("img.TicketItem_image__U6xq6")).getAttribute("src");
        String url = String.format("%s/goods/%s", BASE_URL, itemId);

        return createPerformanceInfo(itemId, title, url, imageUrl, date, place, regionName, performanceType);
    }

    private CrawledPerformanceInfo createPerformanceInfo(String itemId, String title, String url, String imageUrl,
                                                       String date, String place, String region,
                                                       PerformanceType performanceType) {
        String[] parsedDates = parseDate(date);


        PerformanceStatus performanceStatus = getPerformanceStatus(parsedDates[0], parsedDates[1]);

        //8자리 String Date를 LocalDate로 변환
        LocalDate startDate = LocalDate.parse(parsedDates[0], DateTimeFormatter.ofPattern("yyyyMMdd"));
        LocalDate endDate = LocalDate.parse(parsedDates[1], DateTimeFormatter.ofPattern("yyyyMMdd"));

        return CrawledPerformanceInfo.builder()
                .interparkPerformanceId(itemId)
                .performanceName(title)
                .performanceUrl(url)
                .imageUrl(imageUrl)
                .startDate(startDate)
                .endDate(endDate)
                .theaterName(place)
                .region(region)
                .performanceType(performanceType)
                .performanceStatus(performanceStatus)
                .build();
    }

    private PerformanceStatus getPerformanceStatus(String startDate, String endDate) {
        //startDate, endDate는 20250701 형식
        LocalDate currentDate = LocalDate.now(ZoneId.of("Asia/Seoul"));
        LocalDate convertedStartDate = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("yyyyMMdd"));
        LocalDate convertedendDate = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("yyyyMMdd"));

        if (convertedStartDate.isBefore(currentDate) && convertedendDate.isAfter(currentDate)) {
            return PerformanceStatus.ONGOING;
        } else if (convertedendDate.isBefore(currentDate)) {
            return PerformanceStatus.ENDED;
        } else {
            return PerformanceStatus.UPCOMING;
        }
    }

    private String[] parseDate(String date) {
        log.debug("📅 날짜 파싱 시작: {}", date);

        String[] dateParts = date.split(" ~ ");

        if (!date.contains("~")) {
            dateParts = new String[]{date, date};
        } else if (dateParts.length != 2) {
            log.error("❌ 날짜 형식 오류: {}", date);
            return new String[]{"", ""};
        }

        String startDate = normalizeDate(dateParts[0].trim());
        String endDate = normalizeDate(dateParts[1].trim());

        // Handle case where end date doesn't have a year (e.g., "10.31")
        if (endDate.split("\\.").length == 2) {
            endDate = startDate.substring(0, 5) + endDate;
        }

        return new String[]{
            to8DigitDate(startDate),
            to8DigitDate(endDate)
        };
    }

    private String normalizeDate(String date) {
        // Normalize date formats like "2023.7.1" to "2023.07.01"
        String[] parts = date.split("\\.");
        if (parts.length < 2 || parts.length > 3) {
            log.warn("❌ 잘못된 날짜 형식: {}", date);
            return "20000101";
        }

        StringBuilder normalized = new StringBuilder(parts[0]); // year
        for (int i = 1; i < parts.length; i++) {
            normalized.append(".").append(String.format("%02d", Integer.parseInt(parts[i])));
        }
        return normalized.toString();
    }

    private String to8DigitDate(String date) {
        if (date == null || date.isEmpty()) {
            return "20000101";
        }

        String[] parts = date.split("\\.");
        if (parts.length != 3) {
            log.warn("❌ 날짜 형식 오류: {}", date);
            return "20000101";
        }

        String year = parts[0];
        String month = String.format("%02d", Integer.parseInt(parts[1]));
        String day = String.format("%02d", Integer.parseInt(parts[2]));

        return year + month + day;
    }


//    private void initWebDriver() {
//        WebDriverManager.chromedriver().setup();
//        ChromeOptions options = new ChromeOptions();
//
//        driver = new ChromeDriver(options);
//        wait = new WebDriverWait(driver, DEFAULT_WAIT_TIMEOUT);
//    }

    private void initWebDriver() {
        WebDriverManager.chromedriver().setup();

        // 고유한 user-data-dir 경로 생성
        String userDataDir = "/tmp/selenium-profile-" + UUID.randomUUID();
        String downloadDir = "/tmp/downloads-" + UUID.randomUUID();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new"); // Headless 모드 (new는 크롬 최신 방식)
        options.addArguments("--no-sandbox"); // root 권한 문제 방지
        options.addArguments("--disable-dev-shm-usage"); // /dev/shm 크기 부족 문제 방지
        options.addArguments("--disable-gpu"); // GPU 렌더링 비활성화 (리눅스 서버에서 안정적)
        options.addArguments("--window-size=1920,1080"); // 브라우저 해상도 설정
        options.addArguments("--user-data-dir=" + userDataDir); // 고유한 프로필로 충돌 방지
        options.addArguments("--lang=ko-KR"); // 언어 설정 (선택 사항)

        // 자동 다운로드 방지를 위한 설정 (필요 시)
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("download.default_directory", downloadDir);
        prefs.put("profile.default_content_settings.popups", 0);
        options.setExperimentalOption("prefs", prefs);

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, DEFAULT_WAIT_TIMEOUT);

        log.info("🧭 WebDriver 초기화 완료 (userDataDir: {}, downloadDir: {})", userDataDir, downloadDir);
    }



    private List<CrawledPerformanceInfo> crawlInterPark(String url, PerformanceType performanceType) {
        List<CrawledPerformanceInfo> performanceInfos = new ArrayList<>();

        try {
            initWebDriver();

            driver.get(url);

            //전체보기 탭 클릭
            clickAllTab();

            //장르별 탭 순회
            List<WebElement> genreTabs = driver.findElements(By.cssSelector(".genre-tab-item"));


            Collection<CrawledPerformanceInfo> performanceInfosByRegionAndGenre = null;
            for (WebElement genreTab : genreTabs) {
                //장르 탭 클릭
                safeClick(genreTab);

                String performanceGenre = genreTab.getText();
                log.info("🎭 인터파크 [{}] 지역 필터링 크롤러 시작", performanceGenre);

                //PerformanceGenre 외 다른 장르이면 다음 탭으로 이동
                if (!filterByPerformanceGenre(performanceGenre)) continue;

                //지역 선택
                performanceInfosByRegionAndGenre = processRegions(performanceType);
                PerformanceGenre genre;

                if (performanceGenre.equals("뮤지컬")) {
                    genre = PerformanceGenre.FAMILY_MUSICAL;
                    PerformanceType type = PerformanceType.MUSICAL;
                    performanceInfosByRegionAndGenre.forEach(performanceInfo -> performanceInfo.setPerformanceType(type));
                } else if (performanceGenre.equals("연극")) {
                    genre = PerformanceGenre.FAMILY_PLAY;
                    PerformanceType type = PerformanceType.PLAY;
                    performanceInfosByRegionAndGenre.forEach(performanceInfo -> performanceInfo.setPerformanceType(type));
                } else {
                    genre = PerformanceGenre.fromDescription(performanceGenre);
                }
                //genre 로깅
                log.info("선택된 genre ------------------------------------ : {}", genre);

                performanceInfosByRegionAndGenre.forEach(performanceInfo -> performanceInfo.setPerformanceGenre(genre));

                if (performanceType != null) {
                    log.info("🎭 인터파크 [{}] [{}] 지역 필터링 크롤러 완료", performanceType.getDescription(), performanceGenre);
                }
                for (CrawledPerformanceInfo performanceInfo : performanceInfosByRegionAndGenre) {
                    log.info("✅ 공연 정보 수집됨: {}", performanceInfo);
                }

                performanceInfos.addAll(performanceInfosByRegionAndGenre);

            }



        } catch (Exception e) {
            log.error("❌ 크롤링 중 치명적 오류 발생", e);
            throw new RuntimeException("크롤링에 실패했습니다.", e);
        } finally {
            cleanup();
        }

        return performanceInfos;
    }

    //장르 ENUM에 있는 지역만 크롤링
    private boolean filterByPerformanceGenre(String performanceGenre) {
        //아동/가족 공연은 장르탭 대신 뮤지컬, 연극 탭으로 구성
        if(performanceGenre.equals("뮤지컬") || performanceGenre.equals("연극")) return true;

        return PerformanceGenre.contains(performanceGenre);
    }

    private void clickAllTab() {
        try {
            WebElement allTab = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(text(), '전체')]")));
            safeClick(allTab);
        } catch (TimeoutException e) {
            log.warn("⚠️ '전체' 탭을 찾을 수 없습니다. 계속 진행합니다.");
        }
    }

    //지역 선택 받고 크롤링
    private Collection<CrawledPerformanceInfo> processRegions(PerformanceType performanceType) {

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        //지역 필터링
        openRegionFilter();

        //지역 선택 버튼
        List<WebElement> regionButtons = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                By.cssSelector(".FilterDropDown_dropdown__877eb main button")));




        //스크롤 시작 위치
        WebElement scrollAnchor = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("div.Panel_title__4b2ff")));

        Collection<CrawledPerformanceInfo> performanceInfos = new ArrayList<>();

        //지역별 크롤링
        for (int i = 1; i < regionButtons.size(); i++) {
            Collection<CrawledPerformanceInfo> regionPerformanceInfos = processSingleRegion(performanceType, i, scrollAnchor);
            performanceInfos.addAll(regionPerformanceInfos);
        }
        return performanceInfos;
    }


    private Collection<CrawledPerformanceInfo> processSingleRegion(PerformanceType performanceType, int regionIndex, WebElement scrollAnchor) {
        try {
            openRegionFilter();
            scrollToElement(scrollAnchor);
            
            List<WebElement> refreshedRegions = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                    By.cssSelector(".FilterDropDown_dropdown__877eb main button")));
            
            if (regionIndex >= refreshedRegions.size()) {
                log.warn("⚠️ 지역 인덱스 {}가 유효하지 않습니다. 최대 인덱스: {}", regionIndex, refreshedRegions.size() - 1);
                return Collections.emptyList();
            }
            
            WebElement regionButton = refreshedRegions.get(regionIndex);
            String regionName = regionButton.getText();
            safeClick(regionButton);
            
            log.info("✅ [{}] 지역 선택됨", regionName);
            closePopupIfExists();
            return crawlPerformances(regionName, performanceType);
            
        } catch (Exception e) {
            log.error("❌ [{}] 지역 처리 중 오류 발생: {}", regionIndex, e.getMessage(), e);
        }

        return Collections.emptyList();
    }



}
