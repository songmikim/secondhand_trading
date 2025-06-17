package org.koreait.global.configs;

import lombok.RequiredArgsConstructor;
import org.koreait.member.entities.Member;
import org.koreait.member.libs.MemberUtil;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuditorAwareImpl implements AuditorAware<String> {

    private final MemberUtil memberUtil;

    @Override
    public Optional<String> getCurrentAuditor() {
        String email = null;

        if(memberUtil.isLogin()){
            Member member = memberUtil.getMember();
            email = member.getEmail();
        }

        return Optional.ofNullable(email);
    }
}
