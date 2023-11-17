package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;

public interface YourEntityRepository extends JpaRepository<YourEntity, Long> {
    // 추가적인 데이터베이스 조회 메서드를 정의할 수 있습니다.
}
