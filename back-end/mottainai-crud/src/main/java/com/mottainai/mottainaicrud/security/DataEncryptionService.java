package com.mottainai.mottainaicrud.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import java.security.SecureRandom;
import java.security.GeneralSecurityException;

import java.util.Base64;

// PROTEÇÃO REVERSÍVEL DOS DADOS PESSOAIS — AES-256-GCM
// Usa chave de 32 bytes, IV aleatório e tag de autenticação; não substitui o BCrypt utilizado nas senhas.

@Service
public class DataEncryptionService {

    // Define o algoritmo de criptografia que será usado:
    // AES no modo GCM, sem padding.
    private static final String ALGORITHM = "AES/GCM/NoPadding";

    // Tamanho do IV em bytes.
    // No AES-GCM, 12 bytes (96 bits) é o tamanho recomendado.
    private static final int IV_LENGTH = 12;

    // Tamanho da tag de autenticação do GCM em bits.
    // Ela ajuda a verificar se o dado criptografado foi alterado.
    private static final int TAG_LENGTH = 128;

    // Objeto usado para representar a chave AES que será usada
    // tanto para criptografar quanto para descriptografar.
    private final SecretKeySpec key;

    // Gerador de números aleatórios seguro.
    // É usado para gerar um IV diferente a cada criptografia.
    private final SecureRandom secureRandom = new SecureRandom();

    public DataEncryptionService(
            @Value("${mottainai.crypto.key}") String encodedKey) {

        // Converte a chave que está em Base64 para os bytes reais da chave.
        byte[] keyBytes =
                Base64.getDecoder().decode(encodedKey);

        // AES-256 precisa de uma chave de exatamente 32 bytes,
        // porque 32 bytes = 256 bits.
        if (keyBytes.length != 32) {
            throw new IllegalArgumentException(
                    "A chave AES deve possuir 256 bits."
            );
        }

        // Cria o objeto de chave que o Java consegue usar com AES.
        this.key =
                new SecretKeySpec(keyBytes, "AES");
    }

    // CRIPTOGRAFIA E FORMATO PERSISTIDO
    // Gera IV novo por operação e armazena Base64 de IV + ciphertext/tag; a tag protege a integridade do conteúdo.
    // O mesmo CPF ou email pode produzir ciphertext diferente, portanto igualdade de valores cifrados não implementa busca textual.
    public String encrypt(String value) {
        // Evita tentar criptografar um valor inexistente.
        if (value == null) {
            return null;
        }

        try {
            // Cria um espaço de 12 bytes para armazenar o IV.
            byte[] iv =
                    new byte[IV_LENGTH];

            // Preenche o IV com valores aleatórios seguros.
            secureRandom.nextBytes(iv);

            // Cria uma instância do mecanismo de criptografia
            // usando AES/GCM/NoPadding.
            Cipher cipher =
                    Cipher.getInstance(ALGORITHM);

            // Configura os parâmetros específicos do GCM:
            // tamanho da tag de autenticação + IV.
            GCMParameterSpec parameterSpec =
                    new GCMParameterSpec(
                            TAG_LENGTH,
                            iv
                    );

            // Configura o Cipher para o modo de criptografia,
            // usando a chave AES e o IV gerado.
            cipher.init(
                    Cipher.ENCRYPT_MODE,
                    key,
                    parameterSpec
            );

            // Converte a String para bytes em UTF-8
            // e realiza a criptografia.
            byte[] encrypted =
                    cipher.doFinal(
                            value.getBytes(
                                    StandardCharsets.UTF_8
                            )
                    );

            // Junta o IV e o conteúdo criptografado
            // em um único array de bytes.
            //
            // O IV precisa ser guardado junto do resultado
            // porque será necessário depois na descriptografia.
            byte[] result =
                    ByteBuffer
                            .allocate(
                                    iv.length
                                            + encrypted.length
                            )
                            .put(iv)
                            .put(encrypted)
                            .array();

            // Converte os bytes para Base64,
            // permitindo armazenar o resultado como String no banco.
            return Base64
                    .getEncoder()
                    .encodeToString(result);

        } // Captura erros relacionados às operações de criptografia,
          // como algoritmo inválido, chave inválida ou falha durante a criptografia.
        catch (GeneralSecurityException e) {
            throw new IllegalStateException(
                    "Erro ao criptografar dado.",
                    e
            );
        }
    }
    // RECUPERAÇÃO AUTENTICADA
    // Reusa o IV armazenado e verifica a tag antes de devolver texto; falhas de formato/chave/integridade viram erro técnico.
    // Os Services não enviam marcadores irreversíveis ANON_ para esta operação.
    public String decrypt(String value) {

        // Evita tentar descriptografar um valor inexistente.
        if (value == null) {
            return null;
        }

        try {

            // Converte a String em Base64 que veio do banco
            // novamente para o array de bytes original.
            byte[] decoded =
                    Base64.getDecoder().decode(value);

            // O resultado criptografado precisa conter pelo menos:
            // 12 bytes do IV + 16 bytes da tag de autenticação do GCM.
            //
            // TAG_LENGTH está em bits, por isso dividimos por 8.
            if (decoded.length < IV_LENGTH + (TAG_LENGTH / 8)) {
                throw new IllegalArgumentException(
                        "Dado criptografado inválido."
                );
            }

            // Permite percorrer os bytes armazenados e separar
            // novamente o IV do conteúdo criptografado.
            ByteBuffer buffer =
                    ByteBuffer.wrap(decoded);

            // Separa os primeiros 12 bytes,
            // que correspondem ao IV usado na criptografia.
            byte[] iv =
                    new byte[IV_LENGTH];

            buffer.get(iv);

            // Cria um array com o tamanho de tudo que restou
            // depois da retirada do IV.
            byte[] encrypted =
                    new byte[buffer.remaining()];

            // Coloca nesse array o conteúdo criptografado
            // junto da tag de autenticação gerada pelo GCM.
            buffer.get(encrypted);

            // Cria novamente o mecanismo AES-GCM.
            Cipher cipher =
                    Cipher.getInstance(ALGORITHM);

            // Recria os mesmos parâmetros usados na criptografia,
            // utilizando o IV que estava armazenado junto dos dados.
            GCMParameterSpec parameterSpec =
                    new GCMParameterSpec(
                            TAG_LENGTH,
                            iv
                    );

            // Configura o Cipher para descriptografar,
            // utilizando a mesma chave AES.
            cipher.init(
                    Cipher.DECRYPT_MODE,
                    key,
                    parameterSpec
            );

            // Descriptografa os bytes.
            //
            // O AES-GCM também verifica aqui se o conteúdo
            // ou a tag foram alterados.
            byte[] decrypted =
                    cipher.doFinal(encrypted);

            // Converte os bytes recuperados de volta
            // para a String original.
            return new String(
                    decrypted,
                    StandardCharsets.UTF_8
            );

            // GeneralSecurityException cobre os erros da criptografia.
            // IllegalArgumentException cobre, por exemplo,
            // um valor que não seja um Base64 válido.
        } catch (GeneralSecurityException | IllegalArgumentException e) {
            throw new IllegalStateException(
                    "Erro ao descriptografar dado.",
                    e
            );
        }
    }
}