package edu.unl.ec.gimnasia.faces.validator;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;

@FacesValidator("cedulaEcuatorianaValidator")
public class CedulaEcuatorianaValidator implements Validator<String> {

    private static final int LONGITUD_CEDULA = 10;
    private static final int[] COEFICIENTES = {2, 1, 2, 1, 2, 1, 2, 1, 2};

    @Override
    public void validate(FacesContext context, UIComponent component, String value) throws ValidatorException {
        if (value == null || value.isBlank()) {

            return;
        }
        String cedula = value.trim();
        if (!tieneFormatoValido(cedula) || !tieneDigitoVerificadorValido(cedula)) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Cédula inválida",
                    "El número de cédula ingresado no corresponde a una cédula ecuatoriana valida."));
        }
    }

    private boolean tieneFormatoValido(String cedula) {
        if (cedula.length() != LONGITUD_CEDULA || !cedula.chars().allMatch(Character::isDigit)) {
            return false;
        }

        Integer codigoProvincia = Integer.valueOf(cedula.substring(0, 2));
        int tercerDigito = Character.getNumericValue(cedula.charAt(2));
        return codigoProvincia >= 1 && codigoProvincia <= 24 && tercerDigito < 6;
    }

    private boolean tieneDigitoVerificadorValido(String cedula) {
        int suma = 0;
        for (int posicion = 0; posicion < COEFICIENTES.length; posicion++) {
            int digito = Character.getNumericValue(cedula.charAt(posicion)) * COEFICIENTES[posicion];
            suma += digito >= 10 ? digito - 9 : digito;
        }
        int digitoVerificador = Character.getNumericValue(cedula.charAt(9));
        int residuo = suma % 10;
        int digitoEsperado = residuo == 0 ? 0 : 10 - residuo;
        return digitoEsperado == digitoVerificador;
    }
}
