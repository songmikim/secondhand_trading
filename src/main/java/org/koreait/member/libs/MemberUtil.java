package org.koreait.member.libs;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.koreait.member.MemberInfo;
import org.koreait.member.constants.Authority;
import org.koreait.member.entities.Member;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Lazy
@Component
@RequiredArgsConstructor
public class MemberUtil {
    private final HttpServletRequest request;

    // 로그인 여부
    public boolean isLogin() {
        return getMember() != null;
    }

    // 관리자 여부
    public boolean isAdmin() {
        return isLogin() && getMember().getAuthority() == Authority.ADMIN;
    }

    // 로그인한 회원 정보
    public Member getMember() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof MemberInfo memberInfo) {
            return memberInfo.getMember();
        }

        return null;
    }

    // 비회원 : IP + User-Agent / 회원 : IP + User-Agent + 회원번호
    public int getUserHash() {
        String ip = request.getRemoteAddr();
        String ua = request.getHeader("User-Agent");

        return isLogin() ? Objects.hash(ip, ua, getMember().getSeq()) : Objects.hash(ip, ua);
    }
}
