package com.projeto.unify.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String remetente;

    public void enviarCredenciaisAcesso(String emailDestinatario, String emailInstitucional,
                                        String senha, String nomeDestinatario) {
        String assunto = "UniFy - Suas credenciais de acesso administrativo";

        String conteudo =
                "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;'>" +
                        "<h2 style='color: #4285f4;'>Bem-vindo ao UniFy!</h2>" +
                        "<p>Olá, " + nomeDestinatario + "!</p>" +
                        "<p>Suas credenciais de acesso ao sistema Unify foram criadas:</p>" +
                        "<p><strong>Email institucional:</strong> " + emailInstitucional + "</p>" +
                        "<p><strong>Senha temporária:</strong> " + senha + "</p>" +
                        "<p style='color: red;'><strong>IMPORTANTE:</strong> " +
                        "Você deverá alterar esta senha no seu primeiro acesso ao sistema.</p>" +
                        "<p>Atenciosamente,<br>Equipe UniFy</p>" +
                        "</div>";

        try {
            logger.info("Iniciando envio de email para: {}", emailDestinatario);

            MimeMessage mensagem = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensagem, true, "UTF-8");

            helper.setFrom(remetente);
            helper.setTo(emailDestinatario);
            helper.setSubject(assunto);
            helper.setText(conteudo, true); // HTML

            mailSender.send(mensagem);
            logger.info("Email enviado com sucesso para: {}", emailDestinatario);
        } catch (MessagingException e) {
            logger.error("Erro ao enviar e-mail: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao enviar e-mail: " + e.getMessage(), e);
        }
    }
}