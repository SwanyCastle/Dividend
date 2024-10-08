package com.dividend.web;

import com.dividend.model.Company;
import com.dividend.model.constants.CacheKey;
import com.dividend.persist.entity.CompanyEntity;
import com.dividend.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
//import org.springframework.data.redis.cache.RedisCacheManager; // -> 갑자기 안됨 왜지 근데
import org.springframework.cache.CacheManager; // 이거 추가하니깐 또 되네 ...
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/company")
public class CompanyController {

    private final CompanyService companyService;
    private final CacheManager redisCacheManager;

    /**
     * 회사 검색 자동완성
     *
     * @param keyword
     * @return List<String>
     */
    @GetMapping("/auto-complete")
    public ResponseEntity<List<String>> autoComplete(@RequestParam String keyword) {
//        return ResponseEntity.ok(companyService.autoComplete(keyword));
        return ResponseEntity.ok(companyService.getCompanyNamesByKeyword(keyword));
    }

    /**
     * 회사 정보 전체 조회
     *
     * @return ResponseEntity<List < CompanyEntity>>
     * 회사 티커 목록 : O, MMM, NKE, COKE, AAPL, QQQ, SPY, DIA
     */
    @GetMapping
    @PreAuthorize("hasRole('READ')")
    public ResponseEntity<Page<CompanyEntity>> searchComapny(final Pageable pageable) {
        return ResponseEntity.ok(companyService.getAllCompany(pageable));
    }

    /**
     * 회사 및 배당금 정보 저장
     *
     * @param companyRequest
     * @return ResponseEntity<Company>
     */
    @PostMapping
    @PreAuthorize("hasRole('WRITE')")
    public ResponseEntity<Company> addCompany(
            @RequestBody Company companyRequest
    ) {
        String ticker = companyRequest.getTicker().trim();
        if (ObjectUtils.isEmpty(ticker)) {
            throw new RuntimeException("Ticker is empty");
        }

        Company savedCompany = companyService.save(ticker);
        companyService.addAutoCompleteKeyword(savedCompany.getName());
        return ResponseEntity.ok(savedCompany);
    }

    /**
     * 회사 정보 삭제
     * @param ticker
     * @return String
     */
    @DeleteMapping("/{ticker}")
    @PreAuthorize("hasRole('WRITE')")
    public ResponseEntity<String> deleteCompany(@PathVariable String ticker) {
        String companyName = companyService.deleteCompany(ticker);
        clearFinanceCache(companyName);
        return ResponseEntity.ok(companyName);
    }

    public void clearFinanceCache(String companyName) {
        redisCacheManager.getCache(CacheKey.KEY_FINANCE).evict(companyName);
    }
}
