package com.honeygoods.member.repository;

import com.honeygoods.common.config.JpaAuditConfig;
import com.honeygoods.member.domain.Member;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaAuditConfig.class)
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    private final Member YOON = Member.builder()
            .name("yoonminsoo")
            .nickname("yoonKun")
            .email("yoon@test.com")
            .password("123456789")
            .build();

    @Test
    @DisplayName("회원을 저장한다.")
    void save_test() {
        Member save = memberRepository.save(YOON);

        assertThat(save.getId()).isNotNull();
    }

    @Test
    @DisplayName("식별자로 회원을 조회한다.")
    void findById_test() {
        Member save = memberRepository.save(YOON);

        Member find = memberRepository.findById(save.getId()).orElseThrow();

        assertThat(find.getId()).isEqualTo(save.getId());
        assertThat(find.getName()).isEqualTo(find.getName());
        assertThat(find.getNickname()).isEqualTo(find.getNickname());
        assertThat(find.getEmail()).isEqualTo(find.getEmail());
        assertThat(find.getPassword()).isEqualTo(find.getPassword());
        assertThat(find.getRole()).isEqualTo(find.getRole());
    }

    @Test
    @DisplayName("닉네임의 존재 여부를 확인한다.")
    void exsiststByNickname_test() {
        Member save = memberRepository.save(YOON);

        boolean exists = memberRepository.existsByNickname(save.getNickname());

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("이메일의 존재 여부를 확인한다.")
    void exsistsByEmail_test() {
        Member save = memberRepository.save(YOON);

        boolean exists = memberRepository.existsByEmail(save.getEmail());

        assertThat(exists).isTrue();
    }


}