package com.mottainai.mottainaicrud.util;

// IDENTIFICAÇÃO DOS REGISTROS TÉCNICOS
// Reúne CNPJs, nome do cargo e ID usados pelos filtros, proteções e carga sintética já existentes.

public final class AnonymizationConstants {

    private AnonymizationConstants() {
        // Impede instanciação da classe.
    }

    // REFERÊNCIAS DA ANONIMIZAÇÃO
    // EmployeeService procura a loja pelo CNPJ e o cargo pelo nome; o ID abaixo é usado nos filtros/carga que já o assumem.
    public static final String TECHNICAL_COMPANY_CNPJ = "00000000000000";

    public static final String TECHNICAL_STORE_CNPJ = "00000000000001";

    public static final String TECHNICAL_ROLE_NAME = "Registro anonimizado";

    public static final int TECHNICAL_RECORD_ID = 101;
}