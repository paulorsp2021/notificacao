package com.javapaulo.notificacao.business;

import com.javapaulo.notificacao.Infrastructure.exceptions.EmailException;
import com.javapaulo.notificacao.business.dto.TarefasDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Value("${envio.email.remetente}")
    private String remetente;

    @Value("${envio.email.nomeRemetente}")
    private String nomeRemetente;

    public void enviaEmail(TarefasDTO tarefasDTO){

        try {
            MimeMessage mensagem = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mensagem, true, StandardCharsets.UTF_8.name());

            messageHelper.setFrom(new InternetAddress(remetente, nomeRemetente));
            messageHelper.setTo(InternetAddress.parse(tarefasDTO.getEmailUsuario()));
            messageHelper.setSubject("Notificação de Tarefa");

            Context context = new Context();
            context.setVariable("nomeTarefa", tarefasDTO.getNomeTarefa());
            context.setVariable("dataEvento", tarefasDTO.getDataEvento());
            context.setVariable("descricao", tarefasDTO.getDescricao());
            String template = templateEngine.process("notificacao", context);
            messageHelper.setText(template, true);
            javaMailSender.send(mensagem);

        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new EmailException("Erro ao Enviar Email", e.getCause());
        }
    }

}
