package sample.cafekiosk.spring.api.service.mail;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sample.cafekiosk.spring.IntegrationTestSupport;
import sample.cafekiosk.spring.domain.history.mail.MailSendHistoryRepository;

class MailServiceTest extends IntegrationTestSupport {

    @Autowired
    private MailService mailService;

    @Autowired
    private MailSendHistoryRepository mailSendHistoryRepository;

    @AfterEach
    void tearDown() {
        mailSendHistoryRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("메일 전송이 성공하면 이력을 저장한다")
    void sendMail() {
        // given
        String fromEmail = "수찬이@gmail.com";
        String toEmail = "우테코@gmail.com";
        String subject = "이수찬 우테코 합격 메일 전송";
        String content = "축하합니다. 이수찬님은 우테코에 합격하셨습니다.";

        // stubbing
        when(mailSendClient.sendEmail(any(String.class), any(), any(), any()))
                .thenReturn(true);

        // when
        boolean result = mailService.sendMail(fromEmail, toEmail, subject, content);

        // then
        assertThat(result).isTrue();
        assertThat(mailSendHistoryRepository.findAll()).hasSize(1)
                .extracting("fromEmail", "toEmail", "subject", "content")
                .containsExactly(
                        tuple(fromEmail, toEmail, subject, content)
                );
    }

    @Test
    @DisplayName("메일 전송이 실패하면 이력을 저장하지 않는다")
    void sendMail_fail() {
        // given
        String fromEmail = "수찬이@gmail.com";
        String toEmail = "우테코@gmail.com";
        String subject = "이수찬 우테코 합격 메일 전송";
        String content = "축하합니다. 이수찬님은 우테코에 합격하셨습니다.";

        // stubbing
        when(mailSendClient.sendEmail(any(String.class), any(), any(), any()))
                .thenReturn(false);

        // when
        boolean result = mailService.sendMail(fromEmail, toEmail, subject, content);

        // then
        assertThat(result).isFalse();
        assertThat(mailSendHistoryRepository.findAll()).isEmpty();
    }

}
